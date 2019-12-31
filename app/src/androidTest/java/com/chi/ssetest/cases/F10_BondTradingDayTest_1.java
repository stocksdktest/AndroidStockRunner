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
import com.mitake.core.CompanyInfo;
import com.mitake.core.Importantnotice;
import com.mitake.core.QuoteItem;
import com.mitake.core.bean.MorePriceItem;
import com.mitake.core.bean.log.ErrorInfo;
import com.mitake.core.request.AddValueRequest;
import com.mitake.core.request.AssetAllocationRequest;
import com.mitake.core.request.BankuaisortingRequest;
import com.mitake.core.request.BondTradingDayRequest;
import com.mitake.core.request.CategoryType;
import com.mitake.core.request.CatequoteRequest;
import com.mitake.core.request.CompanyInfoRequest;
import com.mitake.core.request.FundBasicRequest;
import com.mitake.core.request.ImportantnoticeRequest;
import com.mitake.core.request.IndustryPortfolioRequest;
import com.mitake.core.request.MorePriceRequest;
import com.mitake.core.request.QuoteRequest;
//import com.mitake.core.request.offer.OfferQuoteSort;
import com.mitake.core.response.AddValueResponse;
import com.mitake.core.response.BankuaiRankingResponse;
import com.mitake.core.response.Bankuaisorting;
import com.mitake.core.response.BankuaisortingResponse;
import com.mitake.core.response.CatequoteResponse;
import com.mitake.core.response.F10V2Response;
import com.mitake.core.response.IResponseInfoCallback;
import com.mitake.core.response.ImportantnoticeResponse;
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
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *新债日历
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
@StockTestcase(StockTestcaseName.F10_BONDTRADINGDAYTEST_1)
public class F10_BondTradingDayTest_1 {
    private static final StockTestcaseName testcaseName = StockTestcaseName.F10_BONDTRADINGDAYTEST_1;
    private static SetupConfig.TestcaseConfig testcaseConfig;
    private static final int timeout_ms = 1000000;
    @BeforeClass
    public static void setup() throws Exception {
        Log.d("F10_BondTradingDayTest_1", "Setup");
        testcaseConfig = RunnerSetup.getInstance().getTestcaseConfig(testcaseName);
        if (testcaseConfig == null ) {
            throw new Exception(String.format("Testcase(%s) setup failed, config is empty", testcaseName));
        }
    }
    @Rule
    public TestcaseConfigRule rule = new TestcaseConfigRule(testcaseConfig);
    @Test(timeout = timeout_ms)
    public void requestWork() throws Exception {
        Log.d("F10_BondTradingDayTest_1", "requestWork");
        // TODO get custom args from param
        final String quoteNumbers = rule.getParam().optString("SOURCETYPE");
        final CompletableFuture result = new CompletableFuture<JSONObject>();
//        for (int i=0;i<quoteNumbers.length;i++){
        String src;
        if (quoteNumbers.equals("null")){
            src="g";
        }else {
            src=quoteNumbers;
        }
            BondTradingDayRequest request = new BondTradingDayRequest();
            request.send(src,new IResponseInfoCallback<F10V2Response>() {
                @Override
                public void callback(F10V2Response f10V2Response) {
                    try {
                        assertNotNull(f10V2Response.infos);
                    } catch (AssertionError e) {
                        result.completeExceptionally(e);
                    }
                    JSONObject uploadObj = new JSONObject();
                    List<HashMap<String,Object>> list = f10V2Response.infos;
                    try {
                        if(list!=null){
                            for (int i=0;i<list.size();i++){
                                JSONObject uploadObj_1 = new JSONObject();
                                uploadObj_1.put("sg",list.get(i).get("sg"));
                                uploadObj_1.put("jjsg",list.get(i).get("jjsg"));
                                uploadObj_1.put("dss",list.get(i).get("dss"));
                                uploadObj_1.put("NORMALDAY",list.get(i).get("NORMALDAY"));
                                uploadObj.put((String) list.get(i).get("NORMALDAY"),uploadObj_1);
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
                RunnerSetup.getInstance().getCollector().onTestResult(testcaseName, rule.getParam(), resultObj);
            } catch (Exception e) {
                throw new Exception(e);
            }
//        }
    }
}