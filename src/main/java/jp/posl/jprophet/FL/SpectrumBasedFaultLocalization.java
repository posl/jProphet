package jp.posl.jprophet.FL;

import jp.posl.jprophet.ProjectConfiguration;
import jp.posl.jprophet.FL.coverage.CoverageCollector;
import jp.posl.jprophet.FL.coverage.TestResults;
import jp.posl.jprophet.FL.strategy.Coefficient;
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
    public SpectrumBasedFaultLocalization(ProjectConfiguration project, Coefficient coefficient) {
        this.projectBuilder.build(project);
        this.buildPath = project.getBuildPath();
        this.coefficient = coefficient;
        getFQN(project);
    }
    /**
     * テスト対象の全てのソースファイルの行ごとの疑惑値を算出する
     * @return List[ファイルのFQDN, 行, 疑惑値]
     */
    public List<Suspiciousness> exec() {
        List<Suspiciousness> suspiciousnessList = new ArrayList<Suspiciousness>();
        TestResults testResults;
        CoverageCollector coverageCollector = new CoverageCollector(buildPath);
        try{
            testResults = coverageCollector.exec(sourceClassFilePaths, testClassFilePaths);
            SuspiciousnessCollector suspiciousnessCollector = new SuspiciousnessCollector(testResults, coefficient);
            suspiciousnessCollector.exec();
            suspiciousnessList = suspiciousnessCollector.getSuspiciousnessList();
        }catch (Exception e){
            System.err.println(e.getMessage());
            System.exit(-1);
        }
        return suspiciousnessList;
    }

    /**
     * ファイルパスからFQNを取得する
     * @param project
     */
    private void getFQN(ProjectConfiguration project){
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
