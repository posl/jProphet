package jp.posl.jprophet.FL.strategy;

import jp.posl.jprophet.FL.StatementStatus;

public class Ochiai implements Coefficient {

    public double calculate(StatementStatus statementStatus, int numberOfSuccessedTests, int numberOfFailedTests) {
        double suspiciousenesses;
        double NCF = (double)statementStatus.getNumberOfFailedTestsCoveringStatement();
        double NCS = (double)statementStatus.getNumberOfSuccessedTestsCoveringStatement();
        double NF = (double)numberOfFailedTests;

        suspiciousenesses = NCF / Math.sqrt(NF * (NCF + NCS));
        return suspiciousenesses;
    }
}