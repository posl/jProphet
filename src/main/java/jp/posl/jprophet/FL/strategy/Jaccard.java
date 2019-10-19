package jp.posl.jprophet.FL.strategy;

import jp.posl.jprophet.FL.StatementStatus;

public class Jaccard implements Coefficient {

    public double calculate(StatementStatus statementStatus, int numberOfSuccessedTests, int numberOfFailedTests) {
        double suspiciousenesses;
        double NCF = (double) statementStatus.getNumberOfFailedTestsCoveringStatement();
        double NUF = (double) statementStatus.getNumberOfFailedTestsNotCoveringStatement();
        double NCS = (double) statementStatus.getNumberOfSuccessedTestsCoveringStatement();

        suspiciousenesses = NCF / (NCF + NUF + NCS);
        return suspiciousenesses;
    }
}