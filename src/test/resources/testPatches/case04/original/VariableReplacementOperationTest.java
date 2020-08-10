package jp.posl.jprophet.operation;

import org.junit.Test;

import jp.posl.jprophet.NodeUtility;
import com.github.javaparser.ast.Node;
import static org.assertj.core.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class VariableReplacementOperationTest{
    

    /**
     * 引数を変数に置換する機能のテスト
     */
    @Test public void testForArgumentReplace(){

        List<String> expectedSources = expectedTargetSources.stream()
            .map(str -> {
                return new StringBuilder().append("")
                    .append(beforeTargetStatement)
                    .append(str)
                    .append(afterTargetStatement)
                    .toString();
            })
            .collect(Collectors.toList());

        List<Node> repairUnits = NodeUtility.getAllNodesFromCode(targetSource);
        List<String> candidateSources = new ArrayList<String>();
        for(Node node : repairUnits){
            VariableReplacementOperation vr = new VariableReplacementOperation();
            candidateSources.addAll(vr.exec(node).stream()
                .map(cu -> cu.toString())
                .collect(Collectors.toList())
            );
        }
        assertThat(candidateSources).containsOnlyElementsOf(expectedSources);
        return;
    }

}