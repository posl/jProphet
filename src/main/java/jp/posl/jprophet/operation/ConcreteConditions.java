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
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.PrimitiveType;
import com.github.javaparser.ast.type.PrimitiveType.Primitive;


/**
 * 穴あきの条件式を元に具体的な条件式を生成するクラス
 */
public class ConcreteConditions {
    private List<Expression> expressions = new ArrayList<Expression>();

    /**
     * 参照可能な変数から以下の複数の条件式を生成する</br>
     * @param vars 変数のリスト
     * @param parameters パラメータのリスト
     */
	public ConcreteConditions(List<VariableDeclarator> vars, List<Parameter> parameters) {
        final List<String> booleanVarNames = this.collectBooleanNames(vars, parameters);
        final List<String> allVarNames = this.collectObjectNames(vars, parameters);
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
     * 与えられた変数からObject型の変数の名前を全て収集する 
     * @param vars 仮引数以外の変数
     * @param parameters 仮引数
     * @return 変数の名前のリスト
     */
    private List<String> collectObjectNames(List<VariableDeclarator> vars, List<Parameter> parameters) {
        final List<String> names = new ArrayList<String>();
        vars.stream()
            .filter(v -> v.getType() instanceof ClassOrInterfaceType)
            .map(v -> v.getNameAsString())
            .forEach(names::add);
        parameters.stream()
            .filter(v -> v.getType() instanceof ClassOrInterfaceType)
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
                this.expressions.add(this.generateBinaryExpr(name, new BooleanLiteralExpr(true), Operator.EQUALS));
                this.expressions.add(this.generateBinaryExpr(name, new BooleanLiteralExpr(false), Operator.EQUALS));
            });
        allVarNames.stream()
            .forEach(name -> {
                this.expressions.add(this.generateBinaryExpr(name, new NullLiteralExpr(), Operator.EQUALS));
                this.expressions.add(this.generateBinaryExpr(name, new NullLiteralExpr(), Operator.NOT_EQUALS));
            });
        this.expressions.add(new BooleanLiteralExpr(true));
    }

    /**
     * BinaryExprを生成 
     * @param leftExprName BinarExprの左辺の変数名
     * @param rightExpr BinarExprの右辺の変数名
     * @param operator BinaryExprの演算子
     * @return 生成されたのBinaryExpr
     */
    private BinaryExpr generateBinaryExpr(String leftExprName, Expression rightExpr, Operator operator){
        final BinaryExpr newBinaryExpr = new BinaryExpr(new NameExpr(leftExprName), rightExpr, operator);
        return newBinaryExpr;
    }

}
