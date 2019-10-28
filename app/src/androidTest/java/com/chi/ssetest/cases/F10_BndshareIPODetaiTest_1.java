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
import com.mitake.core.request.BankuaisortingRequest;
import com.mitake.core.request.BndshareIPODetaiRequest;
import com.mitake.core.request.CategoryType;
import com.mitake.core.request.CatequoteRequest;
import com.mitake.core.request.CompanyInfoRequest;
import com.mitake.core.request.FundBasicRequest;
import com.mitake.core.request.ImportantnoticeRequest;
import com.mitake.core.request.MorePriceRequest;
import com.mitake.core.request.QuoteRequest;
import com.mitake.core.request.offer.OfferQuoteSort;
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

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *新债详情
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
@StockTestcase(StockTestcaseName.F10_BNDSHAREIPODETAITEST_1)
public class F10_BndshareIPODetaiTest_1 {
    private static final StockTestcaseName testcaseName = StockTestcaseName.F10_BNDSHAREIPODETAITEST_1;
    private static SetupConfig.TestcaseConfig testcaseConfig;
    @BeforeClass
    public static void setup() throws Exception {
        Log.d("F10_BndshareIPODetaiTest_1", "Setup");
        testcaseConfig = RunnerSetup.getInstance().getTestcaseConfig(testcaseName);
        if (testcaseConfig == null ) {
            throw new Exception(String.format("Testcase(%s) setup failed, config is empty", testcaseName));
        }
    }
    @Rule
    public TestcaseConfigRule rule = new TestcaseConfigRule(testcaseConfig);
    @Test(timeout = 5000)
    public void requestWork() throws Exception {
        Log.d("F10_BndshareIPODetaiTest_1", "requestWork");
        // TODO get custom args from param
        final String quoteNumbers = rule.getParam().optString("code");
        final String quoteNumbers1 = rule.getParam().optString("src");
        final CompletableFuture result = new CompletableFuture<JSONObject>();
        //CategoryType
//        for (int i=0;i<quoteNumbers.length;i++){
        String src;
        if (quoteNumbers1.equals("null")){
            src="g";
        }else {
            src=quoteNumbers1;
        }
            BndshareIPODetaiRequest request = new BndshareIPODetaiRequest();
            request.send(quoteNumbers,src,new IResponseInfoCallback<F10V2Response>() {
                @Override
                public void callback(F10V2Response f10V2Response) {
                    try {
                        assertNotNull(f10V2Response.info);
                    } catch (AssertionError e) {
                        result.completeExceptionally(e);
                    }
                    JSONObject uploadObj = new JSONObject();
                    HashMap<String,Object> info = f10V2Response.info;
                    try {
                        if (info!=null){
                            uploadObj.put("APPLYCODE",info.get("APPLYCODE"));
                            uploadObj.put("TRADINGCODE",info.get("TRADINGCODE"));
                            uploadObj.put("STOCKTRADINGCODE",info.get("STOCKTRADINGCODE"));
                            uploadObj.put("STOCKSECUABBR",info.get("STOCKSECUABBR"));
                            uploadObj.put("ISSUEPRICE",info.get("ISSUEPRICE"));
                            uploadObj.put("CONVERTPRICE",info.get("CONVERTPRICE"));
                            uploadObj.put("PREFERREDPLACINGCODE",info.get("PREFERREDPLACINGCODE"));
                            uploadObj.put("PREFERREDPLACINGNAME",info.get("PREFERREDPLACINGNAME"));
                            uploadObj.put("ISSUERRATING",info.get("ISSUERRATING"));
                            uploadObj.put("ISSUEVAL",info.get("ISSUEVAL"));
                            uploadObj.put("INTERESTTERM",info.get("INTERESTTERM"));
                            uploadObj.put("CAPPLYVOL",info.get("CAPPLYVOL"));
                            uploadObj.put("BOOKSTARTDATEON",info.get("BOOKSTARTDATEON"));
                            uploadObj.put("SUCCRESULTNOTICEDATE",info.get("SUCCRESULTNOTICEDATE"));
                            uploadObj.put("LISTINGDATE",info.get("LISTINGDATE"));
                            uploadObj.put("ALLOTRATEON",info.get("ALLOTRATEON"));
                            uploadObj.put("SECUABBR",info.get("SECUABBR"));
                        }else {
                            uploadObj.put("info",info);
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