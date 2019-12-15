package jp.posl.jprophet;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FileUtils;

import jp.posl.jprophet.project.GradleProject;
import jp.posl.jprophet.project.Project;
import jp.posl.jprophet.fl.spectrumbased.SpectrumBasedFaultLocalization;
import jp.posl.jprophet.fl.FaultLocalization;
import jp.posl.jprophet.fl.Suspiciousness;
import jp.posl.jprophet.fl.spectrumbased.strategy.*;
import jp.posl.jprophet.operation.*;
import jp.posl.jprophet.patch.PatchCandidate;
import jp.posl.jprophet.test.executor.TestExecutor;
import jp.posl.jprophet.test.executor.UnitTestExecutor;
import jp.posl.jprophet.test.result.TestResult;
import jp.posl.jprophet.test.result.TestResultStore;
import jp.posl.jprophet.test.exporter.TestResultExporter;
import jp.posl.jprophet.test.exporter.CSVTestResultExporter;

public class JProphetMain {
    public static void main(String[] args) {
        final String buildDir = "./tmp/"; 
        final String resultDir = "./result/"; 
        String projectPath = "src/test/resources/FizzBuzz01";
        if(args.length > 0){
            projectPath = args[0];
        }
        final Project                  project                  = new GradleProject(projectPath);
        final RepairConfiguration      config                   = new RepairConfiguration(buildDir, resultDir, project);
        final Coefficient              coefficient              = new Jaccard();
        final FaultLocalization        faultLocalization        = new SpectrumBasedFaultLocalization(config, coefficient);
        final PatchCandidateGenerator  patchCandidateGenerator  = new PatchCandidateGenerator();
        final PatchEvaluator           patchEvaluator           = new PatchEvaluator();
        final TestExecutor             testExecutor             = new UnitTestExecutor();
        final PatchedProjectGenerator    patchedProjectGenerator  = new PatchedProjectGenerator(config);
        final TestResultStore          testResultStore          = new TestResultStore();
        final TestResultExporter       testResultExporter       = new CSVTestResultExporter(resultDir);

        final List<AstOperation> operations = new ArrayList<AstOperation>(Arrays.asList(
            new CondRefinementOperation(),
            new CondIntroductionOperation(), 
            new CtrlFlowIntroductionOperation(), 
            new InsertInitOperation(), 
            new VariableReplacementOperation(),
            new CopyReplaceOperation()
        ));

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

    public boolean run(RepairConfiguration config, FaultLocalization faultLocalization, PatchCandidateGenerator patchCandidateGenerator,
            List<AstOperation> operations, PatchEvaluator patchEvaluator, TestExecutor testExecutor,
            PatchedProjectGenerator patchedProjectGenerator, TestResultStore testResultStore, TestResultExporter testResultExporter
            ) {
        // フォルトローカライゼーション
        final List<Suspiciousness> suspiciousenesses = faultLocalization.exec();
        
        // 各ASTに対して修正テンプレートを適用し抽象修正候補の生成
        final List<PatchCandidate> patchCandidates = patchCandidateGenerator.exec(config.getTargetProject(), operations);
        
        // 学習モデルやフォルトローカライゼーションのスコアによってソート
        patchEvaluator.descendingSortBySuspiciousness(patchCandidates, suspiciousenesses);
        
        // 修正パッチ候補ごとにテスト実行
        for(PatchCandidate patchCandidate: patchCandidates) {
            Project patchedProject = patchedProjectGenerator.applyPatch(patchCandidate);
            final List<TestResult> results = testExecutor.exec(new RepairConfiguration(config, patchedProject));
            testResultStore.addTestResults(results, patchCandidate);
            if(results.get(0).getIsSuccess()) { //ここが微妙な気がする
                testResultExporter.export(testResultStore);
                return true;
            }
        }
        testResultExporter.export(testResultStore);
        return false;
    }
}
