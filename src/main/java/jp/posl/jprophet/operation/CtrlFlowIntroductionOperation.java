package jp.posl.jprophet.operation;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
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
    public List<CompilationUnit> exec(Node targetNode){
        if(!(targetNode instanceof Statement)) return new ArrayList<>();
        if(targetNode instanceof BlockStmt) return new ArrayList<>();

        final List<CompilationUnit> compilationUnits = new ArrayList<CompilationUnit>();
        this.insertIfStmtWithAbstCond(targetNode, new ReturnStmt())
            .map(expr -> new ConcreteConditions((Expression)expr).getCompilationUnits())
            .ifPresent(compilationUnits::addAll);

        if(targetNode.findParent(ForStmt.class).isPresent() || targetNode.findParent(WhileStmt.class).isPresent()) {
            this.insertIfStmtWithAbstCond(targetNode, new BreakStmt((SimpleName) null))
                .map(expr -> new ConcreteConditions((Expression)expr).getCompilationUnits())
                .ifPresent(compilationUnits::addAll);
        }

        return compilationUnits;
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