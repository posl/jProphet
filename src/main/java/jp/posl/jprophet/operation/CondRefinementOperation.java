package jp.posl.jprophet.operation;

import java.util.ArrayList;
import java.util.List;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.expr.EnclosedExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.UnaryExpr;
import com.github.javaparser.ast.expr.BinaryExpr.Operator;
import com.github.javaparser.ast.stmt.IfStmt;

import jp.posl.jprophet.patch.OperationDiff;
import jp.posl.jprophet.patch.OperationDiff.ModifyType;


/**
 * if文における条件式の変更操作
 * 条件を狭める操作と緩める操作の二つを行う
 */
public class CondRefinementOperation implements AstOperation{
    /**
     * {@inheritDoc}
     */
    public List<OperationDiff> exec(Node targetNode){
        if (!(targetNode instanceof IfStmt)) return new ArrayList<OperationDiff>();

        final DeclarationCollector collector = new DeclarationCollector();
        final List<VariableDeclarator> vars = new ArrayList<VariableDeclarator>();
        vars.addAll(collector.collectFileds(targetNode));
        vars.addAll(collector.collectLocalVarsDeclared(targetNode));
        final List<Parameter> parameters = collector.collectParameters(targetNode);

        final IfStmt copiedNode = (IfStmt)targetNode.clone();
        final Expression condition = (Expression)copiedNode.getCondition();

        final List<Expression> conditions = new ConcreteConditions(vars, parameters).getExpressions();
        final List<OperationDiff> operationDiffs = new ArrayList<OperationDiff>();

        conditions.stream()
            .map(expr -> this.replaceWithBinaryExprWithAbst(condition, new EnclosedExpr(expr), Operator.OR))
            .forEach(expr -> operationDiffs.add(new OperationDiff(ModifyType.CHANGE, ((IfStmt)targetNode).getCondition(), expr)));
        
        conditions.stream()
            .map(expr -> this.replaceWithBinaryExprWithAbst(condition, new UnaryExpr(new EnclosedExpr(expr), UnaryExpr.Operator.LOGICAL_COMPLEMENT), Operator.AND))
            .forEach(expr -> operationDiffs.add(new OperationDiff(ModifyType.CHANGE, ((IfStmt)targetNode).getCondition(), expr)));

        return operationDiffs;

    }

    /**
     * 条件文をexpressionからexpression operation rightExprに書き換える
     * m() -> m() && m2() など
     * @param expression 変更前の条件式
     * @param rightExpr 変更前の条件式に加える条件式
     * @param operator 比較演算子
     * @return
     */
    private BinaryExpr replaceWithBinaryExprWithAbst(Expression expression, Expression rightExpr, Operator operator){
        final Expression condition = expression.clone();
        final Expression leftExpr = new EnclosedExpr (condition);
        final BinaryExpr newBinaryExpr = new BinaryExpr(leftExpr, rightExpr, operator);

        return newBinaryExpr;
    }
} 