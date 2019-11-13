package jp.posl.jprophet.operation;

import java.util.List;
import java.util.stream.Collectors;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.stmt.IfStmt;

public class ConditionGenerator {
    public ConditionGenerator() {

    }

	public List<Expression> generateCondition(Expression abstHole) {
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
                    "        if (localObjectA != null)\n"
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

        return expectedCondExpressions;
	}
}
