package jp.posl.jprophet.operation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

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

import jp.posl.jprophet.RepairUnit;

/**
 * 抽象条件式がtrueの時に実行されるような,
 * コントロールフローを制御するステートメント(return, breakなど)を
 * 対象の前に挿入する
 */
public class CtrlFlowIntroductionOperation implements AstOperation{
    public List<RepairUnit> exec(RepairUnit repairUnit){
        Node targetNode = repairUnit.getTargetNode();
        if(!(targetNode instanceof Statement)) return new ArrayList<>();
        if(targetNode instanceof BlockStmt) return new ArrayList<>();
        BlockStmt blockStmt; 
        try {
            blockStmt = targetNode.findParent(BlockStmt.class).orElseThrow();
        } catch (Exception e) {
            return new ArrayList<>();
        }

        List<RepairUnit> candidates = new ArrayList<RepairUnit>();
        this.insertIfStmtWithReturn(blockStmt, targetNode, repairUnit).map(candidates::add);
        this.insertIfStmtWithBreak(blockStmt, targetNode, repairUnit).map(candidates::add);

        return candidates;
    }

    public Optional<RepairUnit> insertIfStmtWithReturn(BlockStmt blockStmt, Node targetNode, RepairUnit repairUnit) {
        NodeList<Statement> statements = blockStmt.clone().getStatements();
        try {
            statements.addBefore(new IfStmt(null, new NameExpr("JPROPHET_ABST_HOLE") , new ReturnStmt(), null), (Statement) targetNode);
        } catch (Exception e) {
            return Optional.empty();
        }

        RepairUnit newCandidate = RepairUnit.deepCopy(repairUnit);
        newCandidate.getTargetNode().findParent(BlockStmt.class)
            .map(b -> b.setStatements(statements));
    
        return Optional.of(newCandidate);
    }

    public Optional<RepairUnit> insertIfStmtWithBreak(BlockStmt blockStmt, Node targetNode, RepairUnit repairUnit) throws IllegalArgumentException {
        if(!targetNode.findParent(ForStmt.class).isPresent()) {
            return Optional.empty();
        }
        NodeList<Statement> statements = blockStmt.clone().getStatements();
        try {
            statements.addBefore(new IfStmt(null, new NameExpr("JPROPHET_ABST_HOLE") , new BreakStmt((SimpleName) null), null), (Statement) targetNode);
        } catch (Exception e) {
            return Optional.empty();
        }

        RepairUnit newCandidate = RepairUnit.deepCopy(repairUnit);
        newCandidate.getTargetNode().findParent(BlockStmt.class)
            .map(b -> b.setStatements(statements));
    
        return Optional.of(newCandidate);
    }
}