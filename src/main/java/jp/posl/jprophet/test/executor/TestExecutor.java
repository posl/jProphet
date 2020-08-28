package jp.posl.jprophet.test.executor;

import java.util.List;

import jp.posl.jprophet.RepairConfiguration;
import jp.posl.jprophet.fl.spectrumbased.ExecutionTest;
import jp.posl.jprophet.test.result.TestExecutorResult;
import jp.posl.jprophet.test.result.UnitTestResult;

/**
 * 修正後のプロジェクトに対してテスト検証を行うインターフェース
 */
public interface TestExecutor {

    /**
     * テスト検証を行い、テスト結果を取得する
     * @param config 対象プロジェクトのconfig
     * @return テスト結果のリスト
     */
    public TestExecutorResult exec(RepairConfiguration config);

    default public TestExecutorResult exec(RepairConfiguration config, List<ExecutionTest> executionTests, String sourceFqn){
        return new TestExecutorResult(false, List.of(new UnitTestResult(false)));
    }

} 