package jp.posl.jprophet;

import jp.posl.jprophet.FL.TestRunner1;

import java.util.HashMap;
import jp.posl.jprophet.ProjectConfiguration;
import jp.posl.jprophet.FL.CoreTutorial;
import jp.posl.jprophet.FL.ClassInfo;

public class FaultLocalization {
	public FaultLocalization() {

		// test_jacoco
		CoreTutorial CoreT = new CoreTutorial(System.out);
		try{
			CoreT.execute();
			System.out.println(System.getProperty("java.class.path"));
			
		}catch (Exception e){
			//例外に対する処理
		}
		

		// ClassInfo ClassI = new ClassInfo(System.out);
		// try{
		// 	ClassI.execute();
		// }catch (Exception e){
		// 	System.out.println("エラー");
		// }

		
		// TestRunner1 Junittest1 = new TestRunner1();
		// Junittest1.runtest1();
		
		
	}
	public HashMap<AstNode, Integer> exec(ProjectConfiguration projectConfiguration) {
		return new HashMap<>();
	}
	
}
