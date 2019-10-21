package jp.posl.jprophet.FL.strategy;

import jp.posl.jprophet.FL.StatementStatus;

public class Tarantula implements Coefficient {

    /**
     * Tarantulaで疑惑値算出
     * @param statementStatus
     * @param numberOfSuccessedTests
     * @param numberOfFailedTests
     */
    public double calculate(StatementStatus statementStatus, int numberOfSuccessedTests, int numberOfFailedTests) {
        double suspiciousenesses;
        double ncf = (double)statementStatus.getNumberOfFailedTestsCoveringStatement();
        double ncs = (double)statementStatus.getNumberOfSuccessedTestsCoveringStatement();
        double nf = (double)numberOfFailedTests;
        double ns = (double)numberOfSuccessedTests;

        if (nf == 0 || ns == 0 || ncf / nf + ncs / ns == 0){
            suspiciousenesses = 0;
        }else{
            suspiciousenesses = (ncf / nf) / (ncf / nf + ncs / ns);
        }
        return suspiciousenesses;
    }
}