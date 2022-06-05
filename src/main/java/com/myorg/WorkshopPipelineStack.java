package com.myorg;

import java.util.List;

import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.pipelines.CodeBuildStep;
import software.amazon.awscdk.pipelines.CodePipeline;
import software.amazon.awscdk.pipelines.CodePipelineSource;
import software.amazon.awscdk.services.codecommit.Repository;
import software.constructs.Construct;

public class WorkshopPipelineStack extends Stack {
    public WorkshopPipelineStack(final Construct parent, final String id) {
        this(parent, id, null);
    }

    public WorkshopPipelineStack(final Construct parent, final String id, final StackProps props) {
        super(parent, id, props);

        // Defines CodeCommit repository resource
        Repository repo = Repository.Builder.create(this, "WorkshopRepo")
                .repositoryName("WorkshopRepo")
                .build();

        CodePipeline.Builder.create(this, "Pipeline")
                .pipelineName("WorkshopPipeline")
                .synth(CodeBuildStep.Builder.create("SynthStep")
                        .input(CodePipelineSource.codeCommit(repo, "master"))
                        .installCommands(List.of(
                                "npm install -g aws-cdk"))
                        .commands(List.of(
                                "mvn package",
                                "npx cdk synth"))
                        .build())
                .build();
    }
}
