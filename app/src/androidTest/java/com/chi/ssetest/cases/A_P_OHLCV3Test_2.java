package com.chi.ssetest.cases;

import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import com.chi.ssetest.StockTestcase;
import com.chi.ssetest.StockTestcaseName;
import com.chi.ssetest.TestcaseException;
import com.chi.ssetest.protos.SetupConfig;
import com.chi.ssetest.setup.RunnerSetup;
import com.chi.ssetest.setup.TestcaseConfigRule;
import com.mitake.core.OHLCItem;
import com.mitake.core.QuoteItem;
import com.mitake.core.bean.log.ErrorInfo;
import com.mitake.core.request.OHLCRequestV3;
import com.mitake.core.request.QuoteDetailRequest;
import com.mitake.core.response.IResponseCallback;
import com.mitake.core.response.IResponseInfoCallback;
import com.mitake.core.response.OHLCResponse;
import com.mitake.core.response.QuoteResponse;
import com.mitake.core.response.Response;
import com.mitake.core.util.FormatUtility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertNotNull;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
//历史K线和竞品对比 方法二 传入结束时间和K线条数，返回结束时间点往前的count条K线数据 对应历史k线方法六
@RunWith(AndroidJUnit4.class)
@StockTestcase(StockTestcaseName.A_P_OHLCV3Test_2)
public class A_P_OHLCV3Test_2 {
    private static final StockTestcaseName testcaseName = StockTestcaseName.A_P_OHLCV3Test_2;
    private static SetupConfig.TestcaseConfig testcaseConfig;
    private static final int timeout_ms = 100000000;
    @BeforeClass
    public static void setup() throws Exception {
        Log.d("A_P_OHLCV3Test_2", "Setup");
        testcaseConfig = RunnerSetup.getInstance().getTestcaseConfig(testcaseName);
        if (testcaseConfig == null ) {
            throw new Exception(String.format("Testcase(%s) setup failed, config is empty", testcaseName));
        }
    }

    @Rule
    public TestcaseConfigRule rule = new TestcaseConfigRule(testcaseConfig);

    @Test(timeout = timeout_ms)
    public void requestWork() throws Exception {
        Log.d("A_P_OHLCV3Test_2", "requestWork");
        // TODO get custom args from param
        final String CODES = rule.getParam().optString("CODE_A", "");
        final String EndDates = (rule.getParam().optString("ENDDATE", "")).replace("-","");
        final String Types = rule.getParam().optString("PERIOD", "");
        final String FqTypes = rule.getParam().optString("PRICEADJUSTEDMODE", "");
        final String Numbers = rule.getParam().optString("COUNT", "");
//        OHLChartType
        final CompletableFuture result = new CompletableFuture<JSONObject>();
//        for (int i=0;i<CODES.length;i++){
        QuoteDetailRequest quoteDetailRequest=new QuoteDetailRequest();
        quoteDetailRequest.send(CODES, new IResponseInfoCallback() {
            @Override
            public void callback(Response response) {
                QuoteResponse quoteResponse=(QuoteResponse) response;
                QuoteItem quoteItem=quoteResponse.quoteItems.get(0);
                OHLCRequestV3 request = new OHLCRequestV3();
                String data1;
                if (EndDates.equals("null")){
                    data1=null;
                }else {
                    data1=EndDates;
                }
                if (Types.equals("dayk")||Types.equals("weekk")||Types.equals("monthk")||Types.equals("yeark")){
                    data1=data1.substring(0,8);
                }
                request.send(quoteItem,data1,Types,Integer.parseInt(FqTypes),Integer.parseInt(Numbers), new IResponseCallback() {
                    @Override
                    public void callback(Response response) {
                        OHLCResponse ohlcResponse = (OHLCResponse) response;
                        try {
                            assertNotNull(ohlcResponse.historyItems);
                        } catch (AssertionError e) {
                            //                        result.completeExceptionally(e);
                            result.complete(new JSONObject());
                        }
                        CopyOnWriteArrayList<OHLCItem> list =ohlcResponse.historyItems;
                        JSONObject uploadObj = new JSONObject();
                        try {
                            if(list!=null){
                                for (int k=0;k<list.size();k++){
                                    JSONObject uploadObj_1 = new JSONObject();
                                    String timedata;
                                    if (Types.equals("dayk")||Types.equals("weekk")||Types.equals("monthk")||Types.equals("yeark")){
                                        timedata=list.get(k).datetime;
                                    }else {
                                        timedata=(list.get(k).datetime+list.get(k).time).substring(0,12);
                                    }
                                    uploadObj_1.put("datetime",timedata);
                                    uploadObj_1.put("openPrice",list.get(k).openPrice == null ? "-" : list.get(k).openPrice);
                                    uploadObj_1.put("highPrice",list.get(k).highPrice == null ? "-" : list.get(k).highPrice);
                                    uploadObj_1.put("lowPrice",list.get(k).lowPrice == null ? "-" : list.get(k).lowPrice);
                                    uploadObj_1.put("closePrice",list.get(k).closePrice == null ? "-" : list.get(k).closePrice);
                                    uploadObj_1.put("tradeVolume",list.get(k).tradeVolume == null ? "-" : String.valueOf(Math.round(Float.parseFloat(list.get(k).tradeVolume))));
                                    uploadObj_1.put("transaction_price",list.get(k).transaction_price == null ? "-" : list.get(k).transaction_price);
                                    uploadObj.put(timedata,uploadObj_1);
                                }
                            }
//                            Log.d("data", String.valueOf(uploadObj));
                            result.complete(uploadObj);
                        } catch (JSONException e) {
                            result.completeExceptionally(e);
                        }
                    }

                    @Override
                    public void exception(int i, String s) {
                        result.completeExceptionally(new Exception(s));
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
            RunnerSetup.getInstance().getCollector().onTestResult(testcaseName,rule.getParam(), resultObj);
        } catch (Exception e) {
            //                throw new Exception(e);
            throw new TestcaseException(e,rule.getParam());
        }
//        }
    }
}
