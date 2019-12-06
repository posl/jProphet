package jp.posl.jprophet.operation;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.github.javaparser.ast.CompilationUnit;
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
 * 穴あきの条件式を元に具体的な条件式を生成するクラス
 */
public class ConcreteConditions {
    private List<Expression> expressions = new ArrayList<Expression>();

    /**
     * 参照可能な変数から以下の複数の条件式を生成する</br>
     * 
     * @param abstCondition 条件式を生成したい箇所のExpressionノード
     */
	public ConcreteConditions(Expression abstCondition) {
        final DeclarationCollector collector = new DeclarationCollector();
        final List<VariableDeclarator> vars = new ArrayList<VariableDeclarator>();
        vars.addAll(collector.collectFileds(abstCondition));
        vars.addAll(collector.collectLocalVarsDeclared(abstCondition));
        final List<Parameter> parameters = collector.collectParameters(abstCondition);
        
        final List<String> booleanVarNames = this.collectBooleanNames(vars, parameters);
        final List<String> allVarNames = this.collectNames(vars, parameters);
        this.generateExpressions(abstCondition, booleanVarNames, allVarNames);
    }

    /**
     * 条件式を取得 
     * @return Expressionノードのリスト
     */
    public List<Expression> getExpressions(){
        return this.expressions;
    }

    /**
     * 生成した条件式を含んだCompilationUnitを取得
     * @return CompilationUnitのリスト
     */
    public List<CompilationUnit> getCompilationUnits() {
        final List<CompilationUnit> compilationUnits = new ArrayList<CompilationUnit>();
        this.expressions.stream()
            .forEach(c -> compilationUnits.add(c.findCompilationUnit().orElseThrow()));

        return compilationUnits;
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
     * 参照可能な変数名を元に条件式を生成
     * <ul>
     * <li>全ての変数とnullを==と!=で比較</li>
     * <li>全てのBool変数とtrueを==と!=で比較</li>
     * <li>true（恒真）</li>
     * </ul>
     * @param abstCondition 生成して置き換わる条件式
     * @param booleanVarNames Boolen変数名
     * @param allVarNames 全ての変数名
     */
    private void generateExpressions(Expression abstCondition, List<String> booleanVarNames, List<String> allVarNames) {
        booleanVarNames.stream()
            .forEach(name -> {
                this.replaceWithBinaryExpr(abstCondition, name, new BooleanLiteralExpr(true), Operator.EQUALS)
                    .ifPresent(this.expressions::add);

                this.replaceWithBinaryExpr(abstCondition, name, new BooleanLiteralExpr(false), Operator.EQUALS)
                    .ifPresent(this.expressions::add);
            });
        allVarNames.stream()
            .forEach(name -> {
                this.replaceWithBinaryExpr(abstCondition, name, new NullLiteralExpr(), Operator.EQUALS)
                    .ifPresent(this.expressions::add);

                this.replaceWithBinaryExpr(abstCondition, name, new NullLiteralExpr(), Operator.NOT_EQUALS)
                    .ifPresent(this.expressions::add);
            });
        this.replaceWithExpr(abstCondition, new BooleanLiteralExpr(true))
            .ifPresent(this.expressions::add);
    }

    /**
     * BinaryExprで置換 
     * @param exprToReplace 置換される元のExpression
     * @param leftExprName BinarExprの左辺の変数名
     * @param rightExpr BinarExprの右辺の変数名
     * @param operator BinaryExprの演算子
     * @return 置換後のBinaryExpr
     */
    private Optional<BinaryExpr> replaceWithBinaryExpr(Expression exprToReplace, String leftExprName, Expression rightExpr, Operator operator){
        final BinaryExpr newBinaryExpr = new BinaryExpr(new NameExpr(leftExprName), rightExpr, operator);
        return this.replaceWithExpr(exprToReplace, newBinaryExpr)
            .map(expr -> (BinaryExpr)expr);
    }

    /**
     * Expressionを置換 
     * @param exprToReplace 置換される元のExpression
     * @param exprToReplaceWith 新しいExpression
     * @return 置換後の新しいExpression
     */
    private Optional<Expression> replaceWithExpr(Expression exprToReplace, Expression exprToReplaceWith){
        final Expression newCondition = (Expression)NodeUtility.deepCopyByReparse(exprToReplace);
        return NodeUtility.replaceNode(exprToReplaceWith, newCondition)
            .map(expr -> (Expression)expr);
    }

}
