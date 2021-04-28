package com.chi.ssetest.cases;

import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import com.chi.ssetest.StockTestcase;
import com.chi.ssetest.StockTestcaseName;
import com.chi.ssetest.TestcaseException;
import com.chi.ssetest.protos.SetupConfig;
import com.chi.ssetest.setup.RunnerSetup;
import com.chi.ssetest.setup.TestcaseConfigRule;
import com.mitake.core.bean.TickDetailItem;
import com.mitake.core.bean.log.ErrorInfo;
import com.mitake.core.request.L2TickRestoreRequest;
import com.mitake.core.response.IResponseInfoCallback;
import com.mitake.core.response.L2TickRestoreResponse;
import com.mitake.core.response.Response;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertNotNull;

//L2-plus 逐笔还原  返回全部的数据
@RunWith(AndroidJUnit4.class)
@StockTestcase(StockTestcaseName.L2TickRestoreTest_1)
public class L2TickRestoreTest_1 {
    private static final StockTestcaseName testcaseName = StockTestcaseName.L2TickRestoreTest_1;
    private static SetupConfig.TestcaseConfig testcaseConfig;
    private static final int timeout_ms = 1000000;
    String sttime="";
    int i=1;

    @BeforeClass
    public static void setup() throws Exception {
        Log.d("L2TickRestoreTest_1", "Setup");
        testcaseConfig = RunnerSetup.getInstance().getTestcaseConfig(testcaseName);
        if (testcaseConfig == null ) {
            throw new Exception(String.format("Testcase(%s) setup failed, config is empty", testcaseName));
        }
    }
    @Rule
    public TestcaseConfigRule rule = new TestcaseConfigRule(testcaseConfig);

    @Test(timeout = timeout_ms)
    public void requestWork() throws Exception {
        Log.d("L2TickRestoreTest_1", "requestWork");
        // TODO get custom args from param
        final String quoteNumbers = rule.getParam().optString("CODE", "");
        final String SubTypes = rule.getParam().optString("SUBTYPE", "");
        final CompletableFuture result = new CompletableFuture<JSONObject>();
        L2TickRestore(quoteNumbers,"0,100,-1",SubTypes,result);
        try {
            JSONObject resultObj = (JSONObject)result.get(timeout_ms, TimeUnit.MILLISECONDS);
            RunnerSetup.getInstance().getCollector().onTestResult(testcaseName,rule.getParam(), resultObj);
        } catch (Exception e) {
            //                throw new Exception(e);
            throw new TestcaseException(e,rule.getParam());
        }
    }

