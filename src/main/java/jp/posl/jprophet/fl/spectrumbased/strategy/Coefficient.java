package jp.posl.jprophet.fl.spectrumbased.strategy;

import jp.posl.jprophet.fl.spectrumbased.statement.StatementStatus;

public interface Coefficient{
    public double calculate(StatementStatus statementStatus, int numberOfSuccessedTests, int numberOfFailedTests);
}