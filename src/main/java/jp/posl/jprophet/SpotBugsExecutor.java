package jp.posl.jprophet;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

//import com.github.spotbugs.*;

public class SpotBugsExecutor {

    private ProjectConfiguration projectConfiguration;
    private File resultDir;

    public SpotBugsExecutor(ProjectConfiguration projectConfiguration, File resultDir) {
        this.projectConfiguration = projectConfiguration;
        this.resultDir = resultDir;
    }

    public void exec() {

        ProjectBuilder projectBuilder = new ProjectBuilder();
        projectBuilder.build(projectConfiguration);

        if(!resultDir.exists()) {
            resultDir.mkdir();
        }    

        List<File> classFiles = searchClassFile();

        for (File file : classFiles) {
            System.out.println(file.toPath());
            runSpotBugs(file);
        }

    }


    private List<File> searchClassFile() {
        List<File> classFiles = projectConfiguration.getSourceFilePaths().stream()
            .map(path -> new File(path))
            .map(file -> file.getName())
            .map(name -> name.replace(".java", ".class"))
            .map(name -> projectConfiguration.getBuildPath() + "/" + name)
            .map(path -> new File(path))
            .collect(Collectors.toList());      // もっとシンプルな方法は無いか？
        return classFiles;
    }


    private void runSpotBugs(File classFile) {
        Runtime runtime = Runtime.getRuntime();
        final String inputPath = classFile.toPath().toString();
        final String outputPath = resultDir.getPath() + "/" + classFile.getName().replace(".class", "-warnings.txt");
        String[] commands = { "spotbugs", "-textui", "-output", outputPath, inputPath };
        try {
            Process process = runtime.exec(commands, null, new File("./"));
            process.waitFor();
            //BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));
            //System.out.println(br.readLine());
        } catch (IOException|InterruptedException e) {
            e.printStackTrace();
        }
    }

}