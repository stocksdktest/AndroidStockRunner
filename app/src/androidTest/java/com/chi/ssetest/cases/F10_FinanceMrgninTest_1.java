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
import com.mitake.core.f10request.FinanceMrgninRequest;
import com.mitake.core.request.AddValueRequest;
import com.mitake.core.request.AssetAllocationRequest;
import com.mitake.core.request.BankuaisortingRequest;
import com.mitake.core.request.CategoryType;
import com.mitake.core.request.CatequoteRequest;
import com.mitake.core.request.CompanyInfoRequest;
import com.mitake.core.request.FundBasicRequest;
import com.mitake.core.request.ImportantnoticeRequest;
import com.mitake.core.request.IndustryPortfolioRequest;
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
 *沪深---融资融券--分市场提供最近交易日1
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
@StockTestcase(StockTestcaseName.F10_FINANCEMRGNINTEST_1)
public class F10_FinanceMrgninTest_1 {
    private static final StockTestcaseName testcaseName = StockTestcaseName.F10_FINANCEMRGNINTEST_1;
    private static SetupConfig.TestcaseConfig testcaseConfig;
    private static final int timeout_ms = 1000000;
    @BeforeClass
    public static void setup() throws Exception {
        Log.d("F10_FinanceMrgninTest_1", "Setup");
        testcaseConfig = RunnerSetup.getInstance().getTestcaseConfig(testcaseName);
        if (testcaseConfig == null ) {
            throw new Exception(String.format("Testcase(%s) setup failed, config is empty", testcaseName));
        }
    }
    @Rule
    public TestcaseConfigRule rule = new TestcaseConfigRule(testcaseConfig);
    @Test(timeout = timeout_ms)
    public void requestWork() throws Exception {
        Log.d("F10_FinanceMrgninTest_1", "requestWork");
        // TODO get custom args from param
        final String quoteNumbers = rule.getParam().optString("code");
        final String quoteNumbers1 = rule.getParam().optString("src");
        final String quoteNumbers2 = rule.getParam().optString("param");
        final String quoteNumbers3 = rule.getParam().optString("part");
        final CompletableFuture result = new CompletableFuture<JSONObject>();
//        for (int i=0;i<quoteNumbers.length;i++){
        String src;
        if (quoteNumbers1.equals("null")){
            src="g";
        }else {
            src=quoteNumbers1;
        }
        String part;
        if (quoteNumbers3.equals("null")){
            part=null;
        }else {
            part=quoteNumbers3;
        }
            FinanceMrgninRequest request = new FinanceMrgninRequest();
            request.sendSubMarket(quoteNumbers,src,quoteNumbers2,part,new IResponseInfoCallback<F10V2Response>() {
                @Override
                public void callback(F10V2Response f10V2Response) {
                    try {
                        assertNotNull(f10V2Response.infos);
                    } catch (AssertionError e) {
                        result.completeExceptionally(e);
                    }
                    JSONObject uploadObj = new JSONObject();
                    List<HashMap<String,Object>> infos = f10V2Response.infos;
                    try {
                        uploadObj.put("pageNumber",f10V2Response.pageNumber);
                        uploadObj.put("page",f10V2Response.page);
                        for (int i=0;i<infos.size();i++){
                            JSONObject uploadObj_1 = new JSONObject();
                            uploadObj_1.put("TRADEDATE",infos.get(i).get("TRADEDATE"));
                            uploadObj_1.put("TRADING",infos.get(i).get("TRADING"));
                            uploadObj_1.put("FINBALANCE",infos.get(i).get("FINBALANCE"));
                            uploadObj_1.put("FINBUYAMT",infos.get(i).get("FINBUYAMT"));
                            uploadObj_1.put("FINREPAYAMT",infos.get(i).get("FINREPAYAMT"));
                            uploadObj_1.put("FINROEBUY",infos.get(i).get("FINROEBUY"));
                            uploadObj_1.put("MRGGBAL",infos.get(i).get("MRGGBAL"));
                            uploadObj_1.put("MRGNRESQTY",infos.get(i).get("MRGNRESQTY"));
                            uploadObj_1.put("MRGNSELLAMT",infos.get(i).get("MRGNSELLAMT"));
                            uploadObj_1.put("MRGNREPAYAMT",infos.get(i).get("MRGNREPAYAMT"));
                            uploadObj_1.put("MRGNROESELL",infos.get(i).get("MRGNROESELL"));
                            uploadObj_1.put("FINMRGHBAL",infos.get(i).get("FINMRGHBAL"));
                            uploadObj_1.put("FINMRGNBAL",infos.get(i).get("FINMRGNBAL"));
                            uploadObj.put(String.valueOf(i+1),uploadObj_1);
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