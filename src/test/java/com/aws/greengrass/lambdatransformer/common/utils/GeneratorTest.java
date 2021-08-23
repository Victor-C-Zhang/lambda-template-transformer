/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package com.aws.greengrass.lambdatransformer.common.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Test;

public class GeneratorTest {
    @Test
    void test() throws JsonProcessingException {
        // assert does not throw
        GenerateTemplateSchemaFromDefaults.generateTemplateSchemaFromDefaults();
    }
}
