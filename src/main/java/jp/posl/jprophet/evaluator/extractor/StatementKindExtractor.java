package jp.posl.jprophet.evaluator.extractor;

import java.util.Optional;

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


/**
 * AST情報を基にプログラムの各行のステートメントの種類を抽出するクラス
 */
public class StatementKindExtractor {
    public enum StatementKind {
        /* 代入文 */
        ASSIGN,
        /* メソッド呼び出し */
        METHOD_CALL,
        /* ループ構文 */
        LOOP,
        /* if文 */
        IF,
        /* return文 */
        RETURN,
        /* break文 */
        BREAK,
        /* continue文 */
        CONTINUE
    };

    /**
     * 種類の特定を行う 
     * @param line 種類を特定したい行番号
     * @return ステートメントの特定
     */
    public Optional<StatementKind> extract(Node node) {
        if(node instanceof AssignExpr) {
            return Optional.of(StatementKind.ASSIGN);
        }
        if(node instanceof MethodCallExpr) {
            return Optional.of(StatementKind.METHOD_CALL);
        }
        // Streamは未対応
        if(node instanceof ForStmt || node instanceof WhileStmt || node instanceof ForeachStmt) {
            return Optional.of(StatementKind.LOOP);
        }
        if(node instanceof IfStmt) {
            return Optional.of(StatementKind.IF);
        }
        if(node instanceof ReturnStmt) {
            return Optional.of(StatementKind.RETURN);
        }
        if(node instanceof BreakStmt) {
            return Optional.of(StatementKind.BREAK);
        }
        if(node instanceof ContinueStmt) {
            return Optional.of(StatementKind.CONTINUE);
        }
        return Optional.empty();
    }
}