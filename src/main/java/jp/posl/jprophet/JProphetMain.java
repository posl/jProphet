package jp.posl.jprophet;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import jp.posl.jprophet.ProjectConfiguration;
import jp.posl.jprophet.FaultLocalization;
import jp.posl.jprophet.AstNode;
import jp.posl.jprophet.AstGenerator;
import jp.posl.jprophet.AbstractRepairCandidate;
import jp.posl.jprophet.ConcreteRepairCandidate;
import jp.posl.jprophet.RepairCandidateGenerator;
import jp.posl.jprophet.PlausibilityAnalyzer;
import jp.posl.jprophet.StagedCondGenerator;


public class JProphetMain {   
	
	
	
    public static void main(String[] args) {
    	ProjectConfiguration project = new ProjectConfiguration();
    	
    	// フォルトローカライゼーション
    	FaultLocalization faultLocalization = new FaultLocalization();
    	HashMap<AstNode, Integer> suspiciousenesses = faultLocalization.exec(project);
    	
    	// 修正対象コードの全ASTノードの取得
    	AstGenerator astGenerator = new AstGenerator();
    	List<AstNode> astNodes = astGenerator.getAllAstNode();
    	
    	// 各ASTに対して修正テンプレートを適用し抽象修正候補の生成
    	RepairCandidateGenerator repairCandidateGenerator = new RepairCandidateGenerator();
    	List<AbstractRepairCandidate> abstractRepairCandidates = new ArrayList<AbstractRepairCandidate>();
    	for(AstNode astNode : astNodes) {
    		abstractRepairCandidates.addAll(repairCandidateGenerator.applyTemplate(astNode));
    	}
    	
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
    			if(testExecutor.test(modifiedProject)) {
    				return;
    			}
    		}
    	}
    	
    	
    }
}
