package com.chi.ssetest;

import android.os.Bundle;
import android.util.Log;

import com.chi.ssetest.protos.SetupConfig;
import com.chi.ssetest.setup.RunnerSetup;
import com.chi.ssetest.setup.Utils;

import org.junit.runner.Description;
import org.junit.runner.Runner;
import org.junit.runner.manipulation.Filter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;

public class TestcaseFilter extends Filter {
    private Set<StockTestcaseName> allowExecutionNames;
    public TestcaseFilter(Bundle bundle) throws Exception {
        Log.d("RunnerSetup", "bundle: " + bundle.toString());
        String runnerConfigPath = bundle.getString(RunnerSetup.RUNNER_CONFIG_PATH);
        Log.d("runnerConfigPath", "runnerConfigPath: " + runnerConfigPath);
        if (runnerConfigPath == null) {
            throw new RunnerSetup.RunnerSetupException("runner_config_file is null");
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
                throw new RunnerSetup.RunnerSetupException("runner_config_file is empty");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        String confStr = String.valueOf(stringBuilder);
        Log.d("RunnerConfig", "RunnerConfig: " + confStr);
        if (confStr == null) {
            throw new Exception("runner config is empty");
        }
        byte[] protoData = Utils.base64Decode(confStr);
        SetupConfig.RunnerConfig cfg = SetupConfig.RunnerConfig.parseFrom(protoData);
        allowExecutionNames = new HashSet<>();
        for (SetupConfig.TestcaseConfig testcaseConfig : cfg.getCasesConfigList()) {
            StockTestcaseName enumName = StockTestcaseName.fromString(testcaseConfig.getTestcaseID());
            if (enumName != null) {
                allowExecutionNames.add(enumName);
            }
        }
    }

    @Override
    public boolean shouldRun(Description description) {
        StockTestcaseName testcaseName = Utils.getTestcaseNameFromDesc(description);
        if (testcaseName == null) {
            return false;
        }
        return allowExecutionNames.contains(testcaseName);
    }

    @Override
    public String describe() {
        return null;
    }
}
