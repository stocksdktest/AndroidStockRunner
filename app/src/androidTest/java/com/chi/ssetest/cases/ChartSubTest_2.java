package com.chi.ssetest.cases;

import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

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

/**走势副图2
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
@StockTestcase(StockTestcaseName. CHARTSUBTEST_2)
public class ChartSubTest_2 {
    private static final StockTestcaseName testcaseName = StockTestcaseName. CHARTSUBTEST_2;
    private static SetupConfig.TestcaseConfig testcaseConfig;
    private static final int timeout_ms = 1000000;
    @BeforeClass
    //ChartType
    public static void setup() throws Exception {
        Log.d("   ChartSubSampleTest_2", "Setup");
        testcaseConfig = RunnerSetup.getInstance().getTestcaseConfig(testcaseName);
        if (testcaseConfig == null ) {
            throw new Exception(String.format("Testcase(%s) setup failed, config is empty", testcaseName));
        }
    }
    @Rule
    public TestcaseConfigRule rule = new TestcaseConfigRule(testcaseConfig);
    @Test(timeout = timeout_ms)
    public void requestWork() throws Exception {
        Log.d("  ChartSubSampleTest_2", "requestWork");
        // TODO get custom args from param
        final String quoteNumbers = rule.getParam().optString("CODE");
        final String quoteNumbers1 = rule.getParam().optString("ChartType");
        final String quoteNumbers2 = rule.getParam().optString("BEGIN");
        final String quoteNumbers3 = rule.getParam().optString("END");
        final String quoteNumbers4 = rule.getParam().optString("TYPE");
        final CompletableFuture result = new CompletableFuture<JSONObject>();
//        for (int i = 0; i < quoteNumbers.length; i++) {
            QuoteDetailRequest quoteDetailRequest=new QuoteDetailRequest();
            quoteDetailRequest.send(quoteNumbers, new IResponseInfoCallback() {
                @Override
                public void callback(Response response) {
                    QuoteResponse quoteResponse=(QuoteResponse) response;
                    QuoteItem quoteItem=quoteResponse.quoteItems.get(0);
                    final ChartSubRequest request = new ChartSubRequest();
                    request.send(quoteItem,quoteNumbers1,Integer.parseInt(quoteNumbers2),Integer.parseInt(quoteNumbers3),quoteNumbers4,new IResponseInfoCallback<ChartSubResponse>() {
                        public void callback(ChartSubResponse chartSubResponse) {
                            try {
                                assertNotNull(chartSubResponse.line);
                            } catch (AssertionError e) {
                                result.completeExceptionally(e);
                            }
                            String[][] list=chartSubResponse.line;
                            JSONObject uploadObj = new JSONObject();
                            try {
                                if (list!=null){
                                    String[] kname=quoteNumbers4.split(",");
                                    for (int i=0;i<list.length;i++){
                                        JSONObject uploadObj_1 = new JSONObject();
                                        for (int k=1;k<list[i].length;k++){
                                            uploadObj_1.put(kname[k],list[i][k]);
                                        }
                                        uploadObj.put(String.valueOf(i+1),uploadObj_1);
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
                throw new Exception(e);
            }
//        }
    }
}
