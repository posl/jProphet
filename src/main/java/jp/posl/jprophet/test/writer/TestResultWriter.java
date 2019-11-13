package jp.posl.jprophet.test.writer;

import java.util.List;

import jp.posl.jprophet.patch.PatchCandidate;
import jp.posl.jprophet.test.result.TestResult;

public interface TestResultWriter {

    public void addTestResult(List<TestResult> results, PatchCandidate patch);

    public void write();

}