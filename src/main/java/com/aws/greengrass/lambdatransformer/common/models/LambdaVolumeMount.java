/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package com.aws.greengrass.lambdatransformer.common.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import static com.aws.greengrass.lambdatransformer.common.Constants.DEFAULT_ADD_GROUP_OWNER;
import static com.aws.greengrass.lambdatransformer.common.Constants.DEFAULT_LAMBDA_FILE_SYSTEM_PERMISSION;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LambdaVolumeMount {
    private String sourcePath;
    private String destinationPath;

    @Builder.Default
    private LambdaFilesystemPermission permission = DEFAULT_LAMBDA_FILE_SYSTEM_PERMISSION;

    @Builder.Default
    private Boolean addGroupOwner = DEFAULT_ADD_GROUP_OWNER;
}
