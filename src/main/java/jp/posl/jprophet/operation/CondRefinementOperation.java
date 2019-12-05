package jp.posl.jprophet.operation;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.expr.EnclosedExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.UnaryExpr;
import com.github.javaparser.ast.expr.BinaryExpr.Operator;
import com.github.javaparser.ast.stmt.IfStmt;

import jp.posl.jprophet.NodeUtility;


/**
 * if文における条件式の変更操作
 * 条件を狭める操作と緩める操作の二つを行う
 */
public class CondRefinementOperation implements AstOperation{
    /**
     * {@inheritDoc}
     */
    public List<CompilationUnit> exec(Node node){
        if (!(node instanceof IfStmt)) return new ArrayList<CompilationUnit>();

        final List<CompilationUnit> compilationUnits = new ArrayList<CompilationUnit>();
        Expression condition = (Expression)NodeUtility.deepCopyByReparse(((IfStmt)node).getCondition());
        final String abstractConditionName = "ABST_HOLE";

        this.replaceWithBinaryExprWithAbst(condition, new EnclosedExpr (new MethodCallExpr(abstractConditionName)), Operator.OR)
            .map(expr -> this.collectConcreteConditions(((EnclosedExpr)expr.getRight()).getInner()))
            .ifPresent(compilationUnits::addAll);
        this.replaceWithBinaryExprWithAbst(condition, new UnaryExpr (new EnclosedExpr (new MethodCallExpr(abstractConditionName)), UnaryExpr.Operator.LOGICAL_COMPLEMENT), Operator.AND)
            .map(expr -> this.collectConcreteConditions(((EnclosedExpr)((UnaryExpr)expr.getRight()).getExpression()).getInner()))
            .ifPresent(compilationUnits::addAll);
            
        return compilationUnits;
    }

    /**
     * 条件文をexpressionからexpression operation rightExprに書き換える
     * m() -> m() && m2() など
     * @param expression 変更前の条件式
     * @param rightExpr 変更前の条件式に加える条件式
     * @param operator 比較演算子
     * @return
     */
    private Optional<BinaryExpr> replaceWithBinaryExprWithAbst(Expression expression, Expression rightExpr, Operator operator){
        Expression condition = (Expression)NodeUtility.deepCopyByReparse(expression);
        Expression leftExpr = new EnclosedExpr ((Expression)NodeUtility.deepCopyByReparse(expression));
        final BinaryExpr newBinaryExpr = new BinaryExpr(leftExpr, rightExpr, operator);

        return NodeUtility.replaceNode(newBinaryExpr, condition)
            .map(expr -> (BinaryExpr)expr);
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