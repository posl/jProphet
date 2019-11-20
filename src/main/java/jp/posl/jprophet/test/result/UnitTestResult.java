package jp.posl.jprophet.test.result;

import java.util.Map;

/**
 * 単体テストの結果を格納するクラス
 */
public class UnitTestResult implements TestResult {


    private final boolean isSuccess;


    /**
     * UnitTestResultのコンストラクタ
     * @param isSuccess テストに成功していたか
     */
    public UnitTestResult(boolean isSuccess) {
        this.isSuccess = isSuccess;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public boolean getIsSuccess() {
        return this.isSuccess;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, String> toStringMap() {
        return Map.of("isSuccess", String.valueOf(isSuccess));
    }

}

