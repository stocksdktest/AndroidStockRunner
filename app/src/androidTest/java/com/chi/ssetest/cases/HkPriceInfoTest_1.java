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
import com.mitake.core.bean.quote.HkPriceInfoItem;
import com.mitake.core.request.AddValueRequest;
import com.mitake.core.request.BankuaisortingRequest;
import com.mitake.core.request.CategoryType;
import com.mitake.core.request.CatequoteRequest;
import com.mitake.core.request.HkPriceInfoRequest;
import com.mitake.core.request.MorePriceRequest;
import com.mitake.core.request.QuoteRequest;
import com.mitake.core.request.offer.OfferQuoteSort;
import com.mitake.core.response.AddValueResponse;
import com.mitake.core.response.BankuaiRankingResponse;
import com.mitake.core.response.Bankuaisorting;
import com.mitake.core.response.BankuaisortingResponse;
import com.mitake.core.response.CatequoteResponse;
import com.mitake.core.response.IResponseCallback;
import com.mitake.core.response.IResponseInfoCallback;
import com.mitake.core.response.MorePriceResponse;
import com.mitake.core.response.QuoteResponse;
import com.mitake.core.response.Response;
import com.mitake.core.response.quote.HkPriceInfoResponse;

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
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *获取港股价差对照表， 与TradeQuoteRequest结合使用
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
@StockTestcase(StockTestcaseName.HKPRICEINFOTEST_1)
public class HkPriceInfoTest_1 {
    private static final StockTestcaseName testcaseName = StockTestcaseName.HKPRICEINFOTEST_1;
    private static SetupConfig.TestcaseConfig testcaseConfig;
    @BeforeClass
    public static void setup() throws Exception {
        Log.d(" HkPriceInfoTest_1", "Setup");
        testcaseConfig = RunnerSetup.getInstance().getTestcaseConfig(testcaseName);
        if (testcaseConfig == null ) {
            throw new Exception(String.format("Testcase(%s) setup failed, config is empty", testcaseName));
        }
    }
    @Rule
    public TestcaseConfigRule rule = new TestcaseConfigRule(testcaseConfig);
    @Test(timeout = 5000)
    public void requestWork() throws Exception {
        Log.d(" HkPriceInfoTest_1", "requestWork");
        // TODO get custom args from param
        final String quoteNumbers = rule.getParam().optString("");
        final CompletableFuture result = new CompletableFuture<JSONObject>();
        //CategoryType
            HkPriceInfoRequest request = new HkPriceInfoRequest();
            request.send(new IResponseInfoCallback<HkPriceInfoResponse>() {
                @Override
                public void callback(HkPriceInfoResponse hkPriceInfoResponse) {
                    try {
                        assertNotNull(hkPriceInfoResponse.hkPriceInfo);
                    } catch (AssertionError e) {
                        result.completeExceptionally(e);
                    }
                    JSONObject uploadObj = new JSONObject();
                    Map<String, List<HkPriceInfoItem>> list=hkPriceInfoResponse.hkPriceInfo;
                    Set<String> key=list.keySet();
                    try {
                        for (String str : key) {
                            for (int i=0;i<list.get(str).size();i++){
                                JSONObject uploadObj_1 = new JSONObject();
                                uploadObj_1.put("upLimit",list.get(str).get(i).upLimit);
                                uploadObj_1.put("lowLimit",list.get(str).get(i).lowLimit);
                                uploadObj_1.put("priceDifference",list.get(str).get(i).priceDifference);
                                uploadObj.put(String.valueOf(i),uploadObj_1);
                                Log.d("data", String.valueOf(uploadObj_1));
                            }
                        }
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
                JSONObject resultObj = (JSONObject)result.get(5000, TimeUnit.MILLISECONDS);
                RunnerSetup.getInstance().getCollector().onTestResult(testcaseName, rule.getParam(), resultObj);
            } catch (Exception e) {
                throw new Exception(e);
            }
        }
    }
