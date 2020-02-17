package jp.posl.jprophet;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;

import jp.posl.jprophet.project.Project;
import jp.posl.jprophet.spotbugs.SpotBugsResultXMLReader;
import jp.posl.jprophet.spotbugs.SpotBugsWarning;
import jp.posl.jprophet.test.executor.SpotBugsTestExecutor;
import jp.posl.jprophet.test.executor.TestExecutor;
import jp.posl.jprophet.test.exporter.CSVTestResultExporter;
import jp.posl.jprophet.test.exporter.PatchDiffExporter;
import jp.posl.jprophet.test.exporter.TestResultExporter;
import jp.posl.jprophet.test.result.TestResultStore;
import jp.posl.jprophet.fl.FaultLocalization;
import jp.posl.jprophet.fl.spotbugsbased.SpotBugsBasedFaultLocalization;
import jp.posl.jprophet.operation.*;
import jp.posl.jprophet.project.GradleProject;
import jp.posl.jprophet.project.MavenProject;

import static org.assertj.core.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FilenameUtils;



/**
 * SpotBugsのワーニング解消機能の結合テスト
 */
public class SpotBugsIntegrationTest {

    private final String buildDir = "./tmp/"; 
    private final String resultDir = "./result/"; 
    private List<AstOperation> operations;
    public static String nowFile;
    public static int lineStart;
    public static int lineEnd;
    public static int id = 914;

    /**
     * オペレーションの準備
     */
    @Before
    public void SetUpOperations() {
        this.operations = new ArrayList<AstOperation>(Arrays.asList(
            new CondRefinementOperation(),
            new CondIntroductionOperation(), 
            new CtrlFlowIntroductionOperation(), 
            new VariableReplacementOperation(),
            new CopyReplaceOperation()
        ));

    }


    /**
     * 修正結果ファイルが出力されているかテスト
     */
    @Test
    public void testForRoughConstantValue() {
        String projectName = "src/test/resources/time";
        Project project = new MavenProject(projectName);
        SpotBugsResultXMLReader reader = new SpotBugsResultXMLReader();
        List<SpotBugsWarning> list = reader.readAllSpotBugsWarnings("patch_data/time/warnings.xml", project);

        for (SpotBugsWarning spotBugsWarning : list) {
            System.out.println(spotBugsWarning.getType());
            //nowFile = spotBugsWarning.getFqn().replace(".", "/");
            //lineStart = spotBugsWarning.getStartLine();
            //lineEnd = spotBugsWarning.getStartLine();
            //runjProphet(projectName);
        }
        
        /*
        File file = new File("result/result.csv");
        assertThat(file.exists()).isTrue();
        try {
            FileUtils.deleteDirectory(new File("./result/"));
        } catch (IOException e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
        */
    }


    /**
     * 指定したファイルに対してjProphetを実行する
     * @param projectPath 対象のプロジェクトのパス
     */
    private void runjProphet(String projectPath) {
        final Project                  project                  = new MavenProject(projectPath);
        final RepairConfiguration      config                   = new RepairConfiguration(buildDir, resultDir, project);
        final FaultLocalization        faultLocalization        = new SpotBugsBasedFaultLocalization(config);
        final PatchCandidateGenerator  patchCandidateGenerator  = new PatchCandidateGenerator();
        final PatchEvaluator           patchEvaluator           = new PatchEvaluator();
        final TestExecutor             testExecutor             = new SpotBugsTestExecutor(SpotBugsBasedFaultLocalization.getSpotBugsResultFilePath());
        final PatchedProjectGenerator  patchedProjectGenerator  = new PatchedProjectGenerator(config);
        final TestResultStore          testResultStore          = new TestResultStore();
        final List<TestResultExporter> testResultExporters = new ArrayList<TestResultExporter>(Arrays.asList(
            new CSVTestResultExporter(resultDir),
            new PatchDiffExporter(resultDir)
        ));
        final JProphetMain jprophet = new JProphetMain();
        final boolean isRepairSuccess = jprophet.run(config, faultLocalization, patchCandidateGenerator, operations, patchEvaluator, testExecutor, patchedProjectGenerator, testResultStore, testResultExporters);
        try {
            FileUtils.deleteDirectory(new File(buildDir));
            if(!isRepairSuccess){
                FileUtils.deleteDirectory(new File(config.getFixedProjectDirPath() + FilenameUtils.getBaseName(project.getRootPath())));
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
    }

}