package jp.posl.jprophet;

import java.io.File;
import java.io.IOException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import com.github.javaparser.printer.*;

/**
 * 修正パッチ候補を元にプロジェクト全体を生成する
 * TODO:修正パッチ候補ごとにプロジェクト全体を生成しなおす仕様になっているので効率が悪い
 */
public class FixedProjectGenerator {
    public Project exec(RepairConfiguration config, RepairCandidate repairCandidate) {
        final String originalProjectPath = config.getTargetProject().getProjectPath();
        final String fixedProjectPath       = config.getFixedProjectDirPath() + FilenameUtils.getBaseName(originalProjectPath);
        final File   originalProjectDir  = new File(originalProjectPath);
        final File   fixedProjectDir           = new File(fixedProjectPath);
        try {
            FileUtils.copyDirectory(originalProjectDir, fixedProjectDir);
        } catch (IOException e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
            System.exit(-1);
        }

        final String fixedFilePath = repairCandidate.getFixedFilePath();
        final File fixedFile = new File(fixedProjectDir + fixedFilePath.replace(originalProjectPath, ""));
        final String fixedSourceCode = new PrettyPrinter(new PrettyPrinterConfiguration()).print(repairCandidate.getCompilationUnit());

        try {
            FileUtils.write(fixedFile, fixedSourceCode, "utf-8");
        } catch (IOException e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
            System.exit(-1);
        }

        final Project fixedProject = new Project(fixedProjectPath);
        return fixedProject;
    }
}
