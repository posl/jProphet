package jp.posl.jprophet;


import jp.posl.jprophet.ProjectConfiguration;
import jp.posl.jprophet.FL.Suspiciousness;
import jp.posl.jprophet.FL.CoverageProject;
import jp.posl.jprophet.ProjectBuilder;
import java.util.List;
import java.util.ArrayList;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.File;
import java.util.stream.Collectors;
import java.io.IOException;
import java.util.regex.Pattern;
import java.util.regex.Matcher;



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
		//System.out.println(project.getSourceFilePaths());
		//System.out.println(project.getTestFilePaths());

		List<String> SourceFilePaths = project.getSourceFilePaths();
		List<String> TestFilePaths = project.getTestFilePaths();
		List<String> SourceFileNames = new ArrayList<String>();
		List<String> TestFileNames = new ArrayList<String>();

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

		//System.out.println(SourceFileNames);
		//System.out.println(TestFileNames);

		try{
		this.classDir =  Paths.get(this.buildPath);

		List<File> classFilelist = Files.walk(classDir).map(path -> path.toFile()).filter(file -> file.isFile()).collect(Collectors.toList());

		this.classFilePaths = classFilelist.stream().map(f -> 
				f.getPath()	
			).collect(Collectors.toList());

		}catch(NullPointerException | IOException e){
			//例外処理
		}
			
		//System.out.println(classFilePaths);

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

		

		CoverageProject coverageproject = new CoverageProject(System.out);
		try{
			coverageproject.execute();
		}catch (Exception e){
			//例外処理
		}

		return list;
	}
	
}