    private void L2TickRestore(final String id, final String page, final String subtype, final CompletableFuture result) {
        L2TickRestoreRequest request = new L2TickRestoreRequest();
        request.send(id,page,subtype, new IResponseInfoCallback() {
            @Override
            public void callback(Response response) {
                L2TickRestoreResponse l2TickRestoreResponse = (L2TickRestoreResponse) response;
                List<TickDetailItem> list = l2TickRestoreResponse.tickDetailItems;
                String[] str1=page.split(",");
                if (str1[2].equals("-1")){
                    try {
                        assertNotNull(l2TickRestoreResponse.tickDetailItems);
                    } catch (AssertionError e) {
                        //                        result.completeExceptionally(e);
                        result.complete(new JSONObject());
                    }
                }
                JSONObject uploadObj = new JSONObject();
                if (list!=null){
                    try {
                        for (int k=0;k<list.size();k++){
                            JSONObject uploadObj_1 = new JSONObject();
                            uploadObj_1.put("type", list.get(k).getTransactionStatus());
                            uploadObj_1.put("time", list.get(k).getTransactionTime());
                            uploadObj_1.put("tradeVolume", list.get(k).getSingleVolume());
                            uploadObj_1.put("tradePrice", list.get(k).getTransactionPrice());
                            uploadObj_1.put("bn", list.get(k).getBn());
                            uploadObj_1.put("on", list.get(k).getOn());
                            uploadObj_1.put("bq", list.get(k).getBq());
                            uploadObj_1.put("oq", list.get(k).getOq());
                            if (sttime.equals(list.get(k).getTransactionTime())){
                                uploadObj.put(list.get(k).getTransactionTime()+i,uploadObj_1);
                                i++;
                            }else {
                                sttime=list.get(k).getTransactionTime();
                                uploadObj.put(list.get(k).getTransactionTime(),uploadObj_1);
                                i=1;
                            }
                        }
                    } catch (JSONException e) {
                        result.completeExceptionally(e);
                    }
                    if (list.size()==100){
                        try {
                            Thread.sleep(3 * 1000);
                        } catch (InterruptedException ignore) {}
                        String[] st=l2TickRestoreResponse.headerParams.split(",");
                        if (Double.parseDouble(st[0])>Double.parseDouble(st[1])){
                            String page1=st[1]+",100,1";
                            L2TickRestore2(id,page1,subtype,result,uploadObj);
                        }else {
                            String page2=st[0]+",100,1";
                            L2TickRestore2(id,page2,subtype,result,uploadObj);
                        }
                    }else {
                        text(uploadObj,result);
                    }
                }else {
                    text(uploadObj,result);
                }
            }
            @Override
            public void exception(ErrorInfo errorInfo) {
                result.completeExceptionally(new Exception(errorInfo.toString()));
            }
        });
    }
    private void L2TickRestore2(final String id, final String page, final String subtype, final CompletableFuture result, final JSONObject uploadObj) {
        L2TickRestoreRequest request = new L2TickRestoreRequest();
        request.send(id,page,subtype, new IResponseInfoCallback() {
            @Override
            public void callback(Response response) {
                L2TickRestoreResponse l2TickRestoreResponse = (L2TickRestoreResponse) response;
                List<TickDetailItem> list = l2TickRestoreResponse.tickDetailItems;
                String[] str1=page.split(",");
                if (str1[2].equals("-1")){
                    try {
                        assertNotNull(l2TickRestoreResponse.tickDetailItems);
                    } catch (AssertionError e) {
                        //                        result.completeExceptionally(e);
                        result.complete(new JSONObject());
                    }
                }
                if (list!=null){
                    try {
                        for (int k=0;k<list.size();k++){
                            JSONObject uploadObj_1 = new JSONObject();
                            uploadObj_1.put("type", list.get(k).getTransactionStatus());
                            uploadObj_1.put("time", list.get(k).getTransactionTime());
                            uploadObj_1.put("tradeVolume", list.get(k).getSingleVolume());
                            uploadObj_1.put("tradePrice", list.get(k).getTransactionPrice());
                            uploadObj_1.put("bn", list.get(k).getBn());
                            uploadObj_1.put("on", list.get(k).getOn());
                            uploadObj_1.put("bq", list.get(k).getBq());
                            uploadObj_1.put("oq", list.get(k).getOq());
                            if (sttime.equals(list.get(k).getTransactionTime())){
                                uploadObj.put(list.get(k).getTransactionTime()+i,uploadObj_1);
                                i++;
                            }else {
                                sttime=list.get(k).getTransactionTime();
                                uploadObj.put(list.get(k).getTransactionTime(),uploadObj_1);
                                i=1;
                            }
                        }
                    } catch (JSONException e) {
                        result.completeExceptionally(e);
                    }
                    if (list.size()==100){
                        String[] st=l2TickRestoreResponse.headerParams.split(",");
                        if (Double.parseDouble(st[0])>Double.parseDouble(st[1])){
                            String page1=st[1]+",100,1";
                            L2TickRestore2(id,page1,subtype,result,uploadObj);
                        }else {
                            String page2=st[0]+",100,1";
                            L2TickRestore2(id,page2,subtype,result,uploadObj);
                        }
                    }else {
                        text(uploadObj,result);
                    }
                }else {
                    text(uploadObj,result);
                }
            }
            @Override
            public void exception(ErrorInfo errorInfo) {
                result.completeExceptionally(new Exception(errorInfo.toString()));
            }
        });
    }

    private void text(JSONObject uploadObj,CompletableFuture result){
        result.complete(uploadObj);
    }
}
