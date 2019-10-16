package jp.posl.jprophet.FL;

import java.util.List;

public class TestResult {

	private FullyQualifiedName methodName;
	private boolean failed;
	private List<Coverage> coverages;

	/**
	 * constructor
	 * テストの結果(成功or失敗)とカバレッジをテストのメソッドごとに生成
	 * 
	 * @param methodName 実行したテストメソッドの名前
	 * @param failed テストの結果
	 * @param coverages テスト対象それぞれの行ごとのCoverage計測結果
	 */
	public TestResult(FullyQualifiedName methodName, boolean failed, List<Coverage> coverages) {
		this.methodName = methodName;
		this.failed = failed;
		this.coverages = coverages;
	}

	public boolean wasFailed() {
		return failed;
	}

	public FullyQualifiedName getMethodName() {
		return methodName;
	}

	public List<Coverage> getCoverages() {
		return coverages;
	}

}