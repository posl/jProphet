package jp.posl.jprophet.FL;

import jp.posl.jprophet.FL.coverage.TestResults;

/**
 * テスト対象のソースファイルのステートメント(行)の失敗(成功)してその行を通った(通ってない)テストの数を格納
 */
public class StatementStatus{

    private int numberOfFailedTestsCoveringStatement;
    private int numberOfFailedTestsNotCoveringStatement;
    private int numberOfSuccessedTestsCoveringStatement;
    private int numberOfSuccessedTestsNotCoveringStatement;
    private int numberOfTestsCoveringStatement;
    private int numberOfTestsNotCoveringStatement;

    /**
     * filenumで指定されたソースファイルの「line」行の,失敗(成功)してその行を通った(通ってない)テストの数を取得
     * @param testResulets
     * @param line          テストファイルの行番号
     * @param fileNum       Coverlageのリストの何番目のテストファイルかを表す(できればstream()をうまく使ってString filenameにしたい)
     */
    public StatementStatus(TestResults testResulets, int line, int fileNum){
        this.numberOfFailedTestsCoveringStatement = calculateNumberOfFailedTestsCoveringStatement(testResulets, line, fileNum);
        this.numberOfFailedTestsNotCoveringStatement = calculateNumberOfFailedTestsNotCoveringStatement(testResulets, line, fileNum);
        this.numberOfSuccessedTestsCoveringStatement = calculateNumberOfSuccessedTestsCoveringStatement(testResulets, line, fileNum);
        this.numberOfSuccessedTestsNotCoveringStatement = calculateNumberOfSuccessedTestsNotCoveringStatement(testResulets, line, fileNum);
        this.numberOfTestsCoveringStatement = this.numberOfFailedTestsCoveringStatement + this.numberOfSuccessedTestsCoveringStatement;
        this.numberOfTestsNotCoveringStatement = this.numberOfFailedTestsNotCoveringStatement + this.numberOfSuccessedTestsNotCoveringStatement;
    }

    //TODO 以下4つの関数でカバレッジ結果が2(COVERD)の時のみその行を通ったことにしているが,3(PARTLY_COVERED)のときどうするか要検討
    
    /**
     * fileNumで指定されたline行目のnumberOfFailedTestsCoveringStatementを計算
     * @param testResulets
     * @param line
     * @param fileNum
     * @return
     */
    private int calculateNumberOfFailedTestsCoveringStatement(TestResults testResulets, int line, int fileNum) {
        int size = testResulets.getFailedTestResults().size();
        int ncf = 0;
        for(int k = 0; k < size; k++){
            if (testResulets.getFailedTestResults().get(k).getCoverages().get(fileNum).getStatusOfLine().get(line) == 2){
                ncf = ncf + 1;
            }
        }
        return ncf;
    }

    /**
     * fileNumで指定されたline行目のnumberOfFailedTestsNotCoveringStatementを計算
     * @param testResulets
     * @param line
     * @param fileNum
     * @return
     */
    private int calculateNumberOfFailedTestsNotCoveringStatement(TestResults testResulets, int line, int fileNum) {
        int size = testResulets.getFailedTestResults().size();
        int nuf = 0;
        for(int k = 0; k < size; k++){
            if (testResulets.getFailedTestResults().get(k).getCoverages().get(fileNum).getStatusOfLine().get(line) != 2){
                nuf = nuf + 1;
            }
        }
        return nuf;
    }

    /**
     * fileNumで指定されたline行目のnumberOfSuccessedTestsCoveringStatementを計算
     * @param testResulets
     * @param line
     * @param fileNum
     * @return
     */
    private int calculateNumberOfSuccessedTestsCoveringStatement(TestResults testResulets, int line, int fileNum) {
        int size = testResulets.getSuccessedTestResults().size();
        int ncs = 0;
        for(int k = 0; k < size; k++){
            if (testResulets.getSuccessedTestResults().get(k).getCoverages().get(fileNum).getStatusOfLine().get(line) == 2){
                ncs = ncs + 1;
            }
        }
        return ncs;
    }

    /**
     * fileNumで指定されたline行目のnumberOfSuccessedTestsNotCoveringStatementを計算
     * @param testResulets
     * @param line
     * @param fileNum
     * @return
     */
    private int calculateNumberOfSuccessedTestsNotCoveringStatement(TestResults testResulets, int line, int fileNum) {
        int size = testResulets.getSuccessedTestResults().size();
        int nus = 0;
        for(int k = 0; k < size; k++){
            if (testResulets.getSuccessedTestResults().get(k).getCoverages().get(fileNum).getStatusOfLine().get(line) != 2){
                nus = nus + 1;
            }
        }
        return nus;
    }

    public int getNumberOfFailedTestsCoveringStatement(){
        return this.numberOfFailedTestsCoveringStatement;
    }

    public int getNumberOfFailedTestsNotCoveringStatement(){
        return this.numberOfFailedTestsNotCoveringStatement;
    }

    public int getNumberOfSuccessedTestsCoveringStatement(){
        return this.numberOfSuccessedTestsCoveringStatement;
    }

    public int getNumberOfSuccessedTestsNotCoveringStatement(){
        return this.numberOfSuccessedTestsNotCoveringStatement;
    }

    public int getNumberOfTestsCoveringStatement(){
        return this.numberOfTestsCoveringStatement;
    }

    public int getNumberOfTestsNotCoveringStatement(){
        return this.numberOfTestsNotCoveringStatement;
    }

}