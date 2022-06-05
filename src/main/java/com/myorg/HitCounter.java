package com.myorg;

import java.util.HashMap;
import java.util.Map;

import software.amazon.awscdk.services.dynamodb.Attribute;
import software.amazon.awscdk.services.dynamodb.AttributeType;
import software.amazon.awscdk.services.dynamodb.Table;
import software.amazon.awscdk.services.lambda.Code;
import software.amazon.awscdk.services.lambda.Function;
import software.amazon.awscdk.services.lambda.Runtime;
import software.constructs.Construct;

public class HitCounter extends Construct {
    private final Function handler;
    private final Table table;

    public HitCounter(final Construct scope, final String id, final HitCounterProps props) {
        super(scope, id);

        this.table = Table.Builder.create(this, "Hits")
            .partitionKey(Attribute.builder()
                .name("path")
                .type(AttributeType.STRING)
                .build())
            .build();

        final Map<String, String> environment = new HashMap<>();
        environment.put("DOWNSTREAM_FUNCTION_NAME", props.getDownstream().getFunctionName());
        environment.put("HITS_TABLE_NAME", this.table.getTableName());

        this.handler = Function.Builder.create(this, "HitCounterHandler")
            .runtime(Runtime.NODEJS_14_X)
            .handler("hitcounter.handler")
            .code(Code.fromAsset("lambda"))
            .environment(environment)
            .build();

        this.table.grantWriteData(this.handler);
        props.getDownstream().grantInvoke(this.handler);
    }

    public Function getHandler() {
        return this.handler;
    }

    public Table getTable() {
        return this.table;
    }
}