package com.chi.ssetest.cases;

import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import com.chi.ssetest.StockTestcase;
import com.chi.ssetest.StockTestcaseName;
import com.chi.ssetest.TestcaseException;
import com.chi.ssetest.protos.SetupConfig;
import com.chi.ssetest.setup.RunnerSetup;
import com.chi.ssetest.setup.TestcaseConfigRule;
import com.mitake.core.bean.log.ErrorInfo;
import com.mitake.core.request.chart.BidChartRequest;
import com.mitake.core.response.IResponseInfoCallback;
import com.mitake.core.response.chart.BidChartResponse;
import com.mitake.core.response.chart.BidItem;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertNotNull;

/**
 * Example local unit test, which will execute on the development machine (host).
 *走势数据对比竞品 方法四  对应集合竞价走势 方法一      盘前走势
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
@StockTestcase(StockTestcaseName.A_P_ChartV2Test_4)
public class A_P_ChartV2Test_4 {
    private static final StockTestcaseName testcaseName = StockTestcaseName.A_P_ChartV2Test_4;
    private static SetupConfig.TestcaseConfig testcaseConfig;
    private static final int timeout_ms = 100000000;
    @BeforeClass
    public static void setup() throws Exception {
        Log.d(" A_P_ChartV2Test_2", "Setup");
        testcaseConfig = RunnerSetup.getInstance().getTestcaseConfig(testcaseName);
        if (testcaseConfig == null ) {
            throw new Exception(String.format("Testcase(%s) setup failed, config is empty", testcaseName));
        }
    }
    @Rule
    public TestcaseConfigRule rule = new TestcaseConfigRule(testcaseConfig);
    @Test(timeout = timeout_ms)
    public void requestWork() throws Exception {
        Log.d("A_P_ChartV2Test_2", "requestWork");
        // TODO get custom args from param
        final String quoteNumbers = rule.getParam().optString("CODE_A");
        final CompletableFuture result = new CompletableFuture<JSONObject>();
//        for (int i = 0; i < quoteNumbers.length; i++) {
            BidChartRequest request = new BidChartRequest();
            request.send(quoteNumbers,new IResponseInfoCallback<BidChartResponse>() {
                @Override
                public void callback(BidChartResponse bidChartResponse) {
                    try {
                        assertNotNull(bidChartResponse.bidItems);
                    } catch (AssertionError e) {
                        //                        result.completeExceptionally(e);
                        result.complete(new JSONObject());
                    }
                    JSONObject uploadObj = new JSONObject();
                    try {
                        if(bidChartResponse.bidItems!=null){
                            for (BidItem item : bidChartResponse.bidItems) {
                                JSONObject uploadObj_1 = new JSONObject();
                                uploadObj_1.put("closePrice",item.closePrice);
                                uploadObj_1.put("datetime",item.time);
//                                Log.d("data", String.valueOf(uploadObj_1));
                                uploadObj.put(item.time,uploadObj_1);
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
                JSONObject resultObj = (JSONObject) result.get(timeout_ms, TimeUnit.MILLISECONDS);
                RunnerSetup.getInstance().getCollector().onTestResult(testcaseName, rule.getParam(),resultObj);
            } catch (Exception e) {
                //                throw new Exception(e);
                throw new TestcaseException(e,rule.getParam());
            }
//        }
    }
}
