package com.myorg;

import software.amazon.awscdk.App;
import software.amazon.awscdk.Environment;
import software.amazon.awscdk.StackProps;

public final class CdkWorkshopApp {
    public static void main(final String[] args) {
        App app = new App();
        
        Environment envDev = makeEnv(Config.ACCOUNT_ID, Config.REGION_ID);

        //Using CDK Pipelines
        new WorkshopPipelineStack(app, "WorkshopPipelineStack", 
            StackProps.builder()
                .env(envDev)
                .build());

        //Testing create stacks with CDK
        // new CdkWorkshopStack(app, "CdkWorkshopStack", 
        //     StackProps.builder()
        //         .env(envDev)
        //         .build());

        app.synth();
    }

    static Environment makeEnv(String account, String region) {
        return Environment.builder()
                .account(account)
                .region(region)
                .build();
    }    
}
