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
        final IfStmt replacedIfStmt = (IfStmt)NodeUtility.replaceNode(newIfStmt, targetNode);
        final Expression abstCondition = replacedIfStmt.getCondition();

        List<CompilationUnit> candidates = this.collectConcreteConditions(abstCondition);
        return candidates;
    }

    /**
     * 穴あきの条件式から最終的な条件式を生成
     * @param abstCondition 置換される穴あきの条件式
     * @return 生成された条件式を含むCompilationUnit
     */
    private List<CompilationUnit> collectConcreteConditions(Expression abstCondition) {
        final ConditionGenerator conditionGenerator = new ConditionGenerator();
        final List<Expression> concreteConditions = conditionGenerator.generateCondition(abstCondition);

        final List<CompilationUnit> candidates = new ArrayList<CompilationUnit>();
        concreteConditions.stream()
            .forEach(c -> candidates.add(c.findCompilationUnit().orElseThrow()));

        return candidates;
    }

}