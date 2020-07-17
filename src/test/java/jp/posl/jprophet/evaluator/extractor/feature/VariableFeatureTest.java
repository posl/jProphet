package jp.posl.jprophet.evaluator.extractor.feature;

import org.junit.Test;

import jp.posl.jprophet.evaluator.extractor.feature.VariableFeature.VarType;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;


public class VariableFeatureTest {
    /**
     * 引数なしコンストラクタをテスト
     */
    @Test public void testConstructor() {
        VariableFeature feature = new VariableFeature();

        assertThat(feature.getTypes().isEmpty()).isTrue();
    }

    /**
     * 引数ありコンストラクタをテスト
     */
    @Test public void testConstructorWithParameter() {
        Set<VarType> expectedTypes = Set.of(VarType.ARGUMENT, VarType.BOOLEAN);
        VariableFeature feature = new VariableFeature(expectedTypes);

        assertThat(feature.getTypes()).isEqualTo(expectedTypes);
    }

    /**
     * addメソッドによる各特徴同士の足し算をテスト
     */
    @Test public void testForFeatureAddition() {
        Set<VarType> addendTypes = new HashSet<>(Arrays.asList(VarType.ARGUMENT));
        Set<VarType> augendTypes = new HashSet<>(Arrays.asList(VarType.BOOLEAN));
        VariableFeature addend = new VariableFeature(addendTypes);
        VariableFeature augend = new VariableFeature(augendTypes);
        augend.add(addend);

        VariableFeature expected = new VariableFeature(Set.of(VarType.ARGUMENT, VarType.BOOLEAN));

        assertThat(augend).isEqualToComparingFieldByField(expected);
    }

    /**
     * addメソッドによる特徴の追加をテスト
     */
    @Test public void testForTypeAddition() {
        Set<VarType> augendTypes = new HashSet<>(Arrays.asList(VarType.ARGUMENT));
        VariableFeature augend = new VariableFeature(augendTypes);
        augend.add(VarType.BOOLEAN);

        VariableFeature expected = new VariableFeature(Set.of(VarType.ARGUMENT, VarType.BOOLEAN));

        assertThat(augend).isEqualToComparingFieldByField(expected);
    }
}