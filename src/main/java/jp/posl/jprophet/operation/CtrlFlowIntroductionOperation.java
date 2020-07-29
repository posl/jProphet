package jp.posl.jprophet.operation;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.SimpleName;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.BreakStmt;
import com.github.javaparser.ast.stmt.ForStmt;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.stmt.WhileStmt;

import jp.posl.jprophet.NodeUtility;
import jp.posl.jprophet.patch.DiffWithType;
import jp.posl.jprophet.patch.DiffWithType.ModifyType;


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
    public List<DiffWithType> exec(Node targetNode){
        if(!(targetNode instanceof Statement)) return new ArrayList<>();
        if(targetNode instanceof BlockStmt) return new ArrayList<>();

        final DeclarationCollector collector = new DeclarationCollector();
        final List<VariableDeclarator> vars = new ArrayList<VariableDeclarator>();
        vars.addAll(collector.collectFileds(targetNode));
        vars.addAll(collector.collectLocalVarsDeclared(targetNode));
        final List<Parameter> parameters = collector.collectParameters(targetNode);

        final String abstractConditionName = "ABST_HOLE";
        final List<Expression> conditions = new ConcreteConditions(new MethodCallExpr(abstractConditionName), vars, parameters).getExpressions();

        final List<DiffWithType> diffWithTypes = new ArrayList<DiffWithType>();
        conditions.stream()
            .map(c -> new IfStmt(c, new ReturnStmt(), null))
            .forEach(stmt -> diffWithTypes.add(new DiffWithType(ModifyType.INSERT, targetNode, stmt)));

        if(targetNode.findParent(ForStmt.class).isPresent() || targetNode.findParent(WhileStmt.class).isPresent()) {
            conditions.stream()
                .map(c -> new IfStmt(c, new BreakStmt((SimpleName) null), null))
                .forEach(stmt -> diffWithTypes.add(new DiffWithType(ModifyType.INSERT, targetNode, stmt)));
        }

        return diffWithTypes;
    }

    /**
     * 条件式が穴あきの状態のif文を指定したノードの前に挿入する
     * 穴あきは"ABST_HOLE()"というメソッド呼び出しを入れておく 
     * @param nextNode 挿入したい箇所の次のノード
     * @param stmtInIfBlockToInsert 挿入するif文のブロック内の文
     * @return 挿入したif文における穴あきの条件式
     */
    private Optional<Expression> insertIfStmtWithAbstCond(Node nextNode, Statement stmtInIfBlockToInsert){
        final String abstractConditionName = "ABST_HOLE";
        final IfStmt newIfStmt =  new IfStmt(new MethodCallExpr(abstractConditionName), stmtInIfBlockToInsert, null);
        return NodeUtility.insertNodeWithNewLine(newIfStmt, nextNode)
            .map(s -> ((IfStmt)s).getCondition());
    }
}