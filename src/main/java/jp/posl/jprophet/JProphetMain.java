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
        final String buildDir = "./tmp/"; 
        final String resultDir = "./result/"; 
        String projectPath = "src/test/resources/testGradleProject01";
        if(args.length > 0){
            projectPath = args[0];
        }
        final Project                  project                  = new Project(projectPath);
        final RepairConfiguration      config                   = new RepairConfiguration(buildDir, resultDir, project);
        final Coefficient              coefficient              = new Jaccard();
        final FaultLocalization        faultLocalization        = new SpectrumBasedFaultLocalization(config, coefficient);
        final RepairCandidateGenerator repairCandidateGenerator = new RepairCandidateGenerator();
        final PlausibilityAnalyzer     plausibilityAnalyzer     = new PlausibilityAnalyzer();  
        final StagedCondGenerator      stagedCondGenerator      = new StagedCondGenerator();
        final TestExecutor             testExecutor             = new TestExecutor();
        final FixedProjectGenerator    fixedProjectGenerator    = new FixedProjectGenerator();

        final JProphetMain jprophet = new JProphetMain();
        jprophet.run(config, faultLocalization, repairCandidateGenerator, plausibilityAnalyzer, stagedCondGenerator, testExecutor, fixedProjectGenerator);

        try {
            FileUtils.deleteDirectory(new File(buildDir));
            FileUtils.deleteDirectory(new File(resultDir));
        } catch (IOException e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
    }

    private void run(RepairConfiguration config, FaultLocalization faultLocalization,
            RepairCandidateGenerator repairCandidateGenerator, PlausibilityAnalyzer plausibilityAnalyzer,
            StagedCondGenerator stagedCondGenerator, TestExecutor testExecutor, FixedProjectGenerator fixedProjectGenerator
            ) {
        // フォルトローカライゼーション
        List<Suspiciousness> suspiciousenesses = faultLocalization.exec();
        
        // 各ASTに対して修正テンプレートを適用し抽象修正候補の生成
        List<RepairCandidate> abstractRepairCandidates = new ArrayList<RepairCandidate>();
        abstractRepairCandidates.addAll(repairCandidateGenerator.exec(config.getTargetProject()));
        
        // 学習モデルとフォルトローカライゼーションのスコアによってソート
        List<RepairCandidate> sortedAbstractRepairCandidate = plausibilityAnalyzer.sortRepairCandidates(abstractRepairCandidates, suspiciousenesses);
        
        // 抽象修正候補中の条件式の生成
        for(RepairCandidate abstractRepairCandidate: sortedAbstractRepairCandidate) {
            List<RepairCandidate> repairCandidates = stagedCondGenerator.applyConditionTemplate(abstractRepairCandidate);
            for(RepairCandidate repairCandidate: repairCandidates) {
                Project fixedProject = fixedProjectGenerator.exec(config, repairCandidate);
                if(testExecutor.run(fixedProject, config)) {
                    return;
                }
            }
        }
    }
}
