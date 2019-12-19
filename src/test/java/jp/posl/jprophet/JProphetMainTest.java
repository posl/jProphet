package jp.posl.jprophet;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

import jp.posl.jprophet.fl.FaultLocalization;
import jp.posl.jprophet.fl.spectrumbased.SpectrumBasedFaultLocalization;
import jp.posl.jprophet.fl.spectrumbased.strategy.Coefficient;
import jp.posl.jprophet.fl.spectrumbased.strategy.Jaccard;
import jp.posl.jprophet.operation.AstOperation;
import jp.posl.jprophet.patchgenerator.AstPatchCandidateGenerator;
import jp.posl.jprophet.patchgenerator.PatchCandidateGenerator;
import jp.posl.jprophet.project.MavenProject;
import jp.posl.jprophet.project.Project;
import jp.posl.jprophet.test.executor.TestExecutor;
import jp.posl.jprophet.test.executor.UnitTestExecutor;
import jp.posl.jprophet.test.exporter.CSVTestResultExporter;
import jp.posl.jprophet.test.exporter.TestResultExporter;
import jp.posl.jprophet.test.result.TestResultStore;
import jp.posl.jprophet.operation.*;

import static org.assertj.core.api.Assertions.*;

public class JProphetMainTest {
    /**
     * FizzBuzz01プロジェクトのバグが治るかテスト
     * Value Replace Operationのみで治るバグ 
     */
    @Test public void testFizzBuzz(){
        String[] project = {"src/test/resources/FizzBuzz01"};
        JProphetMain.main(project);
        File file = new File("src/test/resources/FizzBuzz01");
        assertThat(file.exists()).isTrue();
        try {
            FileUtils.deleteDirectory(new File("./result/"));
        } catch (IOException e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
    }


    @Test public void testForMavenProject() {
        final List<AstOperation> operations = new ArrayList<AstOperation>(Arrays.asList(
            new CondRefinementOperation(),
            new CondIntroductionOperation(), 
            new CtrlFlowIntroductionOperation(), 
            new InsertInitOperation(), 
            new VariableReplacementOperation(),
            new CopyReplaceOperation()
        ));
        final String buildDir = "./tmp/";
        final String resultDir = "./result/";
        final Project                  project                  = new MavenProject("src/test/resources/MavenFizzBuzz01");
        final RepairConfiguration      config                   = new RepairConfiguration(buildDir, resultDir, project);
        final Coefficient              coefficient              = new Jaccard();
        final FaultLocalization        faultLocalization        = new SpectrumBasedFaultLocalization(config, coefficient);
        final PatchCandidateGenerator  patchCandidateGenerator  = new AstPatchCandidateGenerator(operations);
        final PatchEvaluator           patchEvaluator           = new PatchEvaluator();
        final TestExecutor             testExecutor             = new UnitTestExecutor();
        final PatchedProjectGenerator  patchedProjectGenerator  = new PatchedProjectGenerator(config);
        final TestResultStore          testResultStore          = new TestResultStore();
        final TestResultExporter       testResultExporter       = new CSVTestResultExporter(resultDir);
        final JProphetMain jprophet = new JProphetMain();
        final boolean isRepairSuccess = jprophet.run(config, faultLocalization, patchCandidateGenerator, operations, patchEvaluator, testExecutor, patchedProjectGenerator, testResultStore, testResultExporter);
        try {
            FileUtils.deleteDirectory(new File(buildDir));
            if(!isRepairSuccess){
                FileUtils.deleteDirectory(new File(resultDir));

            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
    }


    
}