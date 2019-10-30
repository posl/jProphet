package jp.posl.jprophet.fl.spectrumbased.strategy;

import jp.posl.jprophet.fl.spectrumbased.statement.StatementStatus;

public class Tarantula implements Coefficient {

    /**
     * Tarantulaで疑惑値算出
     * @param statementStatus
     * @param numberOfSuccessedTests
     * @param numberOfFailedTests
     */
    public double calculate(StatementStatus statementStatus, int numberOfSuccessedTests, int numberOfFailedTests) {
        final double suspiciousenesses;
        final double ncf = (double)statementStatus.getNumberOfFailedTestsCoveringStatement();
        final double ncs = (double)statementStatus.getNumberOfSuccessedTestsCoveringStatement();
        final double nf = (double)numberOfFailedTests;
        final double ns = (double)numberOfSuccessedTests;

        if (nf == 0 || ns == 0 || ncf / nf + ncs / ns == 0){
            suspiciousenesses = 0;
        }else{
            suspiciousenesses = (ncf / nf) / (ncf / nf + ncs / ns);
        }
        return suspiciousenesses;
    }
}