package com.chi.ssetest.setup;

import android.util.Log;

import com.chi.ssetest.protos.SetupConfig;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.rules.ErrorCollector;
import org.junit.rules.MethodRule;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;

public class TestcaseConfigRule implements MethodRule {
    private SetupConfig.TestcaseConfig config;
    private JSONObject param;
    public TestcaseConfigRule(SetupConfig.TestcaseConfig config) throws AssertionError {
        this.config = config;
        if (config.getParamStr() != null) {
            try {
                this.param = new JSONObject(config.getParamStr());
                Log.d("RunnerSetup", this.param.toString());
            } catch (JSONException e) {
                throw new AssertionError(e);
            }
        }
    }

    public JSONObject getParam() {
        return param;
    }

    @Override
    public Statement apply(final Statement base, FrameworkMethod method, Object target) {
        if (config.getContinueWhenFailed()) {
            return new Statement() {
                private MultipleErrorCollector errorCollector = new MultipleErrorCollector();
                @Override
                public void evaluate() throws Throwable {
                    for (int i = 0; i < config.getExecutionTimes(); i++) {
                        try {
                            base.evaluate();
                        } catch (Throwable e) {
                            errorCollector.addError(e);
                        }
                    }
                    errorCollector.verify();
                }
            };
        } else {
            return new Statement() {
                @Override
                public void evaluate() throws Throwable {
                    for (int i = 0; i < config.getExecutionTimes(); i++) {
                        base.evaluate();
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
