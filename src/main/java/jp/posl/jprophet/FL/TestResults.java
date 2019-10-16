package jp.posl.jprophet.FL;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TestResults {

	private final List<TestResult> testResults;

	public TestResults() {
		testResults = new ArrayList<>();
	}

	public void add(TestResult testResult) {
		this.testResults.add(testResult);
	}

	/**
	 * 失敗したテスト結果の一覧を取得
	 * 
	 * @return 失敗したテスト結果
	 */
	public List<TestResult> getFailedTestResults() {
		return this.testResults.stream().filter(r -> r.wasFailed()).collect(Collectors.toList());
	}

	/**
	 * 成功したテスト結果の一覧を取得
	 * @return 成功したテスト結果
	 */
	public List<TestResult> getSuccessedTestResults() {
		return this.testResults.stream().filter(r -> !r.wasFailed()).collect(Collectors.toList());
	}

	/**
	 * 全てのテスト結果の一覧を取得している
	 * @return 全てのテスト結果
	 */
	public List<TestResult> getTestResults() {
		return testResults;
	}

	public TestResult getTestResult(int num){
		return testResults.get(num);
	}
	
	public List<String> getFailedTestNames() {
		return getFailedTestResults().stream().map(r -> r.getMethodName()).collect(Collectors.toList());
	}


	public static String getSerFilename() {
		return "tmp/__testresults.ser";
	}

}