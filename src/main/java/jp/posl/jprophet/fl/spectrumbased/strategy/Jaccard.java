package jp.posl.jprophet.fl.spectrumbased.strategy;

public class Jaccard implements Coefficient {

    /**
     * Jaccardで疑惑値を計算
     */
    public double calculate(int numberOfFailedTestsCoveringStatement, int numberOfFailedTestsNotCoveringStatement, int numberOfSuccessedTestsCoveringStatement, int numberOfSuccessedTestsNotCoveringStatement) {
        final double suspiciousenesses;
        final double ncf = (double)numberOfFailedTestsCoveringStatement;
        final double nuf = (double)numberOfFailedTestsNotCoveringStatement;
        final double ncs = (double)numberOfSuccessedTestsCoveringStatement;

        if (ncf + nuf + ncs == 0){
            suspiciousenesses = 0;
        }else{
            suspiciousenesses = ncf / (ncf + nuf + ncs);
        }     
        return suspiciousenesses;
    }
}