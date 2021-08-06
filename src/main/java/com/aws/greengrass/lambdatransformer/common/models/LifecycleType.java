/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package com.aws.greengrass.lambdatransformer.common.models;

public enum LifecycleType {
    Install,
    Run,
    Standby,
    Restart,
    startup,
    shutdown,
    setenv
}

