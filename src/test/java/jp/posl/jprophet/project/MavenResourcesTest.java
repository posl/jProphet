package jp.posl.jprophet.project;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;

import jp.posl.jprophet.ProjectBuilder;
import jp.posl.jprophet.RepairConfiguration;
import jp.posl.jprophet.fl.spectrumbased.coverage.CoverageCollector;
import jp.posl.jprophet.fl.spectrumbased.coverage.TestResults;

import static org.assertj.core.api.Assertions.assertThat;
import java.io.File;
import java.io.IOException;

public class MavenResourcesTest {
    TestResults testResults;
    boolean isSuccess;

    @Before public void setUp(){
        final String buildDir = "./tmp/"; 
        final String resultDir = "./result/"; 
        String projectPath = "src/test/resources/MavenResourcesProject";

        final Project                  project                  = new MavenProject(projectPath);
        final RepairConfiguration      config                   = new RepairConfiguration(buildDir, resultDir, project);

        ProjectBuilder builder = new ProjectBuilder();
        this.isSuccess = builder.build(config);

        CoverageCollector coverageCollector = new CoverageCollector(config.getBuildPath());
        try {
            this.testResults = coverageCollector.exec(config.getTargetProject().getSrcFileFqns(), config.getTargetProject().getTestFileFqns());
        } catch (Exception e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
            System.exit(-1);
        }
        try {
            FileUtils.deleteDirectory(new File(buildDir));
        } catch (IOException e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * src/test/resources/のファイルを参照するようなテストに対応しているかのテスト
     */
    @Test public void testForResources() {
        assertThat(this.isSuccess); //ビルドができたか確認
        assertThat(!testResults.getTestResults().get(0).wasFailed()); //対象プロジェクトのテストの実行結果が正しいか
        assertThat(testResults.getTestResults().get(0).getCoverages().size()).isEqualTo(1); //対象プロジェクトのテストカバレッジが取れているか
    }


    
    
    
}