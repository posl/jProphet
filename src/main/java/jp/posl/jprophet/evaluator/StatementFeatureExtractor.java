package jp.posl.jprophet.evaluator;

import java.util.List;
import java.util.stream.Collectors;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.stmt.BreakStmt;
import com.github.javaparser.ast.stmt.ContinueStmt;
import com.github.javaparser.ast.stmt.ForStmt;
import com.github.javaparser.ast.stmt.ForeachStmt;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.stmt.WhileStmt;

import jp.posl.jprophet.NodeUtility;
import jp.posl.jprophet.evaluator.StatementFeature.StatementType;

/**
 * AST情報を基にプログラムの各行のステートメントの特徴を抽出するクラス
 */
public class StatementFeatureExtractor {
    final List<Node> nodesInOrderOfSourceCode;

    /**
     * 抽出対象のプログラムのASTを元に特徴抽出を行う抽出器を作成
     * @param root 特徴抽出するプログラムのASTノード
     */
    public StatementFeatureExtractor(Node root) {
        nodesInOrderOfSourceCode = NodeUtility.getAllNodesInDepthFirstOrder(root);
    }

    /**
     * 特徴抽出を行う 
     * @param line 特徴抽出したい行番号
     * @return ステートメントの特徴
     */
    public StatementFeature extract(int line) {
        List<Node> nodesInTheLine = this.nodesInOrderOfSourceCode.stream()
            .filter(n ->  {
                if(!n.getBegin().isPresent()) return false;
                return n.getBegin().get().line == line;
            })
            .collect(Collectors.toList());
        if(nodesInTheLine.isEmpty()) {
            throw new IllegalArgumentException("There is no such line in the AST.");
        }

        StatementFeature feature = new StatementFeature();
        for(Node nodeInTheLine: nodesInTheLine) {
            if(nodeInTheLine instanceof AssignExpr) {
                feature.add(StatementType.ASSIGN);
            }
            if(nodeInTheLine instanceof MethodCallExpr) {
                feature.add(StatementType.METHOD_CALL);
            }
            // Streamは未対応
            if(nodeInTheLine instanceof ForStmt || nodeInTheLine instanceof WhileStmt || nodeInTheLine instanceof ForeachStmt) {
                feature.add(StatementType.LOOP);
            }
            if(nodeInTheLine instanceof IfStmt) {
                feature.add(StatementType.IF);
            }
            if(nodeInTheLine instanceof ReturnStmt) {
                feature.add(StatementType.RETURN);
            }
            if(nodeInTheLine instanceof BreakStmt) {
                feature.add(StatementType.BREAK);
            }
            if(nodeInTheLine instanceof ContinueStmt) {
                feature.add(StatementType.CONTINUE);
            }
        }
        return feature;
    }    
}