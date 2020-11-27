package jp.posl.jprophet.fl.spectrumbased.coverage;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.io.IOUtils;
import org.jacoco.core.analysis.Analyzer;
import org.jacoco.core.analysis.CoverageBuilder;
import org.jacoco.core.data.ExecutionData;
import org.jacoco.core.data.ExecutionDataStore;
import org.jacoco.core.data.SessionInfoStore;
import org.jacoco.core.instr.Instrumenter;
import org.jacoco.core.runtime.IRuntime;
import org.jacoco.core.runtime.LoggerRuntime;
import org.jacoco.core.runtime.RuntimeData;
import org.junit.runner.Description;
import org.junit.runner.JUnitCore;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;

import jp.posl.jprophet.RepairConfiguration;
import jp.posl.jprophet.fl.spectrumbased.TestCase;
import jp.posl.jprophet.test.executor.TestThread;

/**
 * junit+jacocoでテスト対象プロジェクトのカバレッジを回収する
 */
public class CoverageCollector {
    private MemoryClassLoader memoryClassLoader;
    private final IRuntime jacocoRuntime;
    private final Instrumenter jacocoInstrumenter;
    private final RuntimeData jacocoRuntimeData;

    private final long waitTime = 5000; //タイムアウトさせる時間[ms]

    public CoverageCollector(String buildpath) {
        this.memoryClassLoader = null;
        this.jacocoRuntime = new LoggerRuntime();
        this.jacocoInstrumenter = new Instrumenter(jacocoRuntime);
        this.jacocoRuntimeData = new RuntimeData();

        // ここであらかじめビルド済みのクラスファイルをクラスローダーが読み込んでおく
        try {
            this.memoryClassLoader = new MemoryClassLoader(new URL[] { new URL("file:./" + buildpath + "/") });
        } catch (MalformedURLException e){
            System.err.println(e.getMessage());
            e.printStackTrace();
            System.exit(-1);
        }
    }

    /**
     * JaCoCo + JUnitの実行
     * sourceClassesで指定したソースをJaCoCoでinstrumentして,JUnitを実行する
     * 
     * @param sourceFQNs 計測対象のソースコードのFQNのリスト
     * @param testFQNs 実行する単体テストのFQNのリスト
     * @return テスト結果
     * @throws Exception
     */
    public TestResults exec(final List<String> sourceFQNs, final List<String> testFQNs) throws Exception {
        final TestResults testResults = new TestResults();

        //対象ソースコードをinstrumentしたものの定義をmemoryClassLoaderに追加(ロードはしない)
        for (String sourceFQN : sourceFQNs) {
            InputStream is = this.getTargetClassInputStream(sourceFQN);
            if (is != null) {
                byte[] bytes = IOUtils.toByteArray(is);
                byte[] instrumentedBytes = jacocoInstrumenter.instrument(bytes, "");
                this.memoryClassLoader.addDefinition(sourceFQN, instrumentedBytes);
            }
        }

        //対象テストコードの定義をmemoryClassLoaderに追加(ロードはしない)
        for (String testFQN : testFQNs) {
            InputStream is = this.getTargetClassInputStream(testFQN);
            if (is != null) {
                byte[] bytes = IOUtils.toByteArray(is);
                this.memoryClassLoader.addDefinition(testFQN, bytes);
            }
        }

        final List<Class<?>> junitClasses = loadAllClasses(testFQNs);

        jacocoRuntime.startup(jacocoRuntimeData);
        
        for (Class<?> junitClass : junitClasses) {
            final JUnitCore junitCore = new JUnitCore();
            final CoverageMeasurementListener listener = new CoverageMeasurementListener(testResults);
            junitCore.addListener(listener);
            
            //タイムアウト処理
            TestThread testThread = new TestThread(junitCore, junitClass);
            testThread.start();
            try {
                //waitTime ms 経過でスキップ
                testThread.join(waitTime);
            } catch (InterruptedException e) {
                System.err.println(e.getMessage());
                e.printStackTrace();
                System.exit(-1);
            }
        }
        
        return testResults;
    }
    
    /**
     * 正しく動くテストのみを厳選する
     * @param config 対象プロジェクトのconfig
     * @return 厳選したテストケース
     */
    public List<TestCase> getTestCases(RepairConfiguration config) {
        try {
            TestResults testResults = this.exec(config.getTargetProject().getSrcFileFqns() , config.getTargetProject().getTestFileFqns());
            List<TestCase> testsToBeExecuted = new ArrayList<TestCase>();
            for (String sourceFqn : config.getTargetProject().getSrcFileFqns()) {
                List<String> testNames = testResults.getTestResults().stream()
                    .filter(t -> t.getCoverages().stream().filter(c -> c.getName().equals(sourceFqn)).findFirst().isPresent())
                    .map(t -> t.getMethodName().substring(0,  t.getMethodName().lastIndexOf(".")))
                    .distinct()
                    .collect(Collectors.toList());

                testsToBeExecuted.add(new TestCase(sourceFqn, testNames));
            }
            return testsToBeExecuted;
        } catch (Exception e) {
            return new ArrayList<TestCase>();
        }
    }

