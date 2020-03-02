package com.chi.ssetest.setup;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.test.InstrumentationRegistry;
import android.util.Log;

import com.chi.ssetest.StockTestcaseName;
import com.chi.ssetest.TestResultCollector;
import com.chi.ssetest.TestResultLogcatCollector;
import com.chi.ssetest.protos.SetupConfig;

import com.google.protobuf.InvalidProtocolBufferException;
import com.mitake.core.AppInfo;
import com.mitake.core.config.SseSdk;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class RunnerSetup {
    public static final String RUNNER_CONFIG_PATH = "runner_config";

    private static RunnerSetup instance = null;
    private SetupConfig.RunnerConfig runnerConfig;
    private Map<StockTestcaseName, SetupConfig.TestcaseConfig> testcaseConfigMap;
    private TestResultCollector collector;

    public static synchronized RunnerSetup getInstance() throws RunnerSetupException {
        if (instance == null) {
            Bundle bundle = InstrumentationRegistry.getArguments();
            Log.d("RunnerSetup", "bundle: " + bundle.toString());
            String runnerConfigPath = bundle.getString(RUNNER_CONFIG_PATH);
            Log.d("runnerConfigPath", "runnerConfigPath: " + runnerConfigPath);
            if (runnerConfigPath == null) {
                throw new RunnerSetupException("runner_config_file is null");
            }
            File file = new File(runnerConfigPath);
            FileInputStream is = null;
            StringBuilder stringBuilder = null;
            try {
                if (file.length() != 0) {
                    is = new FileInputStream(file);
                    InputStreamReader streamReader = new InputStreamReader(is);
                    BufferedReader reader = new BufferedReader(streamReader);
                    String line;
                    stringBuilder = new StringBuilder();
                    while ((line = reader.readLine()) != null) {
                        stringBuilder.append(line);
                    }
                    reader.close();
                    is.close();
                } else {
                    throw new RunnerSetupException("runner_config_file is empty");
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            String confStr = String.valueOf(stringBuilder);
            Log.d("RunnerConfig", "RunnerConfig: " + confStr);

            if (confStr == null) {
                throw new RunnerSetupException("runner_config is empty");
            }
            try {
                byte[] protoData = Utils.base64Decode(confStr);
                instance = new RunnerSetup(SetupConfig.RunnerConfig.parseFrom(protoData), bundle);
            } catch (InvalidProtocolBufferException e) {
                throw new RunnerSetupException(e);
            }
        }
        return instance;
    }

    public SetupConfig.TestcaseConfig getTestcaseConfig(@NonNull StockTestcaseName name) {
        return testcaseConfigMap.get(name);
    }

    public @Nullable TestResultCollector getCollector() {
        return collector;
    }

    public SetupConfig.RunnerConfig getRunnerConfig() {
        return runnerConfig;
    }

    private RunnerSetup(SetupConfig.RunnerConfig cfg, Bundle bundle) throws RunnerSetupException {
        Log.d("RunnerSetup", cfg.toString());
        try {
            this.collector = new TestResultLogcatCollector(cfg, bundle);
        } catch (IOException e) {
            throw new RunnerSetupException(e);
        }

        sdkInit(cfg.getSdkConfig());

        testcaseConfigMap = new HashMap<>();
        for (SetupConfig.TestcaseConfig testcaseConfig : cfg.getCasesConfigList()) {
            StockTestcaseName enumName = StockTestcaseName.fromString(testcaseConfig.getTestcaseID());
            if (enumName != null) {
                testcaseConfigMap.put(enumName, testcaseConfig);
            }
        }
    }

    private void sdkInit(SetupConfig.SDKConfig cfg) throws RunnerSetupException {
        AppInfo.packageName = "com.chi.ssetest";
        AppInfo.versionCode = "99";
        SseSdk.setDebug(true);

        try {
            SDKSetup.setup(cfg, InstrumentationRegistry.getTargetContext());
        } catch (SDKSetup.SDKSetupException e) {
            throw new RunnerSetupException(e);
        }
    }

    public static class RunnerSetupException extends Exception {
        public RunnerSetupException(String msg) {
            super(msg);
        }
        public RunnerSetupException(Throwable e) {
            super(e);
        }
    }
}
