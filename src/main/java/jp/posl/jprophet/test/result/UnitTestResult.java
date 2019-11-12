package jp.posl.jprophet.test.result;

import java.util.Map;

public class UnitTestResult implements TestResult {


    private final boolean isSuccess;

    public UnitTestResult(boolean isSuccess) {
        this.isSuccess = isSuccess;
    }


    @Override
    public boolean getIsSuccess() {
        return this.isSuccess;
    }


    @Override
    public Map<String, String> toStringMap() {
        return Map.of("isSuccess", String.valueOf(isSuccess));
    }

}

