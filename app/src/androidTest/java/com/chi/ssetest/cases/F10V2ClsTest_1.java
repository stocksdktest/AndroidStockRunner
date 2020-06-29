package com.chi.ssetest.cases;

import android.app.Activity;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import com.chi.ssetest.StockTestcase;
import com.chi.ssetest.StockTestcaseName;
import com.chi.ssetest.TestcaseException;
import com.chi.ssetest.protos.SetupConfig;
import com.chi.ssetest.setup.RunnerSetup;
import com.chi.ssetest.setup.TestcaseConfigRule;
import com.mitake.core.bean.log.ErrorInfo;
import com.mitake.core.request.F10Type;
import com.mitake.core.request.F10V2Request;
import com.mitake.core.response.F10V2Response;
import com.mitake.core.response.IResponseInfoCallback;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static junit.framework.TestCase.assertNotNull;

/**
 * Example local unit test, which will execute on the development machine (host).
 *财联社接口1
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
@StockTestcase(StockTestcaseName.F10V2CLSTEST_1)
public class F10V2ClsTest_1 {
    private static final StockTestcaseName testcaseName = StockTestcaseName.F10V2CLSTEST_1;
    private static SetupConfig.TestcaseConfig testcaseConfig;
    private static final int timeout_ms = 1000000;
    @BeforeClass
    public static void setup() throws Exception {
        Log.d("F10V2ClsTest_1", "Setup");
        testcaseConfig = RunnerSetup.getInstance().getTestcaseConfig(testcaseName);
        if (testcaseConfig == null ) {
            throw new Exception(String.format("Testcase(%s) setup failed, config is empty", testcaseName));
        }
    }

    @Rule
    public TestcaseConfigRule rule = new TestcaseConfigRule(testcaseConfig);
    @Test(timeout = timeout_ms)

    public void requestWork() throws Exception {
        Log.d("F10V2ClsTest_1", "requestWork");
        // TODO get custom args from param
        final String quoteNumbers = rule.getParam().optString("SYMBOL");
        final String quoteNumbers1 = rule.getParam().optString("PARAMS");
        final String quoteNumbers2 = rule.getParam().optString("REQUESTTYPE");
        final CompletableFuture result = new CompletableFuture<JSONObject>();
        F10V2Request request = new F10V2Request();
        request.sendCls(quoteNumbers,quoteNumbers1,quoteNumbers2,new IResponseInfoCallback<F10V2Response>() {
            @Override
            public void callback(F10V2Response f10V2Response) {
                if (quoteNumbers2.equals("/clsimportantnewslist")&&(quoteNumbers.equals("10")||quoteNumbers.equals("13"))){
                    try {
                        assertNotNull(f10V2Response.infos);
                    } catch (AssertionError e) {
                        //                        result.completeExceptionally(e);
                        result.complete(new JSONObject());
                    }
                }else {
                    try {
                        assertNotNull(f10V2Response.info);
                    } catch (AssertionError e) {
                        //                        result.completeExceptionally(e);
                        result.complete(new JSONObject());
                    }
                }
                JSONObject uploadObj = new JSONObject();
                HashMap<String,Object> info = f10V2Response.info;
                try {
                    switch (quoteNumbers2){
                        //电报列表
                        case F10Type.CLS_TELEGRAM_LIST:
                            if (info!=null){
                                uploadObj.put("Page",info.get("Page"));
                                uploadObj.put("PageNumber",info.get("PageNumber"));
                                List<HashMap<String,Object>> list= (List<HashMap<String, Object>>) info.get("List");
                                for (int i=0;i<list.size();i++){
                                    JSONObject uploadObj_1 = new JSONObject();
                                    uploadObj_1.put("ID",list.get(i).get("ID"));
                                    uploadObj_1.put("TITLE",list.get(i).get("TITLE"));
                                    uploadObj_1.put("CONTENT",list.get(i).get("CONTENT"));
                                    uploadObj_1.put("PUBDATE",list.get(i).get("PUBDATE"));
                                    uploadObj_1.put("RECOMMEND",list.get(i).get("RECOMMEND"));
                                    uploadObj_1.put("STOCKS",list.get(i).get("STOCKS"));
                                    uploadObj_1.put("TOPICS",list.get(i).get("TOPICS"));
                                    uploadObj_1.put("BRIEF",list.get(i).get("BRIEF"));
                                    uploadObj.put(String.valueOf(list.get(i).get("ID")),uploadObj_1);
                                }
                            }

                            break;
                        //要闻列表
                        case F10Type.CLS_IMPORTANT_NEWS_LIST:
                            if (quoteNumbers.equals("10")||quoteNumbers.equals("13")){
                                List<HashMap<String,Object>> list=f10V2Response.infos;
                                if (list!=null){
                                    for (int i=0;i<list.size();i++){
                                        JSONObject uploadObj_1 = new JSONObject();
                                        uploadObj_1.put("ID",list.get(i).get("ID"));
                                        uploadObj_1.put("TITLE",list.get(i).get("TITLE"));
                                        uploadObj_1.put("BRIEF",list.get(i).get("BRIEF"));
                                        uploadObj_1.put("IMG",list.get(i).get("IMG"));
                                        uploadObj_1.put("PUBDATE",list.get(i).get("PUBDATE"));
                                        uploadObj_1.put("SUBJECTID",list.get(i).get("SUBJECTID"));
                                        uploadObj_1.put("SUBJECTNAME",list.get(i).get("SUBJECTNAME"));
                                        uploadObj_1.put("PID",list.get(i).get("PID"));
                                        uploadObj_1.put("PID_ID",list.get(i).get("PID_ID"));
                                        uploadObj_1.put("STOCKS",list.get(i).get("STOCKS"));
                                        uploadObj_1.put("TOPICS",list.get(i).get("TOPICS"));
                                        uploadObj.put((String) list.get(i).get("ID"),uploadObj_1);
                                    }
                                }
                            }else {
                                if (info!=null){
                                    uploadObj.put("Page",info.get("Page"));
                                    uploadObj.put("PageNumber",info.get("PageNumber"));
                                    List<HashMap<String,Object>> list= (List<HashMap<String, Object>>) info.get("List");
                                    for (int i=0;i<list.size();i++){
                                        JSONObject uploadObj_1 = new JSONObject();
                                        uploadObj_1.put("ID",list.get(i).get("ID"));
                                        uploadObj_1.put("TITLE",list.get(i).get("TITLE"));
                                        uploadObj_1.put("BRIEF",list.get(i).get("BRIEF"));
                                        uploadObj_1.put("IMG",list.get(i).get("IMG"));
                                        uploadObj_1.put("PUBDATE",list.get(i).get("PUBDATE"));
                                        uploadObj_1.put("SUBJECTID",list.get(i).get("SUBJECTID"));
                                        uploadObj_1.put("SUBJECTNAME",list.get(i).get("SUBJECTNAME"));
                                        uploadObj_1.put("PID",list.get(i).get("PID"));
                                        uploadObj_1.put("PID_ID",list.get(i).get("PID_ID"));
                                        uploadObj_1.put("STOCKS",list.get(i).get("STOCKS"));
                                        uploadObj_1.put("TOPICS",list.get(i).get("TOPICS"));
                                        uploadObj.put((String) list.get(i).get("ID"),uploadObj_1);
                                    }
                                }
                            }
                            break;
                        //要闻详情
                        case F10Type.CLS_IMPORTANT_NEWS:
                            if (info!=null){
                                uploadObj.put("ID",info.get("ID"));
                                uploadObj.put("TITLE",info.get("TITLE"));
                                uploadObj.put("CONTENT",info.get("CONTENT"));
                                uploadObj.put("BRIEF",info.get("BRIEF"));
                                uploadObj.put("IMG",info.get("IMG"));
                                uploadObj.put("AUTHOR",info.get("AUTHOR"));
                                uploadObj.put("STOCKS",info.get("STOCKS"));
                                uploadObj.put("PUBDATE",info.get("PUBDATE"));
                                uploadObj.put("SUBJECTID",info.get("SUBJECTID"));
                                uploadObj.put("SUBJECTNAME",info.get("SUBJECTNAME"));
                                uploadObj.put("PID",info.get("PID"));
                                uploadObj.put("PID_ID",info.get("PID_ID"));
                                uploadObj.put("STOCKS",info.get("STOCKS"));
                                uploadObj.put("TOPICS",info.get("TOPICS"));
                            }
                            break;
                        //VIP列表
                        case F10Type.CLS_VIP_LIST:
                            if (info!=null){
                                uploadObj.put("Page",info.get("Page"));
                                uploadObj.put("PageNumber",info.get("PageNumber"));
                                List<HashMap<String,Object>> list= (List<HashMap<String, Object>>) info.get("List");
                                for (int i=0;i<list.size();i++){
                                    JSONObject uploadObj_1 = new JSONObject();
                                    uploadObj_1.put("ID",list.get(i).get("ID"));
                                    uploadObj_1.put("TITLE",list.get(i).get("TITLE"));
                                    uploadObj_1.put("BRIEF",list.get(i).get("BRIEF"));
                                    uploadObj_1.put("PUBDATE",list.get(i).get("PUBDATE"));
                                    uploadObj_1.put("STOCKS",list.get(i).get("STOCKS"));
                                    uploadObj_1.put("TOPICS",list.get(i).get("TOPICS"));
                                    uploadObj.put((String) list.get(i).get("ID"),uploadObj_1);
                                }
                            }
                            break;
                        //VIP详情
                        case F10Type.CLS_VIP:
                            if (info!=null){
                                uploadObj.put("ID",info.get("ID"));
                                uploadObj.put("TITLE",info.get("TITLE"));
                                uploadObj.put("CONTENT",info.get("CONTENT"));
                                uploadObj.put("PUBDATE",info.get("PUBDATE"));
                                uploadObj.put("BRIEF",info.get("BRIEF"));
                                uploadObj.put("STOCKS",info.get("STOCKS"));
                                uploadObj.put("TOPICS",info.get("TOPICS"));
                            }
                            break;
                        //风口内参列表
                        case F10Type.CLS_IN_PARAMS_LIST:
                            if (info!=null){
                                uploadObj.put("Page",info.get("Page"));
                                uploadObj.put("PageNumber",info.get("PageNumber"));
                                List<HashMap<String,Object>> list= (List<HashMap<String, Object>>) info.get("List");
                                for (int i=0;i<list.size();i++){
                                    JSONObject uploadObj_1 = new JSONObject();
                                    uploadObj_1.put("ID",list.get(i).get("ID"));
                                    uploadObj_1.put("NAME",list.get(i).get("NAME"));
                                    uploadObj_1.put("BRIEF",list.get(i).get("BRIEF"));
                                    uploadObj_1.put("PUBDATE",list.get(i).get("PUBDATE"));
                                    uploadObj_1.put("STOCKS",list.get(i).get("STOCKS"));
                                    uploadObj_1.put("TOPICS",list.get(i).get("TOPICS"));
                                    uploadObj.put((String) list.get(i).get("ID"),uploadObj_1);
                                }
                            }
                            break;
                        //风口内参详情
                        case F10Type.CLS_IN_PARAMS:
                            if (info!=null){
                                uploadObj.put("ID",info.get("ID"));
                                uploadObj.put("TITLE",info.get("TITLE"));
                                uploadObj.put("CONTENT",info.get("CONTENT"));
                                uploadObj.put("BRIEF",info.get("BRIEF"));
                                uploadObj.put("NAME",info.get("NAME"));
                                uploadObj.put("STOCKS",info.get("STOCKS"));
                                uploadObj.put("PUBDATE",info.get("PUBDATE"));
                                uploadObj.put("TOPICS",info.get("TOPICS"));
                            }
                            break;
                        //个股/自选资讯列表
                        case F10Type.CLS_STOCK_NEWS_LIST:
                            if (info!=null){
                                uploadObj.put("Page",info.get("Page"));
                                uploadObj.put("PageNumber",info.get("PageNumber"));
                                List<HashMap<String,Object>> list= (List<HashMap<String, Object>>) info.get("List");
                                for (int i=0;i<list.size();i++){
                                    JSONObject uploadObj_1 = new JSONObject();
                                    uploadObj_1.put("ID",list.get(i).get("ID"));
                                    uploadObj_1.put("TITLE",list.get(i).get("TITLE"));
                                    uploadObj_1.put("BRIEF",list.get(i).get("BRIEF"));
                                    uploadObj_1.put("PUBDATE",list.get(i).get("PUBDATE"));
                                    uploadObj_1.put("TRADING",list.get(i).get("TRADING"));
                                    uploadObj_1.put("ISPDF",list.get(i).get("ISPDF"));
                                    uploadObj.put((String) list.get(i).get("ID"),uploadObj_1);
                                }
                            }
                            break;
                        //个股/自选资讯详情
                        case F10Type.CLS_STOCK_NEWS:
                            if (info!=null){
                                uploadObj.put("ID",info.get("ID"));
                                uploadObj.put("TITLE",info.get("TITLE"));
                                uploadObj.put("CONTENT",info.get("CONTENT"));
                                uploadObj.put("BRIEF",info.get("BRIEF"));
                                uploadObj.put("STOCKS",info.get("STOCKS"));
                                uploadObj.put("PUBDATE",info.get("PUBDATE"));
                                uploadObj.put("TOPICS",info.get("TOPICS"));
                                uploadObj.put("PURL",info.get("PURL"));
                            }
                            break;
                        //风口列表
                        case F10Type.CLS_RECOMMEND_LIST:
                            if (info!=null){
                                uploadObj.put("Page",info.get("Page"));
                                uploadObj.put("PageNumber",info.get("PageNumber"));
                                List<HashMap<String,Object>> list= (List<HashMap<String, Object>>) info.get("List");
                                for (int i=0;i<list.size();i++){
                                    JSONObject uploadObj_1 = new JSONObject();
                                    uploadObj_1.put("ID",list.get(i).get("ID"));
                                    uploadObj_1.put("TITLE",list.get(i).get("TITLE"));
                                    uploadObj_1.put("BRIEF",list.get(i).get("BRIEF"));
                                    uploadObj_1.put("PUBDATE",list.get(i).get("PUBDATE"));
                                    uploadObj_1.put("STOCKS",list.get(i).get("STOCKS"));
                                    uploadObj_1.put("TOPICS",list.get(i).get("TOPICS"));
                                    uploadObj.put((String) list.get(i).get("ID"),uploadObj_1);
                                }
                            }
                            break;
                        //风口详情
                        case F10Type.CLS_RECOMMEND:
                            if (info!=null){
                                uploadObj.put("ID",info.get("ID"));
                                uploadObj.put("TITLE",info.get("TITLE"));
                                uploadObj.put("CONTENT",info.get("CONTENT"));
                                uploadObj.put("BRIEF",info.get("BRIEF"));
                                uploadObj.put("STOCKS",info.get("STOCKS"));
                                uploadObj.put("TOPICS",info.get("TOPICS"));
                                uploadObj.put("PUBDATE",info.get("PUBDATE"));
                            }
                            break;
                        //个股研报列表
                        case F10Type.CLS_STOCK_REPORT_LIST:
                            if (info!=null){
                                uploadObj.put("Page",info.get("Page"));
                                uploadObj.put("PageNumber",info.get("PageNumber"));
                                List<HashMap<String,Object>> list= (List<HashMap<String, Object>>) info.get("List");
                                for (int i=0;i<list.size();i++){
                                    JSONObject uploadObj_1 = new JSONObject();
                                    uploadObj_1.put("ID",list.get(i).get("ID"));
                                    uploadObj_1.put("TITLE",list.get(i).get("TITLE"));
                                    uploadObj_1.put("BRIEF",list.get(i).get("BRIEF"));
                                    uploadObj_1.put("PUBDATE",list.get(i).get("PUBDATE"));
                                    uploadObj_1.put("TRADING",list.get(i).get("TRADING"));
                                    uploadObj.put((String) list.get(i).get("ID"),uploadObj_1);
                                }
                            }
                            break;
                        //个股研报详情
                        case F10Type.CLS_STOCK_REPORT:
                            if (info!=null){
                                uploadObj.put("ID",info.get("ID"));
                                uploadObj.put("TITLE",info.get("TITLE"));
                                uploadObj.put("CONTENT",info.get("CONTENT"));
                                uploadObj.put("BRIEF",info.get("BRIEF"));
                                uploadObj.put("STOCKS",info.get("STOCKS"));
                                uploadObj.put("PUBDATE",info.get("PUBDATE"));
                                uploadObj.put("TOPICS",info.get("TOPICS"));
                            }
                            break;
                        //个股公告列表
                        case F10Type.CLS_STOCK_BULLETIN_LIST:
                            if (info!=null){
                                uploadObj.put("Page",info.get("Page"));
                                uploadObj.put("PageNumber",info.get("PageNumber"));
                                List<HashMap<String,Object>> list= (List<HashMap<String, Object>>) info.get("List");
                                for (int i=0;i<list.size();i++){
                                    JSONObject uploadObj_1 = new JSONObject();
                                    uploadObj_1.put("ID",list.get(i).get("ID"));
                                    uploadObj_1.put("TITLE",list.get(i).get("TITLE"));
                                    uploadObj_1.put("BRIEF",list.get(i).get("BRIEF"));
                                    uploadObj_1.put("PUBDATE",list.get(i).get("PUBDATE"));
                                    uploadObj_1.put("TRADING",list.get(i).get("TRADING"));
                                    uploadObj_1.put("ISPDF",list.get(i).get("ISPDF"));
                                    uploadObj.put((String) list.get(i).get("ID"),uploadObj_1);
                                }
                            }
                            break;
                        //个股公告详情
                        case F10Type.CLS_STOCK_BULLETIN:
                            if (info!=null){
                                uploadObj.put("ID",info.get("ID"));
                                uploadObj.put("TITLE",info.get("TITLE"));
                                uploadObj.put("CONTENT",info.get("CONTENT"));
                                uploadObj.put("BRIEF",info.get("BRIEF"));
                                uploadObj.put("STOCKS",info.get("STOCKS"));
                                uploadObj.put("PUBDATE",info.get("PUBDATE"));
                                uploadObj.put("TOPICS",info.get("TOPICS"));
                                uploadObj.put("URL",info.get("URL"));
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
            //                throw new Exception(e);
            throw new TestcaseException(e,rule.getParam());
        }
//        }
    }
}
