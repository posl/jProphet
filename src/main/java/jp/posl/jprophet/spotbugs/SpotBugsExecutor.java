package jp.posl.jprophet.spotbugs;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import edu.umd.cs.findbugs.FindBugs;
import edu.umd.cs.findbugs.FindBugs2;
import edu.umd.cs.findbugs.TextUICommandLine;
import edu.umd.cs.findbugs.filter.FilterException;
import jp.posl.jprophet.RepairConfiguration;

//import com.github.spotbugs.*;



public class SpotBugsExecutor {

    private RepairConfiguration config;
    private File resultDir;
    private jp.posl.jprophet.Project project;


    /**
     * 対象のプロジェクトに対してSpotBugsを適用し、その結果をxmlファイルとして保存する
     * @param projectConfiguration 対象のプロジェクト
     * @param resultDir 結果を格納するディレクトリ
     */
    public SpotBugsExecutor(RepairConfiguration config, File resultDir) {
        this.config = config;
        this.resultDir = resultDir;
        this.project = config.getTargetProject();
    }

    /**
     * SpotBugsを適用する
     */
    public void exec() {
        /*
        ProjectBuilder projectBuilder = new ProjectBuilder();
        projectBuilder.build(projectConfiguration);

        if (!resultDir.exists()) {
            resultDir.mkdir();
        }
        */

        List<File> classFiles = searchClassFile();
        try {
            runSpotBugs(classFiles);
        }
        catch(FilterException|IOException|InterruptedException e) {
            e.printStackTrace();
        }


    }


    /**
     * ソースファイル名からクラスファイル名に変換する
     * @return クラスファイルのリスト
     */
    private List<File> searchClassFile() {
        List<File> classFiles = null;
        //config.getTargetProject().getSourceFilePaths().stream().map(path -> new File(path)).map(file -> file.getName()).map(name -> name.replace(".java", ".class")).map(name -> projectConfiguration.getBuildPath() + "/" + name).map(path -> new File(path)).collect(Collectors.toList()); // もっとシンプルな方法は無いか？
        return classFiles;
    }
    

    /**
     * クラスファイルを受け取り、それらを対象にSpotBugsを適用する
     * @param classFile クラスファイルのリスト
     * @throws FilterException
     * @throws IOException
     * @throws InterruptedException
     */
    private void runSpotBugs(List<File> classFile) throws FilterException, IOException, InterruptedException {
        
        //APIをいじって処理を書くのが難しいので、とりあえず対象プロジェクトにspotbugsのプラグインを入れておいてそれを実行する

        Runtime runtime = Runtime.getRuntime();
        String[] commands = { "./gradlew", "spotbugsMain" };
        try {
            Process process = runtime.exec(commands, null, new File(project.getProjectPath()));
            process.waitFor();
        } catch (IOException|InterruptedException e) {
            e.printStackTrace();
        }
        
        /*
        FindBugs2 findBugs = new FindBugs2();
        TextUICommandLine commandLine = new TextUICommandLine();
        final List<String> inputPaths = classFile.stream().map(x -> x.toPath().toString()).collect(Collectors.toList());
        final String outputPath = resultDir.getPath() + "/" + "result.xml";   
        Project project = new Project();
        List<String> argv = Arrays.asList("-xml", "-output", outputPath);
        for (String inputPath : inputPaths) {
            argv.add(inputPath);
            project.addFile(inputPath);
        }
        findBugs.setProject(project);
        System.out.println(findBugs.getProject().getFile(0));
        String[] argvArray = new String[argv.size()];
        FindBugs.processCommandLine(commandLine, argv.toArray(argvArray), findBugs);
        System.out.println(commandLine.getProject().getFile(0));
        FindBugs.runMain(findBugs, commandLine);
        //findBugs.execute();
        findBugs.close();*/
    }


    /**
     * 実行結果ファイルのパスを返す
     * @return 実行結果ファイルのパス
     */
    public String getResultFilePath() {
        return this.resultDir.getPath() + "/main.xml";
    }

}