package jp.posl.jprophet.operation;

import java.util.ArrayList;
import java.util.List;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.Statement;

import jp.posl.jprophet.NodeUtility;


/**
 * 対象のステートメントをif文で挟む
 */
public class CondIntroductionOperation implements AstOperation{
    /**
     * {@inheritDoc}
     */
    public List<CompilationUnit> exec(Node targetNode){
        if(!(targetNode instanceof Statement)) return new ArrayList<>();
        if(targetNode instanceof BlockStmt) return new ArrayList<>();

        final String abstractConditionName = "ABST_HOLE";
        final Statement thenStmt = (Statement)NodeUtility.deepCopyByReparse(targetNode); 
        IfStmt newIfStmt;
        try {
            newIfStmt =  (IfStmt)JavaParser.parseStatement((new IfStmt(new MethodCallExpr(abstractConditionName), thenStmt, null)).toString());
        } catch (Exception e) {
            return new ArrayList<>();
        }
        final IfStmt replacedIfStmt = (IfStmt)NodeUtility.replaceNode(newIfStmt, targetNode).orElseThrow();
        final Expression abstCondition = replacedIfStmt.getCondition();

        final ConcreteConditions concreteConditions = new ConcreteConditions(abstCondition);
        final List<CompilationUnit> candidates = concreteConditions.getCompilationUnits();
        return candidates;
    }
}