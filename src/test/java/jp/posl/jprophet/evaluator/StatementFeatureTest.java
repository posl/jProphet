package jp.posl.jprophet.evaluator;

import org.junit.Test;
import static org.assertj.core.api.Assertions.assertThat;

public class StatementFeatureTest {
    /**
     * 引数なしコンストラクタをテスト
     */
    @Test public void testConstructor() {
        StatementFeature feature = new StatementFeature();

        assertThat(feature.assignStmt).isEqualTo(0);
        assertThat(feature.methodCallStmt).isEqualTo(0);
        assertThat(feature.loopStmt).isEqualTo(0);
        assertThat(feature.ifStmt).isEqualTo(0);
        assertThat(feature.returnStmt).isEqualTo(0);
        assertThat(feature.breakStmt).isEqualTo(0);
        assertThat(feature.continueStmt).isEqualTo(0);
    }

    /**
     * 引数ありコンストラクタをテスト
     */
    @Test public void testConstructorWithParameter() {
        StatementFeature feature = new StatementFeature(1, 2, 3, 4, 5, 6, 7);

        assertThat(feature.assignStmt).isEqualTo(1);
        assertThat(feature.methodCallStmt).isEqualTo(2);
        assertThat(feature.loopStmt).isEqualTo(3);
        assertThat(feature.ifStmt).isEqualTo(4);
        assertThat(feature.returnStmt).isEqualTo(5);
        assertThat(feature.breakStmt).isEqualTo(6);
        assertThat(feature.continueStmt).isEqualTo(7);
    }
}
