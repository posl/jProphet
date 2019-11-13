package jp.posl.jprophet.test.result;

import java.util.Map;

import jp.posl.jprophet.spotbugs.SpotBugsWarning;

public class SpotBugsTestResult implements TestResult {


    private final boolean isSuccess;
    private final SpotBugsWarning fixedWarning;
    private final int numOfOccurredWarning;

    public SpotBugsTestResult(boolean isSuccess, SpotBugsWarning fixedWarning, int numOfOccurredWarnings) {
        this.isSuccess = isSuccess;
        this.fixedWarning = fixedWarning;
        this.numOfOccurredWarning = numOfOccurredWarnings;
    }


    @Override
    public boolean getIsSuccess() {
        return this.isSuccess;
    }


    @Override
    public Map<String, String> toStringMap() {
        return Map.of(
            "isSuccess", String.valueOf(isSuccess),
            "type", fixedWarning.getType(),
            "NumOfOccurredError", String.valueOf(numOfOccurredWarning)
        );   
    }
}