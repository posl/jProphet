package jp.posl.jprophet.fl.spectrumbased.coverage;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.bcel.classfile.ClassFormatException;
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
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;

import jp.posl.jprophet.test.executor.TestThread;

/**
 * junit+jacocoでテスト対象プロジェクトのカバレッジを回収する
 */
public class CoverageCollector {
    private MemoryClassLoader memoryClassLoader;
    private final IRuntime jacocoRuntime;
    private final Instrumenter jacocoInstrumenter;
    private final RuntimeData jacocoRuntimeData;

    //private final BuildResults buildResults;

    private final long waitTime = 5000; //タイムアウトさせる時間[ms]

    public CoverageCollector(String buildpath) {
        //this.memoryClassLoader = null;
        this.jacocoRuntime = new LoggerRuntime();
        this.jacocoInstrumenter = new Instrumenter(jacocoRuntime);
        this.jacocoRuntimeData = new RuntimeData();

        try {
            jacocoRuntime.startup(jacocoRuntimeData);
        } catch (final Exception e) {
            e.printStackTrace();
        }

        //this.buildResults = buildResults;

        // ここであらかじめビルド済みのクラスファイルをクラスローダーが読み込んでおく
        try {
            //this.memoryClassLoader = new MemoryClassLoader(new URL[] { new URL("file:./" + buildpath + "/") });
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

        //this.jacocoRuntime.startup(this.jacocoRuntimeData);

        //loadInstrumentedClasses(sourceFQNs);
        //final List<Class<?>> junitClasses = loadInstrumentedClasses(testFQNs);
        //loadInstrumentedClasses(testFQNs);

        loadInstrumentedClasses(Stream.concat(sourceFQNs.stream(), testFQNs.stream()).collect(Collectors.toList()));
        final List<Class<?>> junitClasses = this.loadAllClasses(testFQNs);

    
        /*
        for (Class<?> junitClass : junitClasses) {
            final JUnitCore junitCore = new JUnitCore();
            final RunListener listener = new CoverageMeasurementListener(sourceFQNs, testResults);
            junitCore.addListener(listener);
            
            //タイムアウト処理
            TestThread testThread = new TestThread(junitCore, junitClass);
            testThread.start();
            try {
                System.out.println("junitCore START");
                //waitTime ms 経過でスキップ
                testThread.join(waitTime);
            } catch (InterruptedException e) {
                System.out.println("junitCore TIMEOUT");
                System.err.println(e.getMessage());
                e.printStackTrace();
                System.exit(-1);
            }
            System.out.println("junitCore END");
        }
        */
        
        
        for (Class<?> junitClass : junitClasses) {
            final JUnitCore junitCore = new JUnitCore();
            final RunListener listener = new CoverageMeasurementListener(sourceFQNs, testResults);
            junitCore.addListener(listener);
            Result result = junitCore.run(junitClass);
            System.out.println("junitCore END");
        }
        
        
        /*
        try {
            final JUnitCore junitCore = new JUnitCore();
            final RunListener listener = new CoverageMeasurementListener(sourceFQNs, testResults);
            junitCore.addListener(listener);
            junitCore.run(junitClasses.toArray(new Class<?>[junitClasses.size()]));
        } catch (Exception e) {}
        */

        return testResults;
    }

    /**
    * MemoryClassLoaderに対して全てのバイトコード定義を追加する（ロードはせず）．<br>
    * プロダクト系ソースコードのみJaCoCoインストルメントを適用する．
    *
    * @param memoryClassLoader
    * @param fqns
    * @param isInstrument
    * @throws IOException
    */
    /*
    private void addAllDefinitions(final MemoryClassLoader memoryClassLoader,
        final List<String> fqns) throws IOException {
        for (final JavaBinaryObject jmo : buildResults.binaryStore.getAll()) {
            final String fqn = jmo.getFqn();
            final byte[] rawBytecode = jmo.getByteCode();
            final byte[] bytecode = jmo.isTest() ? rawBytecode : instrumentBytecode(rawBytecode);
            memoryClassLoader.addDefinition(fqn, bytecode);
        }
    }
    */

    private byte[] instrumentBytecode(final byte[] bytecode) throws IOException {
        return jacocoInstrumenter.instrument(bytecode, "");
    }

    private List<Class<?>> loadAllClasses(final List<String> fqns) throws ClassNotFoundException {
        final List<Class<?>> classes = new ArrayList<>();
        for (final String fqn : fqns) {
            classes.add(this.memoryClassLoader.loadClass(fqn));
        }
        return classes;
    }

    /**
     * jacoco計測のためのクラス書き換えを行い，その書き換え結果をクラスロードする．
     * 
     * @param fqns 書き換え対象（計測対象）クラスのFQNs
     * @return 書き換えたクラスオブジェクトs
     * @throws Exception
     */
    private List<Class<?>> loadInstrumentedClasses(List<String> fqns) throws Exception {
        List<Class<?>> loadedClasses = new ArrayList<>();
        for (final String fqn : fqns) {
            final byte[] instrumentedData = instrument(fqn);
            loadedClasses.add(loadClass(fqn, instrumentedData));
            //loadedClasses.add(this.memoryClassLoader.loadClass(fqn));
        }
        return loadedClasses;
    }

    /**
     * jacoco計測のためのクラス書き換えを行う．
     * 
     * @param fqn 書き換え対象（計測対象）クラスのFQNs
     * @return 書き換えた
     * @throws Exception
     */
    private byte[] instrument(final String fqn) throws Exception {
        return this.jacocoInstrumenter.instrument(getTargetClassInputStream(fqn), fqn);
    }

    /**
     * MemoryClassLoaderを使ったクラスのロード．
     * 
     * @param fqn
     * @param bytes
     * @return
     * @throws ClassNotFoundException
     */
    private Class<?> loadClass(final String fqn, final byte[] bytes) throws ClassNotFoundException {
        this.memoryClassLoader.addDefinition(fqn, bytes);
        return this.memoryClassLoader.loadClass(fqn); // force load instrumented class.
    }

    /**
     * classファイルのInputStreamを取り出す．
     * 
     * @param fqn 読み込み対象のFQN
     * @return
     */
    private InputStream getTargetClassInputStream(final String fqn) {
        final String resource = fqn.replace('.', '/') + ".class";
        //return getClass().getResourceAsStream(resource);
        InputStream is = this.memoryClassLoader.getResourceAsStream(resource);
        return is;
    }

    private URL[] convertClasspathsToURLs(final List<String> classpaths) {
        return classpaths.stream()
            .map(cp -> Paths.get(cp).toUri())
            .map(uri -> toURL(uri))
            .toArray(URL[]::new);
    }

    private URL toURL(final URI uri) {
        try {
          return uri.toURL();
        } catch (MalformedURLException e) {
          // TODO 自動生成された catch ブロック
          e.printStackTrace();
        }
        // TODO
        return null;
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
        private final Description FAILED = Description.createTestDescription("failed", "failed");

        final private List<String> measuredClasses;
        final public TestResults testResults;
        private boolean wasFailed;

        /**
         * constructor
         * 
         * @param measuredFQNs 計測対象のクラス名一覧
         * @param storedTestResults テスト実行結果の保存先
         * @throws Exception
         */
        public CoverageMeasurementListener(List<String> measuredFQNs, TestResults storedTestResults)
                throws Exception {
            //jacocoRuntime.startup(jacocoRuntimeData);
            this.testResults = storedTestResults;
            this.measuredClasses = measuredFQNs;
        }

        @Override
        public void testStarted(Description description) {
            resetJacocoRuntimeData();
            wasFailed = false;
        }

        @Override
        public void testFailure(Failure failure) {
            //noteTestExecutionFail(failure);
            wasFailed = true;
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
         * Failureオブジェクトの持つDescriptionに，当該テストがfailしたことをメモする．
         * @param failure
         */
        private void noteTestExecutionFail(Failure failure) {
            failure.getDescription().addChild(FAILED);
        }

        /**
         * Descriptionから当該テストがfailしたかどうかを返す．
         * @param description
         * @return テストがfailしたかどうか
         */
        private boolean isFailed(Description description) {
            return description.getChildren().contains(FAILED);
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
            //jacocoRuntime.shutdown();

            final Analyzer analyzer = new Analyzer(executionData, coverageBuilder);
            
            for (final String measuredClass : measuredClasses) {
                analyzer.analyzeClass(getTargetClassInputStream(measuredClass), measuredClass);
            }
            /*
            for (final ExecutionData data : executionData.getContents()) {
                if (!data.hasHits()) {
                    continue;
                }

                final String strFqn = data.getName().replace("/", ".");
                try {
                    final byte[] bytecode = instrument(strFqn);
                    analyzer.analyzeClass(bytecode, "");
                } catch (Exception e){

                }
                
            }
            */
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
            final boolean isFailed = isFailed(description);
            List<Coverage> coverages = coverageBuilder.getClasses().stream().map(c -> new Coverage(c))
                .collect(Collectors.toList());

            final TestResult testResult = new TestResult(testMethodFQN, isFailed, coverages);
            testResults.add(testResult);
        }
    }

}
