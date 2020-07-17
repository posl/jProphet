package jp.posl.jprophet.evaluator.extractor.feature;

import org.junit.Test;

import jp.posl.jprophet.evaluator.extractor.feature.ModFeature.ModType;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class ModFeatureTest {
    /**
     * 引数なしコンストラクタをテスト
     */
    @Test public void testConstructor() {
        ModFeature feature = new ModFeature();

        assertThat(feature.getTypes().isEmpty()).isTrue();
    }

    /**
     * 引数ありコンストラクタをテスト
     */
    @Test public void testConstructorWithParameter() {
        Set<ModType> expectedTypes = Set.of(ModType.INSERT_CONTROL, ModType.INSERT_GUARD);
        ModFeature feature = new ModFeature(expectedTypes);

        assertThat(feature.getTypes()).isEqualTo(expectedTypes);
    }

    /**
     * addメソッドによる各特徴同士の足し算をテスト
     */
    @Test public void testForFeatureAddition() {
        Set<ModType> addendTypes = new HashSet<>(Arrays.asList(ModType.INSERT_CONTROL));
        Set<ModType> augendTypes = new HashSet<>(Arrays.asList(ModType.INSERT_GUARD));
        ModFeature addend = new ModFeature(addendTypes);
        ModFeature augend = new ModFeature(augendTypes);
        augend.add(addend);

        ModFeature expected = new ModFeature(Set.of(ModType.INSERT_CONTROL, ModType.INSERT_GUARD));

        assertThat(augend).isEqualToComparingFieldByField(expected);
    }

    /**
     * addメソッドによる特徴の追加をテスト
     */
    @Test public void testForTypeAddition() {
        Set<ModType> augendTypes = new HashSet<>(Arrays.asList(ModType.INSERT_GUARD));
        ModFeature augend = new ModFeature(augendTypes);
        augend.add(ModType.INSERT_CONTROL);

        ModFeature expected = new ModFeature(Set.of(ModType.INSERT_CONTROL, ModType.INSERT_GUARD));

        assertThat(augend).isEqualToComparingFieldByField(expected);
    }
}
