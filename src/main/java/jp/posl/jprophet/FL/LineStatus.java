package jp.posl.jprophet.FL;

import java.util.List;
import java.util.HashMap;

public class LineStatus{
    /**
     * NCF : number of failed test cases that cover a statement <br>
     * NUF : number of failed test cases that do not cover a statement <br>
     * NCS : number of successful test cases that cover a statement <br>
     * NUS : number of successful test cases that do not cover a statement <br>
     * NC  : total number of test cases that cover a statement
     * NU  : total number of test cases that do not cover 
     */
    public int NCF, NUF, NCS, NUS, NC, NU;

    /**
     * filenumで指定されたソースファイルの「line」行のNCF, NUF, NCS, NUSを取得
     * @param testresults
     * @param line          テストファイルの行番号
     * @param filenum       Coverlageのリストの何番目のテストファイルかを表す(できればstream()をうまく使ってString filenameにしたい)
     */
    public LineStatus(TestResults testresults, int line, int filenum){
        this.NCF = getNCF(testresults, line, filenum);
        this.NUF = getNUF(testresults, line, filenum);
        this.NCS = getNCS(testresults, line, filenum);
        this.NUS = getNUS(testresults, line, filenum);
        this.NC = this.NCF + this.NCS;
        this.NU = this.NUF + this.NUS;
    }


    

    private Integer getNCF(TestResults testresults, int line, int filenum) {
        int size = testresults.getFailedTestResults().size();
        int ncf = 0;
        for(int k = 0; k < size; k++){
            if (testresults.getFailedTestResults().get(k).getCoverages().get(filenum).getStatusOfLine().get(line) == 2){
                ncf = ncf + 1;
            }
        }
        return ncf;
    }

    private Integer getNUF(TestResults testresults, int line, int filenum) {
        int size = testresults.getFailedTestResults().size();
        int nuf = 0;
        for(int k = 0; k < size; k++){
            if (testresults.getFailedTestResults().get(k).getCoverages().get(filenum).getStatusOfLine().get(line) != 2){
                nuf = nuf + 1;
            }
        }
        return nuf;
    }

    private Integer getNCS(TestResults testresults, int line, int filenum) {
        int size = testresults.getSuccessedTestResults().size();
        int ncs = 0;
        for(int k = 0; k < size; k++){
            if (testresults.getSuccessedTestResults().get(k).getCoverages().get(filenum).getStatusOfLine().get(line) == 2){
                ncs = ncs + 1;
            }
        }
        return ncs;
    }

    private Integer getNUS(TestResults testresults, int line, int filenum) {
        int size = testresults.getSuccessedTestResults().size();
        int nus = 0;
        for(int k = 0; k < size; k++){
            if (testresults.getSuccessedTestResults().get(k).getCoverages().get(filenum).getStatusOfLine().get(line) != 2){
                nus = nus + 1;
            }
        }
        return nus;
    }
}