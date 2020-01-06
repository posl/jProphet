package jp.posl.jprophet.test.executor;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.ArrayList;
import org.junit.runner.JUnitCore;

import jp.posl.jprophet.project.Project;
import jp.posl.jprophet.test.result.TestExecutorResult;
import jp.posl.jprophet.test.result.UnitTestResult;
import jp.posl.jprophet.ProjectBuilder;
import jp.posl.jprophet.RepairConfiguration;

import static org.mockito.ArgumentMatchers.refEq;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;


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
    public TestExecutorResult exec(RepairConfiguration config)  {
        try {
            builder.build(config);
            getClassLoader(config.getBuildPath());
            testClasses = loadTestClass(config.getTargetProject());
            //final boolean result = runAllTestClass(testClasses);
            final boolean result = testByCommand(config);
            return new TestExecutorResult(result, List.of(new UnitTestResult(result)));
        }
        catch (Exception e) {
            System.err.println(e.getMessage());
            return new TestExecutorResult(false, List.of(new UnitTestResult(false)));
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
            //if(!isSuccess) return false;
            if(!isSuccess)
                System.out.println(testClass.getName());
        }
        return true;
    }

    private boolean testByCommand(RepairConfiguration config) throws Exception {
        Runtime runtime = Runtime.getRuntime();
        String[] Command = { "mvn", "test" };
        Process p = null;
        File dir = new File(config.getTargetProject().getRootPath());

        p = runtime.exec(Command, null, dir);
        p.waitFor();
        BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
        final boolean isSuccess = br.lines().filter(l -> l.contains("BUILD SUCCESS")).count() > 0;

        return isSuccess;
    }

}

