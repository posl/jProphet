package jp.posl.jprophet;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import jp.posl.jprophet.patch.OperationDiff;
import jp.posl.jprophet.patch.PatchCandidate;
import jp.posl.jprophet.patch.OperationDiff.ModifyType;
import jp.posl.jprophet.project.Project;
import jp.posl.jprophet.project.ProjectFactory;

import com.github.javaparser.Position;
import com.github.javaparser.Range;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.BooleanLiteralExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.type.PrimitiveType;

/**
 * 修正パッチ候補を元にプロジェクト全体の生成を行うクラス
 */
public class PatchedProjectGenerator {
    static public class fuga {
        public File patchedFile;
        public File originalFile;
        public fuga(File patchedFile, File originalFile) {
            this.patchedFile = patchedFile;
            this.originalFile = originalFile;
        }
    }
    private final RepairConfiguration config;
    private final Project projectForTestValidation;
    private final String originalProjectPath;
    private final String patchTargetProjectPath;
    // private File lastPatchedFile;
    // private File originalFileOfLastPatched;
    private final List<fuga> fugas = new ArrayList<fuga>();

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
        // if(this.lastPatchedFile != null) this.unpatch();
        if(this.fugas.size() > 0) this.unpatch();

        this.hoge(patchCandidate.getFilePath(), patchCandidate.getFixedCompilationUnit());

        // このメソッドが呼び出される度にprojectを返すが
        // オブジェクトそのものに変更は無い
        return this.projectForTestValidation;
    }

    private void hoge(String filePath, CompilationUnit cu) {
        final String patchTargetFilePath = filePath;
        final File patchTargetFile = new File(this.patchTargetProjectPath + patchTargetFilePath.replace(this.originalProjectPath, ""));
        final String patchedSourceCode = NodeUtility.lexicalPreservingPrint(cu);

        try {
            FileUtils.write(patchTargetFile, patchedSourceCode, "utf-8");
        } catch (IOException e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
            System.exit(-1);
        }

        this.fugas.add(new fuga(patchTargetFile, new File(filePath)));
    }

    public Project applyMultiPatch(List<PatchCandidate> patchCandidates) {
        final String varNamePrefix = "jProphetEnableThisPatch";
        Map<String, CompilationUnit> cuMap = new HashMap<String, CompilationUnit>();

        for (PatchCandidate candidate : patchCandidates) {
            CompilationUnit cu = candidate.getOriginalCompilationUnit();
            final OperationDiff diff = candidate.getOperationDiff();
            if(!(diff.getTargetNodeAfterFix() instanceof Statement)) continue;
            int increasedLineCount = 0;
            CompilationUnit updatedCu = cu; // まだパッチが適用されていないファイルの場合，オリジナルのcuに適用するための初期化
            for (Map.Entry<String, CompilationUnit> entry : cuMap.entrySet()) {
                if (entry.getKey().equals(candidate.getFilePath())) {
                    updatedCu = entry.getValue();
                    int updatedCuLineRange = updatedCu.getEnd().orElseThrow().line; 
                    int originalCuLineRange = cu.getEnd().orElseThrow().line; 
                    increasedLineCount =  updatedCuLineRange - originalCuLineRange;
                }
            }
            final String varNameForEnablePatch = new StringBuilder()
                .append(varNamePrefix)
                .append(String.valueOf(increasedLineCount))
                .toString();
            final Range originalRange = diff.getTargetNodeBeforeFix().getRange().orElseThrow();
            final Position newBegin = new Position(originalRange.begin.line + increasedLineCount, originalRange.begin.column);
            final Position newEnd = new Position(originalRange.end.line + increasedLineCount, originalRange.end.column);
            final Range newRange = new Range(newBegin, newEnd);
            final Node nodeToInsert = NodeUtility.findNodeInCompilationUnitByBeginRange(updatedCu, diff.getTargetNodeBeforeFix(), newRange);
            final Expression condition = new NameExpr(varNameForEnablePatch);
            final BlockStmt blockStmt = new BlockStmt(new NodeList<Statement>(List.of((Statement)diff.getTargetNodeAfterFix())));
            final Node ifEnclosingNode = new IfStmt(condition, blockStmt, null);

            Node node;
            if (diff.getModifyType().equals(ModifyType.INSERT)) {
                node = NodeUtility.insertNodeWithNewLine(ifEnclosingNode, nodeToInsert).orElseThrow();
            } else if (diff.getModifyType().equals(ModifyType.CHANGE)) {
                node = NodeUtility.replaceNode(ifEnclosingNode, nodeToInsert).orElseThrow();
            }
            else {
                continue;
            }
            final Statement varDeclaration = new ExpressionStmt(new VariableDeclarationExpr(new VariableDeclarator(PrimitiveType.booleanType(), varNameForEnablePatch, new BooleanLiteralExpr(false))));
            NodeUtility.insertNodeWithNewLine(varDeclaration, node);
            cu = node.findCompilationUnit().orElseThrow();
            cuMap.put(candidate.getFilePath(), cu);
        }
        cuMap.entrySet().stream()
            .forEach(entry -> {
                this.hoge(entry.getKey(), entry.getValue());
            });

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
            for (fuga fuga: this.fugas) {
                FileUtils.copyFile(fuga.originalFile, fuga.patchedFile, false);
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
            System.exit(-1);
        }
        this.fugas.clear();
    }
}

