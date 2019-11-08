package jp.posl.jprophet.test.result;



public class SpotBugsTestResult implements TestResult {


    private final boolean isSuccess;

    public SpotBugsTestResult(boolean isSuccess) {
        this.isSuccess = isSuccess;
    }


    @Override
    public boolean getIsSuccess() {
        return this.isSuccess;
    }

}