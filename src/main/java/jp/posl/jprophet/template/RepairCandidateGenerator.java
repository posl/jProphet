package jp.posl.jprophet.template;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import jp.posl.jprophet.AbstractRepairCandidate;
import jp.posl.jprophet.AstNode;


public class RepairCandidateGenerator{
	public RepairCandidateGenerator() {
	}

	public List<AbstractRepairCandidate> applyTemplate(List<AstNode> astNodes) {
		List<AbstractRepairCandidate> candidates = new ArrayList<AbstractRepairCandidate>();
		List<AstOperation> astOperations = new ArrayList<AstOperation>(Arrays.asList(
			new CondRefinementOperation(),
			new CondIntroductionOperation(), 
			new CtrlFlowIntroductionOperation(), 
			new InsertInitOperation(), 
			new ValueReplacementOperation(),
			new CopyReplaceOperation()
		));

		for (AstNode astNode : astNodes) {
			for (AstOperation astOperation : astOperations){
				candidates.addAll(astOperation.exec(astNode).stream().map((n)->{
					return new AbstractRepairCandidate(n);
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
