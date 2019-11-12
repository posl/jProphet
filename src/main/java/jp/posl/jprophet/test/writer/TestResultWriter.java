package jp.posl.jprophet.test.writer;

import jp.posl.jprophet.patch.PatchCandidate;
import jp.posl.jprophet.test.result.TestResult;

public interface TestResultWriter {

    public void addTestResult(TestResult result, PatchCandidate patch);

    public void write();

}