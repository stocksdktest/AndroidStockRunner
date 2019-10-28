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
import com.mitake.core.bean.log.ErrorInfo;
import com.mitake.core.request.AddValueRequest;
import com.mitake.core.request.QuoteRequest;
import com.mitake.core.request.chart.BidChartRequest;
import com.mitake.core.response.AddValueResponse;
import com.mitake.core.response.IResponseInfoCallback;
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
 *集合竞价1--传股票代码
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
@StockTestcase(StockTestcaseName.BIDCHARTTEST_1)
public class BidChartTest_1 {
    private static final StockTestcaseName testcaseName = StockTestcaseName.BIDCHARTTEST_1;
    private static SetupConfig.TestcaseConfig testcaseConfig;
    @BeforeClass
    public static void setup() throws Exception {
        Log.d(" BidChartTest_1", "Setup");
        testcaseConfig = RunnerSetup.getInstance().getTestcaseConfig(testcaseName);
        if (testcaseConfig == null ) {
            throw new Exception(String.format("Testcase(%s) setup failed, config is empty", testcaseName));
        }
    }
    @Rule
    public TestcaseConfigRule rule = new TestcaseConfigRule(testcaseConfig);
    @Test(timeout = 5000)
    public void requestWork() throws Exception {
        Log.d("BidChartTest_1", "requestWork");
        // TODO get custom args from param
        final String quoteNumbers = rule.getParam().optString("code");
        final CompletableFuture result = new CompletableFuture<JSONObject>();
//        for (int i = 0; i < quoteNumbers.length; i++) {
            BidChartRequest request = new BidChartRequest();
            request.send(quoteNumbers,new IResponseInfoCallback<BidChartResponse>() {
                @Override
                public void callback(BidChartResponse bidChartResponse) {
                    try {
                        assertNotNull(bidChartResponse.bidItems);
                    } catch (AssertionError e) {
                        result.completeExceptionally(e);
                    }
                    try {
                        for (BidItem item : bidChartResponse.bidItems) {
                            JSONObject uploadObj_1 = new JSONObject();
                            uploadObj_1.put("closePrice",item.closePrice);
                            uploadObj_1.put("referencePrice",item.referencePrice);
                            uploadObj_1.put("time",item.time);
                            uploadObj_1.put("sell1",item.sell1);
                            uploadObj_1.put("sell2",item.sell2);
                            uploadObj_1.put("buy1",item.buy1);
                            uploadObj_1.put("buy2",item.buy2);
                            Log.d("data", String.valueOf(uploadObj_1));
                            result.complete(uploadObj_1);
                        }
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
                JSONObject resultObj = (JSONObject) result.get(5000, TimeUnit.MILLISECONDS);
                RunnerSetup.getInstance().getCollector().onTestResult(testcaseName, rule.getParam(),resultObj);
            } catch (Exception e) {
                throw new Exception(e);
            }
//        }
    }
}
