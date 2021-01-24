package jp.posl.jprophet;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import jp.posl.jprophet.patch.OperationDiff;
import jp.posl.jprophet.patch.PatchCandidate;
import jp.posl.jprophet.patch.OperationDiff.ModifyType;
import jp.posl.jprophet.project.Project;
import jp.posl.jprophet.project.ProjectFactory;

import com.github.javaparser.JavaParser;
import com.github.javaparser.Position;
import com.github.javaparser.Range;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.expr.BooleanLiteralExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.IntegerLiteralExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.expr.BinaryExpr.Operator;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.type.PrimitiveType;

/**
 * 修正パッチ候補を元にプロジェクト全体の生成を行うクラス
 */
public class PatchedProjectGenerator {
    // TODO: 命名
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
    // TODO: 命名
    private final List<PatchedFile> patchedFiles = new ArrayList<PatchedFile>();

    /**
     * オリジナルのプロジェクトをテスト検証用にコピーし， 修正パッチ候補を元にパッチ適用後のファイル生成を行うクラスを生成する
     * 
     * @param config 修正設定. テスト検証用のプロジェクトの生成先パスなど含む
     */
    public PatchedProjectGenerator(RepairConfiguration config) {
        this.config = config;
        this.originalProjectPath = this.config.getTargetProject().getRootPath();
        this.patchTargetProjectPath = this.config.getFixedProjectDirPath()
                + FilenameUtils.getBaseName(this.originalProjectPath);

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
     * 
     * @param patchCandidate 修正パッチ候補
     */
    public Project applyPatch(PatchCandidate patchCandidate) {
        // if(this.lastPatchedFile != null) this.unpatch();
        if (this.patchedFiles.size() > 0)
            this.unpatch();

        this.addPatchedFile(patchCandidate.getFilePath(), patchCandidate.getFixedCompilationUnit());

        // このメソッドが呼び出される度にprojectを返すが
        // オブジェクトそのものに変更は無い
        return this.projectForTestValidation;
    }

    // TODO: 命名
    private void addPatchedFile(String filePath, CompilationUnit cu) {
        final String patchTargetFilePath = filePath;
        final File patchTargetFile = new File(
                this.patchTargetProjectPath + patchTargetFilePath.replace(this.originalProjectPath, ""));
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

    static class PathAndNode {
        String path;
        Node node;

        public PathAndNode(String path, Node node) {
            this.path = path;
            this.node = node;
        }

        @Override
        public boolean equals(Object obj) {
            if(this == obj) {
                return true;
            }
            if(!(obj instanceof PathAndNode)) {
                return false;
            }
            PathAndNode other = (PathAndNode) obj;
            if (obj instanceof PathAndNode) {
                return other.path.equals(this.path) && other.node.equals(this.node);
            }
            return false;
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.path, this.node);
        }
    }

    public Project applyMultiPatch(List<PatchCandidate> patchCandidates) {
        // TODO: 命名
        final Map<String, CompilationUnit> filePathToUpdatedCu = new HashMap<String, CompilationUnit>();
        final Set<Node> enclosedTargetNodes = new HashSet<Node>();

        final Map<CompilationUnit, MovementHistory> originalCuToMvHistory = new HashMap<CompilationUnit, MovementHistory>();
        final Map<PathAndNode, List<Integer>> nodeToIds = new HashMap<PathAndNode, List<Integer>>();

        for (PatchCandidate candidate : patchCandidates) {
            final Node targetNode = candidate.getOperationDiff().getTargetNodeBeforeFix();
            final PathAndNode pathAndNode = new PathAndNode(candidate.getFilePath(), targetNode);
            if (nodeToIds.containsKey(pathAndNode)) {
                nodeToIds.get(pathAndNode).add(candidate.getId());
            } else {
                nodeToIds.put(pathAndNode, new ArrayList<Integer>(Arrays.asList(candidate.getId())));
            }
        }

        for (Map.Entry<PathAndNode, List<Integer>> entry : nodeToIds.entrySet()) {
            final Node originalTargetNode = entry.getKey().node;
            final CompilationUnit originalCu = originalTargetNode.findCompilationUnit().orElseThrow();
            final List<Integer> ids = entry.getValue();

            BinaryExpr be = null;
            for (int id: ids) {
                final String varNameForEnableTarget = new StringBuilder()
                    .append("jProphetTarget")
                    .append(String.valueOf(id))
                    .toString();
                final BinaryExpr equalityExpr = new BinaryExpr(new NameExpr(varNameForEnableTarget), new IntegerLiteralExpr(292925), Operator.EQUALS);
                if (be != null) {
                    be = new BinaryExpr(be, equalityExpr, Operator.OR);
                }
                else {
                    be = equalityExpr;
                }
            }
            final Node clonedTargetNode = originalTargetNode.clone(); // cloneメソッドによりターゲットノードの先祖を消去
            final BlockStmt blockStmtWithTarget = new BlockStmt(new NodeList<Statement>(List.of((Statement)clonedTargetNode)));
            final Node ifEnclosingTargetNode = new IfStmt(be, blockStmtWithTarget, null);

            final String path = entry.getKey().path;
            CompilationUnit updatedCu = originalCu;
            if (filePathToUpdatedCu.containsKey(path)) {
                updatedCu = filePathToUpdatedCu.get(path);
            }
            else {
                originalCuToMvHistory.put(originalCu, new MovementHistory(originalCu));
            }
            final Range originalRange = originalTargetNode.getRange().orElseThrow();


            final int beginLineDelta = originalCuToMvHistory.get(originalCu).get(originalRange.begin.line).lineDelta;
            final int beginColumnDelta = originalCuToMvHistory.get(originalCu).get(originalRange.begin.line).columnDelta;
            final Position updatedBegin = new Position(originalRange.begin.line + beginLineDelta, originalRange.begin.column + beginColumnDelta);

            final int endLineDelta = originalCuToMvHistory.get(originalCu).get(originalRange.end.line).lineDelta;
            final int endColumnDelta = originalCuToMvHistory.get(originalCu).get(originalRange.end.line).columnDelta;
            final Position updatedEnd = new Position(originalRange.end.line + endLineDelta, originalRange.end.column + endColumnDelta);

            final Range updatedRange = new Range(updatedBegin, updatedEnd);
            final Node targetNode = NodeUtility.findNodeByRange(updatedCu, updatedRange);
            final Node enclosedTargetNode = NodeUtility.replaceNode(ifEnclosingTargetNode, targetNode).orElseThrow();
            Node nodeToBeInsertedAboveThis = enclosedTargetNode;
            for (int id: ids) {
                final String varNameForEnableTarget = new StringBuilder()
                    .append("jProphetTarget")
                    .append(String.valueOf(id))
                    .toString();
                final Statement varDeclaration = new ExpressionStmt(new VariableDeclarationExpr(new VariableDeclarator(PrimitiveType.intType(), varNameForEnableTarget, new IntegerLiteralExpr(2721610 + id))));
                nodeToBeInsertedAboveThis = NodeUtility.insertNodeWithNewLine(varDeclaration, nodeToBeInsertedAboveThis).orElseThrow();
            }
            filePathToUpdatedCu.put(path, nodeToBeInsertedAboveThis.findCompilationUnit().orElseThrow());


            final MovementHistory mvHistory = originalCuToMvHistory.get(originalCu);
            mvHistory.addLineDelta(originalTargetNode.getBegin().orElseThrow().line, originalTargetNode.getEnd().orElseThrow().line, 1 + ids.size());
            mvHistory.addLineDelta(originalTargetNode.getEnd().orElseThrow().line + 1, originalCu.getEnd().orElseThrow().line, 2 + ids.size());
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
            final Expression condition = new BinaryExpr(new NameExpr(varNameForEnablePatch), new IntegerLiteralExpr(292925), Operator.EQUALS);
            // ここバグ
            final BlockStmt blockStmt = new BlockStmt(new NodeList<Statement>(List.of((Statement)diff.getTargetNodeAfterFix().clone())));
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
            final Statement varDeclaration = new ExpressionStmt(new VariableDeclarationExpr(new VariableDeclarator(PrimitiveType.intType(), varNameForEnablePatch, new IntegerLiteralExpr(2721600 + candidate.getId()))));
            node = NodeUtility.insertNodeWithNewLine(varDeclaration, node).orElseThrow();
            final CompilationUnit newCu = node.findCompilationUnit().orElseThrow();
            filePathToUpdatedCu.put(candidate.getFilePath(), newCu);

            final MovementHistory mvHistory = originalCuToMvHistory.get(originalCu);
            final Node targetNodeAfterFix = candidate.getOperationDiff().getTargetNodeAfterFix();
            final int linesOfTargetNodeAfterFix = targetNodeAfterFix.toString().split("\r\n|\r|\n").length;
            // final int beginLineOfTargetNodeAfterFix = cu.getBegin().orElseThrow().line;
            // final int endLineOfTargetNodeAfterFix = cu.getEnd().orElseThrow().line;
            final Node originalTargetNode = candidate.getOperationDiff().getTargetNodeBeforeFix();
            mvHistory.addLineDelta(originalTargetNode.getBegin().orElseThrow().line, candidate.getOriginalCompilationUnit().getEnd().orElseThrow().line, linesOfTargetNodeAfterFix + 3);
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

