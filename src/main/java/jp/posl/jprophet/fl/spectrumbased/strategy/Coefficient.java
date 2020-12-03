package jp.posl.jprophet.fl.spectrumbased.strategy;

public interface Coefficient{
    public double calculate(int numberOfFailedTestsCoveringStatement, int numberOfFailedTestsNotCoveringStatement, int numberOfSuccessedTestsCoveringStatement, int numberOfSuccessedTestsNotCoveringStatement);
}