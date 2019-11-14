package jp.posl.jprophet.operation;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.SimpleName;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.BreakStmt;
import com.github.javaparser.ast.stmt.ForStmt;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.stmt.Statement;

import jp.posl.jprophet.NodeUtility;


/**
 * 抽象条件式がtrueの時に実行されるような,
 * コントロールフローを制御するステートメント(return, breakなど)を
 * 対象の前に挿入する
 */
public class CtrlFlowIntroductionOperation implements AstOperation{
    public List<CompilationUnit> exec(Node targetNode){
        if(!(targetNode instanceof Statement)) return new ArrayList<>();
        if(targetNode instanceof BlockStmt) return new ArrayList<>();
        BlockStmt blockStmt; 
        try {
            blockStmt = targetNode.findParent(BlockStmt.class).orElseThrow();
        } catch (Exception e) {
            return new ArrayList<>();
        }

        List<CompilationUnit> compilationUnits = new ArrayList<CompilationUnit>();
        this.insertIfStmtBefore(blockStmt, targetNode, new ReturnStmt()).map(compilationUnits::add);
        if(targetNode.findParent(ForStmt.class).isPresent()) {
            this.insertIfStmtBefore(blockStmt, targetNode, new BreakStmt((SimpleName) null)).map(compilationUnits::add);
        }

        return compilationUnits;
    }

    private Optional<CompilationUnit> insertIfStmtBefore(BlockStmt inThisBlockStmt, Node beforeThisTargetNode, Statement stmtInIfBlock) {
        NodeList<Statement> statements = inThisBlockStmt.clone().getStatements();
        try {
            statements.addBefore(new IfStmt(null, new NameExpr("JPROPHET_ABST_HOLE") , stmtInIfBlock , null), (Statement) beforeThisTargetNode);
            Node copiedTargetNode = NodeUtility.deepCopy(beforeThisTargetNode);
            BlockStmt blockStmt = copiedTargetNode.findParent(BlockStmt.class).orElseThrow();
            blockStmt.setStatements(statements);
            CompilationUnit compilationUnit = blockStmt.findCompilationUnit().orElseThrow();
            return Optional.of(compilationUnit);
        } catch (NoSuchElementException | IllegalArgumentException e) {
            return Optional.empty();
        }
    }
}