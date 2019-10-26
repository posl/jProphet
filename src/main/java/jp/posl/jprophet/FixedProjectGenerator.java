package jp.posl.jprophet;

import java.io.File;
import java.io.IOException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import com.github.javaparser.printer.*;

public class FixedProjectGenerator {
    public Project exec(RepairConfiguration config, RepairCandidate repairCandidate) {
        final String originalProjectPath = config.getTargetProject().getProjectPath();
        final File originalProjectDir = new File(originalProjectPath);
        final String outputDirPath = config.getFixedProjectDirPath() + FilenameUtils.getBaseName(originalProjectPath);
        final File outputDir = new File(outputDirPath);
        try {
            FileUtils.copyDirectory(originalProjectDir, outputDir);
        } catch (IOException e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
            System.exit(-1);
        }

        final String fixedFilePath = repairCandidate.getFixedFilePath();
        final File fixedFile = new File(outputDir + fixedFilePath.replace(originalProjectPath, ""));
        final String fixedSourceCode = new PrettyPrinter(new PrettyPrinterConfiguration()).print(repairCandidate.getCompilationUnit());

        try {
            FileUtils.write(fixedFile, fixedSourceCode, "utf-8");
        } catch (IOException e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
            System.exit(-1);
        }

        return new Project(outputDirPath);
    }

}
