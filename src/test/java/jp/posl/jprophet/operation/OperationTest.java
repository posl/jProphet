package jp.posl.jprophet.operation;

import static org.assertj.core.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.github.javaparser.ast.Node;

import jp.posl.jprophet.NodeUtility;
import jp.posl.jprophet.patch.DiffWithType;

public class OperationTest {
    
    public void test(String targetSource, List<String> expectedSources, AstOperation operation) {
        final List<Node> nodes = NodeUtility.getAllNodesFromCode(targetSource);
        final List<String> actualSources = new ArrayList<String>();
        for(Node node : nodes){
            final List<DiffWithType> results = operation.exec(node);
            actualSources.addAll(results.stream().map(result -> result.getTargetNodeAfterFix().toString()).collect(Collectors.toList()));
        }
        assertThat(actualSources).containsOnlyElementsOf(expectedSources);
        return;
    }
    
}