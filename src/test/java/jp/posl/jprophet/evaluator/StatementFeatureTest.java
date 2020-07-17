package jp.posl.jprophet.evaluator;

import org.junit.Test;

import jp.posl.jprophet.evaluator.extractor.feature.StatementFeature;
import jp.posl.jprophet.evaluator.extractor.feature.StatementFeature.StatementType;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class StatementFeatureTest {
    /**
     * 引数なしコンストラクタをテスト
     */
    @Test public void testConstructor() {
        final StatementFeature feature = new StatementFeature();

        assertThat(feature.getTypes().isEmpty()).isTrue();
    }

    /**
     * 引数ありコンストラクタをテスト
     */
    @Test public void testConstructorWithParameter() {
        final Set<StatementType> expectedTypes = Set.of(StatementType.ASSIGN);
        final StatementFeature feature = new StatementFeature(expectedTypes);

        assertThat(feature.getTypes()).isEqualTo(expectedTypes);
    }

    /**
     * addメソッドによる特徴の追加をテスト
     */
    @Test
    public void testForTypeAddition() {
        Set<StatementType> augendTypes = new HashSet<>(Arrays.asList(StatementType.ASSIGN));
        StatementFeature augend = new StatementFeature(augendTypes);
        augend.add(StatementType.BREAK);

        StatementFeature expected = new StatementFeature(Set.of(StatementType.ASSIGN, StatementType.BREAK));

        assertThat(augend).isEqualToComparingFieldByField(expected);
    }
}
