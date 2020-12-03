package jp.posl.jprophet.fl.spectrumbased.strategy;

public class Tarantula implements Coefficient {

    /**
     * Tarantulaで疑惑値算出
     */
    public double calculate(int numberOfFailedTestsCoveringStatement, int numberOfFailedTestsNotCoveringStatement, int numberOfSuccessedTestsCoveringStatement, int numberOfSuccessedTestsNotCoveringStatement) {
        final double suspiciousenesses;
        final double ncf = (double)numberOfFailedTestsCoveringStatement;
        final double ncs = (double)numberOfSuccessedTestsCoveringStatement;
        final double nf = (double)(numberOfFailedTestsCoveringStatement + numberOfFailedTestsNotCoveringStatement);
        final double ns = (double)( numberOfSuccessedTestsCoveringStatement + numberOfSuccessedTestsNotCoveringStatement);

        if (nf == 0 || ns == 0 || ncf / nf + ncs / ns == 0){
            suspiciousenesses = 0;
        }else{
            suspiciousenesses = (ncf / nf) / (ncf / nf + ncs / ns);
        }
        return suspiciousenesses;
    }
}