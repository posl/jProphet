package jp.posl.jprophet.FL;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TestResults implements Serializable {

	private static final long serialVersionUID = 1L;

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
	
	public List<FullyQualifiedName> getFailedTestNames() {
		return getFailedTestResults().stream().map(r -> r.getMethodName()).collect(Collectors.toList());
	}


	public static String getSerFilename() {
		return "tmp/__testresults.ser";
	}

	public static void serialize(TestResults testResults) {
		try {
			final ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(getSerFilename()));
			out.writeObject(testResults);
			out.close();
		} catch (FileNotFoundException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
	}

	public static TestResults deserialize() {
		ObjectInputStream in;
		try {
			in = new ObjectInputStream(new FileInputStream(getSerFilename()));
			final TestResults testResults = (TestResults) in.readObject();
			in.close();
			return testResults;
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
		return null;
	}

}