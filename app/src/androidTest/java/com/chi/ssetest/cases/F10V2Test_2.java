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
import com.mitake.core.bean.TickItem;
import com.mitake.core.bean.log.ErrorInfo;
import com.mitake.core.request.AddValueRequest;
import com.mitake.core.request.BankuaisortingRequest;
import com.mitake.core.request.CategoryType;
import com.mitake.core.request.CatequoteRequest;
import com.mitake.core.request.F10Type;
import com.mitake.core.request.F10V2Request;
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
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *財汇沪深盘后接口2
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
@StockTestcase(StockTestcaseName.F10V2TEST_2)
public class F10V2Test_2 {
        private static final StockTestcaseName testcaseName = StockTestcaseName.F10V2TEST_2;
    private static SetupConfig.TestcaseConfig testcaseConfig;
    private static final int timeout_ms = 1000000;
    @BeforeClass
    public static void setup() throws Exception {
        Log.d("F10V2Test_2", "Setup");
        testcaseConfig = RunnerSetup.getInstance().getTestcaseConfig(testcaseName);
        if (testcaseConfig == null ) {
            throw new Exception(String.format("Testcase(%s) setup failed, config is empty", testcaseName));
        }
    }
    // F10Type
    @Rule
    public TestcaseConfigRule rule = new TestcaseConfigRule(testcaseConfig);
    @Test(timeout = timeout_ms)
    public void requestWork() throws Exception {
        Log.d("F10V2Test_2", "requestWork");
        // TODO get custom args from param
        final String quoteNumbers = rule.getParam().optString("code");
        final String quoteNumbers1 = rule.getParam().optString("src");
        final String quoteNumbers2 = rule.getParam().optString("param");
        final String quoteNumbers3 = rule.getParam().optString("apiType");
        final CompletableFuture result = new CompletableFuture<JSONObject>();
        // F10Type
//        for (int i=0;i<quoteNumbers.length;i++){
        String code;
        if (quoteNumbers.equals("null")){
            code=null;
        }else {
            code=quoteNumbers;
        }
        String src;
        if (quoteNumbers1.equals("null")){
            src="g";
        }else {
            src=quoteNumbers1;
        }
            F10V2Request request = new F10V2Request();
            request.send(code,src,quoteNumbers2,quoteNumbers3,new IResponseInfoCallback<F10V2Response>() {
                @Override
                public void callback(F10V2Response f10V2Response) {
                    if (quoteNumbers3.equals("/exptperformance")){
                        try {
                            assertNotNull(f10V2Response.info);
                        } catch (AssertionError e) {
                            result.completeExceptionally(e);
                        }
                    }else {
                        try {
                            assertNotNull(f10V2Response.infos);
                        } catch (AssertionError e) {
                            result.completeExceptionally(e);
                        }
                    }
                    JSONObject uploadObj = new JSONObject();
                    HashMap<String,Object> info = f10V2Response.info;
                    List<HashMap<String,Object>> infos=f10V2Response.infos;
                    try {
                        switch (quoteNumbers3) {
                            //沪深股api----大事提醒---按时间
                            case F10Type.IMPORT_NOTICE_DATA:
                                for (int i=0;i<infos.size();i++){
                                    JSONObject uploadObj_1 = new JSONObject();
                                    uploadObj_1.put("REPTITLE",infos.get(i).get("REPTITLE"));
                                    uploadObj_1.put("TRADEDATE",infos.get(i).get("TRADEDATE"));
                                    uploadObj_1.put("TEXT",infos.get(i).get("TEXT"));
                                    uploadObj_1.put("ID",infos.get(i).get("ID"));
                                    uploadObj_1.put("ISPDF",infos.get(i).get("ISPDF"));
                                    uploadObj.put((String) infos.get(i).get("TRADEDATE"),uploadObj_1);
                                }
                                break;
                            //沪深股api----大事提醒---按标题
                            case F10Type.IMPORT_NOTICE_TITLE:
                                for (int i=0;i<infos.size();i++){
                                    JSONObject uploadObj_1 = new JSONObject();
                                    uploadObj_1.put("REPTITLE",infos.get(i).get("REPTITLE"));
                                    uploadObj_1.put("TRADEDATE",infos.get(i).get("TRADEDATE"));
                                    uploadObj_1.put("TEXT",infos.get(i).get("TEXT"));
                                    uploadObj_1.put("ID",infos.get(i).get("ID"));
                                    uploadObj_1.put("ISPDF",infos.get(i).get("ISPDF"));
                                    uploadObj.put((String) infos.get(i).get("TRADEDATE"),uploadObj_1);
                                }
                                break;
                            //大事提醒-业绩预告
                            case F10Type.EXPT_PERFORMANCE:
                                uploadObj.put("Page",info.get("Page"));
                                uploadObj.put("PageNumber",info.get("PageNumber"));
                                List<HashMap<String,Object>> items2= (List<HashMap<String,Object>>) info.get("List");
                                if (items2!=null){
                                    for (int i=0;i<items2.size();i++){
                                        JSONObject uploadObj_1 = new JSONObject();
                                        uploadObj_1.put("SESNAME",items2.get(i).get("SESNAME"));
                                        uploadObj_1.put("PUBLISHDATE",items2.get(i).get("PUBLISHDATE"));
                                        uploadObj_1.put("RETAMAXPROFITSMK",items2.get(i).get("RETAMAXPROFITSMK"));
                                        uploadObj_1.put("RETAMAXPROFITSINC",items2.get(i).get("RETAMAXPROFITSINC"));
                                        uploadObj.put((String) items2.get(i).get("PUBLISHDATE"),uploadObj_1);
                                    }
                                }
                                break;
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
                RunnerSetup.getInstance().getCollector().onTestResult(testcaseName, rule.getParam(),resultObj);
            } catch (Exception e) {
                throw new Exception(e);
            }
//        }
    }
}