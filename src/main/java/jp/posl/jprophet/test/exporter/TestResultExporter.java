package jp.posl.jprophet.test.exporter;


/**
 *テスト結果と修正パッチを受け取り、テキストファイルとして書き込みを行うクラス
 */
public interface TestResultExporter {

    
    /**
     * テキストファイルとして書き込みを行う
     */
    public void export(TestResultStore resultStore);

}