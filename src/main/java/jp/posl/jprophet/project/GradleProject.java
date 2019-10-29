package jp.posl.jprophet.project;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class GradleProject implements Project{
    private List<String> srcFilePaths;
    private List<String> testFilePaths;
    private List<String> srcFileFqns;
    private List<String> testFileFqns;
    private List<String> classPaths;
    private String rootPath;

    /**
     * graldeプロジェクトからソースファイルとテストファイルを収集 
     * @param projectPath Gradleプロジェクトのルートディレクトリのパス
     */
    public GradleProject(String rootPath) {
        this.rootPath = rootPath;
        Path srcDir;
        Path testDir;
        
        try {
            srcDir =  Paths.get(rootPath + "/src/main");
            testDir = Paths.get(rootPath + "/src/test");

            List<File> srcFileList = Files.walk(srcDir)
                .map(path -> path.toFile())
                .filter(file -> file.isFile())
                .collect(Collectors.toList());
            List<File> testFileList = Files.walk(testDir)
                .map(path -> path.toFile())
                .filter(file -> file.isFile())
                .collect(Collectors.toList());
            
            this.srcFilePaths = srcFileList.stream().map(f -> 
                f.getPath()    
            ).collect(Collectors.toList());
            this.testFilePaths = testFileList.stream().map(f -> 
                f.getPath()    
            ).collect(Collectors.toList());

            this.srcFileFqns = this.buildSrcFileFqns();
            this.testFileFqns = this.buildTestFileFqns();

            // とりあえず依存関係はjunitだけ
            this.classPaths = new ArrayList<String>(Arrays.asList("src/main/resources/junit-4.11.jar"));

        } catch (NullPointerException | IOException e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
            System.exit(-1);
        }
    }

    /**
     * プロジェクトのソースファイルのFileLocatorオブジェクトを全て取得 
     * @return ソースファイルのList<FileLocator>
     */
    public List<FileLocator> getSrcFileLocators(){
        List<FileLocator> fileLocators = new ArrayList<FileLocator>();
        for(int i = 0; i < this.srcFilePaths.size(); i++){
           fileLocators.add(new FileLocator(this.srcFilePaths.get(i), this.srcFileFqns.get(i)));
        }
        return fileLocators;
    }; 

    /**
     * プロジェクトのテストファイルのFileLocatorオブジェクトを全てを取得
     * @return テストファイルのList<FileLocator>
     */
    public List<FileLocator> getTestFileLocators(){
        List<FileLocator> fileLocators = new ArrayList<FileLocator>();
        for(int i = 0; i < this.testFilePaths.size(); i++){
           fileLocators.add(new FileLocator(this.testFilePaths.get(i), this.testFileFqns.get(i)));
        }
        return fileLocators;
    }; 

    /**
     * プロジェクトのソースファイルのパスを全て取得 
     * @return ソースファイルのリスト
     */
    public List<String> getSrcFilePaths() {
        return this.srcFilePaths;
    }

    /**
     * プロジェクトのテストファイルのパスを全てを取得
     * @return テストファイルのパスのリスト
     */
    public List<String> getTestFilePaths() {
        return this.testFilePaths;
    }

    /**
     * プロジェクトのソースファイルのFQNを全て取得 
     * @return ソースファイルのFQNのリスト
     */
    public List<String> getSrcFileFqns(){
        return this.srcFileFqns;
    }; 

    /**
     * プロジェクトのテストファイルのFQNを全てを取得
     * @return テストファイルのFQNのリスト
     */
    public List<String> getTestFileFqns(){
        return this.testFileFqns;
    }; 

    /**
     * プロジェクトのソースファイルのFQNを全て生成 
     * @return ソースファイルのFQNのリスト
     */
    public List<String> buildSrcFileFqns(){
        List<String> fqns = new ArrayList<String>();
        for(String srcFilePaths: this.srcFilePaths){
            fqns.add(this.buildSrcFileFqn(srcFilePaths));
        }
        return fqns;
    }; 

    /**
     * プロジェクトのテストファイルのFQNを全てを生成
     * @return テストファイルのFQNのリスト
     */
    public List<String> buildTestFileFqns(){
        List<String> fqns = new ArrayList<String>();
        for(String testFilePaths: this.testFilePaths){
            fqns.add(this.buildTestFileFqn(testFilePaths));
        }
        return fqns;
    }; 



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
    public String getRootPath(){
        return this.rootPath;
    }

    /**
     * ソースファイルのFQNを生成
     * 
     * @param filePath jProphetプロジェクトのルートからのファイルの相対パス
     * @return FQN
     */
    private String buildSrcFileFqn(String filePath){
        final String gradleSrcPath = "/src/main/java/";
        final String srcDirPath = this.rootPath + gradleSrcPath;
        return filePath.replace(srcDirPath, "").replace("/", ".").replace(".java", "");
    }

    /**
     * テストファイルのFQNを生成
     * 
     * @param filePath jProphetプロジェクトのルートからのファイルの相対パス
     * @return FQN
     */
    private String buildTestFileFqn(String filePath){
        final String gradleTestPath = "/src/test/java/";
        final String testDirPath = this.rootPath + gradleTestPath;
        return filePath.replace(testDirPath, "").replace("/", ".").replace(".java", "");
    }
}

