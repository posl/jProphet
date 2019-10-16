package jp.posl.jprophet.FL;

import java.util.List;
import java.util.ArrayList;

public class SuspiciousnessCalculator {

    private int fileNum;
    private List<Suspiciousness> suspiciousnessList;
    private final int numberOfSuccessedTests;
    private final int numberOfFailedTests;
    private TestResults testResults;

    /**
     * テスト結果(カバレッジ情報を含む)から,全てのテスト対象ファイルの各行ごとの疑惑値を計算
     * @param testResults テスト結果
     */
    public SuspiciousnessCalculator(TestResults testResults){
        this.fileNum = testResults.getTestResults().get(0).getCoverages().size();
        this.suspiciousnessList = null;
        this.numberOfSuccessedTests = testResults.getSuccessedTestResults().size();
        this.numberOfFailedTests = testResults.getFailedTestResults().size();
        this.testResults = testResults;

    }

    public void run(){
        List<Suspiciousness> list = new ArrayList<Suspiciousness>();
        int lineLength;
        String testName;
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

    public List<Suspiciousness> getSuspiciousnessList(){
        return this.suspiciousnessList;
    }

}