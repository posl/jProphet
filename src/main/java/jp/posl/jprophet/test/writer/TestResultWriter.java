package jp.posl.jprophet.test.writer;

import jp.posl.jprophet.test.result.TestResult;

public interface TestResultWriter {

    public void addTestResult(TestResult result);

    public void write();

}