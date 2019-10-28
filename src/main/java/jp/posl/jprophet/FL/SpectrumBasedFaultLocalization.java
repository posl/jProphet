package jp.posl.jprophet.FL;

import jp.posl.jprophet.RepairConfiguration;
import jp.posl.jprophet.FL.coverage.CoverageCollector;
import jp.posl.jprophet.FL.coverage.TestResults;
import jp.posl.jprophet.FL.strategy.Coefficient;
import jp.posl.jprophet.Project;
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
    List<String> classFilePaths;
    List<String> sourceClassFilePaths = new ArrayList<String>();
    List<String> testClassFilePaths = new ArrayList<String>();
    Coefficient coefficient;

    /**
     * ソースファイルとテストファイルをビルドして,ビルドされたクラスのFQDNを取得
     * @param project
     */
    public SpectrumBasedFaultLocalization(RepairConfiguration config, Coefficient coefficient) {
        this.projectBuilder.build(config);
        this.buildPath = config.getBuildPath();
        this.coefficient = coefficient;
        getFQN(config.getTargetProject());
    }
    /**
     * テスト対象の全てのソースファイルの行ごとの疑惑値を算出する
     * @return SuspiciousnessList 
     */
    public SuspiciousnessList exec() {
        List<Suspiciousness> suspiciousnesses = new ArrayList<Suspiciousness>();;
        TestResults testResults;
        CoverageCollector coverageCollector = new CoverageCollector(buildPath);

        try {
            testResults = coverageCollector.exec(sourceClassFilePaths, testClassFilePaths);
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

    /**
     * ファイルパスからFQNを取得する
     * @param project
     */
    private void getFQN(Project project){
        final String gradleTestPath = "/src/test/java/";
        final String gradleSourcePath = "/src/main/java/";
        final String testFolderPath = project.getProjectPath() + gradleTestPath;
        final String sourceFolderPath = project.getProjectPath() + gradleSourcePath;
        for (String testPath : project.getTestFilePaths()){
            testClassFilePaths.add(testPath.replace(testFolderPath, "").replace("/", ".").replace(".java", ""));
        }
        for (String sourcePath : project.getSourceFilePaths()){
            sourceClassFilePaths.add(sourcePath.replace(sourceFolderPath, "").replace("/", ".").replace(".java", ""));
        }

    }
    
}
