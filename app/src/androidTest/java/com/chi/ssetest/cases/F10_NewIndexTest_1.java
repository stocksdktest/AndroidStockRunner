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
import com.mitake.core.NewIndex;
import com.mitake.core.QuoteItem;
import com.mitake.core.bean.MorePriceItem;
import com.mitake.core.bean.log.ErrorInfo;
import com.mitake.core.request.AddValueRequest;
import com.mitake.core.request.BankuaisortingRequest;
import com.mitake.core.request.CategoryType;
import com.mitake.core.request.CatequoteRequest;
import com.mitake.core.request.MorePriceRequest;
import com.mitake.core.request.NewIndexRequest;
import com.mitake.core.request.QuoteRequest;
import com.mitake.core.request.offer.OfferQuoteSort;
import com.mitake.core.response.AddValueResponse;
import com.mitake.core.response.BankuaiRankingResponse;
import com.mitake.core.response.Bankuaisorting;
import com.mitake.core.response.BankuaisortingResponse;
import com.mitake.core.response.CatequoteResponse;
import com.mitake.core.response.IResponseInfoCallback;
import com.mitake.core.response.MorePriceResponse;
import com.mitake.core.response.NewIndexResponse;
import com.mitake.core.response.QuoteResponse;
import com.mitake.core.response.Response;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *最新指标
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
@StockTestcase(StockTestcaseName.F10_NEWINDEXTEST_1)
public class F10_NewIndexTest_1 {
    private static final StockTestcaseName testcaseName = StockTestcaseName.F10_NEWINDEXTEST_1;
    private static SetupConfig.TestcaseConfig testcaseConfig;
    @BeforeClass
    public static void setup() throws Exception {
        Log.d("F10_NewIndexTest_1", "Setup");
        testcaseConfig = RunnerSetup.getInstance().getTestcaseConfig(testcaseName);
        if (testcaseConfig == null ) {
            throw new Exception(String.format("Testcase(%s) setup failed, config is empty", testcaseName));
        }
    }
    @Rule
    public TestcaseConfigRule rule = new TestcaseConfigRule(testcaseConfig);
    @Test(timeout = 5000)
    public void requestWork() throws Exception {
        Log.d("F10_NewIndexTest_1", "requestWork");
        // TODO get custom args from param
        final String quoteNumbers = rule.getParam().optString("code");
        final String quoteNumbers1 = rule.getParam().optString("src");
        final CompletableFuture result = new CompletableFuture<JSONObject>();
        //CategoryType
//        for (int i=0;i<quoteNumbers.length;i++){
            NewIndexRequest request = new NewIndexRequest();
            request.sendV2(quoteNumbers,quoteNumbers1,new IResponseInfoCallback<NewIndexResponse>() {
                @Override
                public void callback(NewIndexResponse newIndexResponse) {
                    assertNotNull(newIndexResponse.info);
                    JSONObject uploadObj = new JSONObject();
                    // TODO fill uploadObj with QuoteResponse value
                    NewIndex list = newIndexResponse.info;
                    try {
                        uploadObj.put("netProfitCutParentComYOY",list.netProfitCutParentComYOY);
                        uploadObj.put("netProfitCutParentCom",list.netProfitCutParentCom);
                        uploadObj.put("cutBasicEPS",list.cutBasicEPS);
                        uploadObj.put("annuROE",list.annuROE);
                        uploadObj.put("netProfitCutParentCom",list.netProfitCutParentCom);
                        uploadObj.put("basicEPS",list.basicEPS);
                        uploadObj.put("totalShare",list.totalShare);
                        uploadObj.put("BVPS_",list.BVPS_);
                        uploadObj.put("totalShareL",list.totalShareL);
                        uploadObj.put("reservePS",list.reservePS);
                        uploadObj.put("REPTITLE_",list.REPTITLE_);
                        uploadObj.put("netCashFlowOperPS",list.netCashFlowOperPS);
                        uploadObj.put("grossProfitMargin",list.grossProfitMargin);
                        uploadObj.put("netProfitParentComYOY",list.netProfitParentComYOY);
                        uploadObj.put("retainedEarningPS",list.retainedEarningPS);
                        uploadObj.put("operRevenueYOY",list.operRevenueYOY);
                        uploadObj.put("operRevenue",list.operRevenue);
                    } catch (JSONException e) {
                        result.completeExceptionally(e);
                    }Log.d("data", String.valueOf(uploadObj));
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