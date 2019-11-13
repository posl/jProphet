package jp.posl.jprophet.test.writer;

import java.util.List;

import jp.posl.jprophet.patch.PatchCandidate;
import jp.posl.jprophet.test.result.TestResult;

/**
 *テスト結果と修正パッチを受け取り、テキストファイルとして書き込みを行うクラス
 */
public interface TestResultWriter {

    /**
     * テスト結果と適用した修正パッチを追加する
     * @param results　テスト結果
     * @param patch　修正パッチ
     */
    public void addTestResult(List<TestResult> results, PatchCandidate patch);

    /**
     * テキストファイルとして書き込みを行う
     */
    public void write();

}