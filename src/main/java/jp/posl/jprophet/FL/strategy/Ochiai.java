package jp.posl.jprophet.FL.strategy;

import jp.posl.jprophet.FL.StatementStatus;

public class Ochiai implements Coefficient {

    public double calculate(StatementStatus statementStatus, int numberOfSuccessedTests, int numberOfFailedTests) {
        double suspiciousenesses;
        double ncf = (double)statementStatus.getNumberOfFailedTestsCoveringStatement();
        double ncs = (double)statementStatus.getNumberOfSuccessedTestsCoveringStatement();
        double nf = (double)numberOfFailedTests;

        if (Math.sqrt(nf * (ncf + ncs)) == 0){
            suspiciousenesses = 0;
        }else{
            suspiciousenesses = ncf / Math.sqrt(nf * (ncf + ncs));
        }
        return suspiciousenesses;
    }
}