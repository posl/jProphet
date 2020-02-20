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
    private final long waitTime = 5000;


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
            final boolean result = runAllTestClass(testClasses);
            return new TestExecutorResult(result, List.of(new UnitTestResult(result)));
        }
        catch (MalformedURLException | ClassNotFoundException e) {
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

            //ここをタイムアウト処理する
            //final boolean isSuccess = junitCore.run(testClass).wasSuccessful();
            Thread testThread = new TestThread(junitCore, testClass);
            testThread.start();
            try {
                //waitTime ms たったらスキップ
                testThread.join(waitTime);
            } catch (InterruptedException e) {
                //TODO: handle exception
            }
            final boolean isSuccess = ((TestThread) testThread).getIsSuccess();
            if(!isSuccess) return false;
        }
        return true;
    }

}

class TestThread extends Thread {
    private JUnitCore junitCore;
    private Class<?> testClass;
    public boolean isSuccess;

    public TestThread(JUnitCore junitCore, Class<?> testClass){
        this.junitCore = junitCore;
        this.testClass = testClass;
        this.isSuccess = false;
    }

    @Override
    public void run(){
        this.isSuccess = junitCore.run(testClass).wasSuccessful();
    }

    public boolean getIsSuccess(){
        return this.isSuccess;
    }
}
