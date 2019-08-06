package jp.posl.jprophet;

import jp.posl.jprophet.FL.TestRunner1;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import jp.posl.jprophet.ProjectConfiguration;
import jp.posl.jprophet.ProjectBuilder;
import jp.posl.jprophet.FL.CoreTutorial;
import jp.posl.jprophet.FL.ClassInfo;

public class FaultLocalization {
	public FaultLocalization() {

		String path = System.getProperty("user.dir");
        // System.out.println(path);
		List<String> sourceFiles = new ArrayList<>(Arrays.asList("jacoco_test/src/main/java/jcc/iftest.java"));
		List<String> testFiles = new ArrayList<>(Arrays.asList("jacoco_test/src/test/java/jcc/AppTest.java"));
		// List<String> testFiles = new ArrayList<>();

		List<String> classPaths = new ArrayList<>(Arrays.asList("junit-4.11.jar", "./output/"));
		ProjectConfiguration projectConfig = new ProjectConfiguration(sourceFiles, testFiles, classPaths);
		ProjectBuilder projectBuilder = new ProjectBuilder(projectConfig);
		projectBuilder.build("./output");
		projectBuilder.buildTest("./output");

		// test_jacoco
		CoreTutorial CoreT = new CoreTutorial(System.out);
		try{
			CoreT.execute();
			// System.out.println(System.getProperty("java.class.path"));
			
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
