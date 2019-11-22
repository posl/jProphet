package jp.posl.jprophet.operation;

import java.util.ArrayList;
import java.util.List;

import com.github.javaparser.JavaParser;
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
        final Expression abstConditionOfIfReturn = this.insertIfStmtWithAbstCond(targetNode, new ReturnStmt());
        compilationUnits.addAll(this.collectConcreteConditions(abstConditionOfIfReturn));
        if(targetNode.findParent(ForStmt.class).isPresent()) {
            final Expression abstConditionOfIfBreak = this.insertIfStmtWithAbstCond(targetNode, new BreakStmt((SimpleName) null));
            compilationUnits.addAll(this.collectConcreteConditions(abstConditionOfIfBreak));
        }

        return compilationUnits;
    }

    /**
     * 条件式が穴あきの状態のif文を指定したノードの前に挿入する
     * 穴あきは"ABST_HOLE()"というメソッド呼び出しを入れておく 
     * @param node 挿入したい箇所の次のノード
     * @param stmtInIfBlockToInsert 挿入するif文のブロック内の文
     * @return 挿入したif文における穴あきの条件式
     */
    private Expression insertIfStmtWithAbstCond(Node nextNode, Statement stmtInIfBlockToInsert) {
        final String abstractConditionName = "ABST_HOLE";
        final IfStmt newIfStmt =  (IfStmt)JavaParser.parseStatement((new IfStmt(null, new MethodCallExpr(abstractConditionName), stmtInIfBlockToInsert, null)).toString());
        final IfStmt insertedIfStmt = (IfStmt)NodeUtility.insertNodeWithNewLine(newIfStmt, nextNode);
        final Expression abstCondition = insertedIfStmt.getCondition();
        return abstCondition;
    }

    /**
     * 穴あきの条件式から最終的な条件式を生成
     * @param abstCondition 置換される穴あきの条件式
     * @return 生成された条件式を含むCompilationUnit
     */
    private List<CompilationUnit> collectConcreteConditions(Expression abstCondition) {
        final ConditionGenerator conditionGenerator = new ConditionGenerator();
        final List<Expression> concreteConditions = conditionGenerator.generateCondition(abstCondition);

        final List<CompilationUnit> candidates = new ArrayList<CompilationUnit>();
        concreteConditions.stream()
            .forEach(c -> candidates.add(c.findCompilationUnit().orElseThrow()));

        return candidates;
    }
}