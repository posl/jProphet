package jp.posl.jprophet.test;

import java.util.List;

import jp.posl.jprophet.RepairConfiguration;
import jp.posl.jprophet.test.result.TestResult;


public interface TestExecutor {

    public List<TestResult> exec(RepairConfiguration config);

} 