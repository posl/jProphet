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

public class Project{
    private List<String> sourceFilePaths;
    private List<String> testFilePaths;
    private List<String> classPaths;
    private String projectPath;

    /**
     * graldeプロジェクトからソースファイルとテストファイルを収集 
     * @param projectPath Gradleプロジェクトのルートディレクトリのパス
     */
    public Project(String projectPath) {
        this.projectPath = projectPath;
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

            // とりあえず依存関係はjunitだけ
            this.classPaths = new ArrayList<String>(Arrays.asList("src/main/resources/junit-4.11.jar"));

        } catch (NullPointerException | IOException e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
            System.exit(-1);
        }
    }

    /**
     * プロジェクトのソースファイルのパスを全て取得 
     * @return ソースファイルのリスト
     */
    public List<String> getSourceFilePaths() {
        return this.sourceFilePaths;
    }

    /**
     * プロジェクトのテストファイルのパスを全てを取得
     * @return テストファイルのパスのリスト
     */
    public List<String> getTestFilePaths() {
        return this.testFilePaths;
    }

    /**
     * プロジェクトのビルドに必要なクラスパスを取得
     * @return クラスパスの一覧
     */
    public List<String> getClassPaths() {
        return this.classPaths;
    }

    /**
     * プロジェクトのパスを取得
     * @return プロジェクトのパス
     */
    public String getProjectPath(){
        return this.projectPath;
    }
}

