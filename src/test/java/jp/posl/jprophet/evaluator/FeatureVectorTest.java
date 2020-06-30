package jp.posl.jprophet.evaluator;

import org.junit.Test;

import jp.posl.jprophet.evaluator.FeatureExtractor.StatementPos;
import jp.posl.jprophet.evaluator.ModFeature.ModType;
import jp.posl.jprophet.evaluator.StatementFeature.StatementType;
import jp.posl.jprophet.evaluator.VariableFeature.VarType;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;

public class FeatureVectorTest {

    /**
     * 特徴の入力パラーメータごとに，binaryVectorがtrueになるインデックスが異なることをテスト
     */
    @Test public void testUniqueIndex() {
        final List<FeatureVector>  vectors = new ArrayList<>();
        for(int i = 0; i < ModType.values().length; i++) {
            final FeatureVector vector = new FeatureVector();
            final ModType modType = ModType.values()[i];
            vector.add(modType);
            vectors.add(vector);
        }
         
        for (int i = 0; i < StatementPos.values().length; i++) {
            for (int j = 0; j < StatementType.values().length; j++) {
                for(int k = 0; k < ModType.values().length; k++) {
                    final FeatureVector vector = new FeatureVector();
                    final StatementPos pos = StatementPos.values()[i];
                    final StatementType stmtType = StatementType.values()[j];
                    final ModType modType = ModType.values()[k];
                    vector.add(pos, stmtType, modType);
                    vectors.add(vector);
                }
            }
        }
        for (int i = 0; i < StatementPos.values().length; i++) {
            for (int j = 0; j < VarType.values().length; j++) {
                for(int k = 0; k < VarType.values().length; k++) {
                    final FeatureVector vector = new FeatureVector();
                    final StatementPos pos = StatementPos.values()[i];
                    final VarType originalVarType = VarType.values()[j];
                    final VarType fixedVarType = VarType.values()[k];
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
                final List<Boolean> originalBinaryVector = vectors.get(i).get();
                final int originalIndex = originalBinaryVector.indexOf(true);
                final List<Boolean> fixedBinaryVector = vectors.get(j).get();
                final int fixedIndex = fixedBinaryVector.indexOf(true);
                assertThat(originalIndex).isNotEqualTo(fixedIndex);
            }
        }
    }
}