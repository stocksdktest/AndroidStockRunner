package com.chi.ssetest.cases;

import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import com.chi.ssetest.TestcaseException;
import com.chi.ssetest.protos.SetupConfig;
import com.chi.ssetest.setup.RunnerSetup;
import com.chi.ssetest.StockTestcase;
import com.chi.ssetest.StockTestcaseName;
import com.chi.ssetest.setup.TestcaseConfigRule;
import com.mitake.core.bean.log.ErrorInfo;
import com.mitake.core.bean.quote.MarketUpDownItem;
import com.mitake.core.request.quote.MarketUpDownRequest;
import com.mitake.core.response.IResponseInfoCallback;
import com.mitake.core.response.quote.MarketUpDownResponse;

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

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *沪深当日涨跌统计数据
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
@StockTestcase(StockTestcaseName.MARKETUPDOWNTEST_1)
public class MarketUpDownTest_1 {
    private static final StockTestcaseName testcaseName = StockTestcaseName.MARKETUPDOWNTEST_1;
    private static SetupConfig.TestcaseConfig testcaseConfig;
    private static final int timeout_ms = 100000000;
    @BeforeClass
    public static void setup() throws Exception {
        Log.d(" MorePriceSampleTest1", "Setup");
        testcaseConfig = RunnerSetup.getInstance().getTestcaseConfig(testcaseName);
        if (testcaseConfig == null ) {
            throw new Exception(String.format("Testcase(%s) setup failed, config is empty", testcaseName));
        }
    }
    @Rule
    public TestcaseConfigRule rule = new TestcaseConfigRule(testcaseConfig);
    @Test(timeout = timeout_ms)
    public void requestWork() throws Exception {
        Log.d(" MorePriceSampleTest1", "requestWork");
        // TODO get custom args from param
        final String quoteNumbers = rule.getParam().optString("");
        final CompletableFuture result = new CompletableFuture<JSONObject>();
        //CategoryType
//        for (int i=0;i<quoteNumbers.length;i++){
            MarketUpDownRequest request = new MarketUpDownRequest();
            request.send(new IResponseInfoCallback<MarketUpDownResponse>() {
                @Override
                public void callback(MarketUpDownResponse marketUpDownResponse) {
                    try {
                        assertNotNull(marketUpDownResponse.upDownItem);
                    } catch (AssertionError e) {
                        //                        result.completeExceptionally(e);
                        result.complete(new JSONObject());
                    }
                    JSONObject uploadObj = new JSONObject();
                    MarketUpDownItem upDownItem = marketUpDownResponse.upDownItem;
                    try {
                        if(upDownItem!=null){
                            uploadObj.put("tTime",upDownItem.tTime);
                            uploadObj.put("tUp",upDownItem.tUp);
                            uploadObj.put("tDown",upDownItem.tDown);
                            uploadObj.put("tEqual",upDownItem.tEqual);
                            uploadObj.put("tLimitUp",upDownItem.tLimitUp);
                            uploadObj.put("tLimitDown",upDownItem.tLimitDown);
                            uploadObj.put("yTime",upDownItem.yTime);
                            uploadObj.put("yUp",upDownItem.yUp);
                            uploadObj.put("yDown",upDownItem.yDown);
                            uploadObj.put("yEqual",upDownItem.yEqual);
                            uploadObj.put("yLimitUp",upDownItem.yLimitUp);
                            uploadObj.put("yLimitDown",upDownItem.yLimitDown);

                            List<Integer> list=new ArrayList<>();
                            for (int i=0;i<upDownItem.list.size();i++){
                                list.add(upDownItem.list.get(i));
                            }
                            uploadObj.put("list",new JSONArray(list));
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