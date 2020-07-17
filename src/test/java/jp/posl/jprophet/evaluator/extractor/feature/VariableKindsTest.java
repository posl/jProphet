package jp.posl.jprophet.evaluator.extractor.feature;

import org.junit.Test;

import jp.posl.jprophet.evaluator.extractor.feature.VariableKinds.VarKind;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;


public class VariableKindsTest {
    /**
     * 引数なしコンストラクタをテスト
     */
    @Test public void testConstructor() {
        VariableKinds kinds = new VariableKinds();

        assertThat(kinds.getTypes().isEmpty()).isTrue();
    }

    /**
     * 引数ありコンストラクタをテスト
     */
    @Test public void testConstructorWithParameter() {
        Set<VarKind> expectedTypes = Set.of(VarKind.ARGUMENT, VarKind.BOOLEAN);
        VariableKinds kinds = new VariableKinds(expectedTypes);

        assertThat(kinds.getTypes()).isEqualTo(expectedTypes);
    }

    /**
     * addメソッドによる各特徴同士の足し算をテスト
     */
    @Test public void testForKindAddition() {
        Set<VarKind> addendTypes = new HashSet<>(Arrays.asList(VarKind.ARGUMENT));
        Set<VarKind> augendTypes = new HashSet<>(Arrays.asList(VarKind.BOOLEAN));
        VariableKinds addend = new VariableKinds(addendTypes);
        VariableKinds augend = new VariableKinds(augendTypes);
        augend.add(addend);

        VariableKinds expected = new VariableKinds(Set.of(VarKind.ARGUMENT, VarKind.BOOLEAN));

        assertThat(augend).isEqualToComparingFieldByField(expected);
    }

    /**
     * addメソッドによる特徴の追加をテスト
     */
    @Test public void testForTypeAddition() {
        Set<VarKind> augendTypes = new HashSet<>(Arrays.asList(VarKind.ARGUMENT));
        VariableKinds augend = new VariableKinds(augendTypes);
        augend.add(VarKind.BOOLEAN);

        VariableKinds expected = new VariableKinds(Set.of(VarKind.ARGUMENT, VarKind.BOOLEAN));

        assertThat(augend).isEqualToComparingFieldByField(expected);
    }
}