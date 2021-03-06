package jp.posl.jprophet.operation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.Statement;

import jp.posl.jprophet.patch.OperationDiff;
import jp.posl.jprophet.patch.OperationDiff.ModifyType;


/**
 * 対象のステートメントをif文で挟む
 */
public class CondIntroductionOperation implements AstOperation{
    /**
     * {@inheritDoc}
     */
    public List<OperationDiff> exec(Node targetNode){
        if(!(targetNode instanceof Statement)) return new ArrayList<>();
        if(targetNode instanceof BlockStmt) return new ArrayList<>();

        final DeclarationCollector collector = new DeclarationCollector();
        final List<VariableDeclarator> vars = new ArrayList<VariableDeclarator>();
        vars.addAll(collector.collectFileds(targetNode));
        vars.addAll(collector.collectLocalVarsDeclared(targetNode));
        final List<Parameter> parameters = collector.collectParameters(targetNode);

        final Statement thenStmt = (Statement)targetNode.clone();

        final List<OperationDiff> operationDiffs = new ArrayList<OperationDiff>();

        final ConcreteConditions concreteConditions = new ConcreteConditions(vars, parameters);
        concreteConditions.getExpressions()
            .forEach(expr -> {
                try {
                    IfStmt newIfStmt =  (IfStmt)JavaParser.parseStatement((new IfStmt(expr, thenStmt, null)).toString());
                    operationDiffs.add(new OperationDiff(ModifyType.CHANGE, targetNode, newIfStmt));
                } catch (Exception e) {
                }
            });
        return operationDiffs;
    }
}