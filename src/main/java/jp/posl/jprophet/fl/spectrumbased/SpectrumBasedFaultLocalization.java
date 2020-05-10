package jp.posl.jprophet.fl.spectrumbased;

import jp.posl.jprophet.RepairConfiguration;
import jp.posl.jprophet.fl.spectrumbased.coverage.CoverageCollector;
import jp.posl.jprophet.fl.spectrumbased.coverage.TestResult;
import jp.posl.jprophet.fl.spectrumbased.coverage.TestResults;
import jp.posl.jprophet.fl.spectrumbased.strategy.Coefficient;
import jp.posl.jprophet.fl.FaultLocalization;
import jp.posl.jprophet.fl.Suspiciousness;
import jp.posl.jprophet.fl.spectrumbased.statement.SuspiciousnessCollector;
import jp.posl.jprophet.ProjectBuilder;
import java.util.List;
import java.util.stream.Collectors;
import java.util.ArrayList;
import java.nio.file.Path;

/**
 * FaultLocalizationの実行
 */
public class SpectrumBasedFaultLocalization implements FaultLocalization{
    ProjectBuilder projectBuilder = new ProjectBuilder();
    Path classDir;
    String buildPath;
    List<String> sourceClassFileFqns = new ArrayList<String>();
    List<String> testClassFileFqns = new ArrayList<String>();
    Coefficient coefficient;

    /**
     * ソースファイルとテストファイルをビルドして,ビルドされたクラスのFQDNを取得
     * @param config
     * @param coefficient
     */
    public SpectrumBasedFaultLocalization(RepairConfiguration config, Coefficient coefficient) {
        this.projectBuilder.build(config);
        this.buildPath = config.getBuildPath();
        this.coefficient = coefficient;
        this.sourceClassFileFqns = config.getTargetProject().getSrcFileFqns(); 
        this.testClassFileFqns = config.getTargetProject().getTestFileFqns(); 
    }

    /**
     * テスト対象の全てのソースファイルの行ごとの疑惑値を算出する
     * @return 行ごとの疑惑値のリスト
     */
    @Override
    public List<Suspiciousness> exec() {
        List<Suspiciousness> suspiciousnesses = new ArrayList<Suspiciousness>();;
        TestResults testResults;
        CoverageCollector coverageCollector = new CoverageCollector(buildPath);
        
        try {
            testResults = coverageCollector.exec(sourceClassFileFqns, testClassFileFqns);
            System.out.println("cc END");

            System.out.println(testResults.getFailedTestNames());
            System.out.println(testResults.getFailedTestNames().size());
            List<TestResult> tr = testResults.getTestResults().stream()
                .filter(s -> s.getMethodName().equals("org.apache.commons.math.stat.FrequencyTest.testPcts"))
                .collect(Collectors.toList());

            List<TestResult> ft = testResults.getTestResults().stream()
                .filter(s -> s.wasFailed() == true)
                .collect(Collectors.toList());

            SuspiciousnessCollector suspiciousnessCollector = new SuspiciousnessCollector(testResults, coefficient);
            suspiciousnessCollector.exec();
            System.out.println("sc END");
            suspiciousnesses = suspiciousnessCollector.getSuspiciousnesses();
            System.out.println("gs END");
        } catch (Exception e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
            System.exit(-1);
        }
        return suspiciousnesses;
    }
}
