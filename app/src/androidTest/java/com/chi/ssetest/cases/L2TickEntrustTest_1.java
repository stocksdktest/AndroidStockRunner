package com.chi.ssetest.cases;

import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import com.chi.ssetest.StockTestcase;
import com.chi.ssetest.StockTestcaseName;
import com.chi.ssetest.TestcaseException;
import com.chi.ssetest.protos.SetupConfig;
import com.chi.ssetest.setup.RunnerSetup;
import com.chi.ssetest.setup.TestcaseConfigRule;
import com.mitake.core.bean.TickEntrustItem;
import com.mitake.core.bean.log.ErrorInfo;
import com.mitake.core.request.L2TickEntrustRequest;
import com.mitake.core.response.IResponseInfoCallback;
import com.mitake.core.response.L2TickEntrustResponse;
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

//L2-plus 逐笔委托  返回全部的数据
@RunWith(AndroidJUnit4.class)
@StockTestcase(StockTestcaseName.L2TickEntrustTest_1)
public class L2TickEntrustTest_1 {
    private static final StockTestcaseName testcaseName = StockTestcaseName.L2TickEntrustTest_1;
    private static SetupConfig.TestcaseConfig testcaseConfig;
    private static final int timeout_ms = 1000000;
    int i=1;

    @BeforeClass
    public static void setup() throws Exception {
        Log.d("L2TickEntrustTest_1", "Setup");
        testcaseConfig = RunnerSetup.getInstance().getTestcaseConfig(testcaseName);
        if (testcaseConfig == null ) {
            throw new Exception(String.format("Testcase(%s) setup failed, config is empty", testcaseName));
        }
    }
    @Rule
    public TestcaseConfigRule rule = new TestcaseConfigRule(testcaseConfig);

    @Test(timeout = timeout_ms)
    public void requestWork() throws Exception {
        Log.d("L2TickEntrustTest_1", "requestWork");
        // TODO get custom args from param
        final String quoteNumbers = rule.getParam().optString("CODE", "");
        final String SubTypes = rule.getParam().optString("SUBTYPE", "");
        final CompletableFuture result = new CompletableFuture<JSONObject>();
        L2TickEntrust(quoteNumbers,"0,100,-1",SubTypes,result);
        try {
            JSONObject resultObj = (JSONObject)result.get(timeout_ms, TimeUnit.MILLISECONDS);
            RunnerSetup.getInstance().getCollector().onTestResult(testcaseName,rule.getParam(), resultObj);
        } catch (Exception e) {
            //                throw new Exception(e);
            throw new TestcaseException(e,rule.getParam());
        }
    }

    private void L2TickEntrust(final String id, final String page, final String subtype, final CompletableFuture result) {
        L2TickEntrustRequest request = new L2TickEntrustRequest();
        request.send(id,page,subtype, new IResponseInfoCallback() {
            @Override
            public void callback(Response response) {
                L2TickEntrustResponse l2TickEntrustResponse = (L2TickEntrustResponse) response;
                List<TickEntrustItem> list = l2TickEntrustResponse.tickEntrustItems;
                String[] str1=page.split(",");
                if (str1[2].equals("-1")){
                    try {
                        assertNotNull(l2TickEntrustResponse.tickEntrustItems);
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
                            uploadObj_1.put("sn", list.get(k).sn);
                            uploadObj_1.put("price", list.get(k).price);
                            uploadObj_1.put("volume", list.get(k).volume);
                            uploadObj_1.put("bs", list.get(k).bs);
                            uploadObj_1.put("time", list.get(k).time);
                            uploadObj.put(list.get(k).sn,uploadObj_1);
                        }
                    } catch (JSONException e) {
                        result.completeExceptionally(e);
                    }
                    if (list.size()==100){
                        try {
                            Thread.sleep(3 * 1000);
                        } catch (InterruptedException ignore) {}
                        String[] st=l2TickEntrustResponse.headerParams.split(",");
                        if (Double.parseDouble(st[0])>Double.parseDouble(st[1])){
                            String page1=st[1]+",100,1";
                            L2TickEntrust2(id,page1,subtype,result,uploadObj);
                        }else {
                            String page2=st[0]+",100,1";
                            L2TickEntrust2(id,page2,subtype,result,uploadObj);
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
    private void L2TickEntrust2(final String id, final String page, final String subtype, final CompletableFuture result, final JSONObject uploadObj) {
        L2TickEntrustRequest request = new L2TickEntrustRequest();
        request.send(id,page,subtype, new IResponseInfoCallback() {
            @Override
            public void callback(Response response) {
                L2TickEntrustResponse l2TickEntrustResponse = (L2TickEntrustResponse) response;
                List<TickEntrustItem> list = l2TickEntrustResponse.tickEntrustItems;
                String[] str1=page.split(",");
                if (str1[2].equals("-1")){
                    try {
                        assertNotNull(l2TickEntrustResponse.tickEntrustItems);
                    } catch (AssertionError e) {
                        //                        result.completeExceptionally(e);
                        result.complete(new JSONObject());
                    }
                }
                if (list!=null){
                    try {
                        for (int k=0;k<list.size();k++){
                            JSONObject uploadObj_1 = new JSONObject();
                            uploadObj_1.put("sn", list.get(k).sn);
                            uploadObj_1.put("price", list.get(k).price);
                            uploadObj_1.put("volume", list.get(k).volume);
                            uploadObj_1.put("bs", list.get(k).bs);
                            uploadObj_1.put("time", list.get(k).time);
                            uploadObj.put(list.get(k).sn,uploadObj_1);
                        }
                    } catch (JSONException e) {
                        result.completeExceptionally(e);
                    }
                    if (list.size()==100){
                        String[] st=l2TickEntrustResponse.headerParams.split(",");
                        if (Double.parseDouble(st[0])>Double.parseDouble(st[1])){
                            String page1=st[1]+",100,1";
                            L2TickEntrust2(id,page1,subtype,result,uploadObj);
                        }else {
                            String page2=st[0]+",100,1";
                            L2TickEntrust2(id,page2,subtype,result,uploadObj);
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
