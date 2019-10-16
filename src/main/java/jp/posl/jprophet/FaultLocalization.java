package jp.posl.jprophet;

import jp.posl.jprophet.ProjectConfiguration;
import jp.posl.jprophet.FL.Suspiciousness;
import jp.posl.jprophet.FL.CoverageCollector;
import jp.posl.jprophet.FL.TestResults;
import jp.posl.jprophet.FL.FullyQualifiedName;
import jp.posl.jprophet.FL.SuspiciousnessCalculator;
import jp.posl.jprophet.ProjectBuilder;
import java.util.List;
import java.util.ArrayList;
import java.nio.file.Path;


public class FaultLocalization {
	ProjectBuilder projectBuilder = new ProjectBuilder();
	Path classDir;
	String buildPath;
	List<String> classFilePaths;
	List<String> sourceClassFilePaths = new ArrayList<String>();
	List<String> testClassFilePaths = new ArrayList<String>();

	/**
	 * ソースファイルとテストファイルをビルドして,ビルドされたクラスのFQDNを取得
	 * @param project
	 */
	public FaultLocalization(ProjectConfiguration project) {
		this.projectBuilder.build(project);
		this.buildPath = project.getBuildPath();
		getFQN(project);
	}

	/**
	 * テスト対象の全てのソースファイルの行ごとの疑惑値を算出する
	 * @return List[ファイルのFQDN, 行, 疑惑値]
	 */
	public List<Suspiciousness> exec() {
		List<Suspiciousness> suspiciousnessList = new ArrayList<Suspiciousness>();
				
		List<FullyQualifiedName> sourceClass = new ArrayList<FullyQualifiedName>();
		List<FullyQualifiedName> testClass = new ArrayList<FullyQualifiedName>();
		TestResults testResults = new TestResults();

		//List<String> FQDN -> List<FullyQualifiedName> FQN
		int SCFPsize = sourceClassFilePaths.size();
		for(int k = 0; k < SCFPsize; k++){
			sourceClass.add(new FullyQualifiedName(sourceClassFilePaths.get(k)));
		}
		int TCFPsize = testClassFilePaths.size();
		for(int k = 0; k < TCFPsize; k++){
			testClass.add(new FullyQualifiedName(testClassFilePaths.get(k)));
		}

		CoverageCollector collector = new CoverageCollector(buildPath);
		try{
			testResults = collector.exec(sourceClass, testClass);
			SuspiciousnessCalculator suspiciousnessCalculator = new SuspiciousnessCalculator(testResults);
			suspiciousnessCalculator.run();
			suspiciousnessList = suspiciousnessCalculator.getSuspiciousnessList();
		}catch (Exception e){
			System.out.println("例外");
		}

		return suspiciousnessList;
	}

	/**
	 * ファイルパスからFQNを取得する
	 * @param project
	 */
	private void getFQN(ProjectConfiguration project){
		String gradleTestPath = "/src/test/java/";
		String gradleSourcePath = "/src/main/java/";
		String testFolderPath = project.getProjectPath() + gradleTestPath;
		String sourceFolderPath = project.getProjectPath() + gradleSourcePath;
		for (String testPath : project.getTestFilePaths()){
			testClassFilePaths.add(testPath.replace(testFolderPath, "").replace("/", ".").replace(".java", ""));
		}
		for (String sourcePath : project.getSourceFilePaths()){
			sourceClassFilePaths.add(sourcePath.replace(sourceFolderPath, "").replace("/", ".").replace(".java", ""));
		}

	}
	
}
