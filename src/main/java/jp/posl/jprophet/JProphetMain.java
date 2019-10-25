package jp.posl.jprophet;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;

import jp.posl.jprophet.FL.SpectrumBasedFaultLocalization;
import jp.posl.jprophet.FL.FaultLocalization;
import jp.posl.jprophet.FL.Suspiciousness;
import jp.posl.jprophet.FL.strategy.*;

import jp.posl.jprophet.test.TestExecutor;

public class JProphetMain {
    public static void main(String[] args) {
        final String outDir = "./tmp/"; 
        String projectPath = "src/test/resources/testGradleProject01";
        if(args.length > 0){
            projectPath = args[0];
        }
        final ProjectConfiguration     project                  = new ProjectConfiguration(projectPath, outDir);
        final Coefficient              coefficient              = new Jaccard();
        final FaultLocalization        faultLocalization        = new SpectrumBasedFaultLocalization(project, coefficient);
        final RepairCandidateGenerator repairCandidateGenerator = new RepairCandidateGenerator();
        final PlausibilityAnalyzer     plausibilityAnalyzer     = new PlausibilityAnalyzer();  
        final StagedCondGenerator      stagedCondGenerator      = new StagedCondGenerator();
        final TestExecutor             testExecutor             = new TestExecutor();
        final ProgramGenerator         programGenerator         = new ProgramGenerator();

        final JProphetMain jprophet = new JProphetMain();
        jprophet.run(project, faultLocalization, repairCandidateGenerator, plausibilityAnalyzer, stagedCondGenerator, testExecutor, programGenerator);
        try {
            FileUtils.deleteDirectory(new File(outDir));
        } catch (IOException e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
    }

    private void run(ProjectConfiguration project, FaultLocalization faultLocalization,
            RepairCandidateGenerator repairCandidateGenerator, PlausibilityAnalyzer plausibilityAnalyzer,
            StagedCondGenerator stagedCondGenerator, TestExecutor testExecutor, ProgramGenerator programGenerator) {
        // フォルトローカライゼーション
        List<Suspiciousness> suspiciousenesses = faultLocalization.exec();
        
        // 各ASTに対して修正テンプレートを適用し抽象修正候補の生成
        List<RepairCandidate> abstractRepairCandidates = new ArrayList<RepairCandidate>();
        abstractRepairCandidates.addAll(repairCandidateGenerator.exec(project));
        
        // 学習モデルとフォルトローカライゼーションのスコアによってソート
        List<RepairCandidate> sortedAbstractRepairCandidate = plausibilityAnalyzer.sortRepairCandidates(abstractRepairCandidates, suspiciousenesses);
        
        // 抽象修正候補中の条件式の生成
        for(RepairCandidate abstractRepairCandidate: sortedAbstractRepairCandidate) {
            List<RepairCandidate> repairCandidates = stagedCondGenerator.applyConditionTemplate(abstractRepairCandidate);
            for(RepairCandidate repairCandidate: repairCandidates) {
                ProjectConfiguration modifiedProject = programGenerator.applyPatch(project, repairCandidate);
                if(testExecutor.run(modifiedProject)) {
                    return;
                }
            }
        }
    }
}
