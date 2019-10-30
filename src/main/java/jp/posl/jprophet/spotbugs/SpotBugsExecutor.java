package jp.posl.jprophet.spotbugs;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import edu.umd.cs.findbugs.FindBugs;
import edu.umd.cs.findbugs.FindBugs2;
import edu.umd.cs.findbugs.Project;
import edu.umd.cs.findbugs.TextUICommandLine;
import edu.umd.cs.findbugs.filter.FilterException;
import jp.posl.jprophet.ProjectBuilder;
import jp.posl.jprophet.RepairConfiguration;

//import com.github.spotbugs.*;



public class SpotBugsExecutor {

    private RepairConfiguration config;
    private File resultDir;


    /**
     * 対象のプロジェクトに対してSpotBugsを適用し、その結果をxmlファイルとして保存する
     * @param projectConfiguration 対象のプロジェクト
     * @param resultDir 結果を格納するディレクトリ
     */
    public SpotBugsExecutor(RepairConfiguration config, File resultDir) {
        this.config = config;
        this.resultDir = resultDir;
    }

    /**
     * SpotBugsを適用する
     */
    public void exec() {
        
        ProjectBuilder projectBuilder = new ProjectBuilder();
        projectBuilder.build(config);

        if (!resultDir.exists()) {
            resultDir.mkdir();
        }
        
        try {
            runSpotBugs();
        }
        catch(FilterException|IOException|InterruptedException e) {
            //e.printStackTrace();
        }


    }
    

    /**
     * クラスファイルを受け取り、それらを対象にSpotBugsを適用する
     * @param classFile クラスファイルのリスト
     * @throws FilterException
     * @throws IOException
     * @throws InterruptedException
     */
    private void runSpotBugs() throws FilterException, IOException, InterruptedException {
        
        //APIをいじって処理を書くのが難しいので、とりあえず対象プロジェクトにspotbugsのプラグインを入れておいてそれを実行する

        /*
        Runtime runtime = Runtime.getRuntime();
        String[] commands = { "./gradlew", "spotbugsMain" };
        try {
            Process process = runtime.exec(commands, null, new File(project.getProjectPath()));
            process.waitFor();
        } catch (IOException|InterruptedException e) {
            e.printStackTrace();
        }*/   
        
        FindBugs2 findBugs = new FindBugs2();
        TextUICommandLine commandLine = new TextUICommandLine();
        final String outputPath = resultDir.getPath() + "/" + "result.xml";   
        Project SB_project = new Project();
        List<String> dirList = new ArrayList<String>();
        dirList.add(config.getBuildPath());
        SB_project.addSourceDirs(dirList);
        findBugs.setProject(SB_project);
        String[] argv = new String[]{"-xml", "-output", outputPath, config.getBuildPath()};
        FindBugs.processCommandLine(commandLine, argv, findBugs);
        FindBugs.runMain(findBugs, commandLine);
        findBugs.execute();
        findBugs.close();
    }


    /**
     * 実行結果ファイルのパスを返す
     * @return 実行結果ファイルのパス
     */
    public String getResultFilePath() {
        return this.resultDir.getPath() + "/result.xml";
    }

}