package jp.posl.jprophet.evaluator;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import com.github.javaparser.ast.expr.BooleanLiteralExpr;
import com.github.javaparser.ast.stmt.BlockStmt;

import org.junit.Test;

import jp.posl.jprophet.evaluator.NodeWithDiffType.TYPE;

public class NodeWithDiffTypeTest {
    /**
     * {@code getNode()}をテスト
     */
    @Test public void testGetNode() {
        final NodeWithDiffType node = new NodeWithDiffType(new BooleanLiteralExpr(), TYPE.SAME);
        node.addChildNodes(List.of(new NodeWithDiffType(new BooleanLiteralExpr(), TYPE.CHANGE)));

        assertThat(node.getNode()).isInstanceOf(BooleanLiteralExpr.class);
    }

    /**
     * {@code getDiffType()}をテスト
     */
    @Test public void testGetDiffType() {
        final NodeWithDiffType node = new NodeWithDiffType(new BooleanLiteralExpr(), TYPE.SAME);
        node.addChildNodes(List.of(new NodeWithDiffType(new BooleanLiteralExpr(), TYPE.CHANGE)));

        assertThat(node.getDiffType()).isEqualTo(TYPE.SAME);
    }

    /**
     * {@code addChildNodes()}及び{@code getChildNodes()}をテスト
     */
    @Test public void testChildNodes() {
        final NodeWithDiffType node = new NodeWithDiffType(new BooleanLiteralExpr(), TYPE.SAME);
        node.addChildNodes(List.of(new NodeWithDiffType(new BooleanLiteralExpr(), TYPE.CHANGE)));

        assertThat(node.getChildNodes().size()).isEqualTo(1);
        assertThat(node.getChildNodes().get(0).getNode()).isInstanceOf(BooleanLiteralExpr.class);
        assertThat(node.getChildNodes().get(0).getDiffType()).isEqualTo(TYPE.CHANGE);
    }

    
    /**
     * diffTypeを引数に取る{@code findAll()}をテスト
     */
    @Test public void testFindAllByDiffType() {
        final NodeWithDiffType node = new NodeWithDiffType(new BooleanLiteralExpr(), TYPE.SAME);
        node.addChildNodes(List.of(
            new NodeWithDiffType(new BooleanLiteralExpr(), TYPE.CHANGE),
            new NodeWithDiffType(new BlockStmt(), TYPE.SAME)
        ));

        assertThat(node.findAll(TYPE.CHANGE).size()).isEqualTo(1);
    }

    /**
     * nodeTypeを引数に取る{@code findAll()}をテスト
     */
    @Test public void testFindAllByNodeType() {
        final NodeWithDiffType node = new NodeWithDiffType(new BooleanLiteralExpr(), TYPE.SAME);
        node.addChildNodes(List.of(
            new NodeWithDiffType(new BooleanLiteralExpr(), TYPE.CHANGE),
            new NodeWithDiffType(new BlockStmt(), TYPE.SAME)
        ));

        assertThat(node.findAll(BooleanLiteralExpr.class).size()).isEqualTo(2);
    }
}