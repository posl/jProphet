package jp.posl.jprophet.test.result;




public class UnitTestResult implements TestResult {


    private final boolean isSuccess;

    public UnitTestResult(boolean isSuccess) {
        this.isSuccess = isSuccess;
    }


    @Override
    public boolean getIsSuccess() {
        return this.isSuccess;
    }


}