/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package com.aws.greengrass.lambdatransformer.integrationtests;

import com.aws.greengrass.dependency.State;
import com.aws.greengrass.lifecyclemanager.GlobalStateChangeListener;
import com.aws.greengrass.lifecyclemanager.Kernel;
import com.aws.greengrass.mqttclient.MqttClient;
import com.aws.greengrass.testcommons.testutilities.GGServiceTestUtil;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;

import java.nio.file.Path;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

public class NucleusLaunchUtils extends GGServiceTestUtil {
    private static final long TEST_TIME_OUT_SEC = 30L;

    public Kernel kernel;
    GlobalStateChangeListener listener;
    @TempDir
    Path rootDir;
    @Mock
    MqttClient mqttClient;

    public void startNucleusWithConfig(String configFile) throws InterruptedException {
        startNucleusWithConfig(configFile, State.RUNNING, false, false, false);
    }

    void startNucleusWithConfig(String configFile, boolean mockCloud, boolean mockDao) throws InterruptedException {
        startNucleusWithConfig(configFile, State.RUNNING, false, mockCloud, mockDao);
    }

    void startNucleusWithConfig(String configFile, State expectedState, boolean mockDatabase) throws InterruptedException {
        startNucleusWithConfig(configFile, expectedState, mockDatabase, false, true);
    }

    void startNucleusWithConfig(String configFile, State expectedState, boolean mockDatabase, boolean mockCloud,
                                boolean mockDao) throws InterruptedException {
        CountDownLatch shadowManagerRunning = new CountDownLatch(1);
        kernel.parseArgs("-r", rootDir.toAbsolutePath().toString(), "-i",
                getClass().getResource(configFile).toString());
        kernel.getContext().addGlobalStateChangeListener(listener);
        kernel.getContext().put(MqttClient.class, mqttClient);
        // assume we are always connected
        when(mqttClient.connected()).thenReturn(true);

        // set retry config to only try once so we can test failures earlier
        kernel.launch();

        assertTrue(shadowManagerRunning.await(TEST_TIME_OUT_SEC, TimeUnit.SECONDS));
    }
}
