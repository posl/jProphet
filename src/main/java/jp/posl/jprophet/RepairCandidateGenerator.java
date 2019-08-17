package jp.posl.jprophet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import jp.posl.jprophet.AbstractRepairCandidate;
import jp.posl.jprophet.RepairUnit;
import jp.posl.jprophet.operation.*;


public class RepairCandidateGenerator{

	/**
	 * ASTノードに対して修正テンプレートを適用し，修正候補群を生成する．
	 * 
	 * @param repairUnits テンプレートを適用するASTノードのリスト
	 * @return テンプレートが適用された修正候補のリスト
	 */
	public List<AbstractRepairCandidate> applyTemplate(List<RepairUnit> repairUnits) {
		List<AbstractRepairCandidate> candidates = new ArrayList<AbstractRepairCandidate>();
		List<AstOperation> astOperations = new ArrayList<AstOperation>(Arrays.asList(
			new CondRefinementOperation(),
			new CondIntroductionOperation(), 
			new CtrlFlowIntroductionOperation(), 
			new InsertInitOperation(), 
			new ValueReplacementOperation(),
			new CopyReplaceOperation()
		));

		for (RepairUnit repairUnit: repairUnits) {
			for (AstOperation astOperation : astOperations){
				candidates.addAll(astOperation.exec(repairUnit).stream().map((u)->{
					return new AbstractRepairCandidate(u.getCompilationUnit(), u.getFilePath());
				}).collect(Collectors.toList()));
			}	
		}

		//debug
		for(AbstractRepairCandidate candidate : candidates){
			System.out.println(candidate.toString());
		}
		return candidates;
	}


}
