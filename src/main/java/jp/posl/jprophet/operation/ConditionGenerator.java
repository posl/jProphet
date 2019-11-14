package jp.posl.jprophet.operation;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.expr.BooleanLiteralExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.NullLiteralExpr;
import com.github.javaparser.ast.expr.BinaryExpr.Operator;
import com.github.javaparser.ast.stmt.IfStmt;

import jp.posl.jprophet.NodeUtility;


public class ConditionGenerator {
    public ConditionGenerator() {

    }

	public List<Expression> generateCondition(Expression abstHole) {
        DeclarationCollector collector = new DeclarationCollector();
        List<VariableDeclarator> vars = new ArrayList<VariableDeclarator>();
        vars.addAll(collector.collectFileds(abstHole));
        vars.addAll(collector.collectLocalVars(abstHole));
        List<Parameter> parameters = collector.collectParameters(abstHole);

        List<String> booleanVarNames = new ArrayList<String>();
        vars.stream()
            .filter(v -> v.getTypeAsString().equals("boolean"))
            .map(v -> v.getNameAsString())
            .forEach(booleanVarNames::add);
        parameters.stream()
            .filter(p -> p.getTypeAsString().equals("boolean"))
            .map(p -> p.getNameAsString())
            .forEach(booleanVarNames::add);
        List<String> objectNames = new ArrayList<String>();
        vars.stream()
            .map(v -> v.getNameAsString())
            .forEach(objectNames::add);
        parameters.stream()
            .map(p -> p.getNameAsString())
            .forEach(objectNames::add);


        List<Expression> newConditions = new ArrayList<Expression>();
        booleanVarNames.stream()
            .forEach(name -> {
                Expression newCondition = (Expression)NodeUtility.deepCopy(abstHole); 
                Expression newNode = new BinaryExpr(new NameExpr(name), new BooleanLiteralExpr(true), Operator.EQUALS);
                newCondition.replace(newNode);
                newConditions.add(newNode);
                Expression newCondition2 = (Expression)NodeUtility.deepCopy(abstHole); 
                Expression newNode2 = new BinaryExpr(new NameExpr(name), new BooleanLiteralExpr(false), Operator.EQUALS);
                newCondition2.replace(newNode2);
                newConditions.add(newNode2);
            });

        objectNames.stream()
            .forEach(name -> {
                Expression newCondition = (Expression)NodeUtility.deepCopy(abstHole); 
                Expression newNode = new BinaryExpr(new NameExpr(name), new NullLiteralExpr(), Operator.EQUALS);
                newCondition.replace(newNode);
                newConditions.add(newNode);
                Expression newCondition2 = (Expression)NodeUtility.deepCopy(abstHole); 
                Expression newNode2 = new BinaryExpr(new NameExpr(name), new NullLiteralExpr(), Operator.NOT_EQUALS);
                newCondition2.replace(newNode2);
                newConditions.add(newNode2);
            });
            
        // newCondition.getTokenRange()

        String expectedSourceBeforeTarget = new StringBuilder().append("")
            .append("public class A {\n\n") 
            .append("    boolean fieldBoolVarA;\n\n")
            .append("    private void methodA() {\n")
            .append("        boolean localBoolVarA;\n\n")
            .append("        Object localObjectA;\n\n")
            .toString();

        List<String> expectedTargetSources = List.of(
                    "        if (fieldBoolVarA == true)\n",
                    "        if (fieldBoolVarA == false)\n",
                    "        if (localBoolVarA == true)\n",
                    "        if (localBoolVarA == false)\n",
                    "        if (localObjectA == null)\n",
                    "        if (localObjectA != null)\n",
                    "        if (fieldBoolVarA == null)\n",
                    "        if (fieldBoolVarA == null)\n",
                    "        if (localBoolVarA == null)\n",
                    "        if (localBoolVarA == null)\n"
        );

        String expectedSourceAfterTarget = new StringBuilder().append("")
            .append("            return;\n")
            .append("    }\n")
            .append("}\n")
            .toString();
        
        List<IfStmt> expectedIfStmts = expectedTargetSources.stream()
            .map(s -> { 
                return new StringBuilder()
                    .append(expectedSourceBeforeTarget)
                    .append(s)
                    .append(expectedSourceAfterTarget)
                    .toString();
            })
            .map(s -> { 
                return JavaParser.parse(s).findFirst(IfStmt.class).get();
            })
            .collect(Collectors.toList());

        List<Expression> expectedCondExpressions = expectedIfStmts.stream()
            .map(s -> s.getCondition())
            .collect(Collectors.toList());

        return newConditions;
	}
}
