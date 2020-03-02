package com.chi.ssetest.cases;

import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import com.chi.ssetest.TestcaseException;
import com.chi.ssetest.protos.SetupConfig;
import com.chi.ssetest.setup.RunnerSetup;
import com.chi.ssetest.StockTestcase;
import com.chi.ssetest.StockTestcaseName;
import com.chi.ssetest.setup.TestcaseConfigRule;
import com.mitake.core.AddValueModel;
import com.mitake.core.NewIndex;
import com.mitake.core.OHLCItem;
import com.mitake.core.QuoteItem;
import com.mitake.core.bean.log.ErrorInfo;
import com.mitake.core.request.AddValueRequest;
import com.mitake.core.request.ChartSubRequest;
import com.mitake.core.request.ChartSubType;
import com.mitake.core.request.ChartType;
import com.mitake.core.request.OHLCRequest;
import com.mitake.core.request.OHLChartType;
import com.mitake.core.request.QuoteDetailRequest;
import com.mitake.core.request.QuoteRequest;
import com.mitake.core.request.chart.AfterHoursChartRequest;
import com.mitake.core.request.chart.AfterHoursChartResponse;
import com.mitake.core.request.chart.BidChartRequest;
import com.mitake.core.response.AddValueResponse;
import com.mitake.core.response.ChartSubResponse;
import com.mitake.core.response.IResponseCallback;
import com.mitake.core.response.IResponseInfoCallback;
import com.mitake.core.response.OHLCResponse;
import com.mitake.core.response.QuoteResponse;
import com.mitake.core.response.Response;
import com.mitake.core.response.chart.BidChartResponse;
import com.mitake.core.response.chart.BidItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
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
 *盘后走势2（科创板）
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
@StockTestcase(StockTestcaseName. AFTERHOURSCHARTTEST_2)
public class AfterHoursChartTest_2 {
    private static final StockTestcaseName testcaseName = StockTestcaseName.AFTERHOURSCHARTTEST_2;
    private static SetupConfig.TestcaseConfig testcaseConfig;
    private static final int timeout_ms = 1000000;
    @BeforeClass
    //ChartType
    public static void setup() throws Exception {
        Log.d("AfterHoursChartTest_2", "Setup");
        testcaseConfig = RunnerSetup.getInstance().getTestcaseConfig(testcaseName);
        if (testcaseConfig == null ) {
            throw new Exception(String.format("Testcase(%s) setup failed, config is empty", testcaseName));
        }
    }
    @Rule
    public TestcaseConfigRule rule = new TestcaseConfigRule(testcaseConfig);
    @Test(timeout = timeout_ms)
    public void requestWork() throws Exception {
        Log.d("AfterHoursChartSampleTest", "requestWork");
        // TODO get custom args from param
        final String quoteNumbers = rule.getParam().optString("CODE");
        final CompletableFuture result = new CompletableFuture<JSONObject>();
//        for (int i = 0; i < quoteNumbers.length; i++) {
            QuoteDetailRequest quoteDetailRequest=new QuoteDetailRequest();
            quoteDetailRequest.send(quoteNumbers, new IResponseInfoCallback() {
                @Override
                public void callback(Response response) {
                    QuoteResponse quoteResponse=(QuoteResponse) response;
                    QuoteItem quoteItem=quoteResponse.quoteItems.get(0);
                    AfterHoursChartRequest request = new AfterHoursChartRequest();
                    request.send(quoteItem,new IResponseInfoCallback<AfterHoursChartResponse>() {
                        public void callback(AfterHoursChartResponse afterHoursChartResponse) {
                            try {
                                assertNotNull(afterHoursChartResponse.historyItems);
                            } catch (AssertionError e) {
                                //                        result.completeExceptionally(e);
                                result.complete(new JSONObject());
                            }
                            JSONObject uploadObj = new JSONObject();
                            // TODO fill uploadObj with QuoteResponse value
                            try {
                                if (afterHoursChartResponse.historyItems!=null){
                                    for (OHLCItem item : afterHoursChartResponse.historyItems) {
                                        JSONObject uploadObj_1 = new JSONObject();
                                        uploadObj_1.put("datetime",item.datetime);
                                        uploadObj_1.put("closePrice",item.closePrice);
                                        uploadObj_1.put("tradeVolume",item.tradeVolume);
                                        uploadObj_1.put("reference_price",item.reference_price);
                                        uploadObj_1.put("tickCount",afterHoursChartResponse.tradeTimes);
                                        uploadObj_1.put("lowPrice",item.lowPrice);//接口没值
                                        uploadObj_1.put("openPrice",item.openPrice);//接口没值
                                        uploadObj_1.put("highPrice",item.highPrice);//接口没值
                                        uploadObj_1.put("fp_volume",item.fp_volume);//接口没值
                                        uploadObj_1.put("fp_amount",item.fp_amount);//接口没值
                                        uploadObj_1.put("volRatio",item.volRatio);//接口没值
//                                    Log.d("data", String.valueOf(uploadObj_1));
                                        uploadObj.put(item.datetime,uploadObj_1);
                                    }
                                }
//                                Log.d("data", String.valueOf(uploadObj));
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
                //                throw new Exception(e);
                throw new TestcaseException(e,rule.getParam());
            }
//        }
    }
}
