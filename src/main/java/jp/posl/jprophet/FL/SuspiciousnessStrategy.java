package jp.posl.jprophet.FL;

/**
 * 疑惑値の計算手法をまとめておくクラス
 */
public class SuspiciousnessStrategy {
    private StatementStatus statementStatus;
    final private int numberOfSuccessedTests;
    final private int numberOfFailedTests;

    /**
     * コンストラクタ
     * 成功したテストの数と失敗したテストの数の初期化
     * @param numberOfSuccessedTests
     * @param numberOfFailedTests
     */
    public SuspiciousnessStrategy(int numberOfSuccessedTests, int numberOfFailedTests){
        this.numberOfSuccessedTests = numberOfSuccessedTests;
        this.numberOfFailedTests = numberOfFailedTests;
    }

    public void setStatementStatus(StatementStatus statementStatus) {
        this.statementStatus = statementStatus;
    }

    public double jaccard(){
        double suspiciousenesses;
        double NCF = (double)statementStatus.getNumberOfFailedTestsCoveringStatement();
        double NUF = (double)statementStatus.getNumberOfFailedTestsNotCoveringStatement();
        double NCS = (double)statementStatus.getNumberOfSuccessedTestsCoveringStatement();

        suspiciousenesses = NCF / (NCF + NUF + NCS);
        return suspiciousenesses;
    }

    public double tarantula(){
        double suspiciousenesses;
        double NCF = (double)statementStatus.getNumberOfFailedTestsCoveringStatement();
        double NCS = (double)statementStatus.getNumberOfSuccessedTestsCoveringStatement();
        double NF = (double)numberOfFailedTests;
        double NS = (double)numberOfSuccessedTests;

        suspiciousenesses = (NCF / NF) / (NCF / NF + NCS / NS);
        return suspiciousenesses;
    }

    public double ochiai(){
        double suspiciousenesses;
        double NCF = (double)statementStatus.getNumberOfFailedTestsCoveringStatement();
        double NCS = (double)statementStatus.getNumberOfSuccessedTestsCoveringStatement();
        double NF = (double)numberOfFailedTests;

        suspiciousenesses = NCF / Math.sqrt(NF * (NCF + NCS));
        return suspiciousenesses;
    }

}