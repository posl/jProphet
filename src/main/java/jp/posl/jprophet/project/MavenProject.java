package jp.posl.jprophet.project;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;


public class MavenProject implements Project {

    private List<String> srcFilePaths;
    private List<String> testFilePaths;
    private List<String> srcFileFqns;
    private List<String> testFileFqns;
    private List<String> classPaths;
    private String rootPath;
    private static final String configFileName = "pom.xml";

    /**
     * mavenプロジェクトからソースファイルとテストファイルを収集 
     * @param rootPath mavenプロジェクトのルートディレクトリのパス
     */
    public MavenProject(String rootPath) {
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
            
            this.srcFilePaths = srcFileList.stream()
            .map(f -> f.getPath())
            .filter(s -> s.endsWith(".java"))
            .collect(Collectors.toList());
            this.testFilePaths = testFileList.stream()
            .map(f -> f.getPath())
            .filter(s -> s.endsWith(".java"))
            .collect(Collectors.toList());

            this.srcFileFqns = this.buildSrcFileFqns();
            this.testFileFqns = this.buildTestFileFqns();

            this.classPaths = buildClassFilePaths();

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
        return this.classPaths;
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

    /**
     * クラスファイルを取得する
     * @return クラスファイルのパスのリスト
     */
    private List<String> buildClassFilePaths() {
        final Path pomFilePath = Paths.get(this.rootPath + "/" + configFileName);
        return extractDependencyPaths(pomFilePath).stream()
            .map(p -> p.toString())
            .collect(Collectors.toList());
    }


    /**
     * pomファイルから依存ファイルの情報を抜き取る
     * @param pomFilePath 対象のpomファイルのパス
     * @return クラスファイルのリスト
     */
    private List<Path> extractDependencyPaths(final Path pomFilePath) {
        final List<Path> list = new ArrayList<>();
        try {
            final MavenXpp3Reader reader = new MavenXpp3Reader();
            final Model model = reader.read(Files.newBufferedReader(pomFilePath));
            final Path repositoryPath = Paths.get(rootPath)
              .resolve("src")
              .resolve("main")
              .resolve("resources")
              .resolve("dependency");
            
            //CIでは依存クラスファイルを取得することが難しいため、とりあえず依存ファイルをプロジェクト内に内包して、そこを参照するようにする

            /*
            final String userHome = System.getProperty("user.home");
            final Path repositoryPath = Paths.get(userHome)
              .resolve(".m2")
              .resolve("repository");
            */
    
        for (final Dependency dependency : model.getDependencies()) {
    
            Path path = repositoryPath;
            final String groupId = dependency.getGroupId();
            for (final String string : groupId.split("\\.")) {
                path = path.resolve(string);
            }
    
            final Path libPath = path.resolve(dependency.getArtifactId())
                .resolve(dependency.getVersion());
            if (!Files.isDirectory(libPath)) {
                continue;
            }

            Files.find(libPath, Integer.MAX_VALUE, (p, attr) -> p.toString()
                .endsWith(".jar"))
                .forEach(list::add);
    
            Files.find(libPath, Integer.MAX_VALUE, (p, attr) -> p.toString()
                .endsWith(".pom"))
                .map(this::extractDependencyPaths)
                .flatMap(Collection::stream)
                .forEach(list::add);
            }
        } catch (final IOException | XmlPullParserException e) {            
            System.err.println(e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
        return list;
      }
    

}