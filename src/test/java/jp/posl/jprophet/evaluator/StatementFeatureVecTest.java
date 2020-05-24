package jp.posl.jprophet.evaluator;

import org.junit.Test;
import static org.assertj.core.api.Assertions.assertThat;

public class StatementFeatureVecTest {
    /**
     * 引数なしコンストラクタをテスト
     */
    @Test public void testConstructor() {
        StatementFeatureVec vec = new StatementFeatureVec();

        assertThat(vec.assignStmt).isEqualTo(0);
        assertThat(vec.methodCallStmt).isEqualTo(0);
        assertThat(vec.loopStmt).isEqualTo(0);
        assertThat(vec.ifStmt).isEqualTo(0);
        assertThat(vec.returnStmt).isEqualTo(0);
        assertThat(vec.breakStmt).isEqualTo(0);
        assertThat(vec.continueStmt).isEqualTo(0);
    }

    /**
     * 引数ありコンストラクタをテスト
     */
    @Test public void testConstructorWithParameter() {
        StatementFeatureVec vec = new StatementFeatureVec(1, 2, 3, 4, 5, 6, 7);

        assertThat(vec.assignStmt).isEqualTo(1);
        assertThat(vec.methodCallStmt).isEqualTo(2);
        assertThat(vec.loopStmt).isEqualTo(3);
        assertThat(vec.ifStmt).isEqualTo(4);
        assertThat(vec.returnStmt).isEqualTo(5);
        assertThat(vec.breakStmt).isEqualTo(6);
        assertThat(vec.continueStmt).isEqualTo(7);
    }
}
