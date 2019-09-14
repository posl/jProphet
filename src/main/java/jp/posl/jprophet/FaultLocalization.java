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



public class FaultLocalization {
	ProjectBuilder projectBuilder = new ProjectBuilder();
	Path classDir;
	List<String> classFilePaths;

	public FaultLocalization(ProjectConfiguration project) {
		this.projectBuilder.build(project);

		try{
		this.classDir =  Paths.get("FLtmp");

		List<File> classFilelist = Files.walk(classDir).map(path -> path.toFile()).filter(file -> file.isFile()).collect(Collectors.toList());

		this.classFilePaths = classFilelist.stream().map(f -> 
				f.getPath()	
			).collect(Collectors.toList());

		}catch(NullPointerException | IOException e){
			//例外処理
		}
			
		System.out.println(classFilePaths);

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
