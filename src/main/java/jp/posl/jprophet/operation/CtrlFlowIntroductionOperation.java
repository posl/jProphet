package jp.posl.jprophet.operation;

import java.util.ArrayList;
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
        this.insertIfStmtBefore(blockStmt, targetNode, new ReturnStmt(), repairUnit).map(candidates::add);
        if(targetNode.findParent(ForStmt.class).isPresent()) {
            this.insertIfStmtBefore(blockStmt, targetNode, new BreakStmt((SimpleName) null), repairUnit).map(candidates::add);
        }

        return candidates;
    }

    public Optional<RepairUnit> insertIfStmtBefore(BlockStmt inThisBlockStmt, Node beforeThisNode, Statement stmtInIfBlock, RepairUnit repairUnit) {
        NodeList<Statement> statements = inThisBlockStmt.clone().getStatements();
        try {
            statements.addBefore(new IfStmt(null, new NameExpr("JPROPHET_ABST_HOLE") , stmtInIfBlock , null), (Statement) beforeThisNode);
        } catch (Exception e) {
            return Optional.empty();
        }

        RepairUnit newCandidate = RepairUnit.deepCopy(repairUnit);
        newCandidate.getTargetNode().findParent(BlockStmt.class)
            .map(b -> b.setStatements(statements));
    
        return Optional.of(newCandidate);
    }
}