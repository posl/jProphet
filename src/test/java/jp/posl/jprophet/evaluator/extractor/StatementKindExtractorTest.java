package jp.posl.jprophet.evaluator.extractor;

import com.github.javaparser.ast.Node;

import org.junit.Test;
import static org.assertj.core.api.Assertions.*;

import java.util.List;
import java.util.stream.Collectors;

import jp.posl.jprophet.NodeUtility;
import jp.posl.jprophet.evaluator.extractor.StatementKindExtractor.StatementType;

public class StatementKindExtractorTest {
    /**
     * 各ステートメントタイプを判別できるかテスト
     */
    @Test public void testEachStmtKind() {
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

        final List<Node> nodes = NodeUtility.getAllNodesFromCode(src);
        final StatementKindExtractor extractor = new StatementKindExtractor();
        final List<StatementType> types = nodes.stream()
            .map(node -> extractor.extract(node))
            .filter(type -> type.isPresent())
            .map(type -> type.orElseThrow())
            .collect(Collectors.toList());
        
        List<StatementType> expectedTypes = List.of(
            StatementType.ASSIGN,
            StatementType.METHOD_CALL,
            StatementType.LOOP,
            StatementType.IF,
            StatementType.RETURN,
            StatementType.BREAK,
            StatementType.CONTINUE
        );

        assertThat(types).containsAll(expectedTypes);
    }
}