package jp.posl.jprophet.FL.strategy;

import jp.posl.jprophet.FL.StatementStatus;

public class Jaccard implements Coefficient {

    /**
     * Jaccardで疑惑値算出
     * @param statementStatus
     * @param numberOfSuccessedTests
     * @param numberOfFailedTests
     */
    public double calculate(StatementStatus statementStatus, int numberOfSuccessedTests, int numberOfFailedTests) {
        final double suspiciousenesses;
        final double ncf = (double) statementStatus.getNumberOfFailedTestsCoveringStatement();
        final double nuf = (double) statementStatus.getNumberOfFailedTestsNotCoveringStatement();
        final double ncs = (double) statementStatus.getNumberOfSuccessedTestsCoveringStatement();

        if (ncf + nuf + ncs == 0){
            suspiciousenesses = 0;
        }else{
            suspiciousenesses = ncf / (ncf + nuf + ncs);
        }     
        return suspiciousenesses;
    }
}