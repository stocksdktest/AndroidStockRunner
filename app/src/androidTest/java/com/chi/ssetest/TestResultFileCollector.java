package com.chi.ssetest;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.test.InstrumentationRegistry;
import android.util.Log;

import com.chi.ssetest.protos.SetupConfig;
import com.chi.ssetest.protos.TestRecord;
import com.chi.ssetest.setup.Utils;

import org.json.JSONObject;
import org.junit.runner.Description;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class TestResultFileCollector implements TestResultCollector {
    private String jobID;
    private String runnerID;
    private Map<StockTestcaseName, Long> testStartTimeMap;
    private Logger fileLogger;

    public TestResultFileCollector(SetupConfig.RunnerConfig cfg, Bundle bundle) throws IOException {
        jobID = cfg.getJobID();
        runnerID = cfg.getRunnerID();
        testStartTimeMap = new HashMap<>();
        String filename = bundle.getString("collector_file");
        if (filename == null) {
            throw new IOException("collector_file is null");
        }
        String externalDir = InstrumentationRegistry.getTargetContext().getExternalFilesDir(null).getPath();
        String filepath = externalDir + File.separator + filename;
        File file = new File(filepath);
        if (!file.exists()) {
            file.createNewFile();
        }
        FileHandler handler = new FileHandler(filepath, false);
        handler.setFormatter(new Formatter() {
            @Override
            public String format(LogRecord record) {
                return record.getMessage() + '\n';
            }
        });
        fileLogger = Logger.getAnonymousLogger();
        fileLogger.addHandler(handler);
        fileLogger.setLevel(Level.ALL);
    }

    private StockTestcaseName getClassAnnotationValue(@NonNull Description description) {
        Class<?> parentCls = description.getTestClass();
        if (parentCls == null) {
            return null;
        }
        StockTestcase annotation = parentCls.getAnnotation(StockTestcase.class);
        if (annotation == null) {
            return null;
        }
        return annotation.value();
    }

    @Override
    public void beforeAllTests() {

    }

    @Override
    public void onTestStart(@NonNull Description description) {
        StockTestcaseName testcaseName = getClassAnnotationValue(description);
        if (testcaseName != null) {
            testStartTimeMap.put(testcaseName, System.currentTimeMillis());
        }
    }

    @Override
    public void onTestSuccess(@NonNull Description description) {
        StockTestcaseName testcaseName = getClassAnnotationValue(description);
        if (testcaseName == null) {
            return;
        }
        TestRecord.TestExecutionRecord.Builder builder = TestRecord.TestExecutionRecord.newBuilder()
                .setJobID(jobID)
                .setRunnerID(runnerID)
                .setTestcaseID(testcaseName.val())
                .setRecordID(UUID.randomUUID().toString())
                .setIsPass(true)
                .setStartTime(testStartTimeMap.get(testcaseName))
                .setEndTime(System.currentTimeMillis());
        fileLogger.log(Level.ALL, Utils.base64Encode(builder.build().toByteArray()));
    }

    @Override
    public void onTestResult(@NonNull StockTestcaseName testcaseName, JSONObject param, JSONObject result) {
        TestRecord.TestExecutionRecord.Builder builder = TestRecord.TestExecutionRecord.newBuilder()
                .setJobID(jobID)
                .setRunnerID(runnerID)
                .setTestcaseID(testcaseName.val())
                .setRecordID(UUID.randomUUID().toString())
                .setIsPass(true)
                .setStartTime(testStartTimeMap.get(testcaseName))
                .setEndTime(System.currentTimeMillis());
        fileLogger.log(Level.ALL, Utils.base64Encode(builder.build().toByteArray()));
    }

    @Override
    public void onTestError(@NonNull Failure failure, @Nullable JSONObject param) {
        StockTestcaseName testcaseName = getClassAnnotationValue(failure.getDescription());
        if (testcaseName == null) {
            return;
        }
        TestRecord.TestExecutionRecord.Builder builder = TestRecord.TestExecutionRecord.newBuilder()
                .setJobID(jobID)
                .setRunnerID(runnerID)
                .setTestcaseID(testcaseName.val())
                .setRecordID(UUID.randomUUID().toString())
                .setIsPass(false)
                .setStartTime(testStartTimeMap.get(testcaseName))
                .setEndTime(System.currentTimeMillis());
        if (param != null) {
            builder.setParamData(Utils.jsonToBytes(param));
        }
        if (failure.getException() != null) {
            StringWriter errors = new StringWriter();
            failure.getException().printStackTrace(new PrintWriter(errors));
        }
        fileLogger.log(Level.ALL, Utils.base64Encode(builder.build().toByteArray()));
    }

    @Override
    public void afterAllTests(Result result) {
    }
}
