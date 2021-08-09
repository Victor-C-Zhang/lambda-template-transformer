/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package com.aws.greengrass.lambdatransformer;

import com.amazon.aws.iot.greengrass.component.common.ComponentRecipe;
import com.amazon.aws.iot.greengrass.component.common.ComponentType;
import com.amazon.aws.iot.greengrass.component.common.DependencyProperties;
import com.amazon.aws.iot.greengrass.component.common.DependencyType;
import com.amazon.aws.iot.greengrass.component.common.Platform;
import com.amazon.aws.iot.greengrass.component.common.RecipeFormatVersion;
import com.aws.greengrass.deployment.templating.exceptions.RecipeTransformerException;
import com.aws.greengrass.lambdatransformer.common.models.DefaultConfiguration;
import com.aws.greengrass.lambdatransformer.common.models.LambdaRuntime;
import com.aws.greengrass.lambdatransformer.common.models.LambdaTemplateParams;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vdurmont.semver4j.Semver;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.aws.greengrass.lambdatransformer.common.Constants.LAMBDA_SETENV_ARN_PARAM_NAME;
import static com.aws.greengrass.lambdatransformer.common.Constants.LAMBDA_SETENV_HANDLER_PARAM_NAME;
import static com.aws.greengrass.lambdatransformer.common.Constants.LAMBDA_SETENV_LAMBDA_RUNTIME_PARAM_NAME;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class LambdaTransformerTest {
    private static final ObjectMapper OBJECT_MAPPER =
            new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, true);

    @Test
    public void GIVEN_lambdaRequest_WHEN_create_component_from_lambda_no_dependencies_THEN_return_expected_values() throws Exception {
        Map<String, Object> templateParams = new HashMap<String, Object>() {{
            put("lambdaArn", TestData.LAMBDA_FUNCTION_ARN_1);
            put("lambdaRuntime", LambdaRuntime.Python37);
            put("lambdaHandler", TestData.LAMBDA_FUNCTION_HANDLER);
        }};

        LambdaTemplateParams params = TestData.LAMBDA_PARAMETERS_2
                .platforms(Collections.singletonList(TestData.windows))
                .build();

        ComponentRecipe paramFile = ComponentRecipe.builder()
                .recipeFormatVersion(RecipeFormatVersion.JAN_25_2020)
                .componentName(TestData.COMPONENT_NAME_1)
                .componentVersion(new Semver(TestData.COMPONENT_VERSION_STR_1))
                .templateParameters(templateParams)
                .build();

        ComponentRecipe generated = new LambdaTransformer().transform(paramFile,
                OBJECT_MAPPER.readTree(OBJECT_MAPPER.writeValueAsString(params)));

        verifyGeneratedComponentRecipe(generated, TestData.COMPONENT_NAME_1, new Semver(TestData.COMPONENT_VERSION_STR_1), params);
    }

    @Test
    public void GIVEN_lambdaRequest_WHEN_create_component_from_lambda_multiple_dependencies_THEN_return_expected_values() throws Exception {
        DependencyProperties property1 = DependencyProperties.builder()
                .dependencyType(DependencyType.SOFT)
                .versionRequirement("<2.0.0")
                .build();

        DependencyProperties property2 = DependencyProperties.builder()
                .dependencyType(DependencyType.HARD)
                .versionRequirement(">1.0.0")
                .build();

        HashMap<String, DependencyProperties> dependencies = new HashMap<String, DependencyProperties>() {{
            put("CUSTOMER_DEPENDENCY_1", property1);
            put("CUSTOMER_DEPENDENCY_2", property2);
        }};

        Map<String, Object> templateParams = new HashMap<String, Object>() {{
            put("lambdaArn", TestData.LAMBDA_FUNCTION_ARN_1);
            put("lambdaRuntime", LambdaRuntime.Python37);
            put("lambdaHandler", TestData.LAMBDA_FUNCTION_HANDLER);
        }};

        LambdaTemplateParams params = TestData.LAMBDA_PARAMETERS_2
                .platforms(Collections.singletonList(TestData.all))
                .componentDependencies(dependencies)
                .build();

        ComponentRecipe paramFile = ComponentRecipe.builder()
                .recipeFormatVersion(RecipeFormatVersion.JAN_25_2020)
                .componentName(TestData.COMPONENT_NAME_1)
                .componentVersion(new Semver(TestData.COMPONENT_VERSION_STR_1))
                .templateParameters(templateParams)
                .build();

        ComponentRecipe generated = new LambdaTransformer().transform(paramFile,
                OBJECT_MAPPER.readTree(OBJECT_MAPPER.writeValueAsString(params)));

        verifyGeneratedComponentRecipe(generated, TestData.COMPONENT_NAME_1, new Semver(TestData.COMPONENT_VERSION_STR_1), params);
    }

    @ParameterizedTest
    @NullAndEmptySource
    public void GIVEN_lambdaRequest_WHEN_create_component_from_lambda_with_empty_platform_THEN_throw_transformer_exception(List<Platform> platforms) {
        Map<String, Object> templateParams = new HashMap<String, Object>() {{
            put("lambdaArn", TestData.LAMBDA_FUNCTION_ARN_1);
            put("lambdaRuntime", LambdaRuntime.Python37);
            put("lambdaHandler", TestData.LAMBDA_FUNCTION_HANDLER);
        }};

        LambdaTemplateParams params = TestData.LAMBDA_PARAMETERS_2
                .platforms(platforms)
                .build();

        ComponentRecipe paramFile = ComponentRecipe.builder()
                .recipeFormatVersion(RecipeFormatVersion.JAN_25_2020)
                .componentName(TestData.COMPONENT_NAME_1)
                .componentVersion(new Semver(TestData.COMPONENT_VERSION_STR_1))
                .templateParameters(templateParams)
                .build();

        final RecipeTransformerException ex = assertThrows(RecipeTransformerException.class,
                () -> new LambdaTransformer().transform(paramFile,
                        OBJECT_MAPPER.readTree(OBJECT_MAPPER.writeValueAsString(params)))
        );
        assertThat(ex.getMessage(), CoreMatchers.containsString("At least one platform is expected to be set by caller"));
    }

    void verifyGeneratedComponentRecipe(ComponentRecipe recipe, String providedComponentName,
                                        Semver providedComponentVerison, LambdaTemplateParams params) {
        assertEquals(recipe.getComponentVersion(), providedComponentVerison);
        assertEquals(recipe.getComponentName(), providedComponentName);
        assertEquals(recipe.getComponentType(), ComponentType.LAMBDA);

        assertEquals(recipe.getManifests().size(), 1);

        // only 1 platform is ever given, so this is ok (for now)
        assertEquals(recipe.getManifests().get(0).getPlatform(), params.getPlatforms().get(0));

        assertEquals(recipe.getManifests().get(0).getArtifacts().size(), 1);

        // manifests::lifecycle should be empty
        assertEquals(recipe.getManifests().get(0).getLifecycle().size(), 0);

        // top-level lifecycle tag contains all lifecycle info
        Map<String, Object> lifecycleMap = recipe.getLifecycle();
        Map<String, Object> startupMap = (Map<String, Object>) lifecycleMap.get("startup");
        assertThat(startupMap, hasEntry("requiresPrivilege", true));
        assertThat(startupMap, hasEntry(equalTo("script"), notNullValue()));
        Map<String, Object> shutdownMap = (Map<String, Object>) lifecycleMap.get("shutdown");
        assertThat(shutdownMap, hasEntry("requiresPrivilege", true));
        assertThat(shutdownMap, hasEntry(equalTo("script"), notNullValue()));

        // 3 for injected dependencies
        assertEquals(recipe.getComponentDependencies().size(), params.getComponentDependencies().size() + 3);

        assertNotNull(recipe.getComponentConfiguration().getDefaultConfiguration());
        assertNotEquals(recipe.getComponentConfiguration().getDefaultConfiguration().size(), 0);

        DefaultConfiguration configuration = OBJECT_MAPPER.convertValue(
                recipe.getComponentConfiguration().getDefaultConfiguration(), DefaultConfiguration.class);
        Map<String, String> setEnv = (Map<String, String>) recipe.getLifecycle().get("setenv");
        assertEquals(setEnv.get(LAMBDA_SETENV_ARN_PARAM_NAME), params.getLambdaArn());
        assertEquals(setEnv.get(LAMBDA_SETENV_HANDLER_PARAM_NAME), params.getLambdaHandler());
        assertEquals(setEnv.get(LAMBDA_SETENV_LAMBDA_RUNTIME_PARAM_NAME), params.getLambdaRuntime().toString());

        assertEquals(configuration.getContainerMode(), params.getContainerMode());
        assertEquals(configuration.getInputPayloadEncodingType(), params.getInputPayloadEncodingType());
        assertEquals(configuration.getTimeoutInSeconds(), params.getTimeoutInSeconds());
        assertEquals(configuration.getMaxIdleTimeInSeconds(), params.getMaxIdleTimeInSeconds());
        assertEquals(configuration.getMaxInstancesCount(), params.getMaxInstancesCount());
        assertEquals(configuration.getMaxQueueSize(), params.getMaxQueueSize());
    }
}
