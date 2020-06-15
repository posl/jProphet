package jp.posl.jprophet.evaluator;

import com.github.javaparser.ast.Node;

import org.junit.Test;
import static org.assertj.core.api.Assertions.*;

import java.util.Set;

import jp.posl.jprophet.NodeUtility;
import jp.posl.jprophet.evaluator.StatementFeature.StatementType;

public class StatementFeatureExtractorTest {
    /**
     * 各ステートメントタイプを判別できるかテスト
     */
    @Test public void testEachStmtFeature() {
        final String src = new StringBuilder().append("")
            .append("public class A {\n")
            .append("   public void a() {\n")
            .append("       hoge = 0;\n")
            .append("       fuga();\n")
            .append("       for(;;){}\n")
            .append("       for(int hoge: hoges){}\n")
            .append("       while(true){}\n")
            .append("       if(fuga)\n")
            .append("           return;\n")
            .append("       break;\n")
            .append("       continue;\n")
            .append("   }\n")
            .append("}\n")
            .toString();

        final Node node = NodeUtility.getAllNodesFromCode(src).get(0);
        final StatementFeatureExtractor extractor = new StatementFeatureExtractor(node);
        final StatementFeature expectedAssignStmtFeature     = new StatementFeature(Set.of(StatementType.ASSIGN));
        final StatementFeature expectedMethodCallStmtFeature = new StatementFeature(Set.of(StatementType.METHOD_CALL));
        final StatementFeature expectedLoopStmtFeature       = new StatementFeature(Set.of(StatementType.LOOP));
        final StatementFeature expectedIfStmtFeature         = new StatementFeature(Set.of(StatementType.IF));
        final StatementFeature expectedReturnStmtFeature     = new StatementFeature(Set.of(StatementType.RETURN));
        final StatementFeature expectedBreakStmtFeature      = new StatementFeature(Set.of(StatementType.BREAK));
        final StatementFeature expectedContinueStmtFeature   = new StatementFeature(Set.of(StatementType.CONTINUE));

        assertThat(extractor.extract(3)).isEqualToComparingFieldByField(expectedAssignStmtFeature);
        assertThat(extractor.extract(4)).isEqualToComparingFieldByField(expectedMethodCallStmtFeature);
        assertThat(extractor.extract(5)).isEqualToComparingFieldByField(expectedLoopStmtFeature);
        assertThat(extractor.extract(6)).isEqualToComparingFieldByField(expectedLoopStmtFeature);
        assertThat(extractor.extract(7)).isEqualToComparingFieldByField(expectedLoopStmtFeature);
        assertThat(extractor.extract(8)).isEqualToComparingFieldByField(expectedIfStmtFeature);
        assertThat(extractor.extract(9)).isEqualToComparingFieldByField(expectedReturnStmtFeature);
        assertThat(extractor.extract(10)).isEqualToComparingFieldByField(expectedBreakStmtFeature);
        assertThat(extractor.extract(11)).isEqualToComparingFieldByField(expectedContinueStmtFeature);
    }

    /**
     * 一行あたり複数のステートメントタイプが混在する場合をテスト
     */
    @Test public void testMultipleStmtInOneLine() {
        final String src = new StringBuilder().append("")
            .append("public class A {\n")
            .append("   public void a() {\n")
            .append("       while(true){ if(hoge) fuga = foo(); return; break; continue;}\n")
            .append("   }\n")
            .append("}\n")
            .toString();

        final Node node = NodeUtility.getAllNodesFromCode(src).get(0);
        final StatementFeatureExtractor extractor = new StatementFeatureExtractor(node);
        final Set<StatementType> expectedTypes = Set.of(
            StatementType.ASSIGN, 
            StatementType.METHOD_CALL, 
            StatementType.LOOP, 
            StatementType.IF, 
            StatementType.RETURN, 
            StatementType.BREAK, 
            StatementType.CONTINUE
        );
        final StatementFeature expectedFeature = new StatementFeature(expectedTypes);

        assertThat(extractor.extract(3)).isEqualToComparingFieldByField(expectedFeature);
    }

    @Test public void testIllegalLine() {
        final String src = new StringBuilder().append("")
            .append("public class A {\n")
            .append("   public void a() {\n")
            .append("   }\n")
            .append("}\n")
            .toString();

        final Node node = NodeUtility.getAllNodesFromCode(src).get(0);
        final StatementFeatureExtractor extractor = new StatementFeatureExtractor(node);

        assertThatThrownBy(() -> extractor.extract(10)).isInstanceOf(IllegalArgumentException.class);
    }
}