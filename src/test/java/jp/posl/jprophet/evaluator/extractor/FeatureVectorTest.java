package jp.posl.jprophet.evaluator.extractor;

import org.junit.Test;

import jp.posl.jprophet.evaluator.extractor.FeatureExtractor.StatementPos;
import jp.posl.jprophet.evaluator.extractor.StatementKindExtractor.StatementType;
import jp.posl.jprophet.evaluator.extractor.feature.ModKinds.ModKind;
import jp.posl.jprophet.evaluator.extractor.feature.VariableKinds.VarKind;

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
            for (int j = 0; j < StatementType.values().length; j++) {
                for(int k = 0; k < ModKind.values().length; k++) {
                    final FeatureVector vector = new FeatureVector();
                    final StatementPos pos = StatementPos.values()[i];
                    final StatementType stmtType = StatementType.values()[j];
                    final ModKind modType = ModKind.values()[k];
                    vector.add(pos, stmtType, modType);
                    vectors.add(vector);
                }
            }
        }
        for (int i = 0; i < StatementPos.values().length; i++) {
            for (int j = 0; j < VarKind.values().length; j++) {
                for(int k = 0; k < VarKind.values().length; k++) {
                    final FeatureVector vector = new FeatureVector();
                    final StatementPos pos = StatementPos.values()[i];
                    final VarKind originalVarType = VarKind.values()[j];
                    final VarKind fixedVarType = VarKind.values()[k];
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