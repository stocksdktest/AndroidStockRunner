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
import com.mitake.core.Importantnotice;
import com.mitake.core.MainFinaIndexHas;
import com.mitake.core.NewShareDetail;
import com.mitake.core.NewShareItem;
import com.mitake.core.NewShareList;
import com.mitake.core.NewsDetailItem;
import com.mitake.core.QuoteItem;
import com.mitake.core.StockNewsDetailItem;
import com.mitake.core.StockShareInfo;
import com.mitake.core.TopLiquidShareHolder;
import com.mitake.core.bean.MorePriceItem;
import com.mitake.core.bean.log.ErrorInfo;
import com.mitake.core.request.AddValueRequest;
import com.mitake.core.request.BankuaisortingRequest;
import com.mitake.core.request.CategoryType;
import com.mitake.core.request.CatequoteRequest;
import com.mitake.core.request.ImportantnoticeRequest;
import com.mitake.core.request.MainFinaDataNasRequest;
import com.mitake.core.request.MainFinaIndexNasRequest;
import com.mitake.core.request.MorePriceRequest;
import com.mitake.core.request.NewShareDetailRequest;
import com.mitake.core.request.NewShareListRequest;
import com.mitake.core.request.NewsRequest;
import com.mitake.core.request.QuoteRequest;
import com.mitake.core.request.StockNewsRequest;
import com.mitake.core.request.StockShareInfoRequest;
import com.mitake.core.request.TopLiquidShareHolderRequest;
import com.mitake.core.request.offer.OfferQuoteSort;
import com.mitake.core.response.AddValueResponse;
import com.mitake.core.response.BankuaiRankingResponse;
import com.mitake.core.response.Bankuaisorting;
import com.mitake.core.response.BankuaisortingResponse;
import com.mitake.core.response.CatequoteResponse;
import com.mitake.core.response.IResponseInfoCallback;
import com.mitake.core.response.ImportantnoticeResponse;
import com.mitake.core.response.MainFinaIndexNasResponse;
import com.mitake.core.response.MorePriceResponse;
import com.mitake.core.response.NewShareDetailResponse;
import com.mitake.core.response.NewShareListResponse;
import com.mitake.core.response.NewsResponse;
import com.mitake.core.response.QuoteResponse;
import com.mitake.core.response.Response;
import com.mitake.core.response.StockNewsResponse;
import com.mitake.core.response.StockShareInfoResponse;
import com.mitake.core.response.TopLiquidShareHolderResponse;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *新股详情
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
@StockTestcase(StockTestcaseName.F10_NEWSHAREDETAILTEST_1)
public class F10_NewShareDetailTest_1 {
    private static final StockTestcaseName testcaseName = StockTestcaseName.F10_NEWSHAREDETAILTEST_1;
    private static SetupConfig.TestcaseConfig testcaseConfig;
    private static final int timeout_ms = 1000000;
    @BeforeClass
    public static void setup() throws Exception {
        Log.d("F10_NewShareDetailTest_1", "Setup");
        testcaseConfig = RunnerSetup.getInstance().getTestcaseConfig(testcaseName);
        if (testcaseConfig == null ) {
            throw new Exception(String.format("Testcase(%s) setup failed, config is empty", testcaseName));
        }
    }
    @Rule
    public TestcaseConfigRule rule = new TestcaseConfigRule(testcaseConfig);
    @Test(timeout = timeout_ms)
    public void requestWork() throws Exception {
        Log.d("F10_NewShareDetailTest_1", "requestWork");
        // TODO get custom args from param
        final String quoteNumbers = rule.getParam().optString("CODE");
        final String quoteNumbers1 = rule.getParam().optString("SRC");
        final CompletableFuture result = new CompletableFuture<JSONObject>();
//        for (int i=0;i<quoteNumbers.length;i++){
            NewShareDetailRequest request = new NewShareDetailRequest();
            request.sendv2(quoteNumbers,quoteNumbers1,new IResponseInfoCallback<NewShareDetailResponse>() {
                @Override
                public void callback(NewShareDetailResponse newShareDetailResponse) {
                    try {
                        assertNotNull(newShareDetailResponse.info);
                    } catch (AssertionError e) {
                        //                        result.completeExceptionally(e);
                        result.complete(new JSONObject());
                    }
                    JSONObject uploadObj = new JSONObject();
                    NewShareDetail  list = newShareDetailResponse.info;
                    try {
                        if (list!=null){
                            uploadObj.put("applyCode",list.getApplyCode());
                            uploadObj.put("secuabbr",list.getSecuabbr());
                            uploadObj.put("tradingCode",list.getTradingCode());
                            uploadObj.put("peaIssue",list.getPeaIssue());
                            uploadObj.put("succResultNoticeDate",list.getSuccResultNoticeDate());
                            uploadObj.put("issuePrice",list.getIssuePrice());
                            uploadObj.put("capplyShare",list.getCapplyShare());
                            uploadObj.put("allotrateon",list.getAllotrateon());
                            uploadObj.put("listingDate",list.getListingDate());
                            uploadObj.put("bookStartDateOn",list.getBookStartDateOn());
                            uploadObj.put("issueSharePlan",list.getIssueSharePlan());
                            uploadObj.put("issueShareOnPlan",list.getIssueShareOnPlan());
                            uploadObj.put("issueShare",list.getIssueShare());
                            uploadObj.put("issueShareOn",list.getIssueShareOn());
                            uploadObj.put("capplyPrice",list.getCapplyPrice());
                            uploadObj.put("boradName",list.getBoradName());
                            uploadObj.put("comProfile",list.getComProfile());
                            uploadObj.put("refundDateOn",list.getRefundDateOn());
                            uploadObj.put("issueAllotnOn",list.getIssueAllotnOn());
                            uploadObj.put("businessScope",list.getBusinessScope());
                            uploadObj.put("leadUnderwriter",list.getLeadUnderwriter());
                            uploadObj.put("issuePricePlan",list.getIssuePricePlan() == null ? "-" :list.getIssuePricePlan());
                            uploadObj.put("capplyPricePlan",list.getCapplyPricePlan());
                            uploadObj.put("capplySharePlan",list.getCapplySharePlan());
                            uploadObj.put("newTotRaiseAmt",list.getNewTotRaiseAmt());
                            uploadObj.put("newNetRaiseAmt",list.getNewNetRaiseAmt());
                            uploadObj.put("keyCode",list.getKeyCode());
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
                //                throw new Exception(e);
                throw new TestcaseException(e,rule.getParam());
            }
//        }
    }
}
