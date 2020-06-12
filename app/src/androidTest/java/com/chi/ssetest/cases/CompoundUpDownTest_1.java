package com.chi.ssetest.cases;

import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import com.chi.ssetest.TestcaseException;
import com.chi.ssetest.protos.SetupConfig;
import com.chi.ssetest.setup.RunnerSetup;
import com.chi.ssetest.StockTestcase;
import com.chi.ssetest.StockTestcaseName;
import com.chi.ssetest.setup.TestcaseConfigRule;
import com.mitake.core.bean.compound.CompoundUpDownBean;
import com.mitake.core.bean.log.ErrorInfo;
import com.mitake.core.request.compound.CompoundUpDownRequest;
import com.mitake.core.response.IResponseInfoCallback;
import com.mitake.core.response.compound.CompoundUpDownResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *涨跌分布请求接口
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
@StockTestcase(StockTestcaseName.COMPOUNDUPDOWNTEST_1)
public class CompoundUpDownTest_1 {
    private static final StockTestcaseName testcaseName = StockTestcaseName.COMPOUNDUPDOWNTEST_1;
    private static SetupConfig.TestcaseConfig testcaseConfig;
    private static final int timeout_ms = 1000000;
    @BeforeClass
    public static void setup() throws Exception {
        Log.d(" MorePriceSampleTest1", "Setup");
        testcaseConfig = RunnerSetup.getInstance().getTestcaseConfig(testcaseName);
        if (testcaseConfig == null ) {
            throw new Exception(String.format("Testcase(%s) setup failed, config is empty", testcaseName));
        }
    }
//    CompoundUpDownRequest.DateType
    @Rule
    public TestcaseConfigRule rule = new TestcaseConfigRule(testcaseConfig);
    @Test(timeout = timeout_ms)
    public void requestWork() throws Exception {
        Log.d(" MorePriceSampleTest1", "requestWork");
        // TODO get custom args from param
        final String quoteNumbers = rule.getParam().optString("MARKET");
        final String quoteNumbers1 = rule.getParam().optString("TIME");
        final String quoteNumbers2 = rule.getParam().optString("TYPE");
        final CompletableFuture result = new CompletableFuture<JSONObject>();
//        CompoundUpDownRequest.DateType  Integer.parseInt()
        //        for (int i=0;i<quoteNumbers.length;i++){
        String time;
        if (quoteNumbers1.equals("null")){
            time=null;
        }else {
            time=quoteNumbers1;
        }
            CompoundUpDownRequest request = new CompoundUpDownRequest();
            request.send(quoteNumbers,time, Integer.parseInt(quoteNumbers2),new IResponseInfoCallback<CompoundUpDownResponse>() {
                @Override
                public void callback(CompoundUpDownResponse compoundUpDownResponse) {
                    try {
                        assertNotNull(compoundUpDownResponse.compoundUpDownList);
                    } catch (AssertionError e) {
                        //                        result.completeExceptionally(e);
                        result.complete(new JSONObject());
                    }
                    List<CompoundUpDownBean> list=compoundUpDownResponse.compoundUpDownList;
                    JSONObject uploadObj = new JSONObject();
                    try {
                        if(list!=null){
                            for (int i=0;i<list.size();i++){
                                JSONObject uploadObj_1 = new JSONObject();
                                uploadObj_1.put("dateTime",list.get(i).dateTime);
                                uploadObj_1.put("riseCount",list.get(i).riseCount);
                                uploadObj_1.put("fallCount",list.get(i).fallCount);
                                uploadObj_1.put("flatCount",list.get(i).flatCount);
                                uploadObj_1.put("stopCount",list.get(i).stopCount);
                                uploadObj_1.put("riseLimitCount",list.get(i).riseLimitCount);
                                uploadObj_1.put("fallLimitCount",list.get(i).fallLimitCount);

                                List<String> riseFallRange=new ArrayList<>();
                                for (int k=0;k<list.get(i).riseFallRange.length;k++){
                                    riseFallRange.add(list.get(i).riseFallRange[k]);
                                }
                                uploadObj_1.put("riseFallRange",new JSONArray(riseFallRange));

                                uploadObj_1.put("oneRiseLimitCount",list.get(i).oneRiseLimitCount);
                                uploadObj_1.put("natureRiseLimitCount",list.get(i).natureRiseLimitCount);
//                            Log.d("data", String.valueOf(uploadObj_1));
                                uploadObj.put(list.get(i).dateTime,uploadObj_1);
                            }
                        }
//                        Log.d("data", String.valueOf(uploadObj));
                        result.complete(uploadObj);
                    } catch (JSONException e) {
                        result.completeExceptionally(e);
                    }
                }
                @Override
                public void exception(ErrorInfo errorInfo) {
                    result.completeExceptionally(new Exception(errorInfo.toString()));
                }
            });
            try {
                JSONObject resultObj = (JSONObject)result.get(timeout_ms, TimeUnit.MILLISECONDS);
                RunnerSetup.getInstance().getCollector().onTestResult(testcaseName, rule.getParam(), resultObj);
            } catch (Exception e) {
                //                throw new Exception(e);
                throw new TestcaseException(e,rule.getParam());
            }
//        }
    }
}