package jp.posl.jprophet.fl.spectrumbased.strategy;

import jp.posl.jprophet.fl.spectrumbased.statement.StatementStatus;

public class Ochiai implements Coefficient {

    /**
     * Ochiaiで疑惑値算出
     * @param statementStatus
     * @param numberOfSuccessedTests
     * @param numberOfFailedTests
     */
    public double calculate(StatementStatus statementStatus, int numberOfSuccessedTests, int numberOfFailedTests) {
        final double suspiciousenesses;
        final double ncf = (double)statementStatus.getNumberOfFailedTestsCoveringStatement();
        final double ncs = (double)statementStatus.getNumberOfSuccessedTestsCoveringStatement();
        final double nf = (double)numberOfFailedTests;

        if (Math.sqrt(nf * (ncf + ncs)) == 0){
            suspiciousenesses = 0;
        }else{
            suspiciousenesses = ncf / Math.sqrt(nf * (ncf + ncs));
        }
        return suspiciousenesses;
    }
}