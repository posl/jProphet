package jp.posl.jprophet.test.executor;

import java.util.List;

import jp.posl.jprophet.RepairConfiguration;
import jp.posl.jprophet.test.result.TestResult;

/**
 * 修正後のプロジェクトに対してテスト検証を行うインターフェース
 */
public interface TestExecutor {

    /**
     * テスト検証を行い、テスト結果を取得する
     * @param config 対象プロジェクトのconfig
     * @return テスト結果のリスト
     */
    public List<TestResult> exec(RepairConfiguration config);

} 