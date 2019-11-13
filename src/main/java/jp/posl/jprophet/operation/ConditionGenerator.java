package jp.posl.jprophet.operation;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.stmt.IfStmt;

public class ConditionGenerator {
    public ConditionGenerator() {

    }

	public Expression generateCondition(Expression abstHole) {
        String expectedSource = new StringBuilder().append("")
        .append("public class A {\n\n") 
        .append("    boolean ba;\n\n")
        .append("    private void ma() {\n")
        .append("        if (ba == a)\n")
        .append("            return;\n")
        .append("    }\n")
        .append("}\n")
        .toString();
    
        IfStmt expectedIfStmt = JavaParser.parse(expectedSource).findFirst(IfStmt.class).get();
        Expression expectedCondExpression = expectedIfStmt.getCondition();
		return expectedCondExpression;
	}
}