    /**
    * 全クラスを定義内からロードしてクラスオブジェクトの集合を返す．
    *
    * @param memoryClassLoader クラスローダー
    * @param fqns 取り出すクラスのfqn
    * @return クラスオブジェクトの集合
    * @throws ClassNotFoundException
    */
    private List<Class<?>> loadAllClasses(final List<String> fqns) throws ClassNotFoundException {
        final List<Class<?>> classes = new ArrayList<>();
        for (final String fqn : fqns) {
            classes.add(memoryClassLoader.loadClass(fqn)); // 例外が出るので非stream処理
        }
        return classes;
    }

    /**
     * classファイルのInputStreamを取り出す．
     * 
     * @param fqn 読み込み対象のFQN
     * @return
     */
    private InputStream getTargetClassInputStream(final String fqn) {
        final String resource = fqn.replace('.', '/') + ".class";
        InputStream is = this.memoryClassLoader.getResourceAsStream(resource);
        return is;
    }

    /**
     * JUnit実行のイベントリスナー．内部クラス． 
     * JUnit実行前のJaCoCoの初期化，およびJUnit実行後のJaCoCoの結果回収を行う．
     * 
     * メモ：JUnitには「テスト成功時」のイベントリスナーがないので，テスト成否をDescriptionに強引に追記して管理
     * 
     * @author shinsuke
     *
     */
    class CoverageMeasurementListener extends RunListener {

        final public TestResults testResults;
        private boolean wasFailed;
        private boolean hasErrorMassage = true; 

        /**
         * constructor
         * 
         * @param measuredFQNs 計測対象のクラス名一覧
         * @param storedTestResults テスト実行結果の保存先
         * @throws Exception
         */
        public CoverageMeasurementListener(TestResults storedTestResults) {
            this.testResults = storedTestResults;
        }

        @Override
        public void testStarted(Description description) {
            resetJacocoRuntimeData();
            wasFailed = false;
        }

        @Override
        public void testFailure(Failure failure) {
            wasFailed = true;
            if (failure.getException().getMessage() == null) {
                this.hasErrorMassage = false;
            }
        }

        @Override
        public void testFinished(Description description) throws IOException {
            collectRuntimeData(description);
        }

        /**
         * JaCoCoが回収した実行結果をリセットする．
         */
        private void resetJacocoRuntimeData() {
            jacocoRuntimeData.reset();
        }

        /**
         * Descriptionから実行したテストメソッドのFQNを取り出す．
         * 
         * @param description
         * @return
         */
        private String getTestMethodName(Description description) {
            return description.getTestClass().getName() + "." + description.getMethodName();
        }

        /**
         * jacocoにより計測した行ごとのCoverageを回収し，TestResultsに格納する．
         * 
         * @throws IOException
         */
        private void collectRuntimeData(final Description description) throws IOException {
            final CoverageBuilder coverageBuilder = new CoverageBuilder();
            analyzeJacocoRuntimeData(coverageBuilder);
            addJacocoCoverageToTestResults(coverageBuilder, description);
        }

        /**
         * jacocoにより計測した行ごとのCoverageを回収する．
         * 
         * @param coverageBuilder 計測したCoverageを格納する保存先
         * @throws IOException
         */
        private void analyzeJacocoRuntimeData(final CoverageBuilder coverageBuilder) throws IOException {
            final ExecutionDataStore executionData = new ExecutionDataStore();
            final SessionInfoStore sessionInfo = new SessionInfoStore();
            jacocoRuntimeData.collect(executionData, sessionInfo, false);

            final Analyzer analyzer = new Analyzer(executionData, coverageBuilder);

            for (final ExecutionData data : executionData.getContents()){
                if (!data.hasHits()){
                    continue;
                }

                final String strFqn = data.getName().replace("/", ".");
                final byte[] bytecode = IOUtils.toByteArray(getTargetClassInputStream(strFqn));
                analyzer.analyzeClass(bytecode, strFqn);
            }
        }

        /**
         * 回収したCoverageを型変換しTestResultsに格納する．
         * 
         * @param coverageBuilder Coverageが格納されたビルダー
         * @param description テストの実行情報
         */
        private void addJacocoCoverageToTestResults(final CoverageBuilder coverageBuilder,
            final Description description) {
            final String testMethodFQN = getTestMethodName(description);
            List<Coverage> coverages = coverageBuilder.getClasses().stream().map(c -> new Coverage(c))
                .collect(Collectors.toList());

            final TestResult testResult = new TestResult(testMethodFQN, wasFailed, coverages);
            if (hasErrorMassage && !description.getMethodName().equals("initializationError")) {
                testResults.add(testResult);
            }
        }
    }

}
