package jp.posl.jprophet;

import jp.posl.jprophet.ProjectConfiguration;
import jp.posl.jprophet.FL.Suspiciousness;
import jp.posl.jprophet.FL.CoverageProject;
import jp.posl.jprophet.FL.TestExecutor;
import jp.posl.jprophet.FL.TestResults;
import jp.posl.jprophet.FL.FullyQualifiedName;
import jp.posl.jprophet.FL.LineStatus;
import jp.posl.jprophet.FL.CalculateSuspiciousness;
import jp.posl.jprophet.ProjectBuilder;
import java.util.List;
import java.util.ArrayList;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.File;
import java.util.stream.Collectors;
import java.io.IOException;



public class FaultLocalization {
	ProjectBuilder projectBuilder = new ProjectBuilder();
	Path classDir;
	String buildPath;
	List<String> classFilePaths;
	List<String> SourceClassFilePaths = new ArrayList<String>();
	List<String> TestClassFilePaths = new ArrayList<String>();

	public FaultLocalization(ProjectConfiguration project) {
		this.projectBuilder.build(project);
		this.buildPath = project.getBuildPath();

		List<String> SourceFilePaths = project.getSourceFilePaths();
		List<String> TestFilePaths = project.getTestFilePaths();
		List<String> SourceFileNames = new ArrayList<String>();
		List<String> TestFileNames = new ArrayList<String>();

		//以下ファイルパスからFQDNを取得する処理
		//TODO もっといい書き方があるはず. ファイルパスを処理するクラスをつくってそっちに書いておいた方がいい.
		int Ssize = project.getSourceFilePaths().size();
		for(int i = 0; i < Ssize; i++){	
			String str = SourceFilePaths.get(i);
			File file = new File(str);
			SourceFileNames.add(file.getName().replace(".java", ".class"));
		}

		int Tsize = project.getTestFilePaths().size();
		for(int i = 0; i < Tsize; i++){	
			String str = TestFilePaths.get(i);
			File file = new File(str);
			TestFileNames.add(file.getName().replace(".java", ".class"));
		}

		try{
		this.classDir =  Paths.get(this.buildPath);

		List<File> classFilelist = Files.walk(classDir).map(path -> path.toFile()).filter(file -> file.isFile()).collect(Collectors.toList());

		this.classFilePaths = classFilelist.stream().map(f -> 
				f.getPath()	
			).collect(Collectors.toList());

		}catch(NullPointerException | IOException e){
			//TODO 例外処理
		}
			

		int Csize = classFilePaths.size();
		for(int i = 0; i < Csize; i++){	
			String str = classFilePaths.get(i);
			for(int j = 0; j < Ssize; j++){	
				String Sstr = SourceFileNames.get(j);
				if (str.contains(Sstr)){
					SourceClassFilePaths.add(str.replace(buildPath, "").replace(".class", "").replace("/", ".").substring(1));
				}
			}
			for(int k = 0; k < Tsize; k++){	
				String Tstr = TestFileNames.get(k);
				if (str.contains(Tstr)){
					TestClassFilePaths.add(str.replace(buildPath, "").replace(".class", "").replace("/", ".").substring(1));
				}
			}
		}

		System.out.println("Source FQDN -> " + SourceClassFilePaths);
		System.out.println("Test FQDN -> " + TestClassFilePaths);

	}

	public List<Suspiciousness> exec() {
		List<Suspiciousness> list = new ArrayList<Suspiciousness>();

		
		CoverageProject coverageproject = new CoverageProject(System.out, buildPath);
		try{
			coverageproject.execute(SourceClassFilePaths, TestClassFilePaths);
		}catch (Exception e){
			//例外処理
			System.out.println("エラー");
		}
		
		
		List<FullyQualifiedName> sourceClass = new ArrayList<FullyQualifiedName>();
		List<FullyQualifiedName> testClass = new ArrayList<FullyQualifiedName>();
		TestResults testresults = new TestResults();
		int SCFPsize = SourceClassFilePaths.size();
		for(int k = 0; k < SCFPsize; k++){
			sourceClass.add(new FullyQualifiedName(SourceClassFilePaths.get(k)));
		}
		int TCFPsize = TestClassFilePaths.size();
		for(int k = 0; k < TCFPsize; k++){
			testClass.add(new FullyQualifiedName(TestClassFilePaths.get(k)));
		}
		System.out.println("********************************");
		System.out.println(sourceClass.get(0).value);
		System.out.println(testClass.get(0).value);
		System.out.println(testClass.get(1).value);
		System.out.println(testClass.get(2).value);
		System.out.println(testClass.get(3).value);
		System.out.println(testClass.get(4).value);
		System.out.println("********************************");

		TestExecutor executor = new TestExecutor(buildPath);
		try{
			testresults = executor.exec(sourceClass, testClass);

			System.out.println("Failed");
			int Fsize = testresults.getFailedTestResults().size();
			int Testsize;
			for(int k = 0; k < Fsize; k++){
				System.out.println(testresults.getFailedTestResults().get(k).getMethodName().value);
				Testsize = testresults.getFailedTestResults().get(k).getCoverages().size();
				for(int l = 0; l < Testsize; l++){
					System.out.println(testresults.getFailedTestResults().get(k).getCoverages().get(l));
				}
				System.out.println("\n");
			}

			System.out.println("\nSuccessed");
			int Ssize = testresults.getSuccessedTestResults().size();
			int Testsize2;
			for(int k = 0; k < Ssize; k++){
				System.out.println(testresults.getSuccessedTestResults().get(k).getMethodName().value);
				Testsize2 = testresults.getSuccessedTestResults().get(k).getCoverages().size();
				for(int l = 0; l < Testsize2; l++){
					System.out.println(testresults.getSuccessedTestResults().get(k).getCoverages().get(l));
				}
				System.out.println("\n");
			}

			for(int k = 0; k < Ssize; k++){
				System.out.println(testresults.getSuccessedTestResults().get(k).getMethodName().value);
				Testsize2 = testresults.getSuccessedTestResults().get(k).getCoverages().size();
				for(int l = 0; l < Testsize2; l++){
					System.out.println(testresults.getSuccessedTestResults().get(k).getCoverages().get(l).getStatusOfLisne());
				}
				System.out.println("\n");
			}

			System.out.println("\nNCF of Line" + "6 in " + testresults.getSuccessedTestResults().get(0).getCoverages().get(0).getName() + " = " + new LineStatus(testresults, 3, 0).NCF);
			System.out.println("\nNUF of Line" + "6 in " + testresults.getSuccessedTestResults().get(0).getCoverages().get(0).getName() + " = " + new LineStatus(testresults, 3, 0).NUF);
			System.out.println("\nNCS of Line" + "6 in " + testresults.getSuccessedTestResults().get(0).getCoverages().get(0).getName() + " = " + new LineStatus(testresults, 3, 0).NCS);
			System.out.println("\nNUS of Line" + "6 in " + testresults.getSuccessedTestResults().get(0).getCoverages().get(0).getName() + " = " + new LineStatus(testresults, 3, 0).NUS);
			System.out.println(testresults.getSuccessedTestResults().get(0).getCoverages().get(0).getLength());
			System.out.println(testresults.getSuccessedTestResults().get(0).getCoverages().get(1).getLength());

			
			list = new CalculateSuspiciousness(testresults).slist;


		}catch (Exception e){
			System.out.println("例外");
		}
		
		return list;
	}
	
}
