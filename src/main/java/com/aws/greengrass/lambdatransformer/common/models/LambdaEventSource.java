/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package com.aws.greengrass.lambdatransformer.common.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LambdaEventSource {
    private String topic;
    private LambdaEventSourceType type;

    private enum LambdaEventSourceType {
        PUB_SUB,
        IOT_CORE,
    }
}
