package jp.posl.jprophet.operation;

import java.util.ArrayList;
import java.util.List;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.Statement;

import jp.posl.jprophet.NodeUtility;
import jp.posl.jprophet.patch.DiffWithType;
import jp.posl.jprophet.patch.DiffWithType.ModifyType;


/**
 * 対象のステートメントをif文で挟む
 */
public class CondIntroductionOperation implements AstOperation{
    /**
     * {@inheritDoc}
     */
    public List<DiffWithType> exec(Node targetNode){
        if(!(targetNode instanceof Statement)) return new ArrayList<>();
        if(targetNode instanceof BlockStmt) return new ArrayList<>();

        final DeclarationCollector collector = new DeclarationCollector();
        final List<VariableDeclarator> vars = new ArrayList<VariableDeclarator>();
        vars.addAll(collector.collectFileds(targetNode));
        vars.addAll(collector.collectLocalVarsDeclared(targetNode));
        final List<Parameter> parameters = collector.collectParameters(targetNode);

        final Statement thenStmt = (Statement)NodeUtility.deepCopyByReparse(targetNode); 

        final List<DiffWithType> diffWithTypes = new ArrayList<DiffWithType>();

        final ConcreteConditions concreteConditions = new ConcreteConditions(vars, parameters);
        concreteConditions.getExpressions()
            .forEach(expr -> {
                try {
                    IfStmt newIfStmt =  (IfStmt)JavaParser.parseStatement((new IfStmt(expr, thenStmt, null)).toString());
                    diffWithTypes.add(new DiffWithType(ModifyType.CHANGE, targetNode, newIfStmt));
                } catch (Exception e) {
                }
            });
        return diffWithTypes;
    }
}