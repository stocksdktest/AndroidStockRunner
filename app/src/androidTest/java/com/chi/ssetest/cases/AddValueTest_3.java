package com.chi.ssetest.cases;

import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import com.chi.ssetest.StockTestcase;
import com.chi.ssetest.StockTestcaseName;
import com.chi.ssetest.TestcaseException;
import com.chi.ssetest.protos.SetupConfig;
import com.chi.ssetest.setup.RunnerSetup;
import com.chi.ssetest.setup.TestcaseConfigRule;
import com.mitake.core.AddValueModel;
import com.mitake.core.bean.log.ErrorInfo;
import com.mitake.core.request.AddValueRequest;
import com.mitake.core.response.AddValueResponse;
import com.mitake.core.response.IResponseInfoCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertNotNull;

/**
 * Example local unit test, which will execute on the development machine (host).
 *增值指标 3
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
@StockTestcase(StockTestcaseName.ADDVALUETEST_3)
public class AddValueTest_3 {
    private static final StockTestcaseName testcaseName = StockTestcaseName.ADDVALUETEST_3;
    private static SetupConfig.TestcaseConfig testcaseConfig;
    private static final int timeout_ms = 1000000;
    @BeforeClass
    public static void setup() throws Exception {
        Log.d(" AddValueTest_3", "Setup");
        testcaseConfig = RunnerSetup.getInstance().getTestcaseConfig(testcaseName);
        if (testcaseConfig == null ) {
            throw new Exception(String.format("Testcase(%s) setup failed, config is empty", testcaseName));
        }
    }
    @Rule
    public TestcaseConfigRule rule = new TestcaseConfigRule(testcaseConfig);
    @Test(timeout = timeout_ms)
    public void requestWork() throws Exception {
        Log.d("AddValueTest_3", "requestWork");
        // TODO get custom args from param
        final String quoteNumbers = rule.getParam().optString("CODE");
        final String quoteNumbers1 = rule.getParam().optString("SUBTYPE");
        final String quoteNumbers2 = rule.getParam().optString("MARKET");
        final String[] quoteNumbers3 = rule.getParam().optString("FIELDS").split(",");//自定义字段
        final CompletableFuture result = new CompletableFuture<JSONObject>();
//        for (int i=0;i<quoteNumbers.length;i++){
        int[] addValueColumns = new int[quoteNumbers3.length];
        if (quoteNumbers3[0].equals("null")){
            addValueColumns=null;
        }else {
            for (int i=0;i<quoteNumbers3.length;i++){
                addValueColumns[i]=Integer.parseInt(quoteNumbers3[i]);
            }
        }
            AddValueRequest request = new AddValueRequest();
            request.send(quoteNumbers,quoteNumbers2,quoteNumbers1,addValueColumns, new IResponseInfoCallback<AddValueResponse>() {
                @Override
                public void callback(AddValueResponse addValueResponse) {
                    try {
                        assertNotNull(addValueResponse.list);
                    } catch (AssertionError e) {
                        //                        result.completeExceptionally(e);
                        result.complete(new JSONObject());
                    }
                    JSONObject uploadObj = new JSONObject();
                    // TODO fill uploadObj with QuoteResponse value
                    try {
                        if(addValueResponse.list!=null){
                            int i=0;
                            for (AddValueModel item : addValueResponse.list) {
                                JSONObject uploadObj_1 = new JSONObject();
                                uploadObj_1.put("code",item.code);
                                uploadObj_1.put("date",item.date);
                                uploadObj_1.put("time",item.time);
                                uploadObj_1.put("ultraLargeBuyVolume",item.ultraLargeBuyVolume=="一"? "-" : item.ultraLargeBuyVolume);
                                uploadObj_1.put("ultraLargeSellVolume",item.ultraLargeSellVolume=="一"? "-" : item.ultraLargeSellVolume);
                                uploadObj_1.put("ultraLargeBuyAmount",item.ultraLargeBuyAmount);
                                uploadObj_1.put("ultraLargeSellAmount",item.ultraLargeSellAmount);
                                uploadObj_1.put("largeBuyVolume",item.largeBuyVolume=="一"? "-" : item.largeBuyVolume);
                                uploadObj_1.put("largeSellVolume",item.largeSellVolume=="一"? "-" : item.largeSellVolume);
                                uploadObj_1.put("largeBuyAmount",item.largeBuyAmount);
                                uploadObj_1.put("largeSellAmount",item.largeSellAmount);
                                uploadObj_1.put("mediumBuyVolume",item.mediumBuyVolume=="一"? "-" : item.mediumBuyVolume);
                                uploadObj_1.put("mediumSellVolume",item.mediumSellVolume=="一"? "-" : item.mediumSellVolume);
                                uploadObj_1.put("mediumBuyAmount",item.mediumBuyAmount);
                                uploadObj_1.put("mediumSellAmount",item.mediumSellAmount);
                                uploadObj_1.put("smallBuyVolume",item.smallBuyVolume=="一"? "-" : item.smallBuyVolume);
                                uploadObj_1.put("smallSellVolume",item.smallSellVolume=="一"? "-" : item.smallSellVolume);
                                uploadObj_1.put("smallBuyAmount",item.smallBuyAmount);
                                uploadObj_1.put("smallSellAmount",item.smallSellAmount);
                                uploadObj_1.put("ultraLargeNetInflow",item.ultraLargeNetInflow);
                                uploadObj_1.put("largeNetInflow",item.largeNetInflow);
                                uploadObj_1.put("netCapitalInflow",item.netCapitalInflow);
                                uploadObj_1.put("mediumNetInflow",item.mediumNetInflow);
                                uploadObj_1.put("smallNetInflow",item.smallNetInflow);

                                List<String> fundsInflows=new ArrayList<>();
                                if (item.fundsInflows!=null&&item.fundsInflows.length>0){
                                    for (int j=0;j<item.fundsInflows.length;j++){
                                        fundsInflows.add(item.fundsInflows[j]);
                                    }
                                    uploadObj_1.put("fundsInflows",new JSONArray(fundsInflows));
                                }else {
                                    uploadObj_1.put("fundsInflows",item.fundsInflows);
                                }

                                List<String> fundsOutflows=new ArrayList<>();
                                if (item.fundsOutflows!=null&&item.fundsOutflows.length>0){
                                    for (int j=0;j<item.fundsOutflows.length;j++){
                                        fundsOutflows.add(item.fundsOutflows[j]);
                                    }
                                    uploadObj_1.put("fundsOutflows",new JSONArray(fundsOutflows));
                                }else {
                                    uploadObj_1.put("fundsOutflows",item.fundsOutflows);
                                }

                                uploadObj_1.put("ultraLargeDiffer",item.ultraLargeDiffer);
                                uploadObj_1.put("largeDiffer",item.largeDiffer);
                                uploadObj_1.put("mediumDiffer",item.mediumDiffer);
                                uploadObj_1.put("smallDiffer",item.smallDiffer);
                                uploadObj_1.put("largeBuyDealCount",item.largeBuyDealCount);
                                uploadObj_1.put("largeSellDealCount",item.largeSellDealCount);
                                uploadObj_1.put("dealCountMovingAverage",item.dealCountMovingAverage);
                                uploadObj_1.put("buyCount",item.buyCount);
                                uploadObj_1.put("sellCount",item.sellCount);
                                uploadObj_1.put("BBD",item.BBD);
                                uploadObj_1.put("BBD5",item.BBD5);
                                uploadObj_1.put("BBD10",item.BBD10);
                                uploadObj_1.put("DDX",item.DDX);
                                uploadObj_1.put("DDX5",item.DDX5);
                                uploadObj_1.put("DDX10",item.DDX10);
                                uploadObj_1.put("DDY",item.DDY);
                                uploadObj_1.put("DDY5",item.DDY5);
                                uploadObj_1.put("DDY10",item.DDY10);
                                uploadObj_1.put("DDZ",item.DDZ);
                                uploadObj_1.put("RatioBS",item.RatioBS);
                                List<String> othersFundsInflows=new ArrayList<>();
                                if (item.othersFundsInflows!=null&&item.othersFundsInflows.length>0){
                                    for (int j=0;j<item.othersFundsInflows.length;j++){
                                        othersFundsInflows.add(item.othersFundsInflows[j]);
                                    }
                                    uploadObj_1.put("othersFundsInflows",new JSONArray(othersFundsInflows));
                                }else {
                                    uploadObj_1.put("othersFundsInflows",item.othersFundsInflows);
                                }

                                List<String> othersFundsOutflows=new ArrayList<>();
                                if (item.othersFundsOutflows!=null&&item.othersFundsOutflows.length>0){
                                    for (int j=0;j<item.othersFundsOutflows.length;j++){
                                        othersFundsOutflows.add(item.othersFundsOutflows[j]);
                                    }
                                    uploadObj_1.put("othersFundsOutflows",new JSONArray(othersFundsOutflows));
                                }else {
                                    uploadObj_1.put("othersFundsOutflows",item.othersFundsOutflows);
                                }

                                uploadObj_1.put("fiveMinutesChangeRate",item.fiveMinutesChangeRate);
                                uploadObj_1.put("largeOrderNumB",item.largeOrderNumB);
                                uploadObj_1.put("largeOrderNumS",item.largeOrderNumS);
                                uploadObj_1.put("bigOrderNumB",item.bigOrderNumB);
                                uploadObj_1.put("bigOrderNumS",item.bigOrderNumS);
                                uploadObj_1.put("midOrderNumB",item.midOrderNumB);
                                uploadObj_1.put("midOrderNumS",item.midOrderNumS);
                                uploadObj_1.put("smallOrderNumB",item.smallOrderNumB);
                                uploadObj_1.put("smallOrderNumS",item.smallOrderNumS);
                                uploadObj_1.put("mainforceMoneyNetInflow5",item.mainforceMoneyNetInflow5);
                                uploadObj_1.put("mainforceMoneyNetInflow10",item.mainforceMoneyNetInflow10);
                                uploadObj_1.put("mainforceMoneyNetInflow20",item.mainforceMoneyNetInflow20);
                                uploadObj_1.put("ratioMainforceMoneyNetInflow5",item.ratioMainforceMoneyNetInflow5);
                                uploadObj_1.put("ratioMainforceMoneyNetInflow10",item.ratioMainforceMoneyNetInflow10);
                                uploadObj_1.put("ratioMainforceMoneyNetInflow20",item.ratioMainforceMoneyNetInflow20);
                                uploadObj.put(String.valueOf((i+1)),uploadObj_1);
                                i++;
                            }
                        }
//                        Log.d("data", String.valueOf(uploadObj));
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
                //                throw new Exception(e);
                throw new TestcaseException(e,rule.getParam());
            }
//        }
    }
}