package jp.posl.jprophet.FL.strategy;

import jp.posl.jprophet.FL.StatementStatus;

public class Tarantula implements Coefficient {

    public double calculate(StatementStatus statementStatus, int numberOfSuccessedTests, int numberOfFailedTests) {
        double suspiciousenesses;
        double NCF = (double)statementStatus.getNumberOfFailedTestsCoveringStatement();
        double NCS = (double)statementStatus.getNumberOfSuccessedTestsCoveringStatement();
        double NF = (double)numberOfFailedTests;
        double NS = (double)numberOfSuccessedTests;

        suspiciousenesses = (NCF / NF) / (NCF / NF + NCS / NS);
        return suspiciousenesses;
    }
}