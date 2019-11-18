package jp.posl.jprophet.operation;

import java.util.ArrayList;
import java.util.List;

import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.expr.BooleanLiteralExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.NullLiteralExpr;
import com.github.javaparser.ast.expr.BinaryExpr.Operator;

import jp.posl.jprophet.NodeUtility;


public class ConditionGenerator {

	public List<Expression> generateCondition(Expression abstHole) {
        DeclarationCollector collector = new DeclarationCollector();
        List<VariableDeclarator> vars = new ArrayList<VariableDeclarator>();
        vars.addAll(collector.collectFileds(abstHole));
        vars.addAll(collector.collectLocalVars(abstHole));
        List<Parameter> parameters = collector.collectParameters(abstHole);
        
        List<String> booleanVarNames = this.collectBooleanNames(vars, parameters);
        List<String> objectNames = this.collectObjectNames(vars, parameters);

        List<Expression> newConditions = new ArrayList<Expression>();
        booleanVarNames.stream()
            .forEach(name -> {
                BinaryExpr isTrue = this.replaceWithBinaryExpr(abstHole, name, new BooleanLiteralExpr(true), Operator.EQUALS);
                newConditions.add(isTrue);
                BinaryExpr isFalse = this.replaceWithBinaryExpr(abstHole, name, new BooleanLiteralExpr(false), Operator.EQUALS);
                newConditions.add(isFalse);
            });
        objectNames.stream()
            .forEach(name -> {
                BinaryExpr isNull = this.replaceWithBinaryExpr(abstHole, name, new NullLiteralExpr(), Operator.EQUALS);
                newConditions.add(isNull);
                BinaryExpr isNotNull = this.replaceWithBinaryExpr(abstHole, name, new NullLiteralExpr(), Operator.NOT_EQUALS);
                newConditions.add(isNotNull);
            });
            
        return newConditions;
    }
    
    private List<String> collectBooleanNames(List<VariableDeclarator> vars, List<Parameter> parameters) {
        List<String> booleanVarNames = new ArrayList<String>();
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

    private List<String> collectObjectNames(List<VariableDeclarator> vars, List<Parameter> parameters) {
        List<String> objectNames = new ArrayList<String>();
        vars.stream()
            .map(v -> v.getNameAsString())
            .forEach(objectNames::add);
        parameters.stream()
            .map(p -> p.getNameAsString())
            .forEach(objectNames::add);
        return objectNames;
    }

    private BinaryExpr replaceWithBinaryExpr(Expression replaceThisExpression, String leftExprName, Expression rightExpr, Operator operator){
        Expression newCondition = (Expression)NodeUtility.deepCopy(replaceThisExpression); 
        BinaryExpr newNode = new BinaryExpr(new NameExpr(leftExprName), rightExpr, operator);
        newCondition.replace(newNode);
        return newNode;
    }
}
