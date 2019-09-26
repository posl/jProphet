package jp.posl.jprophet.FL;

import java.util.List;
import java.util.HashMap;

public class LineStatus{
    public int NCF;
    public int NUF;
    public int NCS;
    public int NUS;
    public int NC;
    public int NU;
    public int NS;
    public int NF;

    /**
     * 
     * @param testresults
     * @param line          テストファイルの行番号
     * @param filenum       Coverlageのリストの何番目のテストファイルかを表す(できればString filenameにしたい)
     */
    public LineStatus(TestResults testresults, int line, int filenum){
        this.NCF = getNCF(testresults, line, filenum);
        this.NUF = getNUF(testresults, line, filenum);
        this.NCS = getNCS(testresults, line, filenum);
        this.NUS = getNUS(testresults, line, filenum);
        this.NS = testresults.getSuccessedTestResults().size();
        this.NF = testresults.getFailedTestResults().size();
    }


    

    private Integer getNCF(TestResults testresults, int line, int filenum) {
        int size = testresults.getFailedTestResults().size();
        int ncf = 0;
        for(int k = 0; k < size; k++){
            if (testresults.getFailedTestResults().get(k).getCoverages().get(filenum).getStatusOfLisne().get(line) == 2){
                ncf = ncf + 1;
            }
        }
        return ncf;
    }

    private Integer getNUF(TestResults testresults, int line, int filenum) {
        int size = testresults.getFailedTestResults().size();
        int nuf = 0;
        for(int k = 0; k < size; k++){
            if (testresults.getFailedTestResults().get(k).getCoverages().get(filenum).getStatusOfLisne().get(line) != 2){
                nuf = nuf + 1;
            }
        }
        return nuf;
    }

    private Integer getNCS(TestResults testresults, int line, int filenum) {
        int size = testresults.getSuccessedTestResults().size();
        int ncs = 0;
        for(int k = 0; k < size; k++){
            if (testresults.getSuccessedTestResults().get(k).getCoverages().get(filenum).getStatusOfLisne().get(line) == 2){
                ncs = ncs + 1;
            }
        }
        return ncs;
    }

    private Integer getNUS(TestResults testresults, int line, int filenum) {
        int size = testresults.getSuccessedTestResults().size();
        int nus = 0;
        for(int k = 0; k < size; k++){
            if (testresults.getSuccessedTestResults().get(k).getCoverages().get(filenum).getStatusOfLisne().get(line) != 2){
                nus = nus + 1;
            }
        }
        return nus;
    }
}