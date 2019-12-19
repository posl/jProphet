package jp.posl.jprophet.test.result;

import java.util.List;


/**
 * TestExecutorによるテスト検証の結果を格納するクラス
 */
public class TestExecutorResult {

    private final boolean canEndRepair;
    private final List<TestResult> testResults;


    /**
     * コンストラクタ
     * @param canEndRepair テスト検証が成功したか
     * @param testResults テスト検証をした結果のリスト
     */
    public TestExecutorResult(boolean canEndRepair, List<TestResult> testResults) {
        this.canEndRepair = canEndRepair;
        this.testResults = testResults;
    }

    /**
     * テスト検証が成功したかを返す
     * @return 成功していればTrue, そうでなければFalse
     */
    public boolean canEndRepair() {
        return this.canEndRepair;
    } 

    /**
     * テスト検証をした結果のリストを返す
     * @return テスト検証をした結果のリスト
     */
    public List<TestResult> getTestResults() {
        return this.testResults;
    }

}