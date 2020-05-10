package jp.posl.jprophet.evaluator;

import static org.assertj.core.api.Assertions.assertThat;
import java.util.List;

import com.github.javaparser.ast.expr.BooleanLiteralExpr;

import org.junit.Test;

public class AstDeltaTest {
    /**
     * getDeleteNodes()がコンストラクタの第一引数にdeleteNodesとして渡されたリストを返すかテスト
     */
    @Test public void testForGetDeleteNodes() {
        final AstDelta astDelta = new AstDelta(
            List.of(new BooleanLiteralExpr(true)),
            List.of(new BooleanLiteralExpr(false))
        );
        assertThat(astDelta.getDeleteNodes()).containsOnly(new BooleanLiteralExpr(true));
    }

    /**
     * getAddNodes()がコンストラクタの第二引数にaddNodesとして渡されたリストを返すかテスト
     */
    @Test public void testForGetAddNodes() {
        final AstDelta astDelta = new AstDelta(
            List.of(new BooleanLiteralExpr(true)),
            List.of(new BooleanLiteralExpr(false))
        );
        assertThat(astDelta.getAddNodes()).containsOnly(new BooleanLiteralExpr(false));
    }
}