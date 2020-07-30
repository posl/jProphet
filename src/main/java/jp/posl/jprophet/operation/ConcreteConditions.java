package jp.posl.jprophet.operation;

import java.util.ArrayList;
import java.util.List;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.expr.BooleanLiteralExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.NullLiteralExpr;
import com.github.javaparser.ast.expr.BinaryExpr.Operator;


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
	public ConcreteConditions(List<VariableDeclarator> vars, List<Parameter> parameters) {
        final List<String> booleanVarNames = this.collectBooleanNames(vars, parameters);
        final List<String> allVarNames = this.collectNames(vars, parameters);
        this.generateExpressions(booleanVarNames, allVarNames);
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
    private void generateExpressions(List<String> booleanVarNames, List<String> allVarNames) {
        booleanVarNames.stream()
            .forEach(name -> {
                this.expressions.add(this.replaceWithBinaryExpr(name, new BooleanLiteralExpr(true), Operator.EQUALS));
                this.expressions.add(this.replaceWithBinaryExpr(name, new BooleanLiteralExpr(false), Operator.EQUALS));
            });
        allVarNames.stream()
            .forEach(name -> {
                this.expressions.add(this.replaceWithBinaryExpr(name, new NullLiteralExpr(), Operator.EQUALS));
                this.expressions.add(this.replaceWithBinaryExpr(name, new NullLiteralExpr(), Operator.NOT_EQUALS));
            });
        this.expressions.add(new BooleanLiteralExpr(true));
    }

    /**
     * BinaryExprで置換 
     * @param exprToReplace 置換される元のExpression
     * @param leftExprName BinarExprの左辺の変数名
     * @param rightExpr BinarExprの右辺の変数名
     * @param operator BinaryExprの演算子
     * @return 置換後のBinaryExpr
     */
    private BinaryExpr replaceWithBinaryExpr(String leftExprName, Expression rightExpr, Operator operator){
        final BinaryExpr newBinaryExpr = new BinaryExpr(new NameExpr(leftExprName), rightExpr, operator);
        return newBinaryExpr;
    }

}
