/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package com.aws.greengrass.lambdatransformer.common.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum LambdaIsolationMode {
    @JsonProperty("GreengrassContainer")
    GREENGRASS_CONTAINER,
    @JsonProperty("NoContainer")
    NO_CONTAINER,
}
