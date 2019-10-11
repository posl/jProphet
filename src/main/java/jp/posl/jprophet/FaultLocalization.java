package jp.posl.jprophet;

import jp.posl.jprophet.ProjectConfiguration;
import jp.posl.jprophet.FL.Suspiciousness;
import jp.posl.jprophet.FL.CoverageCollector;
import jp.posl.jprophet.FL.TestResults;
import jp.posl.jprophet.FL.FullyQualifiedName;
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

	/**
	 * ソースファイルとテストファイルをビルドして,ビルドされたクラスのFQDNを取得
	 * @param project
	 */
	public FaultLocalization(ProjectConfiguration project) {
		this.projectBuilder.build(project);
		this.buildPath = project.getBuildPath();
		getFQDN(project);

		//確認用print
		System.out.println("Source FQDN -> " + SourceClassFilePaths);
		System.out.println("Test FQDN -> " + TestClassFilePaths);

	}

	/**
	 * テスト対象の全てのソースファイルの行ごとの疑惑値を算出する
	 * @return List[ファイルのFQDN, 行, 疑惑値]
	 */
	public List<Suspiciousness> exec() {
		List<Suspiciousness> list = new ArrayList<Suspiciousness>();
				
		List<FullyQualifiedName> sourceClass = new ArrayList<FullyQualifiedName>();
		List<FullyQualifiedName> testClass = new ArrayList<FullyQualifiedName>();
		TestResults testresults = new TestResults();

		//List<String> FQDN -> List<FullyQualifiedName> FQN
		int SCFPsize = SourceClassFilePaths.size();
		for(int k = 0; k < SCFPsize; k++){
			sourceClass.add(new FullyQualifiedName(SourceClassFilePaths.get(k)));
		}
		int TCFPsize = TestClassFilePaths.size();
		for(int k = 0; k < TCFPsize; k++){
			testClass.add(new FullyQualifiedName(TestClassFilePaths.get(k)));
		}

		CoverageCollector collector = new CoverageCollector(buildPath);
		try{
			testresults = collector.exec(sourceClass, testClass);
			list = new CalculateSuspiciousness(testresults).slist;

			//確認用print
			/*
			System.out.println(testresults.getFailedTestResults().get(0).getCoverages().get(0).getStatusOfLine());
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
			*/
			

		}catch (Exception e){
			System.out.println("例外");
		}

		return list;
	}

	/**
	 * ファイルパスからFQDNを取得する
	 * TODO もっといい書き方があるはず. ファイルパスを処理するクラスをつくってそっちに書いておいた方がいい?.
	 * @param project
	 */
	private void getFQDN(ProjectConfiguration project){
		List<String> SourceFilePaths = project.getSourceFilePaths();
		List<String> TestFilePaths = project.getTestFilePaths();
		List<String> SourceFileNames = new ArrayList<String>();
		List<String> TestFileNames = new ArrayList<String>();

		//ソースファイルのファイルパスを取得し,.javaを.classに書き換え
		int Ssize = project.getSourceFilePaths().size();
		for(int i = 0; i < Ssize; i++){	
			String str = SourceFilePaths.get(i);
			File file = new File(str);
			SourceFileNames.add(file.getName().replace(".java", ".class"));
		}

		//テストファイルのファイルパスを取得し,.javaを.classに書き換え
		int Tsize = project.getTestFilePaths().size();
		for(int i = 0; i < Tsize; i++){	
			String str = TestFilePaths.get(i);
			File file = new File(str);
			TestFileNames.add(file.getName().replace(".java", ".class"));
		}

		//ビルド済みのテスト対象ファイル(ソースファイルとテストファイルの両方が存在)を収集
		try{
		this.classDir =  Paths.get(this.buildPath);

		List<File> classFilelist = Files.walk(classDir).map(path -> path.toFile()).filter(file -> file.isFile()).collect(Collectors.toList());

		this.classFilePaths = classFilelist.stream().map(f -> 
				f.getPath()	
			).collect(Collectors.toList());

		}catch(NullPointerException | IOException e){
			//TODO 例外処理
		}
			
		//ビルド済みのファイルの種類(ソースかテストか)を分類し,.classと/を消してFQDNに変換
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
	}
	
}
