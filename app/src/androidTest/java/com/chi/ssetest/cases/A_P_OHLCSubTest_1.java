package com.chi.ssetest.cases;

import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import com.chi.ssetest.StockTestcase;
import com.chi.ssetest.StockTestcaseName;
import com.chi.ssetest.TestcaseException;
import com.chi.ssetest.protos.SetupConfig;
import com.chi.ssetest.setup.RunnerSetup;
import com.chi.ssetest.setup.TestcaseConfigRule;
import com.mitake.core.bean.log.ErrorInfo;
import com.mitake.core.parser.FQItem;
import com.mitake.core.request.OHLCSubRequest;
import com.mitake.core.response.IResponseInfoCallback;
import com.mitake.core.response.OHLCSubResponseV2;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertNotNull;

/*K线复权信息
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
@StockTestcase(StockTestcaseName.A_P_OHLCSubTest_1)
public class A_P_OHLCSubTest_1 {
    private static final StockTestcaseName testcaseName = StockTestcaseName.A_P_OHLCSubTest_1;
    private static SetupConfig.TestcaseConfig testcaseConfig;
    private static final int timeout_ms = 100000000;
    @BeforeClass
    public static void setup() throws Exception {
        Log.d("A_P_OHLCSubTest_1", "Setup");
        testcaseConfig = RunnerSetup.getInstance().getTestcaseConfig(testcaseName);
        if (testcaseConfig == null ) {
            throw new Exception(String.format("Testcase(%s) setup failed, config is empty", testcaseName));
        }
    }
    @Rule
    public TestcaseConfigRule rule = new TestcaseConfigRule(testcaseConfig);
    @Test(timeout = timeout_ms)
    public void requestWork() throws Exception {
        Log.d("A_P_OHLCSubTest_1", "requestWork");
        // TODO get custom args from param
        final String quoteNumbers = rule.getParam().optString("CODE_A");
        final CompletableFuture result = new CompletableFuture<JSONObject>();
//        for (int i=0;i<quoteNumbers.length;i++){
            OHLCSubRequest request = new OHLCSubRequest();
            request.sendV2(quoteNumbers, new IResponseInfoCallback<OHLCSubResponseV2>() {
                @Override
                public void callback(OHLCSubResponseV2 ohlcSubResponse) {
                    try {
                        assertNotNull(ohlcSubResponse.fq);
                    } catch (AssertionError e) {
                        //                        result.completeExceptionally(e);
                        result.complete(new JSONObject());
                    }
                    CopyOnWriteArrayList<FQItem> list=ohlcSubResponse.fq;
                    JSONObject uploadObj = new JSONObject();
                    try {
                        if(list!=null){
                            for (int i=0;i<list.size();i++) {
                                System.out.println("+++++");
                                System.out.println(list.get(i));
                                JSONObject uploadObj_1 = new JSONObject();
                                uploadObj_1.put("datetime", list.get(i).dateTime);
                                uploadObj_1.put("increasePrice", list.get(i).increasePrice == null ? "0.000" : String.format("%.3f",Float.parseFloat(list.get(i).increasePrice)));
                                uploadObj_1.put("allotmentPrice", list.get(i).allotmentPrice == null ? "0.000" : String.format("%.3f",Float.parseFloat(list.get(i).allotmentPrice)));
                                uploadObj_1.put("bonusAmount", list.get(i).bonusAmount == null ? "0.000" : String.format("%.3f",Float.parseFloat(list.get(i).bonusAmount)));
                                if(list.get(i).increaseProportion == null){
                                    uploadObj_1.put("bonusProportion", list.get(i).bonusProportion == null ? "0.000" : String.format("%.3f",Float.parseFloat(list.get(i).bonusProportion)));
                                }else {
                                    Float increaseProportion= Float.valueOf(list.get(i).increaseProportion);
                                    String bonusProportion11= list.get(i).bonusProportion == null ?  "0.000" :list.get(i).bonusProportion;
                                    Float bonusProportion= Float.valueOf(bonusProportion11);
                                    uploadObj_1.put("bonusProportion", String.format("%.3f",(increaseProportion+bonusProportion)));
                                }
                                //需处理
                                uploadObj_1.put("increaseProportion", "0.000");

                                uploadObj_1.put("increaseVolume", list.get(i).increaseVolume == null ? "0.000" : String.format("%.3f",Float.parseFloat(list.get(i).increaseVolume)));
                                uploadObj_1.put("allotmentProportion", list.get(i).allotmentProportion == null ? "0.000" : String.format("%.3f",Float.parseFloat(list.get(i).allotmentProportion)));
//                            Log.d("data", String.valueOf(uploadObj_1));
                                uploadObj.put( list.get(i).dateTime,uploadObj_1);
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
                RunnerSetup.getInstance().getCollector().onTestResult(testcaseName, rule.getParam(),resultObj);
            } catch (Exception e) {
                //                throw new Exception(e);
                throw new TestcaseException(e,rule.getParam());
            }
//        }
    }
}