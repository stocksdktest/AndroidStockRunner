package com.chi.ssetest.cases;


import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import com.chi.ssetest.StockTestcase;
import com.chi.ssetest.StockTestcaseName;
import com.chi.ssetest.protos.SetupConfig;
import com.chi.ssetest.setup.RunnerSetup;
import com.chi.ssetest.setup.TestcaseConfigRule;
import com.mitake.core.OHLCItem;
import com.mitake.core.QuoteItem;
import com.mitake.core.bean.log.ErrorInfo;
import com.mitake.core.request.ChartRequestV2;
import com.mitake.core.request.ChartType;
import com.mitake.core.request.QuoteRequest;
import com.mitake.core.response.ChartResponse;
import com.mitake.core.response.IResponseInfoCallback;
import com.mitake.core.response.QuoteResponse;
import com.mitake.core.response.Response;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertNotNull;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
//走势数据 方法二传入code补全
@RunWith(AndroidJUnit4.class)
@StockTestcase(StockTestcaseName.CHARTV2TEST_2)
public class ChartV2Test_2 {
    private static final StockTestcaseName testcaseName = StockTestcaseName.CHARTV2TEST_2;
    private static SetupConfig.TestcaseConfig testcaseConfig;

    @BeforeClass
    public static void setup() throws Exception {
        Log.d("ChartV2Test_2", "Setup");
        testcaseConfig = RunnerSetup.getInstance().getTestcaseConfig(testcaseName);
        if (testcaseConfig == null ) {
            throw new Exception(String.format("Testcase(%s) setup failed, config is empty", testcaseName));
        }
    }

    @Rule
    public TestcaseConfigRule rule = new TestcaseConfigRule(testcaseConfig);

    @Test(timeout = 5000)
    public void requestWork() throws Exception {
        Log.d("ChartV2Test_2", "requestWork");
        // TODO get custom args from param
        final String []quoteNumbers = rule.getParam().getString("CODES").split(",");
        final String []Types = rule.getParam().getString("Chart_Types").split(",");
//        ChartType
        final CompletableFuture result = new CompletableFuture<JSONObject>();

        for (int i=0;i<quoteNumbers.length;i++){
            final int a=i;
            ChartRequestV2 request = new ChartRequestV2();
            request.send(quoteNumbers[i],Types[i], new IResponseInfoCallback() {
                @Override
                public void callback(Response response) {
                    ChartResponse chartResponse = (ChartResponse) response;
                    assertNotNull(chartResponse.historyItems);
                    JSONObject uploadObj = new JSONObject();
                    // TODO fill uploadObj with QuoteResponse value
                    try {
                        uploadObj.put("fake_result", quoteNumbers);
//                    uploadObj.put
                    } catch (JSONException e) {
                        result.completeExceptionally(e);
                    }
                    for (OHLCItem item : chartResponse.historyItems) {
                        Log.d("StockUnittest", quoteNumbers[a]+item.datetime);
                    }
                    result.complete(uploadObj);
                }
                @Override
                public void exception(ErrorInfo errorInfo) {
                    result.completeExceptionally(new Exception(errorInfo.toString()));
                }
            });
            try {
                JSONObject resultObj = (JSONObject)result.get(5000, TimeUnit.MILLISECONDS);
                RunnerSetup.getInstance().getCollector().onTestResult(testcaseName, resultObj);
            } catch (Exception e) {
                throw new Exception(e);
            }
        }
    }
}
