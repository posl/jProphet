package jp.posl.jprophet.FL.strategy;

import jp.posl.jprophet.FL.StatementStatus;

public class Jaccard implements Coefficient {

    public double calculate(StatementStatus statementStatus, int numberOfSuccessedTests, int numberOfFailedTests) {
        double suspiciousenesses;
        double ncf = (double) statementStatus.getNumberOfFailedTestsCoveringStatement();
        double nuf = (double) statementStatus.getNumberOfFailedTestsNotCoveringStatement();
        double ncs = (double) statementStatus.getNumberOfSuccessedTestsCoveringStatement();

        if (ncf + nuf + ncs == 0){
            suspiciousenesses = 0;
        }else{
            suspiciousenesses = ncf / (ncf + nuf + ncs);
        }     
        return suspiciousenesses;
    }
}