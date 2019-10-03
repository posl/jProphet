package jp.posl.jprophet;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.ArrayList;
import org.junit.runner.JUnitCore;
import java.io.File;



public class TestExecutor {

	public ProjectBuilder builder;
	public MemoryClassLoader loader;
	public List<Class<?>> testClasses;
	//ほとんどの関数がProjectConfigurationを引数にしているためメンバとして保持してもいい気はするが、
	//そうするとメイン関数を変更する事になるのでとりあえず後回し
	
	private final String gradleTestPath = "/src/test/java/"; //出来ればgradleから取得したい

	public TestExecutor() {
		this.builder = new ProjectBuilder();
		this.loader = null;
	}

	/**
	 * 対象のプロジェクトのテストを実行し、全て通るかどうかを判定
	 * 
     * @param project 対象プロジェクト
	 * @return 全てのテスト実行が通ったかどうか
	 */

	public boolean test(ProjectConfiguration projectConfiguration)  {
		try {
			builder.build(projectConfiguration);
			getClassLoader(projectConfiguration);
			testClasses = loadTestClass(projectConfiguration);
			return runAllTestClass(testClasses);
		}
		catch (Exception e) {  //エラーの処理はこれでいいのか良く分からない
			System.err.println(e.getMessage());
			return false;
		}
	}


	/**
	 * クラスローダーを取得
	 * 
     * @param project 対象プロジェクト
	 */
	private void getClassLoader(ProjectConfiguration project) throws MalformedURLException {
		File file = new File(project.getBuildPath());
		loader = new MemoryClassLoader(new URL[] { file.toURI().toURL() });
	}


	/**
	 * プロジェクトのテストクラスを取得
	 * 
     * @param project 対象プロジェクト
	 * @return テストクラスのリスト
	 */
	private List<Class<?>> loadTestClass(ProjectConfiguration project) throws Exception {
		List<Class<?>> classes = new ArrayList<Class<?>>();
		final String testFolderPath = project.getProjectPath() + gradleTestPath;
		for (String source : project.getTestFilePaths()) {
			final String fqn = source.replace(testFolderPath, "").replace("/", ".").replace(".java", "");
			classes.add(loader.loadClass(fqn));
		}
		return classes;
	}


	/**
	 * プロジェクトのテストクラスをJunitで実行し、全て通るか判定
	 * 
     * @param classes テストクラスのリスト
	 * @return 全てのテスト実行が通ったかどうか
	 */
	private boolean runAllTestClass(List<Class<?>> classes){
		final JUnitCore junitCore = new JUnitCore();
		for (Class<?> testClass : testClasses){
			final boolean result = junitCore.run(testClass).wasSuccessful();
			if(result == false) return false;
		}
		return true;
	}

}

