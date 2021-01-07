package jp.posl.jprophet;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
    //TODO: 命名
    static public class PatchedFile {
        public File patchedFile;
        public File originalFile;
        public PatchedFile(File patchedFile, File originalFile) {
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
    //TODO: 命名
    private final List<PatchedFile> patchedFiles = new ArrayList<PatchedFile>();

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
        if(this.patchedFiles.size() > 0) this.unpatch();

        this.addPatchedFile(patchCandidate.getFilePath(), patchCandidate.getFixedCompilationUnit());

        // このメソッドが呼び出される度にprojectを返すが
        // オブジェクトそのものに変更は無い
        return this.projectForTestValidation;
    }

    //TODO: 命名
    private void addPatchedFile(String filePath, CompilationUnit cu) {
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

        this.patchedFiles.add(new PatchedFile(patchTargetFile, new File(filePath)));
    }

    public Project applyMultiPatch(List<PatchCandidate> patchCandidates) {
        //TODO: 命名
        final Map<String, CompilationUnit> filePathToUpdatedCu = new HashMap<String, CompilationUnit>();
        final Set<Node> enclosedTargetNodes = new HashSet<Node>();

        final Map<CompilationUnit, MovementHistory> originalCuToMvHistory = new HashMap<CompilationUnit, MovementHistory>();

        // ターゲットノードをif文で囲む処理
        for (PatchCandidate candidate : patchCandidates) {
            final Node originalTargetNode = candidate.getOperationDiff().getTargetNodeBeforeFix();
            if (enclosedTargetNodes.contains(originalTargetNode) && filePathToUpdatedCu.containsKey(candidate.getFilePath())) {
                continue;
            }
            final String varNameForEnableTarget = new StringBuilder()
                .append("jProphetTarget")
                .append(String.valueOf(candidate.getId()))
                .toString();
            final Expression conditionForTarget = new NameExpr(varNameForEnableTarget);
            final Node clonedTargetNode = originalTargetNode.clone(); // cloneメソッドによりターゲットノードの先祖を消去
            final BlockStmt blockStmtWithTarget = new BlockStmt(new NodeList<Statement>(List.of((Statement)clonedTargetNode)));
            final Node ifEnclosingTargetNode = new IfStmt(conditionForTarget, blockStmtWithTarget, null);
            final CompilationUnit originalCu = candidate.getOriginalCompilationUnit();
            CompilationUnit updatedCu = candidate.getOriginalCompilationUnit();
            if (filePathToUpdatedCu.containsKey(candidate.getFilePath())) {
                updatedCu = filePathToUpdatedCu.get(candidate.getFilePath());
            }
            else {
                originalCuToMvHistory.put(originalCu, new MovementHistory(originalCu));
            }
            final Range originalRange = candidate.getOperationDiff().getTargetNodeBeforeFix().getRange().orElseThrow();


            final int beginLineDelta = originalCuToMvHistory.get(originalCu).get(originalRange.begin.line).lineDelta;
            final int beginColumnDelta = originalCuToMvHistory.get(originalCu).get(originalRange.begin.line).columnDelta;
            final Position updatedBegin = new Position(originalRange.begin.line + beginLineDelta, originalRange.begin.column + beginColumnDelta);

            final int endLineDelta = originalCuToMvHistory.get(originalCu).get(originalRange.end.line).lineDelta;
            final int endColumnDelta = originalCuToMvHistory.get(originalCu).get(originalRange.end.line).columnDelta;
            final Position updatedEnd = new Position(originalRange.end.line + endLineDelta, originalRange.end.column + endColumnDelta);

            final Range updatedRange = new Range(updatedBegin, updatedEnd);
            final Node targetNode = NodeUtility.findNodeByRange(updatedCu, updatedRange);
            final Node enclosedTargetNode = NodeUtility.replaceNode(ifEnclosingTargetNode, targetNode).orElseThrow();
            final Statement varDeclaration = new ExpressionStmt(new VariableDeclarationExpr(new VariableDeclarator(PrimitiveType.booleanType(), varNameForEnableTarget, new BooleanLiteralExpr(false))));
            final Node nodeWhereVarDeclarationIsInserted = NodeUtility.insertNodeWithNewLine(varDeclaration, enclosedTargetNode).orElseThrow();
            filePathToUpdatedCu.put(candidate.getFilePath(), nodeWhereVarDeclarationIsInserted.findCompilationUnit().orElseThrow());


            final MovementHistory mvHistory = originalCuToMvHistory.get(originalCu);
            mvHistory.addLineDelta(originalTargetNode.getBegin().orElseThrow().line, originalTargetNode.getEnd().orElseThrow().line, 2);
            mvHistory.addLineDelta(originalTargetNode.getEnd().orElseThrow().line + 1, candidate.getOriginalCompilationUnit().getEnd().orElseThrow().line, 3);
            mvHistory.addColumnDelta(originalTargetNode.getBegin().orElseThrow().line, originalTargetNode.getEnd().orElseThrow().line, 4);
            enclosedTargetNodes.add(originalTargetNode);
        }

        // パッチ候補をif文で囲む
        for (PatchCandidate candidate : patchCandidates) {
            final OperationDiff diff = candidate.getOperationDiff();
            if(!(diff.getTargetNodeAfterFix() instanceof Statement)) continue;
            final String varNameForEnablePatch = new StringBuilder()
                .append("jProphetPatch")
                .append(String.valueOf(candidate.getId()))
                .toString();
            final Expression condition = new NameExpr(varNameForEnablePatch);
            final BlockStmt blockStmt = new BlockStmt(new NodeList<Statement>(List.of((Statement)diff.getTargetNodeAfterFix())));
            final Node ifEnclosingNode = new IfStmt(condition, blockStmt, null);
            final CompilationUnit originalCu = candidate.getOriginalCompilationUnit();
            final Range originalRange = candidate.getOperationDiff().getTargetNodeBeforeFix().getRange().orElseThrow();

            final int beginLineDelta = originalCuToMvHistory.get(originalCu).get(originalRange.begin.line).lineDelta;
            final int beginColumnDelta = originalCuToMvHistory.get(originalCu).get(originalRange.begin.line).columnDelta;
            final Position updatedBegin = new Position(originalRange.begin.line + beginLineDelta - 1, originalRange.begin.column + beginColumnDelta - 4);
            final int endLineDelta = originalCuToMvHistory.get(originalCu).get(originalRange.end.line).lineDelta;
            final Position updatedEnd = new Position(originalRange.end.line + endLineDelta + 1, originalRange.begin.column + beginColumnDelta - 4);
            final Range updatedRange = new Range(updatedBegin, updatedEnd);
            final CompilationUnit updatedCu = filePathToUpdatedCu.get(candidate.getFilePath());
            final Node enclosedTargetNode = NodeUtility.findNodeByRange(updatedCu, updatedRange);

            Node node = NodeUtility.insertNodeWithNewLine(ifEnclosingNode, enclosedTargetNode).orElseThrow();
            final Statement varDeclaration = new ExpressionStmt(new VariableDeclarationExpr(new VariableDeclarator(PrimitiveType.booleanType(), varNameForEnablePatch, new BooleanLiteralExpr(false))));
            node = NodeUtility.insertNodeWithNewLine(varDeclaration, node).orElseThrow();
            final CompilationUnit newCu = node.findCompilationUnit().orElseThrow();
            filePathToUpdatedCu.put(candidate.getFilePath(), newCu);

            final MovementHistory mvHistory = originalCuToMvHistory.get(originalCu);
            final int beginLineOfTargetNodeAfterFix = candidate.getOperationDiff().getTargetNodeAfterFix().getBegin().orElseThrow().line;
            final int endLineOfTargetNodeAfterFix = candidate.getOperationDiff().getTargetNodeAfterFix().getEnd().orElseThrow().line;
            final int linesOfTargetNodeAfterFix = endLineOfTargetNodeAfterFix - beginLineOfTargetNodeAfterFix + 1;
            final Node originalTargetNode = candidate.getOperationDiff().getTargetNodeBeforeFix();
            mvHistory.addLineDelta(originalTargetNode.getEnd().orElseThrow().line, candidate.getOriginalCompilationUnit().getEnd().orElseThrow().line, linesOfTargetNodeAfterFix + 3);
        }

        filePathToUpdatedCu.entrySet().stream()
            .forEach(entry -> {
                this.addPatchedFile(entry.getKey(), entry.getValue());
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
            for (PatchedFile fuga: this.patchedFiles) {
                FileUtils.copyFile(fuga.originalFile, fuga.patchedFile, false);
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
            System.exit(-1);
        }
        this.patchedFiles.clear();
    }
}

