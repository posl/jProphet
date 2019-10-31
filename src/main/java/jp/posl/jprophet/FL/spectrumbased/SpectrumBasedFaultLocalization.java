package jp.posl.jprophet.fl.spectrumbased;

import jp.posl.jprophet.RepairConfiguration;
import jp.posl.jprophet.fl.spectrumbased.coverage.CoverageCollector;
import jp.posl.jprophet.fl.spectrumbased.coverage.TestResults;
import jp.posl.jprophet.fl.spectrumbased.strategy.Coefficient;
import jp.posl.jprophet.fl.FaultLocalization;
import jp.posl.jprophet.fl.Suspiciousness;
import jp.posl.jprophet.fl.SuspiciousnessList;
import jp.posl.jprophet.fl.spectrumbased.statement.SuspiciousnessCollector;
import jp.posl.jprophet.ProjectBuilder;
import java.util.List;
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
     * @return SuspiciousnessList 
     */
    @Override
    public SuspiciousnessList exec() {
        List<Suspiciousness> suspiciousnesses = new ArrayList<Suspiciousness>();;
        TestResults testResults;
        CoverageCollector coverageCollector = new CoverageCollector(buildPath);

        try {
            testResults = coverageCollector.exec(sourceClassFileFqns, testClassFileFqns);
            SuspiciousnessCollector suspiciousnessCollector = new SuspiciousnessCollector(testResults, coefficient);
            suspiciousnessCollector.exec();
            suspiciousnesses = suspiciousnessCollector.getSuspiciousnesses();
        } catch (Exception e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
            System.exit(-1);
        }
        return new SuspiciousnessList(suspiciousnesses);
    }
}
