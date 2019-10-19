package jp.posl.jprophet;

import java.util.ArrayList;
import java.util.List;
import jp.posl.jprophet.ProjectConfiguration;
import jp.posl.jprophet.SpectrumBasedFaultLocalization;
import jp.posl.jprophet.AbstractRepairCandidate;
import jp.posl.jprophet.ConcreteRepairCandidate;
import jp.posl.jprophet.RepairCandidateGenerator;
import jp.posl.jprophet.PlausibilityAnalyzer;
import jp.posl.jprophet.StagedCondGenerator;
import jp.posl.jprophet.FL.Suspiciousness;
import jp.posl.jprophet.FL.strategy.Coefficient;
import jp.posl.jprophet.FL.strategy.Jaccard;

import jp.posl.jprophet.test.TestExecutor;


public class JProphetMain {   
    public static void main(String[] args) {
        final String OUT_DIR = System.getProperty("java.io.tmpdir");
        try {
            String projectPath = "src/test/resources/testGradleProject01";
            if(args.length > 0)
                projectPath = args[0];
            final ProjectConfiguration project = new ProjectConfiguration(projectPath, OUT_DIR);
            final JProphetMain jprophet = new JProphetMain();
            jprophet.run(project);
        }
        catch(IllegalArgumentException e){
            e.printStackTrace();
            System.exit(-1);
        }
    }

    private void run(ProjectConfiguration project){
        // フォルトローカライゼーション
        Coefficient coefficient = new Jaccard();
        SpectrumBasedFaultLocalization faultLocalization = new SpectrumBasedFaultLocalization(project, coefficient);
        List<Suspiciousness> suspiciousenesses = faultLocalization.exec();
        
        // 各ASTに対して修正テンプレートを適用し抽象修正候補の生成
        RepairCandidateGenerator repairCandidateGenerator = new RepairCandidateGenerator();
        List<AbstractRepairCandidate> abstractRepairCandidates = new ArrayList<AbstractRepairCandidate>();
        abstractRepairCandidates.addAll(repairCandidateGenerator.exec(project));
        
        // 学習モデルとフォルトローカライゼーションのスコアによってソート
        PlausibilityAnalyzer plausibilityAnalyzer = new PlausibilityAnalyzer();  
        List<AbstractRepairCandidate> sortedAbstractRepairCandidate = plausibilityAnalyzer.sortRepairCandidates(abstractRepairCandidates, suspiciousenesses);
        
        
        // 抽象修正候補中の条件式の生成
        StagedCondGenerator stagedCondGenerator = new StagedCondGenerator();
        TestExecutor        testExecutor        = new TestExecutor();
        ProgramGenerator    programGenerator    = new ProgramGenerator();
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
