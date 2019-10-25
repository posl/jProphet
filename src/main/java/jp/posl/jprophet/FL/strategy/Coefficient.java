package jp.posl.jprophet.FL.strategy;

import jp.posl.jprophet.FL.StatementStatus;

public interface Coefficient{
    public double calculate(StatementStatus statementStatus, int numberOfSuccessedTests, int numberOfFailedTests);
}