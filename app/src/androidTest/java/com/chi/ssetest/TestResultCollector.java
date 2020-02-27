package com.chi.ssetest;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.json.JSONObject;
import org.junit.runner.Description;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

public interface TestResultCollector {
    void beforeAllTests();
    void onTestStart(@NonNull Description description);
    void onTestSuccess(@NonNull Description description);
    void onTestResult(@NonNull StockTestcaseName testcaseName, JSONObject param, JSONObject result);
    void onTestError(@NonNull Failure failure, @Nullable JSONObject param);
    void afterAllTests(Result result);
}

