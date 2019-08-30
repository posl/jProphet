package jp.posl.jprophet;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ProjectConfiguration {
	private List<String> sourceFilePaths;
	private List<String> testFilePaths;
	private List<String> classPaths;
	private String buildPath;

	public ProjectConfiguration(String projectPath, String buildPath) {
		this.buildPath = buildPath;
		Path srcDir;
		Path testDir;
		try {
			srcDir =  Paths.get(projectPath + "/src/main");
			testDir = Paths.get(projectPath + "/src/test");

			List<File> srcFilelist = Files.walk(srcDir).map(path -> path.toFile()).filter(file -> file.isFile()).collect(Collectors.toList());
			List<File> testFilelist = Files.walk(testDir).map(path -> path.toFile()).filter(file -> file.isFile()).collect(Collectors.toList());
			this.sourceFilePaths = srcFilelist.stream().map(f -> 
				f.getPath()	
			).collect(Collectors.toList());

			this.testFilePaths = testFilelist.stream().map(f -> 
				f.getPath()	
			).collect(Collectors.toList());
			this.classPaths = new ArrayList<String>(Arrays.asList("src/main/resources/junit-4.11.jar"));

		} catch (NullPointerException | IOException e) {
			e.printStackTrace();
			this.sourceFilePaths = new ArrayList<String>();
			this.testFilePaths = new ArrayList<String>();
			this.classPaths = new ArrayList<String>();
			this.buildPath = "";
			return;
		}
		
	}
	public List<String> getSourceFilePaths() {
		return this.sourceFilePaths;
	}

	public List<String> getTestFilePaths() {
		return testFilePaths;
	}

	public List<String> getClassPaths() {
		return classPaths;
	}

	public String getBuildPath(){
		return this.buildPath;
	}
}

