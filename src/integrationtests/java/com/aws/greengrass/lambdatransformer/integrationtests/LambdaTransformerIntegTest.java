/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package com.aws.greengrass.lambdatransformer.integrationtests;

import com.aws.greengrass.componentmanager.ComponentStore;
import com.aws.greengrass.componentmanager.models.ComponentIdentifier;
import com.aws.greengrass.dependency.Context;
import com.aws.greengrass.deployment.templating.TemplateEngine;
import com.aws.greengrass.deployment.templating.exceptions.RecipeTransformerException;
import com.aws.greengrass.deployment.templating.exceptions.TemplateParameterException;
import com.aws.greengrass.testcommons.testutilities.GGExtension;
import com.vdurmont.semver4j.Semver;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;

@ExtendWith({MockitoExtension.class, GGExtension.class})
public class LambdaTransformerIntegTest extends NucleusLaunchUtils {
    private Context context;
    @Mock
    private ComponentStore mockComponentStore;

    @BeforeAll
    static void beforeAll() throws IOException, URISyntaxException {
        Path artifactsDir = Paths.get(LambdaTransformerIntegTest.class.getResource("artifacts").toURI());
        try (Stream<Path> files = Files.walk(artifactsDir)) {
            for (Path r : files.collect(Collectors.toList())) {
                if (!r.toFile().isDirectory() && "transformer-integ.jar".equals(r.getFileName().toString())) {
                    Files.move(r, r.resolveSibling("transformer.jar"), REPLACE_EXISTING);
                }
            }
        }
    }

    @BeforeEach
    void beforeEach() {
        context = new Context();
        ExecutorService executorService = Executors.newCachedThreadPool();
        context.put(ExecutorService.class, executorService);
    }

    @Test
    void WHEN_lambda_with_minimal_parameter_file_found_THEN_it_is_expanded_with_default_values() throws Exception {
        doAnswer(i -> {
            ComponentIdentifier identifier = i.getArgument(0);
            ComponentIdentifier expectedIdentifier = new ComponentIdentifier("python-listener", new Semver("1.0.0"));
            assertEquals(expectedIdentifier, identifier);
            String actualRecipeString = i.getArgument(1);
            Path expectedFile = Paths.get(getClass().getResource("expected_recipes/minimal_recipe.yaml").toURI());
            String expectedRecipeString = new String(Files.readAllBytes(expectedFile));
            if (System.getProperty("os.name").contains("Windows")) {
                expectedRecipeString = expectedRecipeString.replace("\r\n", "\n");
                /* This is necessary for Windows line feed compatibility. In particular, this is necessary because of
                how git deals with line feeds (more accurately, the way git for Windows deals with line feeds). By
                default, when git for Windows pulls repos, it replaces Unix-style LF line feeds with Windows-style
                CRLF. But internally, Jackson and/or Java uses LF line feeds, so we need to reconcile these differences.
                If at any point the default for git for Windows changes, this code will be unnecessary but the build
                will not break.
                 */
            }
            assertEquals(expectedRecipeString, actualRecipeString);
            return null;
        }).when(mockComponentStore).savePackageRecipe(any(), any());

        Path recipesDirPath = Paths.get(getClass().getResource("minimal_recipes").toURI());
        Path artifactsDirPath = Paths.get(getClass().getResource("artifacts").toURI());
        new TemplateEngine(mockComponentStore, null, context).process(recipesDirPath, artifactsDirPath);
    }

    @Test
    void WHEN_lambda_with_full_parameter_file_found_THEN_expansion_works() throws Exception {
        doAnswer(i -> {
            ComponentIdentifier identifier = i.getArgument(0);
            ComponentIdentifier expectedIdentifier = new ComponentIdentifier("cloud-hello", new Semver("3.2.1"));
            assertEquals(expectedIdentifier, identifier);String actualRecipeString = i.getArgument(1);
            Path expectedFile = Paths.get(getClass().getResource("expected_recipes/full_recipe.yaml").toURI());
            String expectedRecipeString = new String(Files.readAllBytes(expectedFile));
            if (System.getProperty("os.name").contains("Windows")) {
                expectedRecipeString = expectedRecipeString.replace("\r\n", "\n");
            }
            assertEquals(expectedRecipeString, actualRecipeString);
            return null;
        }).when(mockComponentStore).savePackageRecipe(any(), any());

        Path recipesDirPath = Paths.get(getClass().getResource("full_recipes").toURI());
        Path artifactsDirPath = Paths.get(getClass().getResource("artifacts").toURI());
        new TemplateEngine(mockComponentStore, null, context).process(recipesDirPath, artifactsDirPath);
    }


    @Test
    void WHEN_lambda_has_bad_parameter_file_THEN_expansion_throws_an_error() throws URISyntaxException{
        Path recipesDirPath = Paths.get(getClass().getResource("bad_recipes").toURI());
        Path artifactsDirPath = Paths.get(getClass().getResource("artifacts").toURI());
        RecipeTransformerException e = assertThrows(RecipeTransformerException.class,
                () -> new TemplateEngine(mockComponentStore, null, context).process(recipesDirPath,
                artifactsDirPath));
        TemplateParameterException ex = (TemplateParameterException) e.getCause();
        assertThat(ex.getMessage(), containsString("Provided parameters do not satisfy template schema"));
        assertThat(ex.getMessage(), containsString("Provided parameter \"lambdaArn\" does not specify required schema"));
        assertThat(ex.getMessage(), containsString("Provided parameter \"inputPayloadEncodingType\" does not specify required schema"));
    }

    @AfterEach
    void after() {
        if (context != null) {
            context.shutdown();
        }
    }
}
