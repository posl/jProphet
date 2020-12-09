package jp.posl.jprophet.project;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
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
     * @param rootPath Gradleプロジェクトのルートディレクトリのパス
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
            this.classPaths = List.of("src/main/resources/junit-4.11.jar");

        } catch (NullPointerException | IOException e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
            System.exit(-1);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<FileLocator> getSrcFileLocators(){
        List<FileLocator> fileLocators = new ArrayList<FileLocator>();
        for(int i = 0; i < this.srcFilePaths.size(); i++){
           fileLocators.add(new FileLocator(this.srcFilePaths.get(i), this.srcFileFqns.get(i)));
        }
        return fileLocators;
    }; 

    /**
     * {@inheritDoc}
     */
    @Override
    public List<FileLocator> getTestFileLocators(){
        List<FileLocator> fileLocators = new ArrayList<FileLocator>();
        for(int i = 0; i < this.testFilePaths.size(); i++){
           fileLocators.add(new FileLocator(this.testFilePaths.get(i), this.testFileFqns.get(i)));
        }
        return fileLocators;
    }; 

    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> getSrcFilePaths() {
        return this.srcFilePaths;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> getTestFilePaths() {
        return this.testFilePaths;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> getSrcFileFqns(){
        return this.srcFileFqns;
    }; 

    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> getTestFileFqns(){
        return this.testFileFqns;
    }; 

    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> getClassPaths() {
        final Path settingFilePath = Paths.get(this.rootPath + "/" + "build.gradle");
        extractDependencyPaths(settingFilePath);
        return extractDependencyPaths(settingFilePath).stream()
            .map(p -> p.toString())
            .collect(Collectors.toList());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getRootPath(){
        return this.rootPath;
    }

    /**
     * プロジェクトのソースファイルのFQNを全て生成 
     * @return ソースファイルのFQNのリスト
     */
    private List<String> buildSrcFileFqns(){
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
    private List<String> buildTestFileFqns(){
        List<String> fqns = new ArrayList<String>();
        for(String testFilePaths: this.testFilePaths){
            fqns.add(this.buildTestFileFqn(testFilePaths));
        }
        return fqns;
    }; 

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

    private List<Path> extractDependencyPaths(final Path settingFilePath) {
        List<Path> paths = new ArrayList<Path>();
        try {
            List<String> lines = Files.readAllLines(settingFilePath, StandardCharsets.UTF_8);
            boolean isFoundStartLine = false;
            for (String line : lines) {
                String[] tokens = line.trim().split("\\s+");
                if(!isFoundStartLine && tokens[0].equals("dependencies")) isFoundStartLine = true;
                if(isFoundStartLine && tokens[0].equals("}")) isFoundStartLine = false;
                if(!isFoundStartLine) continue;
                if(!tokens[0].equals("implementation")) continue;

                String dependencyStr = tokens[1];
                String[] words = dependencyStr.replaceAll("[\'\"]", "").split(":");
                
                final String userHome = System.getProperty("user.home");
                final Path repositoryPath = Paths.get(userHome)
                    .resolve(".m2")
                    .resolve("repository");
                Path dependencyPath = repositoryPath;
                for(int i = 0; i < words.length; i++) {
                    if(i == 0) {
                        String[] domains = words[i].split("\\.");
                        for(String domain : domains) dependencyPath = dependencyPath.resolve(domain); 
                    }
                    else dependencyPath = dependencyPath.resolve(words[i]);
                    System.out.println(dependencyPath);
                }
                
                Files.find(dependencyPath, Integer.MAX_VALUE, (p, attr) -> p.toString()
                    .endsWith(".jar"))
                    .forEach(paths::add);
        
                Files.find(dependencyPath, Integer.MAX_VALUE, (p, attr) -> p.toString()
                    .endsWith(".pom"))
                    .map(this::extractDependencyPaths)
                    .flatMap(Collection::stream)
                    .forEach(paths::add);
            }
        } 
        catch (IOException e) {
            e.printStackTrace();
        }

        return paths;
    }
}

