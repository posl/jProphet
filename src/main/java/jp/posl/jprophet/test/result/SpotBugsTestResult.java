package jp.posl.jprophet.test.result;

import java.util.Map;

import jp.posl.jprophet.spotbugs.SpotBugsWarning;

/**
 * Spotbugsによるテスト検証の結果を格納するクラス
 */
public class SpotBugsTestResult implements TestResult {


    private final boolean isPassedUnitTest;
    private final SpotBugsWarning fixedWarning;
    private final int numOfOccurredWarning;

    /**
     * SpotBugsTestResultのコンストラクタ
     * @param isPassedUnitTest 単体テストに成功していたか
     * @param fixedWarning 修正されたワーニング
     * @param numOfOccurredWarnings 新たに発生したワーニングの数
     */
    public SpotBugsTestResult(boolean isPassedUnitTest, SpotBugsWarning fixedWarning, int numOfOccurredWarnings) {
        this.isPassedUnitTest = isPassedUnitTest;
        this.fixedWarning = fixedWarning;
        this.numOfOccurredWarning = numOfOccurredWarnings;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, String> toStringMap() {
        return Map.of(
            "unitTest", isPassedUnitTest ? "PASSED" : "FAILED",
            "fixedWarning", fixedWarning.getType(),
            "numOfOccurredWarning", String.valueOf(numOfOccurredWarning)
            //TODO 後にpatchCandidateのdiffも記録したい
        );   
    }
}