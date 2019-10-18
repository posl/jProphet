package jp.posl.jprophet;

import jp.posl.jprophet.ProjectConfiguration;
import jp.posl.jprophet.FL.Suspiciousness;
import jp.posl.jprophet.FL.CoverageCollector;
import jp.posl.jprophet.FL.TestResults;
import jp.posl.jprophet.FL.SuspiciousnessCalculator;
import jp.posl.jprophet.ProjectBuilder;
import java.util.List;
import java.util.ArrayList;
import java.nio.file.Path;

/**
 * FaultLocalizationの実行
 */
public class FaultLocalization {
    ProjectBuilder projectBuilder = new ProjectBuilder();
    Path classDir;
    String buildPath;
    List<String> classFilePaths;
    List<String> sourceClassFilePaths = new ArrayList<String>();
    List<String> testClassFilePaths = new ArrayList<String>();

    /**
     * ソースファイルとテストファイルをビルドして,ビルドされたクラスのFQDNを取得
     * @param project
     */
    public FaultLocalization(ProjectConfiguration project) {
        this.projectBuilder.build(project);
        this.buildPath = project.getBuildPath();
        getFQN(project);
    }
    /**
     * テスト対象の全てのソースファイルの行ごとの疑惑値を算出する
     * @return List[ファイルのFQDN, 行, 疑惑値]
     */
    public List<Suspiciousness> exec() {
        List<Suspiciousness> suspiciousnessList = new ArrayList<Suspiciousness>();
        TestResults testResults;
        CoverageCollector collector = new CoverageCollector(buildPath);
        try{
            testResults = collector.exec(sourceClassFilePaths, testClassFilePaths);
            SuspiciousnessCalculator suspiciousnessCalculator = new SuspiciousnessCalculator(testResults);
            suspiciousnessCalculator.exec();
            suspiciousnessList = suspiciousnessCalculator.getSuspiciousnessList();
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
        String gradleTestPath = "/src/test/java/";
        String gradleSourcePath = "/src/main/java/";
        String testFolderPath = project.getProjectPath() + gradleTestPath;
        String sourceFolderPath = project.getProjectPath() + gradleSourcePath;
        for (String testPath : project.getTestFilePaths()){
            testClassFilePaths.add(testPath.replace(testFolderPath, "").replace("/", ".").replace(".java", ""));
        }
        for (String sourcePath : project.getSourceFilePaths()){
            sourceClassFilePaths.add(sourcePath.replace(sourceFolderPath, "").replace("/", ".").replace(".java", ""));
        }

    }
    
}
