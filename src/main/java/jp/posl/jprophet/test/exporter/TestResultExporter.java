package jp.posl.jprophet.test.exporter;

import java.util.List;

import jp.posl.jprophet.patch.PatchCandidate;
import jp.posl.jprophet.test.result.TestResult;
import jp.posl.jprophet.test.result.TestResultStore;

/**
 *テスト結果と修正パッチを受け取り、テキストファイルとして書き込みを行うクラス
 */
public interface TestResultExporter {

    
    /**
     * テキストファイルとして書き込みを行う
     */
    public void export(TestResultStore resultStore);

}