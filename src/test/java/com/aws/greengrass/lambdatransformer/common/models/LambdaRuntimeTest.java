/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package com.aws.greengrass.lambdatransformer.common.models;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class LambdaRuntimeTest {
    private static final ObjectMapper OBJECT_MAPPER =
            new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, true);

    // canary test for the exceptional case
    @Test
    void GIVEN_supported_runtime_WHEN_deserialize_called_THEN_it_works() throws JsonProcessingException {
        String supported = "\"java8\"";
        LambdaRuntime runtime = OBJECT_MAPPER.readValue(supported, LambdaRuntime.class);
        assertEquals(runtime, LambdaRuntime.Java8);
    }

    @Test
    void GIVEN_unsupported_runtime_WHEN_deserialize_called_THEN_throw_exception() {
        String unsupported = "\"fortran\"";
        assertThrows(JsonProcessingException.class, () -> OBJECT_MAPPER.readValue(unsupported, LambdaRuntime.class));
    }


}
