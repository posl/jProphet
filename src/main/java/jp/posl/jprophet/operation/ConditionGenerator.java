package jp.posl.jprophet.operation;

import java.util.ArrayList;
import java.util.List;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseProblemException;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.expr.BooleanLiteralExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.NullLiteralExpr;
import com.github.javaparser.ast.expr.BinaryExpr.Operator;

import jp.posl.jprophet.NodeUtility;

/**
 * 条件式の生成を行うクラス
 */
public class ConditionGenerator {

    /**
     * 参照可能な変数から以下の複数の条件式を生成する</br>
     * <ul>
     * <li>全ての変数とnullを==と!=で比較</li>
     * <li>全てのBool変数とtrueを==と!=で比較</li>
     * <li>true（恒真）</li>
     * </ul>
     * 
     * @param targetCondition 条件式を生成して置き換えられるExpressionノード
     * @return 生成された条件式によって置き換えられたtargetConditionノードのリスト 
     */
	public List<Expression> generateCondition(Expression targetCondition) {
        final DeclarationCollector collector = new DeclarationCollector();
        final List<VariableDeclarator> vars = new ArrayList<VariableDeclarator>();
        vars.addAll(collector.collectFileds(targetCondition));
        vars.addAll(collector.collectLocalVarsDeclared(targetCondition));
        final List<Parameter> parameters = collector.collectParameters(targetCondition);
        
        final List<String> booleanVarNames = this.collectBooleanNames(vars, parameters);
        final List<String> allVarNames = this.collectNames(vars, parameters);

        final List<Expression> newConditions = new ArrayList<Expression>();
        booleanVarNames.stream()
            .forEach(name -> {
                try {
                    final BinaryExpr isTrue = this.replaceWithBinaryExpr(targetCondition, name, new BooleanLiteralExpr(true), Operator.EQUALS);
                    newConditions.add(isTrue);
                } catch (ParseProblemException e) {}
                try {
                    final BinaryExpr isFalse = this.replaceWithBinaryExpr(targetCondition, name, new BooleanLiteralExpr(false), Operator.EQUALS);
                    newConditions.add(isFalse);
                } catch (ParseProblemException e) {}
                    
            });
        allVarNames.stream()
            .forEach(name -> {
                try {
                    final BinaryExpr isNull = this.replaceWithBinaryExpr(targetCondition, name, new NullLiteralExpr(), Operator.EQUALS);
                    newConditions.add(isNull);
                } catch (ParseProblemException e){}
                try {
                    final BinaryExpr isNotNull = this.replaceWithBinaryExpr(targetCondition, name, new NullLiteralExpr(), Operator.NOT_EQUALS);
                    newConditions.add(isNotNull);
                } catch (ParseProblemException e) {}
            });
        
        Expression trueComparison = this.replaceWithExpr(targetCondition, new BooleanLiteralExpr(true));
        if (trueComparison != null)
            newConditions.add(trueComparison);
            
        return newConditions;
    }
    
    /**
     * 与えられた変数からBoolean型の変数の名前を全て収集する 
     * @param vars 仮引数以外の検索対象の変数
     * @param parameters 検索対象の仮引数
     * @return Boolean型変数の名前のリスト
     */
    private List<String> collectBooleanNames(List<VariableDeclarator> vars, List<Parameter> parameters) {
        final List<String> booleanVarNames = new ArrayList<String>();
        vars.stream()
            .filter(v -> v.getTypeAsString().equals("boolean"))
            .map(v -> v.getNameAsString())
            .forEach(booleanVarNames::add);
        parameters.stream()
            .filter(p -> p.getTypeAsString().equals("boolean"))
            .map(p -> p.getNameAsString())
            .forEach(booleanVarNames::add);
        
        return booleanVarNames;
    }

    /**
     * 与えられた全ての変数の名前を収集する 
     * @param vars 仮引数以外の変数
     * @param parameters 仮引数
     * @return 変数の名前のリスト
     */
    private List<String> collectNames(List<VariableDeclarator> vars, List<Parameter> parameters) {
        final List<String> names = new ArrayList<String>();
        vars.stream()
            .map(v -> v.getNameAsString())
            .forEach(names::add);
        parameters.stream()
            .map(p -> p.getNameAsString())
            .forEach(names::add);
        return names;
    }

    /**
     * BinaryExprで置換 
     * @param exprToReplace 置換される元のExpression
     * @param leftExprName BinarExprの左辺の変数名
     * @param rightExpr BinarExprの右辺の変数名
     * @param operator BinaryExprの演算子
     * @return 置換後のBinaryExpr
     */
    private BinaryExpr replaceWithBinaryExpr(Expression exprToReplace, String leftExprName, Expression rightExpr, Operator operator) throws ParseProblemException{
        final BinaryExpr newBinaryExpr = new BinaryExpr(new NameExpr(leftExprName), rightExpr, operator);
        final BinaryExpr insertedBinaryExpr = (BinaryExpr)this.replaceWithExpr(exprToReplace, newBinaryExpr);
        return insertedBinaryExpr;
    }

    /**
     * Expressionを置換 
     * @param exprToReplace 置換される元のExpression
     * @param exprToReplaceWith 新しいExpression
     * @return 置換後の新しいExpression
     */
    private Expression replaceWithExpr(Expression exprToReplace, Expression exprToReplaceWith) throws ParseProblemException{
        final Expression newCondition = (Expression)NodeUtility.deepCopyByReparse(exprToReplace); 
        final Expression insertedExpr = (Expression)NodeUtility.replaceNode(JavaParser.parseExpression(exprToReplaceWith.toString()), newCondition);
        return insertedExpr;
    }

}
