package com.chi.ssetest.cases;

import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import com.chi.ssetest.protos.SetupConfig;
import com.chi.ssetest.setup.RunnerSetup;
import com.chi.ssetest.StockTestcase;
import com.chi.ssetest.StockTestcaseName;
import com.chi.ssetest.setup.TestcaseConfigRule;
import com.mitake.core.AddValueModel;
import com.mitake.core.QuoteItem;
import com.mitake.core.bean.log.ErrorInfo;
import com.mitake.core.parser.FQItem;
import com.mitake.core.request.AddValueRequest;
import com.mitake.core.request.OHLCSubRequest;
import com.mitake.core.request.QuoteRequest;
import com.mitake.core.response.AddValueResponse;
import com.mitake.core.response.IResponseInfoCallback;
import com.mitake.core.response.OHLCSubR;
import com.mitake.core.response.OHLCSubResponse;
import com.mitake.core.response.OHLCSubResponseV2;
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
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

/*K线复权信息
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
@StockTestcase(StockTestcaseName.OHLCSUBTEST_1)
public class OHLCSubTest_1 {
    private static final StockTestcaseName testcaseName = StockTestcaseName.OHLCSUBTEST_1;
    private static SetupConfig.TestcaseConfig testcaseConfig;
    @BeforeClass
    public static void setup() throws Exception {
        Log.d("OHLCSubTest_1", "Setup");
        testcaseConfig = RunnerSetup.getInstance().getTestcaseConfig(testcaseName);
        if (testcaseConfig == null ) {
            throw new Exception(String.format("Testcase(%s) setup failed, config is empty", testcaseName));
        }
    }
    @Rule
    public TestcaseConfigRule rule = new TestcaseConfigRule(testcaseConfig);
    @Test(timeout = 5000)
    public void requestWork() throws Exception {
        Log.d("OHLCSubTest_1", "requestWork");
        // TODO get custom args from param
        final String quoteNumbers = rule.getParam().optString("code");
        final CompletableFuture result = new CompletableFuture<JSONObject>();
//        for (int i=0;i<quoteNumbers.length;i++){
            OHLCSubRequest request = new OHLCSubRequest();
            request.sendV2(quoteNumbers, new IResponseInfoCallback<OHLCSubResponseV2>() {
                @Override
                public void callback(OHLCSubResponseV2 ohlcSubResponse) {
                    assertNotNull(ohlcSubResponse.fq);
                    CopyOnWriteArrayList<FQItem> list=ohlcSubResponse.fq;
                    JSONObject uploadObj = new JSONObject();
                    try {
                        uploadObj.put("stockCode",ohlcSubResponse.stockCode);
                        List<JSONObject> fqlist=new ArrayList<>();
                        for (int i=0;i<list.size();i++) {
                            JSONObject uploadObj_1 = new JSONObject();
                            uploadObj_1.put("dateTime", list.get(i).dateTime);
                            uploadObj_1.put("increasePrice", list.get(i).increasePrice);
                            uploadObj_1.put("allotmentPrice", list.get(i).allotmentPrice);
                            uploadObj_1.put("bonusAmount", list.get(i).bonusAmount);
                            uploadObj_1.put("bonusProportion", list.get(i).bonusProportion);
                            uploadObj_1.put("increaseProportion", list.get(i).increaseProportion);
                            uploadObj_1.put("increaseVolume", list.get(i).increaseVolume);
                            uploadObj_1.put("allotmentProportion", list.get(i).allotmentProportion);
                            uploadObj_1.put("dateTime", list.get(i).dateTime);
                            uploadObj_1.put("dateTime", list.get(i).dateTime);
                            fqlist.add(uploadObj_1);
                        }
                        uploadObj.put("fq",new JSONArray(fqlist));
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