/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package com.aws.greengrass.lambdatransformer.common.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum LambdaRuntime {
    @JsonProperty("nodejs10.x")
    Nodejs10X {
        public String toString() {
            return "nodejs10.x";
        }
    },
    @JsonProperty("nodejs12.x")
    Nodejs12X {
        public String toString() {
            return "nodejs12.x";
        }
    },
    @JsonProperty("java8")
    Java8 {
        public String toString() {
            return "java8";
        }
    },
    @JsonProperty("python2.7")
    Python27 {
        public String toString() {
            return "python2.7";
        }
    },
    @JsonProperty("python3.7")
    Python37 {
        public String toString() {
            return "python3.7";
        }
    },
    @JsonProperty("python3.8")
    Python38 {
        public String toString() {
            return "python3.8";
        }
    },
}
