package jp.posl.jprophet.evaluator;

import org.junit.Test;

import javassist.bytecode.analysis.Type;
import jp.posl.jprophet.evaluator.FeatureExtractor.HOGE;
import jp.posl.jprophet.evaluator.FeatureExtractor.StatementPos;
import jp.posl.jprophet.evaluator.ModFeature.ModType;
import jp.posl.jprophet.evaluator.NodeWithDiffType.TYPE;
import jp.posl.jprophet.evaluator.StatementFeature.StatementType;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;

public class FeatureVectorTest {
    @Test public void hoge() {
        FeatureVector vector = new FeatureVector();
        final StatementFeature stmtFeature = new StatementFeature(Set.of(
            StatementType.ASSIGN,
            StatementType.BREAK
        ));
        final ModFeature modFeature = new ModFeature(Set.of(
            ModType.INSERT_CONTROL,
            ModType.INSERT_GUARD
        ));
        vector.add(StatementPos.TARGET, stmtFeature, modFeature);
    }
}