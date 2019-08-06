package jp.posl.jprophet;

import java.util.List;

public class ProjectConfiguration {
	private final List<String> sourceFiles;
	private final List<String> testFiles;
	private final List<String> classPaths;

	public ProjectConfiguration(final List<String> sourceFiles, final List<String> testFiles, final List <String> classPaths) {
		this.sourceFiles = sourceFiles;
		this.testFiles = testFiles;
		this.classPaths = classPaths;
		
	}
	public List<String> getSourceFiles() {
		return sourceFiles;
	}

	public List<String> getTestFiles() {
		return testFiles;
	}

	public List<String> getClassPaths() {
		return classPaths;
	}
}
