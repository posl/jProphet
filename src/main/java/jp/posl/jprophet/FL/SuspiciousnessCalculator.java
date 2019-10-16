package jp.posl.jprophet.FL;

import java.util.List;
import java.util.ArrayList;

public class SuspiciousnessCalculator {

    public int fileNum;
    public List<Suspiciousness> suspiciousnessList;

    /**
     * テスト結果(カバレッジ情報を含む)から,全てのテスト対象ファイルの各行ごとの疑惑値を計算
     * @param testResults テスト結果
     */
    public SuspiciousnessCalculator(TestResults testResults){
        this.fileNum = testResults.getTestResults().get(0).getCoverages().size();
        List<Suspiciousness> list = new ArrayList<Suspiciousness>();
        int lineLength;
        String testName;

        final int numberOfSuccessedTests = testResults.getSuccessedTestResults().size();
        final int numberOfFailedTests = testResults.getFailedTestResults().size();
        SuspiciousnessStrategy suspiciousnessStrategy = new SuspiciousnessStrategy(numberOfSuccessedTests, numberOfFailedTests);

        
        for (int i = 0; i < fileNum; i++){
            
            //TODO 1つめのメソッドのカバレッジ結果からソースファイルの行数とファイル名を取得している. 他にいい取得方法はないか
            lineLength = testResults.getTestResult(0).getCoverages().get(i).getLength();
            testName = testResults.getTestResult(0).getCoverages().get(i).getName();
            
            for (int k = 1; k <= lineLength; k++){
                StatementStatus statementStatus = new StatementStatus(testResults, k, i);
                suspiciousnessStrategy.setStatementStatus(statementStatus);
                Suspiciousness suspiciousness = new Suspiciousness(testName, k, suspiciousnessStrategy.jaccard());
                list.add(suspiciousness);
            }
        }
        
        this.suspiciousnessList = list;
    }

}