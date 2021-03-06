package jp.posl.jprophet.spotbugs;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;

import edu.umd.cs.findbugs.FindBugs;
import edu.umd.cs.findbugs.FindBugs2;
import edu.umd.cs.findbugs.Project;
import edu.umd.cs.findbugs.TextUICommandLine;
import edu.umd.cs.findbugs.filter.FilterException;
import jp.posl.jprophet.ProjectBuilder;
import jp.posl.jprophet.RepairConfiguration;


/**
 * SpotBugsを適用し、その結果を取得する
 */
public class SpotBugsExecutor {

    private final static String resultDir = "./tmp/SBresult";

    /**
     * プロジェクトをビルドし、SpotBugsを適用する
     * @param config 対象のプロジェクトのconfig
     */
    public void exec(RepairConfiguration config, String resultFileName) {
        final ProjectBuilder projectBuilder = new ProjectBuilder();
        projectBuilder.build(config);
        final File dir = new File(resultDir);
        if (!dir.exists()) {
            dir.mkdir();
        }
        try {
            runSpotBugs(config, resultFileName);
        }
        catch(FilterException|IOException|InterruptedException e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
    }
    

    /**
     * SpotBugsのAPIを利用して実行結果を取得する
     * @param config 対象のプロジェクトのconfig
     * @throws FilterException
     * @throws IOException
     * @throws InterruptedException
     */
    private void runSpotBugs(RepairConfiguration config, String resultFileName) throws FilterException, IOException, InterruptedException {       
        final FindBugs2 findBugs = new FindBugs2();
        final TextUICommandLine commandLine = new TextUICommandLine();  
        final Project SB_project = new Project();
        final String[] argv = new String[]{"-xml", "-output", getResultFilePath(resultFileName), config.getBuildPath()};
        final List<String> dirList = new ArrayList<String>();
        dirList.add(config.getBuildPath());
        SB_project.addSourceDirs(dirList);
        findBugs.setProject(SB_project);
        FindBugs.processCommandLine(commandLine, argv, findBugs);
        FindBugs.runMain(findBugs, commandLine);
        findBugs.execute();
        findBugs.close();
    }


    /**
     * 実行結果ファイル名からファイルのパスに変換する
     * @param resultFileName 実行結果ファイル名
     * @return 実行結果ファイルのパス
     */
    public static String getResultFilePath(String resultFileName) {
        return resultDir + "/" + resultFileName + ".xml";
    }


    /**
     * 実行結果ファイルが格納されているディレクトリを削除する
     */
    public static void deleteResultDirectory() {
        try {
            FileUtils.deleteDirectory(new File(resultDir));
        } catch (IOException e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
    }

}