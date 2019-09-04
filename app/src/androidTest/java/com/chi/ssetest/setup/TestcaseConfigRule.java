package com.chi.ssetest.setup;

import android.util.Log;

import com.chi.ssetest.protos.SetupConfig;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.rules.ErrorCollector;
import org.junit.rules.MethodRule;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;

import java.util.ArrayList;
import java.util.List;

public class TestcaseConfigRule implements MethodRule {
    private SetupConfig.TestcaseConfig config;
    private final List<JSONObject> params;
    private long waitIntervalMillis;
    private int curRound;
    public TestcaseConfigRule(SetupConfig.TestcaseConfig config) throws AssertionError {
        this.config = config;
        this.curRound = 0;
        this.waitIntervalMillis = config.getRoundIntervalSec() * 1000;
        this.params = new ArrayList<>();
        for (int i = 0, n = config.getParamStrsCount(); i < n; i++) {
            try {
                params.add(new JSONObject(config.getParamStrs(i)));
            } catch (JSONException e) {
                throw new AssertionError(e);
            }
        }
    }

    public JSONObject getParam() {
        return params.get(curRound);
    }

    @Override
    public Statement apply(final Statement base, FrameworkMethod method, Object target) {
        if (config.getContinueWhenFailed()) {
            return new Statement() {
                private MultipleErrorCollector errorCollector = new MultipleErrorCollector();
                @Override
                public void evaluate() throws Throwable {
                    while (curRound < params.size()) {
                        try {
                            base.evaluate();
                        } catch (Throwable e) {
                            errorCollector.addError(e);
                        }
                        curRound++;
                        Thread.sleep(waitIntervalMillis);
                    }
                    errorCollector.verify();
                }
            };
        } else {
            return new Statement() {
                @Override
                public void evaluate() throws Throwable {
                    while (curRound < params.size()) {
                        base.evaluate();
                        curRound++;
                        Thread.sleep(waitIntervalMillis);
                    }
                }
            };
        }
    }

    private static class MultipleErrorCollector extends ErrorCollector {
        @Override
        public void verify() throws Throwable {
            super.verify();
        }
    }
}
