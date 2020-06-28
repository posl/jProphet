package jp.posl.jprophet.evaluator;

import java.util.List;
import java.util.Optional;
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
    /**
     * 特徴抽出を行う 
     * @param line 特徴抽出したい行番号
     * @return ステートメントの特徴
     */
    public Optional<StatementType> extract(Node node) {
        if(node instanceof AssignExpr) {
            return Optional.of(StatementType.ASSIGN);
        }
        if(node instanceof MethodCallExpr) {
            return Optional.of(StatementType.METHOD_CALL);
        }
        // Streamは未対応
        if(node instanceof ForStmt || node instanceof WhileStmt || node instanceof ForeachStmt) {
            return Optional.of(StatementType.LOOP);
        }
        if(node instanceof IfStmt) {
            return Optional.of(StatementType.IF);
        }
        if(node instanceof ReturnStmt) {
            return Optional.of(StatementType.RETURN);
        }
        if(node instanceof BreakStmt) {
            return Optional.of(StatementType.BREAK);
        }
        if(node instanceof ContinueStmt) {
            return Optional.of(StatementType.CONTINUE);
        }
        return Optional.empty();
    }
}