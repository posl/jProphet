package jp.posl.jprophet.test;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.ArrayList;
import org.junit.runner.JUnitCore;

import jp.posl.jprophet.project.Project;
import jp.posl.jprophet.test.result.TestResult;
import jp.posl.jprophet.test.result.UnitTestResult;
import jp.posl.jprophet.ProjectBuilder;
import jp.posl.jprophet.RepairConfiguration;

import java.io.File;


/**
 *  コード修正後のプロジェクトに対してテスト実行を行う
 */
public class UnitTestExecutor implements TestExecutor {

    private ProjectBuilder builder;
    private MemoryClassLoader loader;
    private List<Class<?>> testClasses;
    //ほとんどの関数がProjectConfigurationを引数にしているためメンバとして保持してもいい気はするが、
    //そうするとメイン関数を変更する事になるのでとりあえず後回し
    
    private final String gradleTestPath = "/src/test/java/"; //出来ればgradleから取得したい


    /**
     * コンストラクタ
     * 
     */
    public UnitTestExecutor() {
        this.builder = new ProjectBuilder();
        this.loader = null;
    }

    /**
     * 対象のプロジェクトのテストを実行し、全て通るかどうかを判定
     * 
     * @param config 対象プロジェクトの設定
     * @return 全てのテスト実行が通ったかどうか
     */
    @Override
    public List<TestResult> exec(RepairConfiguration config)  {
        try {
            builder.build(config);
            getClassLoader(config.getBuildPath());
            testClasses = loadTestClass(config.getTargetProject());
            return List.of(new UnitTestResult(runAllTestClass(testClasses)));
        }
        catch (MalformedURLException | ClassNotFoundException e) {
            System.err.println(e.getMessage());
            return List.of(new UnitTestResult(false));
        }
    }


    /**
     * クラスローダーを取得
     * 
     * @param project 対象プロジェクト
     */
    private void getClassLoader(String buildPath) throws MalformedURLException {
        File file = new File(buildPath);
        loader = new MemoryClassLoader(new URL[] { file.toURI().toURL() });
    }


    /**
     * プロジェクトのテストクラスを取得
     * 
     * @param project 対象プロジェクト
     * @return テストクラスのリスト
     */
    private List<Class<?>> loadTestClass(Project project) throws ClassNotFoundException {
        List<Class<?>> classes = new ArrayList<Class<?>>();
        final String testFolderPath = project.getRootPath() + gradleTestPath;
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
            final boolean isSuccess = junitCore.run(testClass).wasSuccessful();
            if(!isSuccess) return false;
        }
        return true;
    }

}

