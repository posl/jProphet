package jp.posl.jprophet.evaluator;

import java.util.ArrayList;
import java.util.List;

import com.github.javaparser.ast.Node;

public class PatchFeature {
    void exec(Node original, Node revised) {
        final AstDiff astDiff = new AstDiff();
        final List<AstDelta> astDeltas = astDiff.diff(original, revised);
        final List<Integer> featureVec = new ArrayList<Integer>(List.of(0, 0, 0, 0, 0, 0));
        astDeltas.stream().forEach((astDelta) -> {
            final int deleteNodesSize = astDelta.getDeleteNodes().size();
            final int addNodesSize = astDelta.getAddNodes().size();
            final boolean inserted = addNodesSize != 0 && deleteNodesSize == 0;
            final boolean changed  = addNodesSize != 0 && deleteNodesSize != 0;
        });
    }
}