package jp.posl.jprophet.evaluator;

import java.util.ArrayList;
import java.util.List;

import com.github.javaparser.ast.Node;

public class PatchFeature {
    void exec(Node original, Node revised) {
        final AstDiff astDiff = new AstDiff();
        final List<AstDelta> astDeltas = astDiff.diff(original, revised);
        final List<Integer> featureVec = new ArrayList<Integer>(List.of(0, 0, 0, 0, 0, 0));
        featureVec.set(0, 1);
        astDeltas.stream().forEach((astDelta) -> j{
            // まだなにもしてない
        });
    }
}