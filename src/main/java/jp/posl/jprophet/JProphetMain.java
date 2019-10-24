package jp.posl.jprophet;

import java.util.ArrayList;
import java.util.List;
import jp.posl.jprophet.ProjectConfiguration;
import jp.posl.jprophet.FaultLocalization;
import jp.posl.jprophet.AbstractRepairCandidate;
import jp.posl.jprophet.ConcreteRepairCandidate;
import jp.posl.jprophet.RepairCandidateGenerator;
import jp.posl.jprophet.PlausibilityAnalyzer;
import jp.posl.jprophet.StagedCondGenerator;
import jp.posl.jprophet.FL.Suspiciousness;

import jp.posl.jprophet.test.TestExecutor;


public class JProphetMain {   
    public static void main(String[] args) {
        final String OUT_DIR = System.getProperty("java.io.tmpdir");
        String projectPath = "src/test/resources/testGradleProject01";
        if(args.length > 0)
            projectPath = args[0];
        final ProjectConfiguration project = new ProjectConfiguration(projectPath, OUT_DIR);
        final JProphetMain jprophet = new JProphetMain();
        final FaultLocalization faultLocalization = new FaultLocalization(project);
        final RepairCandidateGenerator repairCandidateGenerator = new RepairCandidateGenerator();
        final PlausibilityAnalyzer plausibilityAnalyzer = new PlausibilityAnalyzer();  
        final StagedCondGenerator stagedCondGenerator = new StagedCondGenerator();
        final TestExecutor        testExecutor        = new TestExecutor();
        final ProgramGenerator    programGenerator    = new ProgramGenerator();
        jprophet.run(project, faultLocalization, repairCandidateGenerator, plausibilityAnalyzer, stagedCondGenerator, testExecutor, programGenerator);
    }

    private void run(ProjectConfiguration project, FaultLocalization faultLocalization, RepairCandidateGenerator repairCandidateGenerator, PlausibilityAnalyzer plausibilityAnalyzer, StagedCondGenerator stagedCondGenerator, TestExecutor testExecutor, ProgramGenerator programGenerator){
        // フォルトローカライゼーション
        List<Suspiciousness> suspiciousenesses = faultLocalization.exec();
        
        // 各ASTに対して修正テンプレートを適用し抽象修正候補の生成
        List<AbstractRepairCandidate> abstractRepairCandidates = new ArrayList<AbstractRepairCandidate>();
        abstractRepairCandidates.addAll(repairCandidateGenerator.exec(project));
        
        // 学習モデルとフォルトローカライゼーションのスコアによってソート
        List<AbstractRepairCandidate> sortedAbstractRepairCandidate = plausibilityAnalyzer.sortRepairCandidates(abstractRepairCandidates, suspiciousenesses);
        
        // 抽象修正候補中の条件式の生成
        for(AbstractRepairCandidate abstractRepairCandidate: sortedAbstractRepairCandidate) {
            List<ConcreteRepairCandidate> concreteRepairCandidates = stagedCondGenerator.applyConditionTemplate(abstractRepairCandidate);
            for(ConcreteRepairCandidate concreteRepairCandidate: concreteRepairCandidates) {
                ProjectConfiguration modifiedProject = programGenerator.applyPatch(concreteRepairCandidate);
                if(testExecutor.run(modifiedProject)) {
                    return;
                }
            }
        }
    }
}
