package com.myorg;

import java.util.List;
import java.util.Map;

import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.pipelines.CodeBuildStep;
import software.amazon.awscdk.pipelines.CodePipeline;
import software.amazon.awscdk.pipelines.CodePipelineSource;
import software.amazon.awscdk.pipelines.StageDeployment;
import software.amazon.awscdk.services.codecommit.Repository;
import software.constructs.Construct;

public class WorkshopPipelineStack extends Stack {
    public WorkshopPipelineStack(final Construct parent, final String id) {
        this(parent, id, null);
    }

    public WorkshopPipelineStack(final Construct parent, final String id, final StackProps props) {
        super(parent, id, props);

        // Defines CodeCommit repository resource
        final Repository repo = Repository.Builder.create(this, "WorkshopRepo")
                .repositoryName("WorkshopRepo")
                .build();

        CodePipeline pipeline = CodePipeline.Builder.create(this, "Pipeline")
                .pipelineName("WorkshopPipeline")
                .synth(CodeBuildStep.Builder.create("SynthStep")
                        .input(CodePipelineSource.codeCommit(repo, "master"))
                        .installCommands(List.of(
                                "npm install -g aws-cdk")) // Cmds to run before build
                        .commands(List.of(
                                "mvn package", // java maven build (language specific)
                                "npx cdk synth")) // Synth cmd
                        .build())
                .build();

        WorkshopPipelineStage deploy = new WorkshopPipelineStage(this, "Deploy");
        StageDeployment stageDeployment = pipeline.addStage(deploy);

        stageDeployment.addPost(
                CodeBuildStep.Builder.create("TestViewerEndpoint")
                        .projectName("TestViewerEndpoint")
                        .commands(List.of("curl -Ssf $ENDPOINT_URL"))
                        .envFromCfnOutputs(Map.of("ENDPOINT_URL", deploy.hcViewerUrl))
                        .build(),

                CodeBuildStep.Builder.create("TestAPIGatewayEndpoint")
                        .projectName("TestAPIGatewayEndpoint")
                        .commands(List.of(
                                "curl -Ssf $ENDPOINT_URL",
                                "curl -Ssf $ENDPOINT_URL/hello",
                                "curl -Ssf $ENDPOINT_URL/test"))
                        .envFromCfnOutputs(Map.of("ENDPOINT_URL", deploy.hcEndpoint))
                        .build()

        );
    }
}
