package com.chi.ssetest.cases;

import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import com.chi.ssetest.TestcaseException;
import com.chi.ssetest.protos.SetupConfig;
import com.chi.ssetest.setup.RunnerSetup;
import com.chi.ssetest.StockTestcase;
import com.chi.ssetest.StockTestcaseName;
import com.chi.ssetest.setup.TestcaseConfigRule;
import com.mitake.core.QuoteItem;
import com.mitake.core.bean.log.ErrorInfo;
import com.mitake.core.request.ChartSubRequest;
import com.mitake.core.request.ChartSubType;
import com.mitake.core.request.QuoteDetailRequest;
import com.mitake.core.response.ChartSubResponse;
import com.mitake.core.response.IResponseInfoCallback;
import com.mitake.core.response.QuoteResponse;
import com.mitake.core.response.Response;

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

/**走势副图1
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
@StockTestcase(StockTestcaseName.CHARTSUBTEST_1)
public class ChartSubTest_1 {
    private static final StockTestcaseName testcaseName = StockTestcaseName.CHARTSUBTEST_1;
    private static SetupConfig.TestcaseConfig testcaseConfig;
    private static final int timeout_ms = 100000000;
    @BeforeClass
     //ChartSubType
    public static void setup() throws Exception {
        Log.d("   ChartSubSampleTest_1", "Setup");
        testcaseConfig = RunnerSetup.getInstance().getTestcaseConfig(testcaseName);
        if (testcaseConfig == null ) {
            throw new Exception(String.format("Testcase(%s) setup failed, config is empty", testcaseName));
        }
    }
    @Rule
    public TestcaseConfigRule rule = new TestcaseConfigRule(testcaseConfig);
    @Test(timeout = timeout_ms)
    public void requestWork() throws Exception {
        Log.d("  ChartSubSampleTest_1", "requestWork");
        // TODO get custom args from param
        final String quoteNumbers = rule.getParam().optString("CODE");
        final String quoteNumbers1 = rule.getParam().optString("ChartType");
        final String quoteNumbers2 = rule.getParam().optString("TYPE");
        final CompletableFuture result = new CompletableFuture<JSONObject>();
//        for (int i = 0; i < quoteNumbers.length; i++) {
            QuoteDetailRequest quoteDetailRequest=new QuoteDetailRequest();
            quoteDetailRequest.send(quoteNumbers, new IResponseInfoCallback() {
                @Override
                public void callback(Response response) {
                    QuoteResponse quoteResponse=(QuoteResponse) response;
                    QuoteItem quoteItem=quoteResponse.quoteItems.get(0);
                    final ChartSubRequest request = new ChartSubRequest();
                    request.send(quoteItem,quoteNumbers1,Integer.parseInt(quoteNumbers2),new IResponseInfoCallback<ChartSubResponse>() {
                        public void callback(ChartSubResponse chartSubResponse) {
                            try {
                                assertNotNull(chartSubResponse.line);
                            } catch (AssertionError e) {
                                //                        result.completeExceptionally(e);
                                result.complete(new JSONObject());
                            }
                            String[][] list=chartSubResponse.line;
                            JSONObject uploadObj = new JSONObject();
                            try {
                                if (list!=null){
                                    for (int i=0;i<list.length;i++){
                                        for (int k=0;k<list[i].length;k++){
                                            JSONObject uploadObj_1 = new JSONObject();
                                            if (quoteNumbers2.equals("1")){
//                                            uploadObj_1.put("time",list[i][0]);
                                                uploadObj_1.put("ddx",list[i][1] == null ? "-" : list[i][1]);
                                                uploadObj_1.put("ddy","-");
                                                uploadObj_1.put("ddz","-");
                                                uploadObj_1.put("bbd","-");
                                                uploadObj_1.put("ratioBS","-");
                                                uploadObj_1.put("largeMoneyInflow","-");
                                                uploadObj_1.put("bigMoneyInflow","-");
                                                uploadObj_1.put("midMoneyInflow","-");
                                                uploadObj_1.put("smallMoneyInflow","-");
                                                uploadObj_1.put("bigNetVolume","-");
                                                uploadObj_1.put("bigTradeNum","-");
                                                uploadObj_1.put("smallTradeNum","-");
                                                uploadObj_1.put("largeTradeNum","-");
                                                uploadObj_1.put("midTradeNum","-");
                                            }else if (quoteNumbers2.equals("2")){
//                                            uploadObj_1.put("time",list[i][0]);
                                                uploadObj_1.put("ddy",list[i][1] == null ? "-" : list[i][1]);
                                                uploadObj_1.put("ddx","-");
                                                uploadObj_1.put("ddz","-");
                                                uploadObj_1.put("bbd","-");
                                                uploadObj_1.put("ratioBS","-");
                                                uploadObj_1.put("largeMoneyInflow","-");
                                                uploadObj_1.put("bigMoneyInflow","-");
                                                uploadObj_1.put("midMoneyInflow","-");
                                                uploadObj_1.put("smallMoneyInflow","-");
                                                uploadObj_1.put("bigNetVolume","-");
                                                uploadObj_1.put("bigTradeNum","-");
                                                uploadObj_1.put("smallTradeNum","-");
                                                uploadObj_1.put("largeTradeNum","-");
                                                uploadObj_1.put("midTradeNum","-");
                                            }else if (quoteNumbers2.equals("3")){
//                                            uploadObj_1.put("time",list[i][0]);
                                                uploadObj_1.put("ddz",list[i][1] == null ? "-" : list[i][1]);
                                                uploadObj_1.put("ddy","-");
                                                uploadObj_1.put("ddx","-");
                                                uploadObj_1.put("bbd","-");
                                                uploadObj_1.put("ratioBS","-");
                                                uploadObj_1.put("largeMoneyInflow","-");
                                                uploadObj_1.put("bigMoneyInflow","-");
                                                uploadObj_1.put("midMoneyInflow","-");
                                                uploadObj_1.put("smallMoneyInflow","-");
                                                uploadObj_1.put("bigNetVolume","-");
                                                uploadObj_1.put("bigTradeNum","-");
                                                uploadObj_1.put("smallTradeNum","-");
                                                uploadObj_1.put("largeTradeNum","-");
                                                uploadObj_1.put("midTradeNum","-");
                                            }else if (quoteNumbers2.equals("4")){
//                                            uploadObj_1.put("time",list[i][0]);
                                                uploadObj_1.put("bbd",list[i][1] == null ? "-" : list[i][1]);
                                                uploadObj_1.put("ddy","-");
                                                uploadObj_1.put("ddx","-");
                                                uploadObj_1.put("ddz","-");
                                                uploadObj_1.put("ratioBS","-");
                                                uploadObj_1.put("largeMoneyInflow","-");
                                                uploadObj_1.put("bigMoneyInflow","-");
                                                uploadObj_1.put("midMoneyInflow","-");
                                                uploadObj_1.put("smallMoneyInflow","-");
                                                uploadObj_1.put("bigNetVolume","-");
                                                uploadObj_1.put("bigTradeNum","-");
                                                uploadObj_1.put("smallTradeNum","-");
                                                uploadObj_1.put("largeTradeNum","-");
                                                uploadObj_1.put("midTradeNum","-");
                                            }else if (quoteNumbers2.equals("5")){
//                                            uploadObj_1.put("time",list[i][0]);
                                                uploadObj_1.put("ratioBS",list[i][1] == null ? "-" : list[i][1]);
                                                uploadObj_1.put("ddy","-");
                                                uploadObj_1.put("ddx","-");
                                                uploadObj_1.put("ddz","-");
                                                uploadObj_1.put("bbd","-");
                                                uploadObj_1.put("largeMoneyInflow","-");
                                                uploadObj_1.put("bigMoneyInflow","-");
                                                uploadObj_1.put("midMoneyInflow","-");
                                                uploadObj_1.put("smallMoneyInflow","-");
                                                uploadObj_1.put("bigNetVolume","-");
                                                uploadObj_1.put("bigTradeNum","-");
                                                uploadObj_1.put("smallTradeNum","-");
                                                uploadObj_1.put("largeTradeNum","-");
                                                uploadObj_1.put("midTradeNum","-");
                                            }else if (quoteNumbers2.equals("6")){
//                                            uploadObj_1.put("time",list[i][0]);
                                                uploadObj_1.put("largeMoneyInflow",list[i][1] == null ? "-" : list[i][1]);
                                                uploadObj_1.put("bigMoneyInflow",list[i][2] == null ? "-" : list[i][2]);
                                                uploadObj_1.put("midMoneyInflow",list[i][3] == null ? "-" : list[i][3]);
                                                uploadObj_1.put("smallMoneyInflow",list[i][4] == null ? "-" : list[i][4]);
                                                uploadObj_1.put("ddy","-");
                                                uploadObj_1.put("ddx","-");
                                                uploadObj_1.put("ddz","-");
                                                uploadObj_1.put("bbd","-");
                                                uploadObj_1.put("ratioBS","-");
                                                uploadObj_1.put("bigNetVolume","-");
                                                uploadObj_1.put("bigTradeNum","-");
                                                uploadObj_1.put("smallTradeNum","-");
                                                uploadObj_1.put("largeTradeNum","-");
                                                uploadObj_1.put("midTradeNum","-");
                                            }else if (quoteNumbers2.equals("7")){
//                                            uploadObj_1.put("time",list[i][0]);
                                                uploadObj_1.put("bigNetVolume",list[i][1] == null ? "-" : list[i][1]);
                                                uploadObj_1.put("ddy","-");
                                                uploadObj_1.put("ddx","-");
                                                uploadObj_1.put("ddz","-");
                                                uploadObj_1.put("bbd","-");
                                                uploadObj_1.put("ratioBS","-");
                                                uploadObj_1.put("largeMoneyInflow","-");
                                                uploadObj_1.put("bigMoneyInflow","-");
                                                uploadObj_1.put("midMoneyInflow","-");
                                                uploadObj_1.put("smallMoneyInflow","-");
                                                uploadObj_1.put("bigTradeNum","-");
                                                uploadObj_1.put("smallTradeNum","-");
                                                uploadObj_1.put("largeTradeNum","-");
                                                uploadObj_1.put("midTradeNum","-");
                                            }
//                                        Log.d("data", String.valueOf(uploadObj_1));
                                            uploadObj.put(String.valueOf(i+1),uploadObj_1);
                                        }
                                    }
                                }
                            } catch (JSONException e) {
                                result.completeExceptionally(e);
                            }
//                            Log.d("data", String.valueOf(uploadObj));
                            result.complete(uploadObj);
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
                RunnerSetup.getInstance().getCollector().onTestResult(testcaseName, rule.getParam(), resultObj);
            } catch (Exception e) {
                //                throw new Exception(e);
                throw new TestcaseException(e,rule.getParam());
            }
//        }
    }
}
