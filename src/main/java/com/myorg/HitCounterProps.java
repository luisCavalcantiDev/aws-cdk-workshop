package com.myorg;

import software.amazon.awscdk.services.lambda.Function;

public interface HitCounterProps {
    public static Builder builder() {
        return new Builder();
    }

    Function getDownstream();

    public static class Builder {
        private Function downstream;

        public Builder downstream(final Function function) {
            this.downstream = function;
            return this;
        }

        public HitCounterProps build() {
            if (this.downstream == null) {
                throw new IllegalArgumentException("The downstream property is required!");
            }
            
            return () -> downstream;
        }
    }
}
