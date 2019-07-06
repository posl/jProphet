package jp.posl.jprophet;

import java.util.HashMap;
import jp.posl.jprophet.ProjectConfiguration;
import jp.posl.jprophet.FL.CoreTutorial;

public class FaultLocalization {
	public FaultLocalization() {
		// test
		//new CoreTutorial(System.out).execute();
		CoreTutorial CoreT = new CoreTutorial(System.out);
		try{
			CoreT.execute();
		}catch (Exception e){
			//例外に対する処理
		}
		
		
	}
	public HashMap<AstNode, Integer> exec(ProjectConfiguration projectConfiguration) {
		return new HashMap<>();
	}
	
}
