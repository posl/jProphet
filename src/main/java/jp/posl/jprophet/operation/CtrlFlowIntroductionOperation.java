package jp.posl.jprophet.operation;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.expr.Expression;
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
        compilationUnits.addAll(this.insertIfStmt(blockStmt, targetNode, new ReturnStmt()));
        if(targetNode.findParent(ForStmt.class).isPresent()) {
            compilationUnits.addAll(this.insertIfStmt(blockStmt, targetNode, new BreakStmt((SimpleName) null)));
        }

        return compilationUnits;
    }

    private List<CompilationUnit> insertIfStmt(BlockStmt inThisBlockStmt, Node beforeThisTargetNode, Statement stmtInIfBlockToInsert) {
        NodeList<Statement> statements = inThisBlockStmt.clone().getStatements();

        NameExpr abstHole = new NameExpr("ABST_HOLE");
        //TODO: addBeforeはプログラム中のNodeを一意に決定できない
        statements.addBefore(new IfStmt(null, abstHole, stmtInIfBlockToInsert , null), (Statement) beforeThisTargetNode);
        Node copiedTargetNode = NodeUtility.deepCopy(beforeThisTargetNode);
        BlockStmt blockStmt;
        try {
            blockStmt = copiedTargetNode.findParent(BlockStmt.class).orElseThrow();
        } catch (Exception e) {
            return new ArrayList<>();
        }
        blockStmt.setStatements(statements);
        ConditionGenerator conditionGenerator = new ConditionGenerator();

        List<Expression> concreteConditions = conditionGenerator.generateCondition(abstHole);
        List<CompilationUnit> candidates = new ArrayList<CompilationUnit>();
        concreteConditions.stream()
            .forEach(c -> {
                CompilationUnit candidate = blockStmt.findCompilationUnit().orElseThrow();
                abstHole.replace(c);
                candidates.add(candidate);
            });

        return candidates;
    }
}