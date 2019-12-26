package com.chi.ssetest.cases;

import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import com.chi.ssetest.protos.SetupConfig;
import com.chi.ssetest.setup.RunnerSetup;
import com.chi.ssetest.StockTestcase;
import com.chi.ssetest.StockTestcaseName;
import com.chi.ssetest.setup.TestcaseConfigRule;
import com.mitake.core.AddValueModel;
import com.mitake.core.QuoteItem;
import com.mitake.core.TradeDate;
import com.mitake.core.bean.log.ErrorInfo;
import com.mitake.core.model.TradeDateModel;
import com.mitake.core.request.AddValueRequest;
import com.mitake.core.request.QuoteRequest;
import com.mitake.core.request.TradeDateRequest;
import com.mitake.core.response.AddValueResponse;
import com.mitake.core.response.IResponseInfoCallback;
import com.mitake.core.response.QuoteResponse;
import com.mitake.core.response.Response;
import com.mitake.core.response.TradeDateResponse;
import com.mitake.core.response.TradeDateResponseV2;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *市场当年交易日2
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
@StockTestcase(StockTestcaseName.TRADEDATETEST_2)
public class TradeDateTest_2 {
    private static final StockTestcaseName testcaseName = StockTestcaseName.TRADEDATETEST_2;
    private static SetupConfig.TestcaseConfig testcaseConfig;
    private static final int timeout_ms = 1000000;
    @BeforeClass
    public static void setup() throws Exception {
        Log.d("TradeDateSampleTest_2", "Setup");
        testcaseConfig = RunnerSetup.getInstance().getTestcaseConfig(testcaseName);
        if (testcaseConfig == null ) {
            throw new Exception(String.format("Testcase(%s) setup failed, config is empty", testcaseName));
        }
    }
    @Rule
    public TestcaseConfigRule rule = new TestcaseConfigRule(testcaseConfig);
    @Test(timeout = timeout_ms)
    public void requestWork() throws Exception {
        Log.d("TradeDateSampleTest_2", "requestWork");
        // TODO get custom args from param
        final String quoteNumbers = rule.getParam().optString("MARKET");
        final String quoteNumbers1 = rule.getParam().optString("DATE");
        final CompletableFuture result = new CompletableFuture<JSONObject>();
//        for (int i=0;i<quoteNumbers.length;i++){
        final String date;
        if (quoteNumbers1.equals("null")){
            date=null;
        }else {
            date=quoteNumbers1;
        }
            TradeDateRequest request = new TradeDateRequest();
            request.send(quoteNumbers,date, new IResponseInfoCallback<TradeDateResponseV2>() {
                @Override
                public void callback(TradeDateResponseV2 tradeDateResponse) {
                    try {
                        assertNotNull(tradeDateResponse.tradeDatesMap);
                    } catch (AssertionError e) {
                        result.completeExceptionally(e);
                    }
                    HashMap<String,ArrayList<TradeDate>> tradeDatesMap = tradeDateResponse.tradeDatesMap;
                    JSONObject uploadObj = new JSONObject();
                    try {
                        if(tradeDatesMap.get(quoteNumbers)!=null){
                            for (int i=0;i<tradeDatesMap.get(quoteNumbers).size();i++){
                                JSONObject uploadObj_1 = new JSONObject();
                                uploadObj_1.put("date",tradeDatesMap.get(quoteNumbers).get(i).date);
                                uploadObj_1.put("isTrade",tradeDatesMap.get(quoteNumbers).get(i).isTrade);
                                uploadObj_1.put("description",tradeDatesMap.get(quoteNumbers).get(i).description);
                                uploadObj.put(tradeDatesMap.get(quoteNumbers).get(i).date,uploadObj_1);
//                                Log.d("data", String.valueOf(uploadObj_1));
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
                RunnerSetup.getInstance().getCollector().onTestResult(testcaseName, rule.getParam(),resultObj);
            } catch (Exception e) {
                throw new Exception(e);
            }
//        }
    }
}