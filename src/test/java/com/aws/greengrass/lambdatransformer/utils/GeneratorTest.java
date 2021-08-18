/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package com.aws.greengrass.lambdatransformer.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.aws.greengrass.lambdatransformer.common.utils.GenerateTemplateSchemaFromDefaults;
import org.junit.jupiter.api.Test;

public class GeneratorTest {
    @Test
    void test() throws JsonProcessingException {
        System.out.println(GenerateTemplateSchemaFromDefaults.generateTemplateSchemaFromDefaults());
    }
}
