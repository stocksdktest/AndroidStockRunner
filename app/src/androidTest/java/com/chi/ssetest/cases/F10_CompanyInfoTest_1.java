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
import com.mitake.core.request.CategoryType;
import com.mitake.core.request.CatequoteRequest;
import com.mitake.core.request.CompanyInfoRequest;
import com.mitake.core.request.ImportantnoticeRequest;
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
 *基本情况
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
@StockTestcase(StockTestcaseName.F10_COMPANYINFOTEST_1)
public class F10_CompanyInfoTest_1 {
    private static final StockTestcaseName testcaseName = StockTestcaseName.F10_COMPANYINFOTEST_1;
    private static SetupConfig.TestcaseConfig testcaseConfig;
    private static final int timeout_ms = 1000000;
    @BeforeClass
    public static void setup() throws Exception {
        Log.d("F10_CompanyInfoTest_1", "Setup");
        testcaseConfig = RunnerSetup.getInstance().getTestcaseConfig(testcaseName);
        if (testcaseConfig == null ) {
            throw new Exception(String.format("Testcase(%s) setup failed, config is empty", testcaseName));
        }
    }
    @Rule
    public TestcaseConfigRule rule = new TestcaseConfigRule(testcaseConfig);
    @Test(timeout = timeout_ms)
    public void requestWork() throws Exception {
        Log.d("F10_CompanyInfoTest_1", "requestWork");
        // TODO get custom args from param
        final String quoteNumbers = rule.getParam().optString("CODE");
        final String quoteNumbers1 = rule.getParam().optString("SOURCETYPE");
        final CompletableFuture result = new CompletableFuture<JSONObject>();
        //CategoryType
//        for (int i=0;i<quoteNumbers.length;i++){
            CompanyInfoRequest request = new CompanyInfoRequest();
            request.sendV2(quoteNumbers,quoteNumbers1,new IResponseInfoCallback<F10V2Response>() {
                @Override
                public void callback(F10V2Response f10V2Response) {
                    try {
                        assertNotNull(f10V2Response.info);
                    } catch (AssertionError e) {
                        result.completeExceptionally(e);
                    }
                    JSONObject uploadObj = new JSONObject();
                    // TODO fill uploadObj with QuoteResponse value
                    F10V2Response mCompanyInfoResponse = (F10V2Response) f10V2Response;
                    try {
                         if(mCompanyInfoResponse.info!=null){
                             if (quoteNumbers1.equals("g")){
                                 uploadObj.put("LEGALREPR",mCompanyInfoResponse.info.get("LEGALREPR"));
                                 uploadObj.put("COREBUSINESS",mCompanyInfoResponse.info.get("COREBUSINESS"));
                                 uploadObj.put("PROVINCENAME",mCompanyInfoResponse.info.get("PROVINCENAME"));
                                 uploadObj.put("LISTINGDATE",mCompanyInfoResponse.info.get("LISTINGDATE"));
                                 uploadObj.put("BUSINESSSCOPE",mCompanyInfoResponse.info.get("BUSINESSSCOPE"));
                                 uploadObj.put("INDUNAMESW",mCompanyInfoResponse.info.get("INDUNAMESW"));
                                 uploadObj.put("REGCAPITAL",mCompanyInfoResponse.info.get("REGCAPITAL"));
                                 uploadObj.put("CHINAME",mCompanyInfoResponse.info.get("CHINAME"));
                                 uploadObj.put("REGADDRESS",mCompanyInfoResponse.info.get("REGADDRESS"));
                             }
                             if (quoteNumbers1.equals("d")){
                                 uploadObj.put("CHINAME",mCompanyInfoResponse.info.get("CHINAME"));
                                 uploadObj.put("SEENGNAME",mCompanyInfoResponse.info.get("SEENGNAME"));
                                 uploadObj.put("AUTHCAPSK",mCompanyInfoResponse.info.get("AUTHCAPSK"));
                                 uploadObj.put("LISTINGDATE",mCompanyInfoResponse.info.get("LISTINGDATE"));
                                 uploadObj.put("ISSUECAPSK",mCompanyInfoResponse.info.get("ISSUECAPSK"));
                                 uploadObj.put("CURNAME",mCompanyInfoResponse.info.get("CURNAME"));
                                 uploadObj.put("PARVALUE",mCompanyInfoResponse.info.get("PARVALUE"));
                                 uploadObj.put("DEBTBOARDLOT",mCompanyInfoResponse.info.get("DEBTBOARDLOT"));
                             }
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
                JSONObject resultObj = (JSONObject)result.get(timeout_ms, TimeUnit.MILLISECONDS);
                RunnerSetup.getInstance().getCollector().onTestResult(testcaseName, rule.getParam(), resultObj);
            } catch (Exception e) {
                throw new Exception(e);
            }
//        }
    }
}