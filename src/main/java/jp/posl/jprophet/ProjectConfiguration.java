package jp.posl.jprophet;

import java.util.List;

public class ProjectConfiguration {
	List<String> filePaths;
	public ProjectConfiguration(String[] args) {
		if(args.length < 1)
			this.filePaths.add("example/target01.java");
		else
			this.filePaths.add(args[0]);
	}

	public List<String> getFilePaths(){
		return this.filePaths;
	}
}
