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
import com.mitake.core.CateType;
import com.mitake.core.QuoteItem;
import com.mitake.core.bean.log.ErrorInfo;
import com.mitake.core.request.AddValueRequest;
import com.mitake.core.request.BankuaisortingRequest;
import com.mitake.core.request.CategoryType;
import com.mitake.core.request.CatequoteRequest;
import com.mitake.core.request.QuoteRequest;
import com.mitake.core.response.AddValueResponse;
import com.mitake.core.response.BankuaiRankingResponse;
import com.mitake.core.response.Bankuaisorting;
import com.mitake.core.response.BankuaisortingResponse;
import com.mitake.core.response.CatequoteResponse;
import com.mitake.core.response.IResponseInfoCallback;
import com.mitake.core.response.QuoteResponse;
import com.mitake.core.response.Response;

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
 *板块排序--send()
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
@StockTestcase(StockTestcaseName.BANKUAISORTINGTEST_1)
public class BankuaisortingTest_1 {
    private static final StockTestcaseName testcaseName = StockTestcaseName.BANKUAISORTINGTEST_1;
    private static SetupConfig.TestcaseConfig testcaseConfig;
    private static final int timeout_ms = 100000000;
    @BeforeClass
    public static void setup() throws Exception {
        Log.d("  BankuaisortingTest_1", "Setup");
        testcaseConfig = RunnerSetup.getInstance().getTestcaseConfig(testcaseName);
        if (testcaseConfig == null ) {
            throw new Exception(String.format("Testcase(%s) setup failed, config is empty", testcaseName));
        }
    }
    @Rule
    public TestcaseConfigRule rule = new TestcaseConfigRule(testcaseConfig);
    @Test(timeout = timeout_ms)
    public void requestWork() throws Exception {
        Log.d("BankuaisortingTest_1", "requestWork");
        // TODO get custom args from param
        final String quoteNumbers = rule.getParam().optString("SYMBOL");
        final String quoteNumbers1 = rule.getParam().optString("PARAMS");
        final CompletableFuture result = new CompletableFuture<JSONObject>();
//        CategoryType
//        for (int i=0;i<quoteNumbers.length;i++){
            BankuaisortingRequest request = new BankuaisortingRequest();
            request.send(quoteNumbers,quoteNumbers1,new IResponseInfoCallback<BankuaisortingResponse>() {
                @Override
                public void callback(BankuaisortingResponse bankuaisortingResponse) {
                    try {
                        assertNotNull(bankuaisortingResponse.list);
                    } catch (AssertionError e) {
                        //                        result.completeExceptionally(e);
                        result.complete(new JSONObject());
                    }
                    List<Bankuaisorting> list=bankuaisortingResponse.list;
                    JSONObject uploadObj= new JSONObject();
                    try {
                        if (list!=null){
                            for (int i=0;i<list.size();i++){
                                JSONObject uploadObj_1 = new JSONObject();
                                uploadObj_1.put("ID",dwnull(list.get(i).s == null ? "-" : list.get(i).s));
                                uploadObj_1.put("name",dwnull(list.get(i).n == null ? "-" : list.get(i).n));
                                uploadObj_1.put("weightedChange",dwnull(list.get(i).qzf == null ? "-" :list.get(i).qzf));
                                uploadObj_1.put("averageChange",dwnull(list.get(i).jzf == null ? "-" : list.get(i).jzf));
                                uploadObj_1.put("amount",dwnull(list.get(i).zcje == null ? "-" : list.get(i).zcje));
                                uploadObj_1.put("advanceAndDeclineCount",dwnull(list.get(i).zdjs == null ? "-" : list.get(i).zdjs));
                                uploadObj_1.put("turnoverRate",dwnull(list.get(i).hsl == null ? "-" : list.get(i).hsl));
                                uploadObj_1.put("stockID",dwnull(list.get(i).lzg == null ? "-" : list.get(i).lzg));
                                uploadObj_1.put("stockName",dwnull(list.get(i).lzgn == null ? "-" : list.get(i).lzgn));
                                uploadObj_1.put("stockChange",dwnull(list.get(i).ggzf == null ? "-" : list.get(i).ggzf));
                                uploadObj_1.put("stockChangeRate",dwnull(list.get(i).ggzfb == null ? "-" : list.get(i).ggzfb));
                                uploadObj_1.put("netCapitalInflow",dwnull(list.get(i).netCapitalInflow == null ? "-" : list.get(i).netCapitalInflow));
                                uploadObj_1.put("netCapitalInflow5",dwnull(list.get(i).zlzjjlr5 == null ? "-" : list.get(i).zlzjjlr5));
                                uploadObj_1.put("netCapitalInflow10",dwnull(list.get(i).zlzjjlr10 == null ? "-" : list.get(i).zlzjjlr10));
                                uploadObj_1.put("capitalInflow",dwnull(list.get(i).zlzjlr == null ? "-" : list.get(i).zlzjlr));
                                uploadObj_1.put("capitalOutflow",dwnull(list.get(i).zlzjlc == null ? "-" : list.get(i).zlzjlc));
                                uploadObj_1.put("type",dwnull(list.get(i).ssbk == null ? "-" : list.get(i).ssbk));
                                uploadObj_1.put("hot",dwnull(list.get(i).hot == null ? "-" : list.get(i).hot));
                                uploadObj_1.put("volume",dwnull(list.get(i).totalHand == null ? "-" : list.get(i).totalHand));
                                uploadObj_1.put("nowVolume",dwnull(list.get(i).present == null ? "-" : list.get(i).present));
                                uploadObj_1.put("lastPrice",dwnull(list.get(i).zxj == null ? "-" : list.get(i).zxj));
                                uploadObj_1.put("changeRate",dwnull(list.get(i).zdf == null ? "-" : list.get(i).zdf));
                                uploadObj_1.put("changeRate5",dwnull(list.get(i).zdf5 == null ? "-" : list.get(i).zdf5));
                                uploadObj_1.put("changeRate10",dwnull(list.get(i).zdf10 == null ? "-" : list.get(i).zdf10));
                                uploadObj_1.put("flowValue",dwnull(list.get(i).lzzh == null ? "-" : list.get(i).lzzh));
                                uploadObj_1.put("totalValue",dwnull(list.get(i).szzh == null ? "-" : list.get(i).szzh));
                                uploadObj_1.put("riseRate",dwnull(list.get(i).zgb == null ? "-" : list.get(i).zgb));
                                uploadObj_1.put("openPrice",dwnull(list.get(i).kpj == null ? "-" : list.get(i).kpj));
                                uploadObj_1.put("highPrice",dwnull(list.get(i).zgj == null ? "-" : list.get(i).zgj));
                                uploadObj_1.put("lowPrice",dwnull(list.get(i).zdj == null ? "-" : list.get(i).zdj));
                                uploadObj_1.put("preClosePrice",dwnull(list.get(i).zsj == null ? "-" : list.get(i).zsj));
                                uploadObj_1.put("change",dwnull(list.get(i).zde == null ? "-" : list.get(i).zde));
                                uploadObj_1.put("entrustBuyVolume",dwnull(list.get(i).wm3 == null ? "-" : list.get(i).wm3));
                                uploadObj_1.put("entrustSellVolume",dwnull(list.get(i).wm4 == null ? "-" : list.get(i).wm4));
                                uploadObj_1.put("orderRatio",dwnull(list.get(i).wb == null ? "-" : list.get(i).wb));
                                uploadObj_1.put("entrustDiff",dwnull(list.get(i).wc == null ? "-" : list.get(i).wc));
                                uploadObj_1.put("PE",dwnull(list.get(i).dtsyl == null ? "-" : list.get(i).dtsyl));
                                uploadObj_1.put("SPE",dwnull(list.get(i).jtsyl == null ? "-" : list.get(i).jtsyl));
                                uploadObj_1.put("ROE",dwnull(list.get(i).sjl == null ? "-" : list.get(i).sjl));
                                uploadObj_1.put("amplitudeRate",dwnull(list.get(i).zf == null ? "-" : list.get(i).zf));
                                uploadObj_1.put("stockLastPrice",dwnull(list.get(i).lzgj == null ? "-" : list.get(i).lzgj));
                                uploadObj_1.put("limitUpCount",dwnull(list.get(i).limitUpCount == null ? "-" : list.get(i).limitUpCount));
                                uploadObj_1.put("limitDownCount",dwnull(list.get(i).limitDownCount == null ? "-" : list.get(i).limitDownCount));
//                                uploadObj_1.put("upDownFlag",list.get(i).upDownFlag);
                                uploadObj.put(String.valueOf(i+1),uploadObj_1);
                            }
                        }
                        Log.d("data", String.valueOf(uploadObj));
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
                JSONObject resultObj = (JSONObject)result.get(timeout_ms, TimeUnit.MILLISECONDS);
                RunnerSetup.getInstance().getCollector().onTestResult(testcaseName, rule.getParam(),resultObj);
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