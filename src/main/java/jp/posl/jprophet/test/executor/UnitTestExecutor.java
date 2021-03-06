package jp.posl.jprophet.test.executor;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.ArrayList;
import org.junit.runner.JUnitCore;

import jp.posl.jprophet.project.Project;
import jp.posl.jprophet.test.result.TestExecutorResult;
import jp.posl.jprophet.test.result.UnitTestResult;
import jp.posl.jprophet.ProjectBuilder;
import jp.posl.jprophet.RepairConfiguration;
import jp.posl.jprophet.fl.spectrumbased.TestCase;
import jp.posl.jprophet.fl.spectrumbased.coverage.MemoryClassLoader;

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
    private final long waitTime = 5000; //タイムアウトさせる時間[ms]


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
            if(builder.build(config)) {
                this.getClassLoader(config.getBuildPath());
                testClasses = loadTestClass(config.getTargetProject());
                final boolean result = runAllTestClass(testClasses);
                return new TestExecutorResult(result, List.of(new UnitTestResult(result)));
            } else {
                return new TestExecutorResult(false, List.of(new UnitTestResult(false)));
            }
        }
        catch (MalformedURLException | ClassNotFoundException e) {
            System.err.println(e.getMessage());
            return new TestExecutorResult(false, List.of(new UnitTestResult(false)));
        }
    }

    @Override
    public TestExecutorResult exec(RepairConfiguration config, List<TestCase> testsToBeExecuted)  {
        try {
            if (builder.build(config)){
                this.getClassLoader(config.getBuildPath());
                List<String> testFqns = testsToBeExecuted.stream()
                    .flatMap(et -> et.getTestNames().stream())
                    .distinct()
                    .collect(Collectors.toList());

                final boolean result = runAllTestFqn(testFqns);
                return new TestExecutorResult(result, List.of(new UnitTestResult(result)));
            } else {
                return new TestExecutorResult(false, List.of(new UnitTestResult(false)));
            }
        }
        catch (MalformedURLException  e) {
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

            //タイムアウト処理
            TestThread testThread = new TestThread(junitCore, testClass);
            testThread.start();
            try {
                //waitTime ms 経過でスキップ
                testThread.join(waitTime);
            } catch (InterruptedException e) {
                System.err.println(e.getMessage());
                e.printStackTrace();
                System.exit(-1);
            }
            final boolean isSuccess = ((TestThread) testThread).getIsSuccess();
            if(!isSuccess) return false;
        }
        return true;
    }
    
    /**
     * 指定されたメソッドを含めたfqnのみをJUnitで実行し，全て通るか判定
     * 
     * @param testFqns メソッドを含めたfqn(fileFqn.methodName)
     * @return 全てのテスト実行が通ったかどうか
     */
    private boolean runAllTestFqn(List<String> testFqns) {
        final JUnitCore junitCore = new JUnitCore();
        for (String testFqn : testFqns) {
            String fileFqn = testFqn.substring(0, testFqn.lastIndexOf("."));
            String methodName = testFqn.substring(testFqn.lastIndexOf(".") + 1, testFqn.length());
            Class<?> testClass;
            try {
                testClass = loader.loadClass(fileFqn);
            } catch (ClassNotFoundException e) {
                continue;
            }
            //タイムアウト処理
            TestThread testThread = new TestThread(junitCore, testClass, methodName);
            testThread.start();
            try {
                //waitTime ms 経過でスキップ
                testThread.join(waitTime);
            } catch (InterruptedException e) {
                System.err.println(e.getMessage());
                e.printStackTrace();
                System.exit(-1);
            }
            final boolean isSuccess = ((TestThread) testThread).getIsSuccess();
            if(!isSuccess) return false;
        }
        return true;
    }

}
