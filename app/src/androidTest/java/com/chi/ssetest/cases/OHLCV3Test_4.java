package com.chi.ssetest.cases;

import android.content.SyncStatusObserver;
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
import com.mitake.core.parser.FQItem;
import com.mitake.core.parser.GBItem;
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
//历史K线  方法四历史数据(包含当天,仅支持沪深市场)
@RunWith(AndroidJUnit4.class)
@StockTestcase(StockTestcaseName.OHLCV3TEST_4)
public class OHLCV3Test_4 {
    private static final StockTestcaseName testcaseName = StockTestcaseName.OHLCV3TEST_4;
    private static SetupConfig.TestcaseConfig testcaseConfig;
    private static final int timeout_ms = 1000000;
    @BeforeClass
    public static void setup() throws Exception {
        Log.d("OHLCV3Test_4", "Setup");
        testcaseConfig = RunnerSetup.getInstance().getTestcaseConfig(testcaseName);
        if (testcaseConfig == null ) {
            throw new Exception(String.format("Testcase(%s) setup failed, config is empty", testcaseName));
        }
    }

    @Rule
    public TestcaseConfigRule rule = new TestcaseConfigRule(testcaseConfig);

    @Test(timeout = timeout_ms)
    public void requestWork() throws Exception {
        Log.d("OHLCV3Test_4", "requestWork");
        // TODO get custom args from param
        final String CODES = rule.getParam().optString("CODE", "");
        final String BeginDates = rule.getParam().optString("BeginDate", "");
        final String EndDates = rule.getParam().optString("EndDate", "");
        final String Types = rule.getParam().optString("PERIOD", "");
        final String FqTypes = rule.getParam().optString("PRICEADJUSTEDMODE", "");
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
                    String begindate;
                    String enddate;
                    if (BeginDates.equals("null")){
                        begindate=null;
                    }else {
                        begindate=BeginDates;
                    }
                    if (EndDates.equals("null")){
                        enddate=null;
                    }else {
                        enddate=EndDates;
                    }
                    request.send(quoteItem, begindate, enddate, Types, Integer.parseInt(FqTypes), new IResponseCallback() {
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
                                        uploadObj_1.put("datetime",list.get(k).datetime);
                                        uploadObj_1.put("openPrice",list.get(k).openPrice);
                                        uploadObj_1.put("highPrice",list.get(k).highPrice);
                                        uploadObj_1.put("lowPrice",list.get(k).lowPrice);
                                        uploadObj_1.put("closePrice",list.get(k).closePrice);
                                        uploadObj_1.put("tradeVolume",list.get(k).tradeVolume);
                                        uploadObj_1.put("averagePrice",list.get(k).averagePrice);//ios需要判断是否存在字段
                                        uploadObj_1.put("reference_price",list.get(k).reference_price);
                                        uploadObj_1.put("transaction_price",list.get(k).transaction_price);
                                        uploadObj_1.put("openInterest",list.get(k).openInterest);//ios需要判断是否存在字段
                                        uploadObj_1.put("fp_volume",list.get(k).fp_volume);
                                        uploadObj_1.put("fp_amount",list.get(k).fp_amount);
                                        uploadObj_1.put("iopv",list.get(k).iopv);
                                        String turnoverRate = FormatUtility.calculateTurnoverRate(list.get(k),ohlcResponse.gb);
                                        uploadObj_1.put("turnoverRate",turnoverRate);
//                                    Log.d("data", String.valueOf(uploadObj_1));
                                        uploadObj.put(list.get(k).datetime,uploadObj_1);
                                    }
                                }
                                if (ohlcResponse.gb!=null){
                                    ArrayList<JSONObject> gblist=new ArrayList<>();
                                    JSONObject uploadObj_1 = new JSONObject();
                                    for(int j=0;j<ohlcResponse.gb.size();j++){
                                        JSONObject uploadObj_2 = new JSONObject();
                                        uploadObj_2.put("date",ohlcResponse.gb.get(j).date);
                                        uploadObj_2.put("gb",ohlcResponse.gb.get(j).gb);
                                        uploadObj_1.put(String.valueOf(j+1),uploadObj_2);
                                        gblist.add(uploadObj_1);
                                    }
                                    uploadObj.put("gblist",new JSONArray(gblist));
                                }
//                                Log.d("data", String.valueOf(uploadObj));
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
