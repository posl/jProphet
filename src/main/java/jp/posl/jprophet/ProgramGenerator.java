package jp.posl.jprophet;

import java.io.File;
import java.io.IOException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import com.github.javaparser.printer.*;

public class ProgramGenerator {
    public ProjectConfiguration applyPatch(ProjectConfiguration project, RepairCandidate repairCandidate) {
        File originalProjectDir = new File(project.getProjectPath());
        String outputDirPath = project.getBuildPath() + FilenameUtils.getBaseName(project.getProjectPath());
        File outputDir = new File(outputDirPath);
        try {
            FileUtils.copyDirectory(originalProjectDir, outputDir);
        } catch (IOException e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
            System.exit(-1);
        }

        String fixedFilePath = repairCandidate.getFixedFilePath();
        File fixedFile = new File(outputDir + fixedFilePath.replace(project.getProjectPath(), ""));
        String fixedSourceCode = new PrettyPrinter(new PrettyPrinterConfiguration()).print(repairCandidate.getCompilationUnit());

        try {
            FileUtils.write(fixedFile, fixedSourceCode, "utf-8");
        } catch (IOException e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
            System.exit(-1);
        }

        return new ProjectConfiguration(outputDirPath, outputDirPath);
    }

}
