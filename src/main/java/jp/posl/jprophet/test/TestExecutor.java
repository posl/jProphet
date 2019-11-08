package jp.posl.jprophet.test;

import jp.posl.jprophet.RepairConfiguration;
import jp.posl.jprophet.test.result.TestResult;


public interface TestExecutor {

    public TestResult exec(RepairConfiguration config);

} 