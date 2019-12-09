package jp.posl.jprophet.operation;

import java.util.List;

import com.github.javaparser.ast.expr.ThisExpr;

public class MethodCollector {
    public List<String> collectMethodName(ThisExpr thisExpr){
        return List.of("ma", "mb", "mc");
    }
}