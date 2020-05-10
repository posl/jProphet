package jp.posl.jprophet.fl.spectrumbased.strategy;

public class Ochiai implements Coefficient {

    /**
     * Ochiaiで疑惑値算出
     */
    public double calculate(int numberOfFailedTestsCoveringStatement, int numberOfFailedTestsNotCoveringStatement, int numberOfSuccessedTestsCoveringStatement, int numberOfSuccessedTestsNotCoveringStatement) {
        final double suspiciousenesses;

        final double ncf = (double)numberOfFailedTestsCoveringStatement;
        final double ncs = (double)numberOfSuccessedTestsCoveringStatement;
        final double nf = (double)(numberOfFailedTestsCoveringStatement + numberOfFailedTestsNotCoveringStatement);

        if (Math.sqrt(nf * (ncf + ncs)) == 0){
            suspiciousenesses = 0;
        }else{
            suspiciousenesses = ncf / Math.sqrt(nf * (ncf + ncs));
        }
        return suspiciousenesses;
    }
}