package jp.posl.jprophet.test;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.ArrayList;
import org.junit.runner.JUnitCore;

import jp.posl.jprophet.ProjectBuilder;
import jp.posl.jprophet.ProjectConfiguration;

import java.io.File;


/**
 *  コード修正後のプロジェクトに対してテスト実行を行う
 */
public class TestExecutor {

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
    public TestExecutor() {
        this.builder = new ProjectBuilder();
        this.loader = null;
    }

    /**
     * 対象のプロジェクトのテストを実行し、全て通るかどうかを判定
     * 
     * @param projectConfiguration 対象プロジェクト
     * @return 全てのテスト実行が通ったかどうか
     */

    public boolean run(ProjectConfiguration projectConfiguration)  {
        try {
            builder.build(projectConfiguration);
            getClassLoader(projectConfiguration);
            testClasses = loadTestClass(projectConfiguration);
            return runAllTestClass(testClasses);
        }
        catch (MalformedURLException | ClassNotFoundException e) {
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
    private List<Class<?>> loadTestClass(ProjectConfiguration project) throws ClassNotFoundException {
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
            final boolean isSuccess = junitCore.run(testClass).wasSuccessful();
            if(!isSuccess) return false;
        }
        return true;
    }

}

