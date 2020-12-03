package jp.posl.jprophet.evaluator.extractor;

import org.junit.Test;

import jp.posl.jprophet.evaluator.extractor.FeatureExtractor.StatementPos;
import jp.posl.jprophet.evaluator.extractor.StatementKindExtractor.StatementKind;
import jp.posl.jprophet.evaluator.extractor.ModKinds.ModKind;
import jp.posl.jprophet.evaluator.extractor.VariableCharacteristics.VarChar;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;

public class FeatureVectorTest {

    /**
     * 特徴の入力パラーメータごとに，binaryVectorがtrueになるインデックスが異なることをテスト
     */
    @Test public void testUniqueIndex() {
        final List<FeatureVector>  vectors = new ArrayList<>();
        for(int i = 0; i < ModKind.values().length; i++) {
            final FeatureVector vector = new FeatureVector();
            final ModKind modType = ModKind.values()[i];
            vector.add(modType);
            vectors.add(vector);
        }
         
        for (int i = 0; i < StatementPos.values().length; i++) {
            for (int j = 0; j < StatementKind.values().length; j++) {
                for(int k = 0; k < ModKind.values().length; k++) {
                    final FeatureVector vector = new FeatureVector();
                    final StatementPos pos = StatementPos.values()[i];
                    final StatementKind stmtType = StatementKind.values()[j];
                    final ModKind modType = ModKind.values()[k];
                    vector.add(pos, stmtType, modType);
                    vectors.add(vector);
                }
            }
        }
        for (int i = 0; i < StatementPos.values().length; i++) {
            for (int j = 0; j < VarChar.values().length; j++) {
                for(int k = 0; k < VarChar.values().length; k++) {
                    final FeatureVector vector = new FeatureVector();
                    final StatementPos pos = StatementPos.values()[i];
                    final VarChar originalVarType = VarChar.values()[j];
                    final VarChar fixedVarType = VarChar.values()[k];
                    vector.add(pos, originalVarType, fixedVarType);
                    vectors.add(vector);
                }
            }
        }
        for (int i = 0; i < vectors.size(); i++) {
            for (int j = 0; j < vectors.size(); j++) {
                if (i == j) {
                    continue;
                }
                final List<Boolean> originalBinaryVector = vectors.get(i).asBooleanList();
                final int originalIndex = originalBinaryVector.indexOf(true);
                final List<Boolean> fixedBinaryVector = vectors.get(j).asBooleanList();
                final int fixedIndex = fixedBinaryVector.indexOf(true);
                assertThat(originalIndex).isNotEqualTo(fixedIndex);
            }
        }
    }

    /**
     * vector同士の論理和のテスト
     */
    @Test public void testVectorAddition() {
        final FeatureVector vector1 = new FeatureVector();
        vector1.add(ModKind.INSERT_CONTROL);
        final FeatureVector vector2 = new FeatureVector();
        vector2.add(ModKind.REPLACE_COND);
        vector1.add(vector2);

        final FeatureVector expectedVector = new FeatureVector();
        expectedVector.add(ModKind.INSERT_CONTROL);
        expectedVector.add(ModKind.REPLACE_COND);

        assertThat(vector1).isEqualToComparingFieldByField(expectedVector);
    }
}