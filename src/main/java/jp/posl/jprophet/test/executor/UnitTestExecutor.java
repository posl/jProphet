package jp.posl.jprophet.test.executor;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;
import java.util.ArrayList;
import java.util.HashMap;

import org.junit.runner.JUnitCore;

import jp.posl.jprophet.project.Project;
import jp.posl.jprophet.test.result.TestExecutorResult;
import jp.posl.jprophet.test.result.UnitTestResult;
import jp.posl.jprophet.PatchSwitcher;
import jp.posl.jprophet.ProjectBuilder;
import jp.posl.jprophet.RepairConfiguration;
import jp.posl.jprophet.fl.spectrumbased.TestCase;
import jp.posl.jprophet.fl.spectrumbased.coverage.MemoryClassLoader;
import jp.posl.jprophet.patch.PatchCandidate;

import java.io.File;
import java.io.IOException;

/**
 * コード修正後のプロジェクトに対してテスト実行を行う
 */
public class UnitTestExecutor implements TestExecutor {

    private ProjectBuilder builder;
    private MemoryClassLoader loader;
    private List<Class<?>> testClasses;
    // ほとんどの関数がProjectConfigurationを引数にしているためメンバとして保持してもいい気はするが、
    // そうするとメイン関数を変更する事になるのでとりあえず後回し

    private final String gradleTestPath = "/src/test/java/"; // 出来ればgradleから取得したい
    private final long waitTime = 5000; // タイムアウトさせる時間[ms]

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
                getClassLoader(config.getBuildPath());
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
    /**
     * 対象のプロジェクトのテストを実行し、全て通るかどうかを判定
     * 
     * @param config 対象プロジェクトの設定
     * @return 全てのテスト実行が通ったかどうか
     */
    @Override
    public Map<PatchCandidate, TestExecutorResult> exec(RepairConfiguration config, List<PatchCandidate> candidates) {
        try {
            if (builder.build(config)) {
                final Path buildDir = Paths.get(config.getBuildPath());
                BiPredicate<Path, BasicFileAttributes> matcher = (path, attr) -> {
                    if (attr.isRegularFile() && path.getFileName().toString().endsWith(".class")) {
                        return true;
                    }
                    return false;
                };

                final Map<Path, byte[]> pathToContent = new HashMap<Path, byte[]>();
                final List<Path> paths = Files.find(buildDir, Integer.MAX_VALUE, matcher)
                    .collect(Collectors.toList());
                for (Path path : paths) {
                    final byte[] content = Files.readAllBytes(path);
                    pathToContent.put(path, content);
                }
                final PatchSwitcher switcher = new PatchSwitcher(pathToContent);

                final Map<PatchCandidate, TestExecutorResult> results = new HashMap<PatchCandidate, TestExecutorResult>();
                for (PatchCandidate candidate : candidates) {
                    final Path rewritedPath = switcher.rewrite(candidate).orElseThrow();
                    getClassLoader(config.getBuildPath());
                    testClasses = loadTestClass(config.getTargetProject());
                    final boolean result = runAllTestClass(testClasses);
                    results.put(candidate, new TestExecutorResult(result, List.of(new UnitTestResult(result))));
                    Files.write(rewritedPath, pathToContent.get(rewritedPath));
                }

            } else {
                return new HashMap<PatchCandidate, TestExecutorResult>();
            }

        } catch (ClassNotFoundException | IOException e) {
            System.err.println(e.getMessage());
            return new HashMap<PatchCandidate, TestExecutorResult>();
        }
        return new HashMap<PatchCandidate, TestExecutorResult>();
    }

    @Override
    public TestExecutorResult selectiveExec(RepairConfiguration config, List<TestCase> executionTests) {
        try {
            if (builder.build(config)) {
                getClassLoader(config.getBuildPath());
                testClasses = new ArrayList<Class<?>>();
                List<String> testFqns = executionTests.stream().flatMap(et -> et.getTestNames().stream()).distinct()
                        .collect(Collectors.toList());

                for (String testFqn : testFqns) {
                    testClasses.add(loader.loadClass(testFqn));
                }

                final boolean result = runAllTestClass(testClasses);
                return new TestExecutorResult(result, List.of(new UnitTestResult(result)));
            } else {
                return new TestExecutorResult(false, List.of(new UnitTestResult(false)));
            }
        } catch (MalformedURLException | ClassNotFoundException e) {
            System.err.println(e.getMessage());
            return new TestExecutorResult(false, List.of(new UnitTestResult(false)));
        }
    }

    /**
     * クラスローダーを取得
     * 
     * @param project 対象プロジェクト
     */
    public void getClassLoader(String buildPath) throws MalformedURLException {
        File file = new File(buildPath);
        loader = new MemoryClassLoader(new URL[] { file.toURI().toURL() });
    }

    /**
     * プロジェクトのテストクラスを取得
     * 
     * @param project 対象プロジェクト
     * @return テストクラスのリスト
     */
    public List<Class<?>> loadTestClass(Project project) throws ClassNotFoundException {
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
    public boolean runAllTestClass(List<Class<?>> classes) {
        final JUnitCore junitCore = new JUnitCore();
        for (Class<?> testClass : testClasses) {

            // タイムアウト処理
            TestThread testThread = new TestThread(junitCore, testClass);
            testThread.start();
            try {
                // waitTime ms 経過でスキップ
                testThread.join(waitTime);
            } catch (InterruptedException e) {
                System.err.println(e.getMessage());
                e.printStackTrace();
                System.exit(-1);
            }
            final boolean isSuccess = ((TestThread) testThread).getIsSuccess();
            if (!isSuccess)
                return false;
        }
        return true;
    }


}
