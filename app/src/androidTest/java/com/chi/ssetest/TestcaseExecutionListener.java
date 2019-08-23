package com.chi.ssetest;

import android.util.Log;

import com.chi.ssetest.setup.RunnerSetup;

import org.junit.runner.Description;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;

public class TestcaseExecutionListener extends RunListener {
    private TestResultCollector collector;

    public void testRunStarted(Description description) throws Exception {
        collector = RunnerSetup.getInstance().getCollector();
        Log.d("TestcaseExecutionListener","Number of tests to execute: " + description.testCount());
    }

    public void testRunFinished(Result result) throws Exception {
        collector.afterAllTests(result);
        Log.d("TestcaseExecutionListener","Number of tests executed: " + result.getRunCount());
    }

    public void testStarted(Description description) throws Exception {
        collector.onTestStart(description);
        Log.d("TestcaseExecutionListener","Starting: " + description.getMethodName());
    }

    public void testFinished(Description description) throws Exception {
        collector.onTestSuccess(description);
        Log.d("TestcaseExecutionListener","Finished: " + description.getMethodName());
    }

    public void testFailure(Failure failure) throws Exception {
        collector.onTestError(failure);
        Log.d("TestcaseExecutionListener","Failed: " + failure.getDescription().getMethodName());
    }

    public void testAssumptionFailure(Failure failure) {
        collector.onTestError(failure);
        Log.d("TestcaseExecutionListener","Failed: " + failure.getDescription().getMethodName());
    }

    public void testIgnored(Description description) throws Exception {
        Log.d("TestcaseExecutionListener","Ignored: " + description.getMethodName());
    }
}
