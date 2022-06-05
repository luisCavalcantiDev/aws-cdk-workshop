package com.myorg;

import io.github.cdklabs.dynamotableviewer.TableViewer;
import software.amazon.awscdk.CfnOutput;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.services.apigateway.LambdaRestApi;
import software.amazon.awscdk.services.lambda.Code;
import software.amazon.awscdk.services.lambda.Function;
import software.amazon.awscdk.services.lambda.Runtime;
import software.constructs.Construct;

public class CdkWorkshopStack extends Stack {
        public final CfnOutput hcViewerUrl;
        public final CfnOutput hcEndpoint;

        public CdkWorkshopStack(final Construct parent, final String id) {
                this(parent, id, null);
        }

        public CdkWorkshopStack(final Construct parent, final String id, final StackProps props) {
                super(parent, id, props);

                // buildQueues();

                // Defines Lambda function resource
                final Function hello = Function.Builder.create(this, "HelloHandler")
                                .runtime(Runtime.NODEJS_14_X)
                                .code(Code.fromAsset("lambda"))
                                .handler("hello.handler")
                                .build();

                final HitCounter helloWithCounter = new HitCounter(this, "HelloHitCounter", HitCounterProps.builder()
                                .downstream(hello)
                                .build());

                // Defines an API Gateway REST API resource based by "hello" lambda function
                final LambdaRestApi gateway = LambdaRestApi.Builder.create(this, "Endpoint")
                                .handler(helloWithCounter.getHandler())
                                .build();

                // Defines a viewer for the HitCounter table - used for dev only!
                // Using Construct library
                final TableViewer tv = TableViewer.Builder.create(this, "ViewerHitCounter")
                                .title("Hello Hits")
                                .table(helloWithCounter.getTable())
                                .sortBy("-hits")
                                .build();

                hcViewerUrl = CfnOutput.Builder.create(this, "TableViwerUrl")
                                .value(tv.getEndpoint())
                                .build();

                hcEndpoint = CfnOutput.Builder.create(this, "GatewayUrl")
                                .value(gateway.getUrl())
                                .build();
        }

        // Defines SQS queue and SNS queue resources and add to SNS queue subscriptions.
        // private void buildQueues() {
        //         final Queue queue = Queue.Builder.create(this, "CdkWorkshopQueue")
        //                         .visibilityTimeout(Duration.seconds(300))
        //                         .build();

        //         final Topic topic = Topic.Builder.create(this, "CdkWorkshopTopic")
        //                         .displayName("My First Topic Yeah")
        //                         .build();

        //         topic.addSubscription(new SqsSubscription(queue));
        // }
}
