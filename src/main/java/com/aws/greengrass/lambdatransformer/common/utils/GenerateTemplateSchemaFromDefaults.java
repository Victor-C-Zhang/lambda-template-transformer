/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package com.aws.greengrass.lambdatransformer.common.utils;

import com.amazon.aws.iot.greengrass.component.common.TemplateParameter;
import com.aws.greengrass.lambdatransformer.common.models.LambdaRuntime;
import com.aws.greengrass.lambdatransformer.common.models.LambdaTemplateParams;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import java.util.HashMap;

/**
 * Lambda default config values can (and probably will) change in the future. This utility script takes the current
 * default values in this repo and generates the full template schema by populating the default-less schema with
 * default values.
 */
public final class GenerateTemplateSchemaFromDefaults {
    // template schema plaintext
    @SuppressWarnings("PMD.AvoidDuplicateLiterals")
    public static final String LAMBDA_TEMPLATE_SCHEMA_LESS_DEFAULTS =
            "lambdaArn:\n" + "  type: string\n" + "  required: true\n" + "lambdaRuntime:\n"
                    + "  type: string\n" + "  required: true\n"
                    + "lambdaHandler:\n" + "  type: string\n" + "  required: true\n"
                    + "pubsubTopics:\n" + "  type: array\n"
                    + "  required: false\n" + "timeoutInSeconds:\n" + "  type: number\n"
                    + "  required: false\n" + "pinned:\n" + "  type: boolean\n"
                    + "  required: false\n" + "statusTimeoutInSeconds:\n"
                    + "  type: number\n" + "  required: false\n" + "maxQueueSize:\n"
                    + "  type: number\n" + "  required: false\n" + "maxInstancesCount:\n"
                    + "  type: number\n" + "  required: false\n" + "maxIdleTimeInSeconds:\n"
                    + "  type: number\n" + "  required: false\n"
                    + "inputPayloadEncodingType:\n" + "  type: string\n" + "  required: false\n"
                    + "platforms:\n" + "  type: array\n" + "  required: false\n"
                    + "componentDependencies:\n" + "  type: object\n"
                    + "  required: false\n" + "lambdaArgs:\n" + "  type: array\n"
                    + "  required: false\n" + "lambdaEnvironmentVariables:\n"
                    + "  type: object\n" + "  required: false\n" + "containerMode:\n"
                    + "  type: string\n" + "  required: false\n"
                    + "containerParams:\n" + "  type: object\n" + "  required: false\n";

    private static final ObjectMapper mapper = new ObjectMapper(new YAMLFactory());

    // utility class
    private GenerateTemplateSchemaFromDefaults() {
    }

    /**
     * Utility function to generate the default template schema string. Useful for generating recipe files.
     * @return ComponentRecipe::TemplateParameterSchema field as stringified YAML.
     * @throws JsonProcessingException if something goes wrong with de/serialization.
     */
    public static String generateTemplateSchemaFromDefaults() throws JsonProcessingException {
        Parameters schema = mapper.readValue(LAMBDA_TEMPLATE_SCHEMA_LESS_DEFAULTS, Parameters.class);
        LambdaTemplateParams params = LambdaTemplateParams.builder()
                .lambdaArn("null")
                .lambdaRuntime(LambdaRuntime.Java8)
                .lambdaHandler("null")
                .build();

        // populate manually :(
        schema.get("pubsubTopics").setDefaultValue(params.getEventSources());
        schema.get("timeoutInSeconds").setDefaultValue(params.getTimeoutInSeconds());
        schema.get("pinned").setDefaultValue(params.getPinned());
        schema.get("statusTimeoutInSeconds").setDefaultValue(params.getStatusTimeoutInSeconds());
        schema.get("maxQueueSize").setDefaultValue(params.getMaxQueueSize());
        schema.get("maxInstancesCount").setDefaultValue(params.getMaxInstancesCount());
        schema.get("maxIdleTimeInSeconds").setDefaultValue(params.getMaxIdleTimeInSeconds());
        schema.get("inputPayloadEncodingType").setDefaultValue(params.getInputPayloadEncodingType());
        schema.get("platforms").setDefaultValue(params.getPlatforms());
        schema.get("componentDependencies").setDefaultValue(params.getComponentDependencies());
        schema.get("lambdaArgs").setDefaultValue(params.getExecArgs());
        schema.get("lambdaEnvironmentVariables").setDefaultValue(params.getEnvironmentVariables());
        schema.get("containerMode").setDefaultValue(params.getContainerMode());
        schema.get("containerParams").setDefaultValue(params.getContainerParams());

        return mapper.writeValueAsString(schema);
    }

    public static class Parameters extends HashMap<String, TemplateParameter> {
        private static final long serialVersionUID = 3136343651854671477L;
    }
}
