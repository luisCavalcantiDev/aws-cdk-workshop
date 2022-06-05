package com.myorg;

import software.amazon.awscdk.Duration;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.services.apigateway.LambdaRestApi;
import software.amazon.awscdk.services.lambda.Code;
import software.amazon.awscdk.services.lambda.Function;
import software.amazon.awscdk.services.lambda.Runtime;
import software.amazon.awscdk.services.sns.Topic;
import software.amazon.awscdk.services.sns.subscriptions.SqsSubscription;
import software.amazon.awscdk.services.sqs.Queue;
import software.constructs.Construct;

public class CdkWorkshopStack extends Stack {
    public CdkWorkshopStack(final Construct parent, final String id) {
        this(parent, id, null);
    }

    public CdkWorkshopStack(final Construct parent, final String id, final StackProps props) {
        super(parent, id, props);

        // buildQueues();

        //Defines Lambda function resource
        final Function hello = Function.Builder.create(this, "HelloHandler")
                .runtime(Runtime.NODEJS_14_X)
                .code(Code.fromAsset("lambda"))
                .handler("hello.handler")
                .build();

        final HitCounter helloWithCounter = new HitCounter(this, "HelloHitCounter", HitCounterProps.builder()
            .downstream(hello)
            .build());
        
        //Defines an API Gateway REST API resource based by "hello" lambda function 
        LambdaRestApi.Builder.create(this, "Endpoint")
                .handler(helloWithCounter.getHandler())
                .build();
    }

    //Defines SQS queue and SNS queue resources and add to SNS queue subscriptions.
    private void buildQueues() {
        final Queue queue = Queue.Builder.create(this, "CdkWorkshopQueue")
                .visibilityTimeout(Duration.seconds(300))
                .build();

        final Topic topic = Topic.Builder.create(this, "CdkWorkshopTopic")
                .displayName("My First Topic Yeah")
                .build();

        topic.addSubscription(new SqsSubscription(queue));
    }
}
