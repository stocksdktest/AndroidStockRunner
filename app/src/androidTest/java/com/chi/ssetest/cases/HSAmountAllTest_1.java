package com.chi.ssetest.cases;

import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import com.chi.ssetest.StockTestcase;
import com.chi.ssetest.StockTestcaseName;
import com.chi.ssetest.TestcaseException;
import com.chi.ssetest.protos.SetupConfig;
import com.chi.ssetest.setup.RunnerSetup;
import com.chi.ssetest.setup.TestcaseConfigRule;
import com.mitake.core.bean.HKTQuotaItem;
import com.mitake.core.bean.log.ErrorInfo;
import com.mitake.core.request.HSAmountAllRequest;
import com.mitake.core.response.HSAmountAllResponse;
import com.mitake.core.response.IResponseInfoCallback;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertNotNull;

/**
 * Example local unit test, which will execute on the development machine (host).
 *港股通额度统计接口
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
@StockTestcase(StockTestcaseName.HSAMOUNTALLTEST_1)
public class HSAmountAllTest_1 {
    private static final StockTestcaseName testcaseName = StockTestcaseName.HSAMOUNTALLTEST_1;
    private static SetupConfig.TestcaseConfig testcaseConfig;
    private static final int timeout_ms = 1000000;
    @BeforeClass
    public static void setup() throws Exception {
        Log.d("HSAmountAllTest_1", "Setup");
        testcaseConfig = RunnerSetup.getInstance().getTestcaseConfig(testcaseName);
        if (testcaseConfig == null ) {
            throw new Exception(String.format("Testcase(%s) setup failed, config is empty", testcaseName));
        }
    }
    @Rule
    public TestcaseConfigRule rule = new TestcaseConfigRule(testcaseConfig);
    @Test(timeout = timeout_ms)
    public void requestWork() throws Exception {
        Log.d("HSAmountAllTest_1", "requestWork");
        // TODO get custom args from param
        final String quoteNumbers = rule.getParam().optString("");
        final CompletableFuture result = new CompletableFuture<JSONObject>();
        //CategoryType
//        for (int i=0;i<quoteNumbers.length;i++){
            HSAmountAllRequest request = new HSAmountAllRequest();
            request.send(new IResponseInfoCallback<HSAmountAllResponse>() {
                @Override
                public void callback(HSAmountAllResponse hsAmountAllResponse) {
                    try {
                        assertNotNull(hsAmountAllResponse.hktQuotaItem);
                    } catch (AssertionError e) {
                        //                        result.completeExceptionally(e);
                        result.complete(new JSONObject());
                    }
                    JSONObject uploadObj = new JSONObject();
                    HKTQuotaItem list = hsAmountAllResponse.hktQuotaItem;
                    // TODO fill uploadObj with QuoteResponse value
                    try {
                        if(list!=null){
                            uploadObj.put("hgtInitialQuotaS",list.hgtInitialQuotaS);
                            uploadObj.put("hgtRemainingQuotaS",list.hgtRemainingQuotaS);
                            uploadObj.put("hgtQuotaStatusS",list.hgtQuotaStatusS);
                            uploadObj.put("hgtTotalBuyAmountS",list.hgtTotalBuyAmountS);
                            uploadObj.put("hgtTotalSellAmountS",list.hgtTotalSellAmountS);
                            uploadObj.put("hgtBuySellTotalAmountS",list.hgtBuySellTotalAmountS);
                            uploadObj.put("hgtInitialQuotaN",list.hgtInitialQuotaN);
                            uploadObj.put("hgtRemainingQuotaN",list.hgtRemainingQuotaN);
                            uploadObj.put("hgtTotalBuyAmountN",list.hgtTotalBuyAmountN);
                            uploadObj.put("hgtTotalSellAmountN",list.hgtTotalSellAmountN);
                            uploadObj.put("hgtBuySellTotalAmountN",list.hgtBuySellTotalAmountN);
                            uploadObj.put("sgtInitialQuotaS",list.sgtInitialQuotaS);
                            uploadObj.put("sgtRemainingQuotaS",list.sgtRemainingQuotaS);
                            uploadObj.put("sgtQuotaStatusS",list.sgtQuotaStatusS);
                            uploadObj.put("sgtTotalBuyAmountS",list.sgtTotalBuyAmountS);
                            uploadObj.put("sgtTotalSellAmountS",list.sgtTotalSellAmountS);
                            uploadObj.put("sgtBuySellTotalAmountS",list.sgtBuySellTotalAmountS);
                            uploadObj.put("sgtInitialQuotaN",list.sgtInitialQuotaN);
                            uploadObj.put("sgtRemainingQuotaN",list.sgtRemainingQuotaN);
                            uploadObj.put("sgtTotalBuyAmountN",list.sgtTotalBuyAmountN);
                            uploadObj.put("sgtTotalSellAmountN",list.sgtTotalSellAmountN);
                            uploadObj.put("sgtBuySellTotalAmountN",list.sgtBuySellTotalAmountN);
                        }
                    } catch (JSONException e) {
                        result.completeExceptionally(e);
                    }
//                    Log.d("data", String.valueOf(uploadObj));
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