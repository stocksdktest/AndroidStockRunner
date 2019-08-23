package com.chi.ssetest;

import android.os.Bundle;

import com.chi.ssetest.protos.SetupConfig;
import com.chi.ssetest.setup.RunnerSetup;
import com.chi.ssetest.setup.Utils;

import org.junit.runner.Description;
import org.junit.runner.manipulation.Filter;

import java.util.HashSet;
import java.util.Set;

public class TestcaseFilter extends Filter {
    private Set<StockTestcaseName> allowExecutionNames;
    public TestcaseFilter(Bundle bundle) throws Exception {
        String confStr = bundle.getString(RunnerSetup.RUNNER_CONFIG_ENV);
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
