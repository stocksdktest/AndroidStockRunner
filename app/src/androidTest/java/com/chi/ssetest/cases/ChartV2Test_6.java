package com.chi.ssetest.cases;

import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import com.chi.ssetest.StockTestcase;
import com.chi.ssetest.StockTestcaseName;
import com.chi.ssetest.protos.SetupConfig;
import com.chi.ssetest.setup.RunnerSetup;
import com.chi.ssetest.setup.TestcaseConfigRule;
import com.mitake.core.OHLCItem;
import com.mitake.core.QuoteItem;
import com.mitake.core.bean.log.ErrorInfo;
import com.mitake.core.request.ChartRequestV2;
import com.mitake.core.request.QuoteDetailRequest;
import com.mitake.core.response.ChartResponse;
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
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertNotNull;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
//走势数据 方法六 传入isNeedAfterHours
@RunWith(AndroidJUnit4.class)
@StockTestcase(StockTestcaseName.CHARTV2TEST_6)
public class ChartV2Test_6 {
    private static final StockTestcaseName testcaseName = StockTestcaseName.CHARTV2TEST_6;
    private static SetupConfig.TestcaseConfig testcaseConfig;

    @BeforeClass
    public static void setup() throws Exception {
        Log.d("ChartV2Test_6", "Setup");
        testcaseConfig = RunnerSetup.getInstance().getTestcaseConfig(testcaseName);
        if (testcaseConfig == null ) {
            throw new Exception(String.format("Testcase(%s) setup failed, config is empty", testcaseName));
        }
    }

    @Rule
    public TestcaseConfigRule rule = new TestcaseConfigRule(testcaseConfig);

    @Test(timeout = 5000)
    public void requestWork() throws Exception {
        Log.d("ChartV2Test_6", "requestWork");
        // TODO get custom args from param
        final String quoteNumbers = rule.getParam().getString("CODES");
        final String Types = rule.getParam().getString("Chart_Types");
        final String isNeedAfterHours = rule.getParam().getString("isNeedAfterHours");

//        ChartType
        final CompletableFuture result = new CompletableFuture<JSONObject>();
//        for (int i=0;i<quoteNumbers.length;i++){
//            final int a=i;
            QuoteDetailRequest quoteDetailRequest=new QuoteDetailRequest();

            quoteDetailRequest.send(quoteNumbers, new IResponseInfoCallback() {
                @Override
                public void callback(Response response) {
                    QuoteResponse quoteResponse=(QuoteResponse) response;
                    QuoteItem quoteItem=quoteResponse.quoteItems.get(0);
                    ChartRequestV2 request = new ChartRequestV2();
                    request.send(quoteItem,Types, new IResponseInfoCallback() {
                        @Override
                        public void callback(Response response) {
                            ChartResponse chartResponse = (ChartResponse) response;
                            try {
                                assertNotNull(chartResponse.historyItems);
                            } catch (AssertionError e) {
                                result.completeExceptionally(e);
                            }
                            CopyOnWriteArrayList<OHLCItem> list=chartResponse.historyItems;
                            JSONObject uploadObj = new JSONObject();
                            List<JSONObject> items = new ArrayList<>();
                            // TODO fill uploadObj with QuoteResponse value
                            for (int k=0;k<list.size();k++) {
                                try {
                                    JSONObject uploadObj_1 = new JSONObject();
                                    //存储到JSON
                                    uploadObj_1.put("code", quoteNumbers);
                                    uploadObj_1.put("datetime",list.get(k).datetime);
                                    uploadObj_1.put("closePrice",list.get(k).closePrice);
                                    uploadObj_1.put("tradeVolume",list.get(k).tradeVolume);
                                    uploadObj_1.put("averagePrice",list.get(k).averagePrice);
                                    uploadObj_1.put("md",list.get(k).getMd());
                                    uploadObj_1.put("openInterest",list.get(k).openInterest);
                                    uploadObj_1.put("iopv",list.get(k).iopv);
                                    uploadObj_1.put("iopvPre",list.get(k).iopvPre);
                                    items.add(uploadObj_1);//添加到数组
                                } catch (JSONException e) {
                                    result.completeExceptionally(e);
                                }
//                        Log.d("StockUnittest", quoteNumbers[a]+list.get(k).datetime);
                            }
                            try {
                                //把数组存储到JSON
                                uploadObj.put("items", new JSONArray(items));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            //解析输出JSON
                            try {
                                JSONArray jsonArray = uploadObj.getJSONArray("items");
                                for (int i=0;i<jsonArray.length();i++){
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                                    Log.d("data", String.valueOf(jsonObject));
//                            System.out.println(jsonObject.optString("code")+","+jsonObject.optString("datetime"));
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            //返回JSON结果
                            result.complete(uploadObj);
                        }
                        @Override
                        public void exception(ErrorInfo errorInfo) {
                            result.completeExceptionally(new Exception(errorInfo.toString()));
                        }
                    }, Boolean.parseBoolean(isNeedAfterHours));
                }
                @Override
                public void exception(ErrorInfo errorInfo) {
                    result.completeExceptionally(new Exception(errorInfo.toString()));
                }
            });
            try {
                JSONObject resultObj = (JSONObject)result.get(5000, TimeUnit.MILLISECONDS);
                RunnerSetup.getInstance().getCollector().onTestResult(testcaseName,rule.getParam(), resultObj);
            } catch (Exception e) {
                throw new Exception(e);
            }
//        }
    }
}
