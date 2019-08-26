package jp.posl.jprophet;

import java.util.List;


public class ProjectConfiguration {
	private final List<String> sourceFilePaths;
	private final List<String> testFilePaths;
	private final List<String> classPaths;

	public ProjectConfiguration(final List<String> sourceFilePaths, final List<String> testFilePaths, final List <String> classPaths) {
		this.sourceFilePaths = sourceFilePaths;
		this.testFilePaths = testFilePaths;
		this.classPaths = classPaths;
		
		if(this.sourceFilePaths.size() < 1)
			this.sourceFilePaths.add("example/target01.java");
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
}
