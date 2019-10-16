package jp.posl.jprophet.FL;


public class StatementStatus{
    /**
     * NCF : number of failed test cases that cover a statement <br>
     * NUF : number of failed test cases that do not cover a statement <br>
     * NCS : number of successful test cases that cover a statement <br>
     * NUS : number of successful test cases that do not cover a statement <br>
     * NC  : total number of test cases that cover a statement
     * NU  : total number of test cases that do not cover 
     */
    private int NCF, NUF, NCS, NUS, NC, NU;

    /**
     * filenumで指定されたソースファイルの「line」行のNCF, NUF, NCS, NUSを取得
     * @param testResulets
     * @param line          テストファイルの行番号
     * @param fileNum       Coverlageのリストの何番目のテストファイルかを表す(できればstream()をうまく使ってString filenameにしたい)
     */
    public StatementStatus(TestResults testResulets, int line, int fileNum){
        this.NCF = calculateNCF(testResulets, line, fileNum);
        this.NUF = calculateNUF(testResulets, line, fileNum);
        this.NCS = calculateNCS(testResulets, line, fileNum);
        this.NUS = calculateNUS(testResulets, line, fileNum);
        this.NC = this.NCF + this.NCS;
        this.NU = this.NUF + this.NUS;
    }

    //TODO 以下4つの関数でカバレッジ結果が2(COVERD)の時のみその行を通ったことにしているが,3(PARTLY_COVERED)のときどうするか要検討
    
    /**
     * fileNumで指定されたline行目のNCFを取得
     * @param testResulets
     * @param line
     * @param fileNum
     * @return
     */
    private Integer calculateNCF(TestResults testResulets, int line, int fileNum) {
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
     * fileNumで指定されたline行目のNUFを取得
     * @param testResulets
     * @param line
     * @param fileNum
     * @return
     */
    private Integer calculateNUF(TestResults testResulets, int line, int fileNum) {
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
     * fileNumで指定されたline行目のNCSを取得
     * @param testResulets
     * @param line
     * @param fileNum
     * @return
     */
    private Integer calculateNCS(TestResults testResulets, int line, int fileNum) {
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
     * fileNumで指定されたline行目のNUFを取得
     * @param testResulets
     * @param line
     * @param fileNum
     * @return
     */
    private Integer calculateNUS(TestResults testResulets, int line, int fileNum) {
        int size = testResulets.getSuccessedTestResults().size();
        int nus = 0;
        for(int k = 0; k < size; k++){
            if (testResulets.getSuccessedTestResults().get(k).getCoverages().get(fileNum).getStatusOfLine().get(line) != 2){
                nus = nus + 1;
            }
        }
        return nus;
    }

    public Integer getNumberOfFailedTestsCoveringStatement(){
        return this.NCF;
    }

    public Integer getNumberOfFailedTestsNotCoveringStatement(){
        return this.NUF;
    }

    public Integer getNumberOfSuccessedTestsCoveringStatement(){
        return this.NCS;
    }

    public Integer getNumberOfSuccessedTestsNotCoveringStatement(){
        return this.NUS;
    }

    public Integer getNumberOfTestsCoveringStatement(){
        return this.NC;
    }

    public Integer getNumberOfTestsNotCoveringStatement(){
        return this.NU;
    }

}