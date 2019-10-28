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

import static com.mitake.core.model.F10KeyToChinese.EPSBASIC;
import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *財汇沪深盘后接口4
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
@StockTestcase(StockTestcaseName.F10V2TEST_4)
public class F10V2Test_4 {
    private static final StockTestcaseName testcaseName = StockTestcaseName.F10V2TEST_4;
    private static SetupConfig.TestcaseConfig testcaseConfig;
    @BeforeClass
    public static void setup() throws Exception {
        Log.d("F10V2Test_4", "Setup");
        testcaseConfig = RunnerSetup.getInstance().getTestcaseConfig(testcaseName);
        if (testcaseConfig == null ) {
            throw new Exception(String.format("Testcase(%s) setup failed, config is empty", testcaseName));
        }
    }
    // F10Type
    @Rule
    public TestcaseConfigRule rule = new TestcaseConfigRule(testcaseConfig);
    @Test(timeout = 5000)
    public void requestWork() throws Exception {
        Log.d("F10V2Test_4", "requestWork");
        // TODO get custom args from param
        final String quoteNumbers = rule.getParam().optString("code");
        final String quoteNumbers1 = rule.getParam().optString("src");
        final String quoteNumbers2 = rule.getParam().optString("param");
        final String quoteNumbers3 = rule.getParam().optString("apiType");
        final String quoteNumbers4 = rule.getParam().optString("part");
        final String quoteNumbers5 = rule.getParam().optString("type");
        final CompletableFuture result = new CompletableFuture<JSONObject>();
        // F10Type
//        for (int i=0;i<quoteNumbers.length;i++){
        String src;
        if (quoteNumbers1.equals("null")){
            src="g";
        }else {
            src=quoteNumbers1;
        }
        String part;
        if (quoteNumbers4.equals("null")){
            part=null;
        }else {
            part=quoteNumbers4;
        }
        String type;
        if (quoteNumbers5.equals("null")){
            type=null;
        }else {
            type=quoteNumbers5;
        }
            F10V2Request request = new F10V2Request();
            request.send(quoteNumbers,src,quoteNumbers2,quoteNumbers3,part,type,new IResponseInfoCallback<F10V2Response>() {
                @Override
                public void callback(F10V2Response f10V2Response) {
                    if (quoteNumbers3.equals("/newsinteractive")){
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
                        if (quoteNumbers3.equals("/newsinteractive")){
                            //董秘问答
                            List<JSONObject> NEWS_INTEARACTIVE=new ArrayList<>();
                            JSONObject uploadObj_6 = new JSONObject();
                            uploadObj_6.put("Page",info.get("Page"));
                            uploadObj_6.put("PageNumber",info.get("PageNumber"));
                            List<HashMap<String,Object>> items1= (List<HashMap<String,Object>>) info.get("List");
                            if (items1!=null){
                                List<JSONObject> list=new ArrayList<>();
                                for (int i=0;i<items1.size();i++){
                                    JSONObject uploadObj_7 = new JSONObject();
                                    uploadObj_7.put("TRADING",items1.get(i).get("TRADING"));
                                    uploadObj_7.put("SESNAME",items1.get(i).get("SESNAME"));
                                    uploadObj_7.put("PROBLEM",items1.get(i).get("PROBLEM"));
                                    uploadObj_7.put("QUESTIONTIME",items1.get(i).get("QUESTIONTIME"));
                                    uploadObj_7.put("REPLY",items1.get(i).get("REPLY"));
                                    uploadObj_7.put("ANSWERTIME",items1.get(i).get("ANSWERTIME"));
                                    uploadObj_7.put("NEWSSOURCE",items1.get(i).get("NEWSSOURCE"));
                                    uploadObj_7.put("INTERACTIVEID",items1.get(i).get("INTERACTIVEID"));
                                    list.add(uploadObj_7);
                                }
                                uploadObj_6.put("list",new JSONArray(list));
                            }else {
                                uploadObj_6.put("list",items1);
                            }
                            NEWS_INTEARACTIVE.add(uploadObj_6);
                            uploadObj.put("NEWS_INTEARACTIVE",new JSONArray(NEWS_INTEARACTIVE));
                        }else {
                            List<JSONObject> IMPORT_NOTICE_TITLE=new ArrayList<>();
                            for (int i=0;i<infos.size();i++){
                                JSONObject uploadObj_6 = new JSONObject();
                                uploadObj_6.put("REPTITLE",infos.get(i).get("REPTITLE"));
                                uploadObj_6.put("TRADEDATE",infos.get(i).get("TRADEDATE"));
                                uploadObj_6.put("TEXT",infos.get(i).get("TEXT"));
                                uploadObj_6.put("ID",infos.get(i).get("ID"));
                                uploadObj_6.put("ISPDF",infos.get(i).get("ISPDF"));
                                IMPORT_NOTICE_TITLE.add(uploadObj_6);
                            }
                            uploadObj.put("IMPORT_NOTICE_TITLE",new JSONArray(IMPORT_NOTICE_TITLE));
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
                RunnerSetup.getInstance().getCollector().onTestResult(testcaseName, rule.getParam(),resultObj);
            } catch (Exception e) {
                throw new Exception(e);
            }
//        }
    }
}