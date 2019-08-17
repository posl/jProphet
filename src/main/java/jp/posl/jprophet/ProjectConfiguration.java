package jp.posl.jprophet;

public class ProjectConfiguration {
	final String filePath;
	public ProjectConfiguration(String[] args) {
		if(args.length < 1){
			throw new IllegalArgumentException("'./gradlew run -Pargs=\"example/target01.java'のように実行時にファイルパスを引数に渡してください.");
		}
		this.filePath = args[0];
	}

	public String getFilePath(){
		return this.filePath;
	}
}
