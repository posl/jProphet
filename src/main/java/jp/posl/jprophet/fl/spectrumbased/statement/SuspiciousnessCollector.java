package jp.posl.jprophet.fl.spectrumbased.statement;

import java.util.List;
import java.util.ArrayList;
import jp.posl.jprophet.fl.spectrumbased.strategy.Coefficient;
import jp.posl.jprophet.fl.spectrumbased.coverage.TestResults;
import jp.posl.jprophet.fl.Suspiciousness;

/**
 * ステートメント(行)ごとの疑惑値の計算
 */
public class SuspiciousnessCollector {

    private List<Suspiciousness> suspiciousnesses;
    final private int fileNum;
    final private int numberOfSuccessedTests;
    final private int numberOfFailedTests;
    final private TestResults testResults;
    final private Coefficient coefficient;

    /**
     * テスト結果(カバレッジ情報を含む)から,全てのテスト対象ファイルの各行ごとの疑惑値を計算
     * @param testResults テスト結果
     */
    public SuspiciousnessCollector(TestResults testResults, Coefficient coefficient){
        this.fileNum = testResults.getTestResult(0).getCoverages().size();
        this.suspiciousnesses = new ArrayList<Suspiciousness>();
        this.numberOfSuccessedTests = testResults.getSuccessedTestResults().size();
        this.numberOfFailedTests = testResults.getFailedTestResults().size();
        this.testResults = testResults;
        this.coefficient = coefficient;
    }

    /**
     * テスト対象のソースコードの各行に対して疑惑値の計算を行う
     * 計算式はSuspiciousnessStrategyの中
     */
    public void exec(){
        System.out.println("fileNum: " + fileNum);

        for (int i = 0; i < fileNum; i++){
            
            //TODO 1つめのメソッドのカバレッジ結果からソースファイルの行数とファイル名を取得している. 他にいい取得方法はないか
            final int lineLength = testResults.getTestResult(0).getCoverages().get(i).getLength();
            final String testName = testResults.getTestResult(0).getCoverages().get(i).getName();

            System.out.println("testName: " + testName);
            System.out.println("lineLength: " + lineLength);
            for (int k = 1; k <= lineLength; k++){
                StatementStatus statementStatus = new StatementStatus(testResults, k, i);
                Suspiciousness suspiciousness = new Suspiciousness(testName, k, coefficient.calculate(statementStatus, numberOfSuccessedTests, numberOfFailedTests));
                this.suspiciousnesses.add(suspiciousness);
                System.out.println("add suspiciousness");
            }
        }
    }

    public List<Suspiciousness> getSuspiciousnesses(){
        return this.suspiciousnesses;
    }

}