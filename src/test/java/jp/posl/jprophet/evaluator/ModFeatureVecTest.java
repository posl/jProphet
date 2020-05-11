package jp.posl.jprophet.evaluator;

import org.junit.Test;
import static org.assertj.core.api.Assertions.assertThat;

public class ModFeatureVecTest {
    /**
     * 引数なしコンストラクタをテスト
     */
    @Test public void testConstructor() {
        ModFeatureVec vec = new ModFeatureVec();

        assertThat(vec.insertControl).isEqualTo(0);
        assertThat(vec.insertGuard).isEqualTo(0);
        assertThat(vec.replaceCond).isEqualTo(0);
        assertThat(vec.replaceStmt).isEqualTo(0);
        assertThat(vec.insertStmt).isEqualTo(0);
    }

    /**
     * 引数なしコンストラクタをテスト
     */
    @Test public void testConstructorWithParameter() {
        ModFeatureVec vec = new ModFeatureVec(1, 2, 3, 4, 5);

        assertThat(vec.insertControl).isEqualTo(1);
        assertThat(vec.insertGuard).isEqualTo(2);
        assertThat(vec.replaceCond).isEqualTo(3);
        assertThat(vec.replaceStmt).isEqualTo(4);
        assertThat(vec.insertStmt).isEqualTo(5);
    }

    /**
     * addメソッドでのベクトル同士の足し算をテスト
     */
    @Test public void testForAddMethod() {
        ModFeatureVec augendVec = new ModFeatureVec(1, 1, 1, 1 ,1);
        ModFeatureVec addendVec = new ModFeatureVec(2, 2, 2, 2, 2);
        augendVec.add(addendVec);

        ModFeatureVec expectedVec = new ModFeatureVec(3, 3, 3, 3, 3);

        assertThat(augendVec).isEqualToComparingFieldByField(expectedVec);
    }
}
