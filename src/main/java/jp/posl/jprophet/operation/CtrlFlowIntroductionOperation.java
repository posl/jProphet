package jp.posl.jprophet.operation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.IfStmt;
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
        BlockStmt blockStmt; 
        try {
            blockStmt = targetNode.findParent(BlockStmt.class).orElseThrow();
        } catch (Exception e) {
            return new ArrayList<>();
        }
        NodeList<Statement> statements = blockStmt.clone().getStatements();
        try {
            statements.addBefore(new IfStmt(), (Statement)targetNode);
        } catch (Exception e) {
            return new ArrayList<>();
        }

        RepairUnit newCandidate = RepairUnit.deepCopy(repairUnit);
        newCandidate.getTargetNode().findParent(BlockStmt.class)
            .map(b -> b.setStatements(statements));
        
        List<RepairUnit> candidates = new ArrayList<RepairUnit>(Arrays.asList(newCandidate));
        return candidates;
    }
}