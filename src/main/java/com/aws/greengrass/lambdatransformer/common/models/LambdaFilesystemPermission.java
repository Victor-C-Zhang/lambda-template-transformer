/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package com.aws.greengrass.lambdatransformer.common.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum LambdaFilesystemPermission {
    @JsonProperty("ro")
    RO,
    @JsonProperty("rw")
    RW,
}
