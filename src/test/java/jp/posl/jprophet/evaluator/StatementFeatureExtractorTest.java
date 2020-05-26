package jp.posl.jprophet.evaluator;

import com.github.javaparser.ast.Node;

import org.junit.Test;
import static org.assertj.core.api.Assertions.assertThat;

import jp.posl.jprophet.NodeUtility;

public class StatementFeatureExtractorTest {
    /**
     * 各ステートメントタイプを判別できるかテスト
     */
    @Test public void testEachStmtVec() {
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

        final StatementFeatureExtractor extractor = new StatementFeatureExtractor();
        final Node node = NodeUtility.getAllNodesFromCode(src).get(0);
        final StatementFeatureVec expectedAssignStmtVec     = new StatementFeatureVec(1, 0, 0, 0, 0, 0, 0);
        final StatementFeatureVec expectedMethodCallStmtVec = new StatementFeatureVec(0, 1, 0, 0, 0, 0, 0);
        final StatementFeatureVec expectedLoopStmtVec       = new StatementFeatureVec(0, 0, 1, 0, 0, 0, 0);
        final StatementFeatureVec expectedIfStmtVec         = new StatementFeatureVec(0, 0, 0, 1, 0, 0, 0);
        final StatementFeatureVec expectedReturnStmtVec     = new StatementFeatureVec(0, 0, 0, 0, 1, 0, 0);
        final StatementFeatureVec expectedBreakStmtVec      = new StatementFeatureVec(0, 0, 0, 0, 0, 1, 0);
        final StatementFeatureVec expectedContinueStmtVec   = new StatementFeatureVec(0, 0, 0, 0, 0, 0, 1);

        assertThat(extractor.extract(3, node)).isEqualToComparingFieldByField(expectedAssignStmtVec);
        assertThat(extractor.extract(4, node)).isEqualToComparingFieldByField(expectedMethodCallStmtVec);
        assertThat(extractor.extract(5, node)).isEqualToComparingFieldByField(expectedLoopStmtVec);
        assertThat(extractor.extract(6, node)).isEqualToComparingFieldByField(expectedLoopStmtVec);
        assertThat(extractor.extract(7, node)).isEqualToComparingFieldByField(expectedLoopStmtVec);
        assertThat(extractor.extract(8, node)).isEqualToComparingFieldByField(expectedIfStmtVec);
        assertThat(extractor.extract(9, node)).isEqualToComparingFieldByField(expectedReturnStmtVec);
        assertThat(extractor.extract(10, node)).isEqualToComparingFieldByField(expectedBreakStmtVec);
        assertThat(extractor.extract(11, node)).isEqualToComparingFieldByField(expectedContinueStmtVec);
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

        final StatementFeatureExtractor extractor = new StatementFeatureExtractor();
        final Node node = NodeUtility.getAllNodesFromCode(src).get(0);
        final StatementFeatureVec expectedVec = new StatementFeatureVec(1, 1, 1, 1, 1, 1, 1);

        assertThat(extractor.extract(3, node)).isEqualToComparingFieldByField(expectedVec);
    }
}