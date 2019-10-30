package jp.posl.jprophet.fl;

/**
 * テスト対象のソースファイルのパスと,ステートメント(行)の疑惑値を格納
 */
public class Suspiciousness {
    final private String fqn;
    final private int lineNumber;
    final private double value;

    public Suspiciousness(String fqn, int lineNumber, double value) {
        this.fqn = fqn;
        this.lineNumber = lineNumber;
        this.value = value;
    }

    public String getFQN() {
        return fqn;
    }

    public int getLineNumber(){
        return lineNumber;
    }

    public double getValue() {
        return value;
    }
}