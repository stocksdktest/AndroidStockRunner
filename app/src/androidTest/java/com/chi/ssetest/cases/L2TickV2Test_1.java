package com.chi.ssetest.cases;

import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import com.chi.ssetest.StockTestcase;
import com.chi.ssetest.StockTestcaseName;
import com.chi.ssetest.protos.SetupConfig;
import com.chi.ssetest.setup.RunnerSetup;
import com.chi.ssetest.setup.TestcaseConfigRule;
import com.mitake.core.bean.TickItem;
import com.mitake.core.bean.log.ErrorInfo;
import com.mitake.core.request.L2TickRequestV2;
import com.mitake.core.request.TickRequest;
import com.mitake.core.response.IResponseInfoCallback;
import com.mitake.core.response.L2TickResponseV2;
import com.mitake.core.response.Response;
import com.mitake.core.response.TickResponse;

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
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
//L2分笔
@RunWith(AndroidJUnit4.class)
@StockTestcase(StockTestcaseName.L2TICKV2TEST_1)
public class L2TickV2Test_1 {
    private static final StockTestcaseName testcaseName = StockTestcaseName.L2TICKV2TEST_1;
    private static SetupConfig.TestcaseConfig testcaseConfig;
    final CompletableFuture result = new CompletableFuture<JSONObject>();
    private static JSONObject uploadObj = new JSONObject();
    private static List<JSONObject> items=new ArrayList<>();
    @BeforeClass
    public static void setup() throws Exception {
        Log.d("L2TickV2Test_1", "Setup");
        testcaseConfig = RunnerSetup.getInstance().getTestcaseConfig(testcaseName);
        if (testcaseConfig == null ) {
            throw new Exception(String.format("Testcase(%s) setup failed, config is empty", testcaseName));
        }
    }
    @Rule
    public TestcaseConfigRule rule = new TestcaseConfigRule(testcaseConfig);

    @Test(timeout = 5000)
    public void requestWork() throws Exception {
        Log.d("L2TickV2Test_1", "requestWork");
        // TODO get custom args from param
        final String []quoteNumbers = rule.getParam().optString("CODES", "").split(",");
        final String []Pages = rule.getParam().optString("PAGES", "").split(";");
        final String []SubTypes = rule.getParam().optString("SUBTYPES", "").split(",");

        for (int i=0;i<quoteNumbers.length;i++){
            L2Tickjk(quoteNumbers[i],Pages[i],SubTypes[i]);
            try {
                JSONObject resultObj = (JSONObject)result.get(5000, TimeUnit.MILLISECONDS);
                RunnerSetup.getInstance().getCollector().onTestResult(testcaseName,rule.getParam(), resultObj);
            } catch (Exception e) {
                throw new Exception(e);
            }
        }
    }
    private void L2Tickjk(final String id, String page, final String subtype) {
        L2TickRequestV2 request = new L2TickRequestV2();
        request.send(id,page,subtype, new IResponseInfoCallback() {
            @Override
            public void callback(Response response) {
                L2TickResponseV2 l2TickResponseV2 = (L2TickResponseV2) response;
                List<TickItem> list=l2TickResponseV2.tickItems;
                assertNotNull(l2TickResponseV2.tickItems);
                if (l2TickResponseV2.tickItems!=null){
                    for (int k=0;k<list.size();k++){
                        try {
                            JSONObject uploadObj_1 = new JSONObject();
                            uploadObj_1.put("code", id);
                            uploadObj_1.put("type", list.get(k).getTransactionStatus());
                            uploadObj_1.put("time", list.get(k).getTransactionTime());
                            uploadObj_1.put("tradeVolume", list.get(k).getSingleVolume());
                            uploadObj_1.put("tradePrice", list.get(k).getTransactionPrice());
                            items.add(uploadObj_1);
                            System.out.println(uploadObj_1.toString());
                        } catch (JSONException e) {
                            result.completeExceptionally(e);
                        }
                    }
//                    for (TickItem item : l2TickResponseV2.tickItems) {
//                        Log.d("StockUnittest", item.getTransactionTime()+"++++"+l2TickResponseV2.tickItems.size());
//                    }
                    if (l2TickResponseV2.tickItems.size()==100){
                        String[] st=l2TickResponseV2.headerParams.split(",");
                        if (Double.parseDouble(st[0])>Double.parseDouble(st[1])){
                            String page1=st[1]+",100,1";
                            L2Tickjk(id,page1,subtype);
                        }else {
                            String page2=st[0]+",100,1";
                            L2Tickjk(id,page2,subtype);
                        }
                    }else {
                        try {
                            uploadObj.put("items",new JSONArray(items));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        //解析输出JSON
                        try {
                            JSONArray jsonArray = uploadObj.getJSONArray("items");
                            for (int i=0;i<jsonArray.length();i++){
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                Log.d("data", String.valueOf(jsonObject));
//                            System.out.println(jsonObject.optString("code")+","+jsonObject.optString("datetime"));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        result.complete(uploadObj);
                    }
                }
            }
            @Override
            public void exception(ErrorInfo errorInfo) {
                result.completeExceptionally(new Exception(errorInfo.toString()));
            }
        });
    }
}
