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
import com.mitake.core.bean.compound.CompoundUpDownBean;
import com.mitake.core.bean.log.ErrorInfo;
import com.mitake.core.request.AddValueRequest;
import com.mitake.core.request.BankuaisortingRequest;
import com.mitake.core.request.CategoryType;
import com.mitake.core.request.CatequoteRequest;
import com.mitake.core.request.MorePriceRequest;
import com.mitake.core.request.QuoteRequest;
import com.mitake.core.request.compound.CompoundUpDownRequest;
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
import com.mitake.core.response.compound.CompoundUpDownResponse;

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
 *涨跌分布请求接口
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
@StockTestcase(StockTestcaseName.COMPOUNDUPDOWNTEST_1)
public class CompoundUpDownTest_1 {
    private static final StockTestcaseName testcaseName = StockTestcaseName.COMPOUNDUPDOWNTEST_1;
    private static SetupConfig.TestcaseConfig testcaseConfig;
    @BeforeClass
    public static void setup() throws Exception {
        Log.d(" MorePriceSampleTest1", "Setup");
        testcaseConfig = RunnerSetup.getInstance().getTestcaseConfig(testcaseName);
        if (testcaseConfig == null ) {
            throw new Exception(String.format("Testcase(%s) setup failed, config is empty", testcaseName));
        }
    }
//    CompoundUpDownRequest.DateType
    @Rule
    public TestcaseConfigRule rule = new TestcaseConfigRule(testcaseConfig);
    @Test(timeout = 5000)
    public void requestWork() throws Exception {
        Log.d(" MorePriceSampleTest1", "requestWork");
        // TODO get custom args from param
        final String quoteNumbers = rule.getParam().optString("market");
        final String quoteNumbers1 = rule.getParam().optString("time");
        final String quoteNumbers2 = rule.getParam().optString("datetype");
        final CompletableFuture result = new CompletableFuture<JSONObject>();
//        CompoundUpDownRequest.DateType
        //        for (int i=0;i<quoteNumbers.length;i++){
        String time;
        if (quoteNumbers1.equals("null")){
            time=null;
        }else {
            time=quoteNumbers1;
        }
            CompoundUpDownRequest request = new CompoundUpDownRequest();
            request.send(quoteNumbers,time, Integer.parseInt(quoteNumbers2),new IResponseInfoCallback<CompoundUpDownResponse>() {
                @Override
                public void callback(CompoundUpDownResponse compoundUpDownResponse) {
                    try {
                        assertNotNull(compoundUpDownResponse.compoundUpDownList);
                    } catch (AssertionError e) {
                        result.completeExceptionally(e);
                    }
                    List<CompoundUpDownBean> list=compoundUpDownResponse.compoundUpDownList;
                    List<JSONObject> items=new ArrayList<>();
                    JSONObject uploadObj = new JSONObject();
                    try {
                        if (list!=null){
                            for (int i=0;i<list.size();i++){
                                JSONObject uploadObj_1 = new JSONObject();
                                uploadObj_1.put("dateTime",list.get(i).dateTime);
                                uploadObj_1.put("riseCount",list.get(i).riseCount);
                                uploadObj_1.put("fallCount",list.get(i).fallCount);
                                uploadObj_1.put("flatCount",list.get(i).flatCount);
                                uploadObj_1.put("stopCount",list.get(i).stopCount);
                                uploadObj_1.put("riseLimitCount",list.get(i).riseLimitCount);
                                uploadObj_1.put("fallLimitCount",list.get(i).fallLimitCount);
                                uploadObj_1.put("riseFallRange",list.get(i).riseFallRange);
                                uploadObj_1.put("oneRiseLimitCount",list.get(i).oneRiseLimitCount);
                                uploadObj_1.put("natureRiseLimitCount",list.get(i).natureRiseLimitCount);
                                items.add(uploadObj_1);
                            }
                            uploadObj.put("compoundUpDownList", new JSONArray(items));
                        }else {
                            uploadObj.put("compoundUpDownList", list);
                        }
                    } catch (JSONException e) {
                        result.completeExceptionally(e);
                    }
                    Log.d("data", String.valueOf(uploadObj));
                    result.complete(uploadObj);
                }
                @Override
                public void exception(ErrorInfo errorInfo) {
                    result.completeExceptionally(new Exception(errorInfo.toString()));
                }
            });
            try {
                JSONObject resultObj = (JSONObject)result.get(5000, TimeUnit.MILLISECONDS);
                RunnerSetup.getInstance().getCollector().onTestResult(testcaseName, rule.getParam(), resultObj);
            } catch (Exception e) {
                throw new Exception(e);
            }
//        }
    }
}