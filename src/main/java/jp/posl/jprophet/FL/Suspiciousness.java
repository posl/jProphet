package jp.posl.jprophet.FL;

/**
 * テスト対象のソースファイルのパスと,ステートメント(行)の疑惑値を格納
 */
public class Suspiciousness {
    final private String fqn;
    final private int line;
    final private double value;

    public Suspiciousness(String fqn, int line, double value) {
        this.fqn = fqn;
        this.line = line;
        this.value = value;
    }

    public String getFQN() {
        return fqn;
    }

    public int getLine(){
        return line;
    }

    public double getValue() {
        return value;
    }
}