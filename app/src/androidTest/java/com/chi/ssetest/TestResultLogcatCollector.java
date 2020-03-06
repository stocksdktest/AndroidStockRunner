package com.chi.ssetest;

import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import com.google.protobuf.InvalidProtocolBufferException;
import com.mongodb.MongoSocketOpenException;
import com.mongodb.client.*;

import org.bson.Document;

public class TestResultLogcatCollector implements TestResultCollector {
    // logcat line limit is 4096
    public static final int LOGCAT_LINE_LIMIT = 3900;
    private String jobID;
    private String runnerID;
    private Map<StockTestcaseName, Long> testStartTimeMap;
    // TODO: collection name could get from params
    private String mongoUri;
    private String dbName;
    private String collectionName;
    private MongoClient mongoClient;
    private MongoDatabase database;
    private MongoCollection<Document> collection;

    public TestResultLogcatCollector(SetupConfig.RunnerConfig cfg, Bundle bundle) throws IOException {
        jobID = cfg.getJobID();
        runnerID = cfg.getRunnerID();
        testStartTimeMap = new HashMap<>();
        // StoreConfig
        mongoUri = cfg.getStoreConfig().getMongoUri();
        dbName = cfg.getStoreConfig().getDbName();
        collectionName = cfg.getStoreConfig().getCollectionName();
        mongoClient = MongoClients.create(mongoUri);
        database = mongoClient.getDatabase(dbName);
        Log.d("mongo", database.getName());
        collection = database.getCollection(collectionName);
    }

    // to keep safety, mongodb will throw exception when the key in bson contains '.' or '$'
    private JSONObject fixDotDollarKey(JSONObject obj) throws JSONException {
        //.replace(".","_").replace("$","_"
        Iterator<String> it = obj.keys();
        Set<String> keys = new HashSet<String>();
        while (it.hasNext()) {
            String key = it.next();
            keys.add(key);
        }

        for (String key : keys) {
            if (key.contains(".") || key.contains("$")) {
                String oldKey = key;
                key = key.replace(".", "_").replace("$", "_");
                if (obj.get(oldKey) instanceof JSONObject) {
                    obj.put(key, fixDotDollarKey((JSONObject) obj.get(oldKey)));
                    obj.remove(oldKey);
                } else {
                    obj.put(key, obj.get(oldKey));
                    obj.remove(oldKey);
                }
            }
        }
        return obj;
    }

    // to keep safety, mongodb will throw exception when the key in bson contains '.' or '$', so replace them
    private Document formatBSON(JSONObject obj) {
        Document document = null;
        try {
            document = Document.parse(fixDotDollarKey(obj).toString());
        } catch (JSONException e) {
            document = Document.parse(obj.toString().replace(".", "_").replace("$", "_"));
        }
        return document;
    }

    private void mongoWriter(String mongoUri, String dbName, String collectionName, Document document) {
        Log.d("mongo", document.toString());
        collection.insertOne(document);
    }

    @Deprecated
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

        //测试成功
        TestRecord.TestExecutionRecord record = builder.build();
        Document document = new Document();
        document.append("jobID", record.getJobID());
        document.append("runnerID", record.getRunnerID());
        document.append("testcaseID", record.getTestcaseID());
        document.append("recordID", record.getRecordID());
        document.append("isPass", record.getIsPass());
        document.append("startTime", record.getStartTime());
        document.append("endTime", record.getEndTime());
        document.append("paramData", null);
        document.append("resultData", null);
        document.append("exceptionData", null);

        try {
            mongoWriter(mongoUri, dbName, collectionName, document);
        } catch (MongoSocketOpenException e) {
            Log.d("mongo", e.getMessage());
        }
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

        //测试结果
        TestRecord.TestExecutionRecord record = builder.build();
        Document document = new Document();
        document.append("jobID", record.getJobID());
        document.append("runnerID", record.getRunnerID());
        document.append("testcaseID", record.getTestcaseID());
        document.append("recordID", record.getRecordID());
        document.append("isPass", record.getIsPass());
        document.append("startTime", record.getStartTime());
        document.append("endTime", record.getEndTime());
        document.append("paramData", formatBSON(param));
        document.append("resultData", formatBSON(result));
        document.append("exceptionData", null);

        try {
            mongoWriter(mongoUri, dbName, collectionName, document);
        } catch (MongoSocketOpenException e) {
            Log.d("mongo", e.getMessage());
        }
    }

    @Override
    public void onTestError(@NonNull Failure failure, @Nullable JSONObject param) {
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
        if (param != null) {
            builder.setParamData(Utils.jsonToBytes(param));
        }

        //测试结果
        TestRecord.TestExecutionRecord record = builder.build();
        Document document = new Document();
        document.append("jobID", record.getJobID());
        document.append("runnerID", record.getRunnerID());
        document.append("testcaseID", record.getTestcaseID());
        document.append("recordID", record.getRecordID());
        document.append("isPass", record.getIsPass());
        document.append("startTime", record.getStartTime());
        document.append("endTime", record.getEndTime());
        document.append("paramData", formatBSON(param));
        document.append("resultData", null);
        document.append("exceptionData", null);

        try {
            JSONObject obj = new JSONObject();
            obj.put("trace", failure.getTrace());
            obj.put("message", failure.getMessage());
            builder.setExceptionData(Utils.jsonToBytes(obj));
            document.append("exceptionData", formatBSON(obj));
        } catch (JSONException e) {
            // pass
        }
        logger("TestExecutionRecord", recordID, builder.build().toByteArray());

        try {
            mongoWriter(mongoUri, dbName, collectionName, document);
        } catch (MongoSocketOpenException e) {
            Log.d("mongo", e.getMessage());
        }
    }

    @Override
    public void afterAllTests(Result result) {
        mongoClient.close();
    }
}
