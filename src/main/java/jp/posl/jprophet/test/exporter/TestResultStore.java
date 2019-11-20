package jp.posl.jprophet.test.exporter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.posl.jprophet.patch.PatchCandidate;
import jp.posl.jprophet.test.result.TestResult;


/**
 * テストの結果を一時的に保管するクラス
 */
public class TestResultStore {

    private final Map<TestResult, PatchCandidate> patchResults;
    
    /**
     * コンストラクタ
     */
    public TestResultStore() {
        patchResults = new HashMap<TestResult, PatchCandidate>();
    }

    /**
     * テスト結果と適用した修正パッチを追加する
     * @param results テスト結果
     * @param patch 修正パッチ
     */
    public void addTestResults(List<TestResult> testResults, PatchCandidate patch) {
        for(TestResult testResult : testResults) {
            patchResults.put(testResult, patch);
        } 
    }

    /**
     * @return the patchResults
     */
    public Map<TestResult, PatchCandidate> getPatchResults() {
        return patchResults;
    }

}