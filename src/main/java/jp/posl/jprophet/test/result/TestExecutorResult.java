package jp.posl.jprophet.test.result;

import java.util.List;


public class TestExecutorResult {

    private final boolean isSuccess;
    private final List<TestResult> testResults;


    /**
     * @param isSuccess テスト検証が成功したか
     * @param testResults テスト検証をした結果のリスト
     */
    public TestExecutorResult(boolean isSuccess, List<TestResult> testResults) {
        this.isSuccess = isSuccess;
        this.testResults = testResults;
    }

    /**
     * テスト検証が成功したかを返す
     * @return 成功していればTrue, そうでなければFalse
     */
    public boolean getIsSuccess() {
        return this.isSuccess;
    } 

    /**
     * テスト検証をした結果のリストを返す
     * @return テスト検証をした結果のリスト
     */
    public List<TestResult> getTestResults() {
        return this.testResults;
    }

}