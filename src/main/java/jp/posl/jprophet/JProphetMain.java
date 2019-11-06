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
import jp.posl.jprophet.test.TestExecutor;

public class JProphetMain {
    public static void main(String[] args) {
        final String buildDir = "./tmp/"; 
        final String resultDir = "./result/"; 
        String projectPath = "src/test/resources/testGradleProject01";
        if(args.length > 0){
            projectPath = args[0];
        }
        final Project                  project                  = new GradleProject(projectPath);
        final RepairConfiguration      config                   = new RepairConfiguration(buildDir, resultDir, project);
        final Coefficient              coefficient              = new Jaccard();
        final FaultLocalization        faultLocalization        = new SpectrumBasedFaultLocalization(config, coefficient);
        final PatchCandidateGenerator  patchCandidateGenerator  = new PatchCandidateGenerator();
        final PlausibilityAnalyzer     plausibilityAnalyzer     = new PlausibilityAnalyzer();  
        final PatchEvaluator           patchEvaluator           = new PatchEvaluator();
        final StagedCondGenerator      stagedCondGenerator      = new StagedCondGenerator();
        final TestExecutor             testExecutor             = new TestExecutor();
        final FixedProjectGenerator    fixedProjectGenerator    = new FixedProjectGenerator();

        final List<AstOperation> operations = new ArrayList<AstOperation>(Arrays.asList(
            new CondRefinementOperation(),
            new CondIntroductionOperation(), 
            new CtrlFlowIntroductionOperation(), 
            new InsertInitOperation(), 
            new VariableReplacementOperation(),
            new CopyReplaceOperation()
        ));

        final JProphetMain jprophet = new JProphetMain();
        jprophet.run(config, faultLocalization, patchCandidateGenerator, operations, plausibilityAnalyzer, patchEvaluator, stagedCondGenerator, testExecutor, fixedProjectGenerator);

        try {
            FileUtils.deleteDirectory(new File(buildDir));
            FileUtils.deleteDirectory(new File(resultDir));
        } catch (IOException e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
    }

    private void run(RepairConfiguration config, FaultLocalization faultLocalization,
            PatchCandidateGenerator patchCandidateGenerator, List<AstOperation> operations, PlausibilityAnalyzer plausibilityAnalyzer, PatchEvaluator patchEvaluator,
            StagedCondGenerator stagedCondGenerator, TestExecutor testExecutor, FixedProjectGenerator fixedProjectGenerator
            ) {
        // フォルトローカライゼーション
        List<Suspiciousness> suspiciousenesses = faultLocalization.exec();
        
        // 各ASTに対して修正テンプレートを適用し抽象修正候補の生成
        List<PatchCandidate> abstractPatchCandidates = patchCandidateGenerator.exec(config.getTargetProject(), operations);
        
        // 学習モデルとフォルトローカライゼーションのスコアによってソート
        patchEvaluator.descendingSortBySuspiciousness(abstractPatchCandidates, suspiciousenesses);
        
        // 抽象修正候補中の条件式の生成
        for(PatchCandidate abstractRepairCandidate: abstractPatchCandidates) {
            List<PatchCandidate> patchCandidates = stagedCondGenerator.applyConditionTemplate(abstractRepairCandidate);
            for(PatchCandidate patchCandidate: patchCandidates) {
                Project fixedProject = fixedProjectGenerator.exec(config, patchCandidate);
                if(testExecutor.run(new RepairConfiguration(config, fixedProject))) {
                    return;
                }
            }
        }
    }
}
