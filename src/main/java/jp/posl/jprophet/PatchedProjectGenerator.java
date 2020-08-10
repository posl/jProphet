package jp.posl.jprophet;

import java.io.File;
import java.io.IOException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import jp.posl.jprophet.patch.PatchCandidate;
import jp.posl.jprophet.project.Project;
import jp.posl.jprophet.project.ProjectFactory;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.printer.lexicalpreservation.LexicalPreservingPrinter;

/**
 * 修正パッチ候補を元にプロジェクト全体の生成を行うクラス
 */
public class PatchedProjectGenerator {
    private final RepairConfiguration config;
    private final Project projectForTestValidation;
    private final String originalProjectPath;
    private final String patchTargetProjectPath;
    private File lastPatchedFile;
    private File originalFileOfLastPatched;

    /**
     * オリジナルのプロジェクトをテスト検証用にコピーし，
     * 修正パッチ候補を元にパッチ適用後のファイル生成を行うクラスを生成する
     * @param config 修正設定. テスト検証用のプロジェクトの生成先パスなど含む
     */
    public PatchedProjectGenerator(RepairConfiguration config) {
        this.config = config;
        this.originalProjectPath = this.config.getTargetProject().getRootPath();
        this.patchTargetProjectPath = this.config.getFixedProjectDirPath() + FilenameUtils.getBaseName(this.originalProjectPath);

        final File originalProjectDir = new File(this.originalProjectPath);
        final File patchTargetProjectDir = new File(this.patchTargetProjectPath);

        try {
            FileUtils.copyDirectory(originalProjectDir, patchTargetProjectDir);
        } catch (IOException e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
            System.exit(-1);
        }

        final ProjectFactory projectFactory = new ProjectFactory();
        this.projectForTestValidation = projectFactory.create(this.config, this.patchTargetProjectPath);
    }

    /**
     * 修正パッチ候補が適用されたファイルを生成する 
     * @param patchCandidate 修正パッチ候補
     */
    public Project applyPatch(PatchCandidate patchCandidate){
        if(this.lastPatchedFile != null) this.unpatch();

        final String patchTargetFilePath = patchCandidate.getFilePath();
        final File patchTargetFile = new File(this.patchTargetProjectPath + patchTargetFilePath.replace(this.originalProjectPath, ""));
        final CompilationUnit cu = patchCandidate.getFixedCompilationUnit();
        LexicalPreservingPrinter.setup(cu);
        final String patchedSourceCode = LexicalPreservingPrinter.print(cu);

        try {
            FileUtils.write(patchTargetFile, patchedSourceCode, "utf-8");
        } catch (IOException e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
            System.exit(-1);
        }

        this.lastPatchedFile = patchTargetFile;
        this.originalFileOfLastPatched = new File(patchCandidate.getFilePath());

        // このメソッドが呼び出される度にprojectを返すが
        // オブジェクトそのものに変更は無い
        return this.projectForTestValidation;
    }

    /**
     * 修正パッチが適用されたテスト検証用プロジェクトをアンパッチする
     * <p>
     * (特定の修正パッチ候補を対象にアンパッチするpublicメソッドにすることも将来的に検討) 
     * </p>
     */
    private void unpatch(){
        try {
            FileUtils.copyFile(this.originalFileOfLastPatched, this.lastPatchedFile, false);
        } catch (IOException e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
            System.exit(-1);
        }
    }
}

