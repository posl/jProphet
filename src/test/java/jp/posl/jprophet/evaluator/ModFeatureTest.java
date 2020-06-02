package jp.posl.jprophet.evaluator;

import org.junit.Test;
import static org.assertj.core.api.Assertions.assertThat;

public class ModFeatureTest {
    /**
     * 引数なしコンストラクタをテスト
     */
    @Test public void testConstructor() {
        ModFeature feature = new ModFeature();

        assertThat(feature.insertControl).isEqualTo(0);
        assertThat(feature.insertGuard).isEqualTo(0);
        assertThat(feature.replaceCond).isEqualTo(0);
        assertThat(feature.replaceVar).isEqualTo(0);
        assertThat(feature.replaceMethod).isEqualTo(0);
        assertThat(feature.insertStmt).isEqualTo(0);
    }

    /**
     * 引数ありコンストラクタをテスト
     */
    @Test public void testConstructorWithParameter() {
        ModFeature feature = new ModFeature(1, 2, 3, 4, 5, 6);

        assertThat(feature.insertControl).isEqualTo(1);
        assertThat(feature.insertGuard).isEqualTo(2);
        assertThat(feature.replaceCond).isEqualTo(3);
        assertThat(feature.replaceVar).isEqualTo(4);
        assertThat(feature.replaceMethod).isEqualTo(5);
        assertThat(feature.insertStmt).isEqualTo(6);
    }

    /**
     * addメソッドによる各特徴同士の足し算をテスト
     */
    @Test public void testForAddMethod() {
        ModFeature augend = new ModFeature(1, 1, 1, 1 ,1, 1);
        ModFeature addend = new ModFeature(2, 2, 2, 2, 2, 2);
        augend.add(addend);

        ModFeature expected = new ModFeature(3, 3, 3, 3, 3, 3);

        assertThat(augend).isEqualToComparingFieldByField(expected);
    }
}
