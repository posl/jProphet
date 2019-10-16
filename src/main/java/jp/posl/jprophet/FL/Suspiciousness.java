package jp.posl.jprophet.FL;

public class Suspiciousness {
// 疑惑値とその場所のリスト pathはStringではなくFullyQualifiedNameにするべき?
	final private String path;
	final private int line;
	final private double value;

	public Suspiciousness(String path, int line, double value) {
		this.path = path;
		this.line = line;
		this.value = value;
	}

	public String getPath() {
		return path;
	}

	public int getLine(){
		return line;
	}

	public double getValue() {
		return value;
	}
}