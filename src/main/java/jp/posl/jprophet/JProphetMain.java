package jp.posl.jprophet;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import jp.posl.jprophet.project.GradleProject;
import jp.posl.jprophet.project.MavenProject;
import jp.posl.jprophet.project.Project;
import jp.posl.jprophet.fl.spectrumbased.SpectrumBasedFaultLocalization;
import jp.posl.jprophet.evaluator.PatchEvaluator;
import jp.posl.jprophet.fl.FaultLocalization;
import jp.posl.jprophet.fl.Suspiciousness;
import jp.posl.jprophet.fl.spectrumbased.strategy.*;
import jp.posl.jprophet.operation.*;
import jp.posl.jprophet.patch.PatchCandidate;
import jp.posl.jprophet.test.executor.TestExecutor;
import jp.posl.jprophet.test.executor.UnitTestExecutor;
import jp.posl.jprophet.test.result.TestExecutorResult;
import jp.posl.jprophet.test.result.TestResultStore;
import jp.posl.jprophet.test.exporter.TestResultExporter;
import jp.posl.jprophet.test.exporter.CSVTestResultExporter;
import jp.posl.jprophet.test.exporter.PatchDiffExporter;

public class JProphetMain {
    public static void main(String[] args) {
        final String buildDir = "./tmp/"; 
        final String resultDir = "./result/"; 
        //String projectPath = "src/test/resources/FizzBuzz01";
        String projectPath = "src/test/resources/lang_buggy";
        //String projectPath = "/Users/yamate/Desktop/TestDependentFiles";
        //String projectPath = "/Users/yamate/Documents/additional_data/Math_75_buggy";
        if(args.length > 0){
            projectPath = args[0];
        }
        final Project                  project                  = new MavenProject(projectPath);
        //final Project                  project                  = new GradleProject(projectPath);
        final RepairConfiguration      config                   = new RepairConfiguration(buildDir, resultDir, project);
        final Coefficient              coefficient              = new Jaccard();
        final FaultLocalization        faultLocalization        = new SpectrumBasedFaultLocalization(config, coefficient);
        final PatchCandidateGenerator  patchCandidateGenerator  = new PatchCandidateGenerator();
        final PatchEvaluator           patchEvaluator           = new PatchEvaluator();
        final TestExecutor             testExecutor             = new UnitTestExecutor();
        final PatchedProjectGenerator  patchedProjectGenerator  = new PatchedProjectGenerator(config);
        final TestResultStore          testResultStore          = new TestResultStore();

        final List<TestResultExporter> testResultExporters = new ArrayList<TestResultExporter>(Arrays.asList(
            new CSVTestResultExporter(resultDir),
            new PatchDiffExporter(resultDir)
        ));

        final List<AstOperation> operations = new ArrayList<AstOperation>(Arrays.asList(
            new CondRefinementOperation()
            // new CondIntroductionOperation(), 
            // new CtrlFlowIntroductionOperation(), 
            // new InsertInitOperation(), 
            // new VariableReplacementOperation(),
            // new CopyReplaceOperation()
        ));


        final JProphetMain jprophet = new JProphetMain();
        final boolean isRepairSuccess = jprophet.run(config, faultLocalization, patchCandidateGenerator, operations, patchEvaluator, testExecutor, patchedProjectGenerator, testResultStore, testResultExporters);
        try {
            FileUtils.deleteDirectory(new File(buildDir));
            if(!isRepairSuccess){
                FileUtils.deleteDirectory(new File(config.getFixedProjectDirPath() + FilenameUtils.getBaseName(project.getRootPath()))); //失敗した場合でもログは残す
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
    }

    public boolean run(RepairConfiguration config, FaultLocalization faultLocalization, PatchCandidateGenerator patchCandidateGenerator,
            List<AstOperation> operations, PatchEvaluator patchEvaluator, TestExecutor testExecutor,
            PatchedProjectGenerator patchedProjectGenerator, TestResultStore testResultStore, List<TestResultExporter> testResultExporters
            ) {
        System.out.println("FL START");
        // フォルトローカライゼーション
        final List<Suspiciousness> suspiciousenesses = faultLocalization.exec();
        System.out.println("FL END");
        
        // 各ASTに対して修正テンプレートを適用し抽象修正候補の生成
        final List<PatchCandidate> patchCandidates = patchCandidateGenerator.exec(config.getTargetProject(), operations);
        System.out.println("PATCH GEN END");
        
        // 学習モデルやフォルトローカライゼーションのスコアによってソート
        patchEvaluator.descendingSortBySuspiciousness(patchCandidates, suspiciousenesses);
        
        // 修正パッチ候補ごとにテスト実行
        for(PatchCandidate patchCandidate: patchCandidates) {
            Project patchedProject = patchedProjectGenerator.applyPatch(patchCandidate);
            final TestExecutorResult result = testExecutor.exec(new RepairConfiguration(config, patchedProject));
            testResultStore.addTestResults(result.getTestResults(), patchCandidate);
            if(result.canEndRepair()) {
                testResultExporters.stream().forEach(exporter -> exporter.export(testResultStore));
                return true;
            }
        }
        testResultExporters.stream().forEach(exporter -> exporter.export(testResultStore));
        return false;
    }
}
