package com.chi.ssetest;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.test.InstrumentationRegistry;
import android.util.Log;

import com.chi.ssetest.protos.SetupConfig;
import com.chi.ssetest.protos.TestRecord;
import com.chi.ssetest.setup.Utils;
import com.google.protobuf.ByteString;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.runner.Description;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class TestResultLogcatCollector implements TestResultCollector {
    // logcat line limit is 4096
    public static final int LOGCAT_LINE_LIMIT = 3900;
    private String jobID;
    private String runnerID;
    private Map<StockTestcaseName, Long> testStartTimeMap;

    public TestResultLogcatCollector(SetupConfig.RunnerConfig cfg, Bundle bundle) throws IOException {
        jobID = cfg.getJobID();
        runnerID = cfg.getRunnerID();
        testStartTimeMap = new HashMap<>();
    }

    private void logger(String tag, String recordID, byte[] data) {
        String dataStr = Utils.base64Encode(data);
        String logTag = "TestResult." + tag;
        Log.d("TestResult.", "sum len: " + dataStr.length());
        if (dataStr.length() <= LOGCAT_LINE_LIMIT) {
            Log.w(logTag, dataStr);
            return;
        }
        int dataLen = dataStr.length(), chunkOffset = 0, logChunkIdx = dataLen / LOGCAT_LINE_LIMIT;
        do {
            int endIndex = chunkOffset + LOGCAT_LINE_LIMIT > dataLen ? dataLen : chunkOffset + LOGCAT_LINE_LIMIT;
            String chunkTag = String.format("Chunk.%s.%s:", recordID, logChunkIdx);
            Log.w(logTag, chunkTag + dataStr.substring(chunkOffset, endIndex));
            Log.d("TestResult.", "len: " + dataStr.substring(chunkOffset, endIndex).length());
            chunkOffset += LOGCAT_LINE_LIMIT;
            logChunkIdx -= 1;
        } while (chunkOffset < dataLen);
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
        String recordID = UUID.randomUUID().toString();
        TestRecord.TestExecutionRecord.Builder builder = TestRecord.TestExecutionRecord.newBuilder()
                .setJobID(jobID)
                .setRunnerID(runnerID)
                .setTestcaseID(testcaseName.val())
                .setRecordID(recordID)
                .setIsPass(true)
                .setStartTime(testStartTimeMap.get(testcaseName))
                .setEndTime(System.currentTimeMillis());
        logger("TestExecutionRecord", recordID, builder.build().toByteArray());
    }

    @Override
    public void onTestResult(@NonNull StockTestcaseName testcaseName, JSONObject param, JSONObject result) {
        String recordID = UUID.randomUUID().toString();
        TestRecord.TestExecutionRecord.Builder builder = TestRecord.TestExecutionRecord.newBuilder()
                .setJobID(jobID)
                .setRunnerID(runnerID)
                .setTestcaseID(testcaseName.val())
                .setRecordID(recordID)
                .setIsPass(true)
                .setStartTime(testStartTimeMap.get(testcaseName))
                .setEndTime(System.currentTimeMillis())
                .setParamData(Utils.jsonToBytes(param))
                .setResultData(Utils.jsonToBytes(result));
        logger("TestExecutionRecord", recordID, builder.build().toByteArray());
    }

    @Override
    public void onTestError(@NonNull Failure failure) {
        StockTestcaseName testcaseName = getClassAnnotationValue(failure.getDescription());
        if (testcaseName == null) {
            return;
        }
        String recordID = UUID.randomUUID().toString();
        TestRecord.TestExecutionRecord.Builder builder = TestRecord.TestExecutionRecord.newBuilder()
                .setJobID(jobID)
                .setRunnerID(runnerID)
                .setTestcaseID(testcaseName.val())
                .setRecordID(recordID)
                .setIsPass(false)
                .setStartTime(testStartTimeMap.get(testcaseName))
                .setEndTime(System.currentTimeMillis());
        try {
            JSONObject obj = new JSONObject();
            obj.put("trace", failure.getTrace());
            obj.put("message", failure.getMessage());
            builder.setExceptionData(Utils.jsonToBytes(obj));
        } catch (JSONException e) {
            // pass
        }
        logger("TestExecutionRecord", recordID, builder.build().toByteArray());
    }

    @Override
    public void afterAllTests(Result result) {
    }
}
