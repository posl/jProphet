package jp.posl.jprophet.operation;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.SimpleName;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.BreakStmt;
import com.github.javaparser.ast.stmt.ForStmt;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.stmt.WhileStmt;
import com.github.javaparser.ast.type.VoidType;

import jp.posl.jprophet.patch.OperationDiff;
import jp.posl.jprophet.patch.OperationDiff.ModifyType;


/**
 * <p>
 * 抽象条件式がtrueの時に実行されるような,
 * コントロールフローを制御するステートメント(return, breakなど)を
 * 対象の前に挿入する  
 * </p>
 * if (true)  
 *     return;   
 * など
 */
public class CtrlFlowIntroductionOperation implements AstOperation{
    /**
     * {@inheritDoc}
     */
    @Override
    public List<OperationDiff> exec(Node targetNode){
        if(!(targetNode instanceof Statement)) return new ArrayList<>();
        if(targetNode instanceof BlockStmt) return new ArrayList<>();
        if (targetNode.findParent(ConstructorDeclaration.class).isPresent()) return new ArrayList<>();

        final DeclarationCollector collector = new DeclarationCollector();
        final List<VariableDeclarator> vars = new ArrayList<VariableDeclarator>();
        vars.addAll(collector.collectFileds(targetNode));
        vars.addAll(collector.collectLocalVarsDeclared(targetNode));
        final List<Parameter> parameters = collector.collectParameters(targetNode);

        final List<Expression> conditions = new ConcreteConditions(vars, parameters).getExpressions();

        final List<OperationDiff> operationDiffs = new ArrayList<OperationDiff>();

        final MethodDeclaration methodDeclaration = targetNode.findParent(MethodDeclaration.class).orElseThrow();
        final boolean targetNodeIsInVoidTypeMethod = methodDeclaration.getChildNodes().stream()
            .anyMatch(child -> child instanceof VoidType);
        if (targetNodeIsInVoidTypeMethod) {
            conditions.stream()
                .map(c -> new IfStmt(c, new ReturnStmt(), null))
                .forEach(stmt -> operationDiffs.add(new OperationDiff(ModifyType.INSERT, targetNode, stmt)));
        }

        if(targetNode.findParent(ForStmt.class).isPresent() || targetNode.findParent(WhileStmt.class).isPresent()) {
            conditions.stream()
                .map(c -> new IfStmt(c, new BreakStmt((SimpleName) null), null))
                .forEach(stmt -> operationDiffs.add(new OperationDiff(ModifyType.INSERT, targetNode, stmt)));
        }

        return operationDiffs;
    }

}