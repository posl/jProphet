package jp.posl.jprophet.FL;

public class SuspiciousnessStrategy {
    private StatementStatus statementStatus;
    private int numberOfSuccessedTests;
    private int numberOfFailedTests;

    public SuspiciousnessStrategy(int numberOfSuccessedTests, int numberOfFailedTests){
        this.numberOfSuccessedTests = numberOfSuccessedTests;
        this.numberOfFailedTests = numberOfFailedTests;
    }

    public void setStatementStatus(StatementStatus statementStatus) {
        this.statementStatus = statementStatus;
    }

    public Double jaccard(){
        double suspiciousenesses;
        double NCF = (double)statementStatus.getNumberOfFailedTestsCoveringStatement();
        double NUF = (double)statementStatus.getNumberOfFailedTestsNotCoveringStatement();
        double NCS = (double)statementStatus.getNumberOfSuccessedTestsCoveringStatement();

        suspiciousenesses = NCF / (NCF + NUF + NCS);
        return suspiciousenesses;
    }

    public Double tarantula(){
        double suspiciousenesses;
        double NCF = (double)statementStatus.getNumberOfFailedTestsCoveringStatement();
        double NCS = (double)statementStatus.getNumberOfSuccessedTestsCoveringStatement();
        double NF = (double)numberOfFailedTests;
        double NS = (double)numberOfSuccessedTests;

        suspiciousenesses = (NCF / NF) / (NCF / NF + NCS / NS);
        return suspiciousenesses;
    }

    public Double ochiai(){
        double suspiciousenesses;
        double NCF = (double)statementStatus.getNumberOfFailedTestsCoveringStatement();
        double NCS = (double)statementStatus.getNumberOfSuccessedTestsCoveringStatement();
        double NF = (double)numberOfFailedTests;

        suspiciousenesses = NCF / Math.sqrt(NF * (NCF + NCS));
        return suspiciousenesses;
    }

}