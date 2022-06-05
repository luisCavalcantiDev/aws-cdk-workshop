package com.myorg;

import software.amazon.awscdk.CfnOutput;
import software.amazon.awscdk.Stage;
import software.amazon.awscdk.StageProps;
import software.constructs.Construct;

public class WorkshopPipelineStage extends Stage {
    public final CfnOutput hcViewerUrl;
    public final CfnOutput hcEndpoint;

    public WorkshopPipelineStage(final Construct scope, final String id) {
        this(scope, id, null);
    }

    public WorkshopPipelineStage(final Construct scope, final String id, final StageProps props) {
        super(scope, id, props);

        final CdkWorkshopStack service = new CdkWorkshopStack(this, "WebService");

        hcViewerUrl = service.hcViewerUrl;
        hcEndpoint = service.hcEndpoint;        
    }
    
}
