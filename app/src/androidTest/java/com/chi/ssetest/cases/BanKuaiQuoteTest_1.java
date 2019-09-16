package com.chi.ssetest.cases;

import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import com.chi.ssetest.protos.SetupConfig;
import com.chi.ssetest.setup.RunnerSetup;
import com.chi.ssetest.StockTestcase;
import com.chi.ssetest.StockTestcaseName;
import com.chi.ssetest.setup.TestcaseConfigRule;
import com.mitake.core.AddValueModel;
import com.mitake.core.CateType;
import com.mitake.core.QuoteItem;
import com.mitake.core.bean.MorePriceItem;
import com.mitake.core.bean.log.ErrorInfo;
import com.mitake.core.request.AddValueRequest;
import com.mitake.core.request.BanKuaiQuoteRequest;
import com.mitake.core.request.BankuaisortingRequest;
import com.mitake.core.request.CategoryType;
import com.mitake.core.request.CatequoteRequest;
import com.mitake.core.request.MorePriceRequest;
import com.mitake.core.request.QuoteRequest;
import com.mitake.core.request.offer.OfferQuoteSort;
import com.mitake.core.response.AddValueResponse;
import com.mitake.core.response.BankuaiRankingResponse;
import com.mitake.core.response.Bankuaisorting;
import com.mitake.core.response.BankuaisortingResponse;
import com.mitake.core.response.CatequoteResponse;
import com.mitake.core.response.IResponseInfoCallback;
import com.mitake.core.response.MorePriceResponse;
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
 *个股所属板块行情
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
@StockTestcase(StockTestcaseName.BANKUAIQUOTETEST_1)
public class BanKuaiQuoteTest_1 {
    private static final StockTestcaseName testcaseName = StockTestcaseName.BANKUAIQUOTETEST_1;
    private static SetupConfig.TestcaseConfig testcaseConfig;
    @BeforeClass
    public static void setup() throws Exception {
        Log.d("BanKuaiQuoteTest_1", "Setup");
        testcaseConfig = RunnerSetup.getInstance().getTestcaseConfig(testcaseName);
        if (testcaseConfig == null ) {
            throw new Exception(String.format("Testcase(%s) setup failed, config is empty", testcaseName));
        }
    }
    @Rule
    public TestcaseConfigRule rule = new TestcaseConfigRule(testcaseConfig);

    @Test(timeout = 5000)
    public void requestWork() throws Exception {
        Log.d("BanKuaiQuoteTest_1", "requestWork");
        // TODO get custom args from param
        final String quoteNumbers = rule.getParam().optString("code");
        final String quoteNumbers1 = rule.getParam().optString("params");
        final CompletableFuture result = new CompletableFuture<JSONObject>();
        //CategoryType
        String param;
        if (quoteNumbers1.equals("null")){
            param=null;
        }else {
            param=quoteNumbers1;
        }
            BanKuaiQuoteRequest request = new BanKuaiQuoteRequest();
            request.send(quoteNumbers,param,new IResponseInfoCallback<BankuaisortingResponse>() {
                @Override
                public void callback(BankuaisortingResponse bankuaisortingResponse) {
                    assertNotNull(bankuaisortingResponse.list);
                    List<Bankuaisorting> list=bankuaisortingResponse.list;
                    List<JSONObject> items=new ArrayList<>();
                    JSONObject uploadObj = new JSONObject();
                    for (int i=0;i<list.size();i++){
                        JSONObject uploadObj_1 = new JSONObject();
                        try {
                            uploadObj_1.put("ID",list.get(i).ns);
                            uploadObj_1.put("name",list.get(i).n);
                            uploadObj_1.put("weightedChange",list.get(i).qzf);
                            uploadObj_1.put("averageChange",list.get(i).jzf);
                            uploadObj_1.put("amount",list.get(i).zcje);
                            uploadObj_1.put("advanceAndDeclineCount",list.get(i).zdjs);
                            uploadObj_1.put("turnoverRate",list.get(i).hsl);
                            uploadObj_1.put("stockID",list.get(i).lzg);
                            uploadObj_1.put("stockName",list.get(i).lzgn);
                            uploadObj_1.put("stockChange",list.get(i).ggzf);
                            uploadObj_1.put("stockChangeRate",list.get(i).ggzfb);
                            uploadObj_1.put("netCapitalInflow",list.get(i).netCapitalInflow);
                            uploadObj_1.put("netCapitalInflow5",list.get(i).zlzjjlr5);
                            uploadObj_1.put("netCapitalInflow10",list.get(i).zlzjjlr10);
                            uploadObj_1.put("capitalInflow",list.get(i).zlzjlr);
                            uploadObj_1.put("capitalOutflow",list.get(i).zlzjlc);
                            uploadObj_1.put("type",list.get(i).ssbk);
                            uploadObj_1.put("hot",list.get(i).hot);
                            uploadObj_1.put("volume",list.get(i).totalHand);
                            uploadObj_1.put("nowVolume",list.get(i).present);
                            uploadObj_1.put("lastPrice",list.get(i).zxj);
                            uploadObj_1.put("changeRate",list.get(i).zdf);
                            uploadObj_1.put("changeRate5",list.get(i).zdf5);
                            uploadObj_1.put("changeRate10",list.get(i).zdf10);
                            uploadObj_1.put("flowValue",list.get(i).lzzh);
                            uploadObj_1.put("totalValue",list.get(i).szzh);
                            uploadObj_1.put("riseRate",list.get(i).zgb);
                            uploadObj_1.put("openPrice",list.get(i).kpj);
                            uploadObj_1.put("highPrice",list.get(i).zgj);
                            uploadObj_1.put("lowPrice",list.get(i).zdj);
                            uploadObj_1.put("preClosePrice",list.get(i).zsj);
                            uploadObj_1.put("change",list.get(i).zde);
                            uploadObj_1.put("entrustBuyVolume",list.get(i).wm3);
                            uploadObj_1.put("entrustSellVolume",list.get(i).wm4);
                            uploadObj_1.put("orderRatio",list.get(i).wb);
                            uploadObj_1.put("entrustDiff",list.get(i).wc);
                            uploadObj_1.put("PE",list.get(i).dtsyl);
                            uploadObj_1.put("SPE",list.get(i).jtsyl);
                            uploadObj_1.put("ROE",list.get(i).sjl);
                            uploadObj_1.put("amplitudeRate",list.get(i).zf);//
                            uploadObj_1.put("lzgj",list.get(i).lzgj);//ios无
                            uploadObj_1.put("limitUpCount",list.get(i).limitUpCount);//ios无
                            uploadObj_1.put("limitDownCount",list.get(i).limitDownCount);//ios无
                            items.add(uploadObj_1);
                        } catch (JSONException e) {
                            result.completeExceptionally(e);
                        }
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
                    result.complete(uploadObj);
                }
                @Override
                public void exception(ErrorInfo errorInfo) {
                    result.completeExceptionally(new Exception(errorInfo.toString()));
                }
            });
            try {
                JSONObject resultObj = (JSONObject)result.get(5000, TimeUnit.MILLISECONDS);
                RunnerSetup.getInstance().getCollector().onTestResult(testcaseName, rule.getParam(),resultObj);
            } catch (Exception e) {
                throw new Exception(e);
            }
//        }
    }
}