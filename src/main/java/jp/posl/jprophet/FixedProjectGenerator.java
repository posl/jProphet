package jp.posl.jprophet;

import java.io.File;
import java.io.IOException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import jp.posl.jprophet.project.GradleProject;
import jp.posl.jprophet.project.Project;

import com.github.javaparser.printer.*;

/**
 * 修正パッチ候補を元にプロジェクト全体の生成を行うクラス
 */
public class FixedProjectGenerator {
    /**
     * 修正パッチ候補を元にプロジェクト全体の生成を行うクラス
     * TODO:修正パッチ候補ごとにプロジェクト全体を生成し直す仕様になっているので効率が悪い
     * @param config 修正設定. 生成先パスなど含む
     * @param patchCandidate 修正パッチ候補
     * @return 修正パッチ適用後のプロジェクト
     */
    public Project exec(RepairConfiguration config, PatchCandidate patchCandidate) {
        final String originalProjectPath = config.getTargetProject().getRootPath();
        final String fixedProjectPath    = config.getFixedProjectDirPath() + FilenameUtils.getBaseName(originalProjectPath);
        final File   originalProjectDir  = new File(originalProjectPath);
        final File   fixedProjectDir     = new File(fixedProjectPath);

        try {
            FileUtils.copyDirectory(originalProjectDir, fixedProjectDir);
        } catch (IOException e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
            System.exit(-1);
        }

        this.generateFixedFile(patchCandidate, fixedProjectPath, originalProjectPath);

        final Project fixedProject = new GradleProject(fixedProjectPath); //TODO: GradleProjetの生成は外部に投げる
        return fixedProject;
    }

    /**
     * 修正パッチ候補が適用されたファイルを生成する 
     * @param patchCandidate 修正パッチ候補
     * @param fixedProjectPath 生成先のプロジェクトのパス
     * @param originalProjectPath 生成元のプロジェクトのパス
     */
    private void generateFixedFile(PatchCandidate patchCandidate, String fixedProjectPath, String originalProjectPath){
        final String fixedFilePath   = patchCandidate.getFilePath();
        final File   fixedFile       = new File(fixedProjectPath + fixedFilePath.replace(originalProjectPath, ""));
        final String fixedSourceCode = new PrettyPrinter(new PrettyPrinterConfiguration()).print(patchCandidate.getCompilationUnit());

        try {
            FileUtils.write(fixedFile, fixedSourceCode, "utf-8");
        } catch (IOException e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
            System.exit(-1);
        }
    }
}
