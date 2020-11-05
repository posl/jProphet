package jp.posl.jprophet.operation;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.github.javaparser.ast.Node;

import jp.posl.jprophet.NodeUtility;
import jp.posl.jprophet.patch.OperationDiff;

public class OperationTest {
    
    public List<String> applyOperation(String targetSource, AstOperation operation) {
        final List<Node> nodes = NodeUtility.getAllNodesFromCode(targetSource);
        final List<String> actualSources = new ArrayList<String>();
        for(Node node : nodes){
            final List<OperationDiff> results = operation.exec(node);
            actualSources.addAll(results.stream().map(result -> result.getTargetNodeAfterFix().toString()).collect(Collectors.toList()));
        }
        return actualSources;
    }
    
}