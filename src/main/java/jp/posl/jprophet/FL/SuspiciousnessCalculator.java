package jp.posl.jprophet.FL;

import java.util.List;
import java.util.ArrayList;

public class SuspiciousnessCalculator {

    private List<Suspiciousness> suspiciousnessList;
    final private int fileNum;
    final private int numberOfSuccessedTests;
    final private int numberOfFailedTests;
    final private TestResults testResults;

    /**
     * テスト結果(カバレッジ情報を含む)から,全てのテスト対象ファイルの各行ごとの疑惑値を計算
     * @param testResults テスト結果
     */
    public SuspiciousnessCalculator(TestResults testResults){
        this.fileNum = testResults.getTestResult(0).getCoverages().size();
        this.suspiciousnessList = new ArrayList<Suspiciousness>();
        this.numberOfSuccessedTests = testResults.getSuccessedTestResults().size();
        this.numberOfFailedTests = testResults.getFailedTestResults().size();
        this.testResults = testResults;

    }

    /**
     * テスト対象のソースコードの各行に対して疑惑値の計算を行う
     * 計算式はSuspiciousnessStrategyの中
     */
    public void exec(){
        SuspiciousnessStrategy suspiciousnessStrategy = new SuspiciousnessStrategy(numberOfSuccessedTests, numberOfFailedTests);

        for (int i = 0; i < fileNum; i++){
            
            //TODO 1つめのメソッドのカバレッジ結果からソースファイルの行数とファイル名を取得している. 他にいい取得方法はないか
            final int lineLength = testResults.getTestResult(0).getCoverages().get(i).getLength();
            final String testName = testResults.getTestResult(0).getCoverages().get(i).getName();
            
            for (int k = 1; k <= lineLength; k++){
                StatementStatus statementStatus = new StatementStatus(testResults, k, i);
                suspiciousnessStrategy.setStatementStatus(statementStatus);
                Suspiciousness suspiciousness = new Suspiciousness(testName, k, suspiciousnessStrategy.jaccard());
                this.suspiciousnessList.add(suspiciousness);
            }
        }
    }

    public List<Suspiciousness> getSuspiciousnessList(){
        return this.suspiciousnessList;
    }

}