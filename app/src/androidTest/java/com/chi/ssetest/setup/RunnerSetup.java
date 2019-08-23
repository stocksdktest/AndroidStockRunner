package com.chi.ssetest.setup;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.test.InstrumentationRegistry;
import android.util.Log;

import com.chi.ssetest.StockTestcaseName;
import com.chi.ssetest.TestResultCollector;
import com.chi.ssetest.TestResultFileCollector;
import com.chi.ssetest.TestResultLogcatCollector;
import com.chi.ssetest.protos.SetupConfig;
import com.chi.ssetest.setup.Utils;

import com.google.protobuf.InvalidProtocolBufferException;
import com.mitake.core.AppInfo;
import com.mitake.core.bean.log.ErrorInfo;
import com.mitake.core.config.HttpChangeMode;
import com.mitake.core.config.MitakeConfig;
import com.mitake.core.config.SseSdk;
import com.mitake.core.permission.MarketPermission;
import com.mitake.core.request.RegisterRequest;
import com.mitake.core.request.SearchRequest;
import com.mitake.core.response.IResponseInfoCallback;
import com.mitake.core.response.RegisterResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class RunnerSetup {
    public static final String RUNNER_CONFIG_ENV = "runner_config";

    private static RunnerSetup instance = null;
    private SetupConfig.RunnerConfig runnerConfig;
    private Map<StockTestcaseName, SetupConfig.TestcaseConfig> testcaseConfigMap;
    private TestResultCollector collector;

    public static synchronized RunnerSetup getInstance() throws SDKSetupException {
        if (instance == null) {
            Bundle bundle = InstrumentationRegistry.getArguments();
            Log.d("RunnerSetup", "bundle: " + bundle.toString());
            String confStr = bundle.getString(RUNNER_CONFIG_ENV);
            if (confStr == null) {
                throw new SDKSetupException("runner_config is empty");
            }
            try {
                byte[] protoData = Utils.base64Decode(confStr);
                instance = new RunnerSetup(SetupConfig.RunnerConfig.parseFrom(protoData), bundle);
            } catch (InvalidProtocolBufferException e) {
                throw new SDKSetupException(e);
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

    private RunnerSetup(SetupConfig.RunnerConfig cfg, Bundle bundle) throws SDKSetupException {
        Log.d("RunnerSetup", cfg.toString());
        try {
            this.collector = new TestResultLogcatCollector(cfg, bundle);
        } catch (IOException e) {
            throw new SDKSetupException(e);
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

    private void sdkInit(SetupConfig.SDKConfig cfg) throws SDKSetupException {
        AppInfo.packageName = "com.chi.ssetest";
        AppInfo.versionCode = "99";

        SseSdk.setDebug(true);
        MitakeConfig config = new MitakeConfig();
        config.setContext(InstrumentationRegistry.getTargetContext())
                .setAppkey(cfg.getAppKey())
                .setHttpChangeMode(HttpChangeMode.DEFAULT);
        SseSdk.setConfig(config);
        MarketPermission marketPermission = SseSdk.permission();
        marketPermission.setLevel(Utils.toPermissionsStr(cfg.getSdkLevel()))
                .setSseLevel(Utils.toPermissionsStr(cfg.getSdkLevel()));
        for (SetupConfig.SDKPermissions perm : cfg.getHkPermsList()) {
            marketPermission.addHkPermission(Utils.toPermissionsStr(perm));
        }

        final CompletableFuture result = new CompletableFuture<Boolean>();
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.send(new IResponseInfoCallback<RegisterResponse>() {
            public void callback(RegisterResponse response) {
                SearchRequest searchRequest = new SearchRequest();
                searchRequest.dowmLoadCodes(null);
                result.complete(Boolean.TRUE);
            }
            public void exception(ErrorInfo errorInfo) {
                result.completeExceptionally(new Exception(errorInfo.toString()));
            }
        });
        try {
            result.get(5, TimeUnit.SECONDS);
        } catch (Exception e) {
              throw new SDKSetupException(e);
        }
    }

    public static class SDKSetupException extends Exception {
        public SDKSetupException(String msg) {
            super(msg);
        }
        public SDKSetupException(Throwable e) {
            super(e);
        }
    }
}
