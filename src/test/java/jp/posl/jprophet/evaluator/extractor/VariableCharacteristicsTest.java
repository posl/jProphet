package jp.posl.jprophet.evaluator.extractor;

import org.junit.Test;

import jp.posl.jprophet.evaluator.extractor.VariableCharacteristics.VarChar;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;


public class VariableCharacteristicsTest {
    /**
     * 引数なしコンストラクタをテスト
     */
    @Test public void testConstructor() {
        VariableCharacteristics chars = new VariableCharacteristics();

        assertThat(chars.getTypes().isEmpty()).isTrue();
    }

    /**
     * 引数ありコンストラクタをテスト
     */
    @Test public void testConstructorWithParameter() {
        Set<VarChar> expectedTypes = Set.of(VarChar.ARGUMENT, VarChar.BOOLEAN);
        VariableCharacteristics chars = new VariableCharacteristics(expectedTypes);

        assertThat(chars.getTypes()).isEqualTo(expectedTypes);
    }

    /**
     * addメソッドによる各特徴同士の足し算をテスト
     */
    @Test public void testForcharAddition() {
        Set<VarChar> addendTypes = new HashSet<>(Arrays.asList(VarChar.ARGUMENT));
        Set<VarChar> augendTypes = new HashSet<>(Arrays.asList(VarChar.BOOLEAN));
        VariableCharacteristics addend = new VariableCharacteristics(addendTypes);
        VariableCharacteristics augend = new VariableCharacteristics(augendTypes);
        augend.add(addend);

        VariableCharacteristics expected = new VariableCharacteristics(Set.of(VarChar.ARGUMENT, VarChar.BOOLEAN));

        assertThat(augend).isEqualToComparingFieldByField(expected);
    }

    /**
     * addメソッドによる特徴の追加をテスト
     */
    @Test public void testForTypeAddition() {
        Set<VarChar> augendTypes = new HashSet<>(Arrays.asList(VarChar.ARGUMENT));
        VariableCharacteristics augend = new VariableCharacteristics(augendTypes);
        augend.add(VarChar.BOOLEAN);

        VariableCharacteristics expected = new VariableCharacteristics(Set.of(VarChar.ARGUMENT, VarChar.BOOLEAN));

        assertThat(augend).isEqualToComparingFieldByField(expected);
    }
}