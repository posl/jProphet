package jp.posl.jprophet.test.writer;

import java.util.ArrayList;
import java.util.List;

import jp.posl.jprophet.test.result.TestResult;

public class UnitTestResultWriter implements TestResultWriter {

    private final List<TestResult> results;

    public UnitTestResultWriter() {
        this.results = new ArrayList<TestResult>();
    }

    @Override
    public void addTestResult(TestResult result) {
        //results.add(result);
    }


    @Override
    public void write() {
        System.out.println("write");
    }


}