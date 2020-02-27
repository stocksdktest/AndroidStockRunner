package com.chi.ssetest;

import org.json.JSONObject;

public class TestcaseException extends Exception {
    public Throwable cause;
    public JSONObject param;
    public TestcaseException(Throwable cause, JSONObject param) {
        this.cause = cause;
        this.param = param;
    }
}
