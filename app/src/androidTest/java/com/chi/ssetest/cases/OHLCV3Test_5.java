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
//历史K线  方法五自定义请求K线条数   不支持的市场：新三板、全球指数、外汇、海外市场
@RunWith(AndroidJUnit4.class)
@StockTestcase(StockTestcaseName.OHLCV3TEST_5)
public class OHLCV3Test_5 {
    private static final StockTestcaseName testcaseName = StockTestcaseName.OHLCV3TEST_5;
    private static SetupConfig.TestcaseConfig testcaseConfig;
    private static final int timeout_ms = 1000000;
    @BeforeClass
    public static void setup() throws Exception {
        Log.d("OHLCV3Test_5", "Setup");
        testcaseConfig = RunnerSetup.getInstance().getTestcaseConfig(testcaseName);
        if (testcaseConfig == null ) {
            throw new Exception(String.format("Testcase(%s) setup failed, config is empty", testcaseName));
        }
    }

    @Rule
    public TestcaseConfigRule rule = new TestcaseConfigRule(testcaseConfig);

    @Test(timeout = timeout_ms)
    public void requestWork() throws Exception {
        Log.d("OHLCV3Test_5", "requestWork");
        // TODO get custom args from param
        final String CODES = rule.getParam().optString("CODE", "");
        final String Types = rule.getParam().optString("PERIOD", "");
        final String FqTypes = rule.getParam().optString("PRICEADJUSTEDMODE", "");
        final String Dates = rule.getParam().optString("DATE", "");
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
                    if (Dates.equals("null")){
                        data1=null;
                    }else {
                        data1=Dates;
                    }
                    request.send(quoteItem,Types,Integer.parseInt(FqTypes),data1,Integer.parseInt(Numbers), new IResponseCallback() {
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
                            ArrayList<GBItem> gblist=ohlcResponse.gb;
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
                                        if (gblist!=null){
                                            uploadObj_1.put("date",gblist.get(k).date);
                                            uploadObj_1.put("gb",gblist.get(k).gb);
                                        }
//                                    Log.d("data", String.valueOf(uploadObj_1));
                                        uploadObj.put(list.get(k).datetime,uploadObj_1);
                                    }
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
