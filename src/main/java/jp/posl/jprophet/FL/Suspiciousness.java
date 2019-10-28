package jp.posl.jprophet.FL;

/**
 * テスト対象のソースファイルのパスと,ステートメント(行)の疑惑値を格納
 */
public class Suspiciousness {
    final private String path;
    final private int lineNumber;
    final private double value;

    public Suspiciousness(String path, int lineNumber, double value) {
        this.path = path;
        this.lineNumber = lineNumber;
        this.value = value;
    }

    public String getPath() {
        return path;
    }

    public int getLineNumber(){
        return lineNumber;
    }

    public double getValue() {
        return value;
    }
}