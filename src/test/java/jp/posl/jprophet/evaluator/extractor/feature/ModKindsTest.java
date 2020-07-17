package jp.posl.jprophet.evaluator.extractor.feature;

import org.junit.Test;

import jp.posl.jprophet.evaluator.extractor.feature.ModKinds.ModKind;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class ModKindsTest {
    /**
     * 引数なしコンストラクタをテスト
     */
    @Test public void testConstructor() {
        ModKinds kinds = new ModKinds();

        assertThat(kinds.getTypes().isEmpty()).isTrue();
    }

    /**
     * 引数ありコンストラクタをテスト
     */
    @Test public void testConstructorWithParameter() {
        Set<ModKind> expectedTypes = Set.of(ModKind.INSERT_CONTROL, ModKind.INSERT_GUARD);
        ModKinds kinds = new ModKinds(expectedTypes);

        assertThat(kinds.getTypes()).isEqualTo(expectedTypes);
    }

    /**
     * addメソッドによる各特徴同士の足し算をテスト
     */
    @Test public void testForKindAddition() {
        Set<ModKind> addendTypes = new HashSet<>(Arrays.asList(ModKind.INSERT_CONTROL));
        Set<ModKind> augendTypes = new HashSet<>(Arrays.asList(ModKind.INSERT_GUARD));
        ModKinds addend = new ModKinds(addendTypes);
        ModKinds augend = new ModKinds(augendTypes);
        augend.add(addend);

        ModKinds expected = new ModKinds(Set.of(ModKind.INSERT_CONTROL, ModKind.INSERT_GUARD));

        assertThat(augend).isEqualToComparingFieldByField(expected);
    }

    /**
     * addメソッドによる特徴の追加をテスト
     */
    @Test public void testForTypeAddition() {
        Set<ModKind> augendTypes = new HashSet<>(Arrays.asList(ModKind.INSERT_GUARD));
        ModKinds augend = new ModKinds(augendTypes);
        augend.add(ModKind.INSERT_CONTROL);

        ModKinds expected = new ModKinds(Set.of(ModKind.INSERT_CONTROL, ModKind.INSERT_GUARD));

        assertThat(augend).isEqualToComparingFieldByField(expected);
    }
}
