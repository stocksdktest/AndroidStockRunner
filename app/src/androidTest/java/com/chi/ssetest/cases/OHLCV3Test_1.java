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
//历史K线 方法一 带入快照无复权
@RunWith(AndroidJUnit4.class)
@StockTestcase(StockTestcaseName.OHLCV3TEST_1)
public class OHLCV3Test_1 {
    private static final StockTestcaseName testcaseName = StockTestcaseName.OHLCV3TEST_1;
    private static SetupConfig.TestcaseConfig testcaseConfig;
    private static final int timeout_ms = 100000000;
    @BeforeClass
    public static void setup() throws Exception {
        Log.d("OHLCV3Test_1", "Setup");
        testcaseConfig = RunnerSetup.getInstance().getTestcaseConfig(testcaseName);
        if (testcaseConfig == null ) {
            throw new Exception(String.format("Testcase(%s) setup failed, config is empty", testcaseName));
        }
    }

    @Rule
    public TestcaseConfigRule rule = new TestcaseConfigRule(testcaseConfig);

    @Test(timeout = timeout_ms)
    public void requestWork() throws Exception {
        Log.d("OHLCV3Test_1", "requestWork");
        // TODO get custom args from param
        final String CODES = rule.getParam().optString("CODE", "");
        final String Types = rule.getParam().optString("PERIOD", "");
//        OHLChartType
        final CompletableFuture result = new CompletableFuture<JSONObject>();
//        for (int i=0;i<CODES.length;i++){
            final QuoteDetailRequest quoteDetailRequest=new QuoteDetailRequest();
            quoteDetailRequest.send(CODES, new IResponseInfoCallback() {
                @Override
                public void callback(Response response) {
                    QuoteResponse quoteResponse=(QuoteResponse) response;
                    final QuoteItem quoteItem=quoteResponse.quoteItems.get(0);
                    OHLCRequestV3 request = new OHLCRequestV3();
                    request.send(quoteItem,Types, new IResponseInfoCallback() {
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
                                if (list!=null){
                                    for (int k=0;k<list.size();k++){
                                        JSONObject uploadObj_1 = new JSONObject();
                                        String timedata;
                                        if (Types.equals("dayk")||Types.equals("weekk")||Types.equals("monthk")||Types.equals("yeark")){
                                            timedata=list.get(k).datetime;
                                        }else {
                                            timedata=list.get(k).datetime+list.get(k).time;
                                        }
                                        uploadObj_1.put("datetime",timedata);
                                        uploadObj_1.put("openPrice",dwnull(list.get(k).openPrice == null ? "-" : list.get(k).openPrice));
                                        uploadObj_1.put("highPrice",dwnull(list.get(k).highPrice == null ? "-" : list.get(k).highPrice));
                                        uploadObj_1.put("lowPrice",dwnull(list.get(k).lowPrice == null ? "-" : list.get(k).lowPrice));
                                        uploadObj_1.put("closePrice",dwnull(list.get(k).closePrice == null ? "-" : list.get(k).closePrice));
                                        uploadObj_1.put("tradeVolume",dwnull(list.get(k).tradeVolume == null ? "-" : list.get(k).tradeVolume));
                                        uploadObj_1.put("averagePrice",dwnull(list.get(k).averagePrice == null ? "-" : list.get(k).averagePrice));//ios需要判断是否存在字段
                                        uploadObj_1.put("reference_price",dwnull(list.get(k).reference_price == null ? "-" : list.get(k).reference_price));
                                        uploadObj_1.put("transaction_price",dwnull(list.get(k).transaction_price == null ? "-" : list.get(k).transaction_price));
                                        uploadObj_1.put("openInterest",dwnull(list.get(k).openInterest == null ? "-" : list.get(k).openInterest));//ios需要判断是否存在字段
                                        //盘后成交量成交额
                                        if (quoteItem.market.equals("sh")||quoteItem.market.equals("sz")){
                                            if (quoteItem.subtype.equals("1004")||quoteItem.subtype.equals("1006")){
                                                uploadObj_1.put("fp_volume",dwnull(list.get(k).fp_volume == null ? "-" : list.get(k).openInterest));
                                                uploadObj_1.put("fp_amount",dwnull(list.get(k).fp_amount == null ? "-" : list.get(k).fp_amount));
                                            }
                                        }
                                        uploadObj_1.put("iopv",dwnull(list.get(k).iopv == null ? "-" : list.get(k).iopv));

                                        String turnoverRate = FormatUtility.calculateTurnoverRate(list.get(k),ohlcResponse.gb);
                                        uploadObj_1.put("turnoverRate",turnoverRate == null ? "-" : turnoverRate);
//                                        Log.d("data", String.valueOf(uploadObj_1));
                                        uploadObj.put(timedata,uploadObj_1);
                                    }
                                }
                                if (ohlcResponse.gb!=null){
                                    JSONObject uploadObj_1 = new JSONObject();
                                    for(int j=0;j<ohlcResponse.gb.size();j++){
                                        JSONObject uploadObj_2 = new JSONObject();
                                        uploadObj_2.put("date",ohlcResponse.gb.get(j).date);
                                        uploadObj_2.put("gb",ohlcResponse.gb.get(j).gb);
                                        uploadObj_1.put(ohlcResponse.gb.get(j).date,uploadObj_2);
                                    }
                                    uploadObj.put("gblist",uploadObj_1);
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
                RunnerSetup.getInstance().getCollector().onTestResult(testcaseName,rule.getParam(), resultObj);
            } catch (Exception e) {
                //                throw new Exception(e);
                throw new TestcaseException(e,rule.getParam());
            }
//        }
    }
    public String dwnull(String st){
        if (st.equals("一")){
            st="-";
        }else if (st.equals("")){
            st="-";
        }else if(st==null){
            st="-";
        }else if (st.isEmpty()){
            st="-";
        }
        return  st;
    }
}
