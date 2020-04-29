package jp.posl.jprophet.evaluator;

import java.util.HashMap;
import java.util.Map;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.stmt.BreakStmt;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.stmt.Statement;

import jp.posl.jprophet.evaluator.NodeWithDiffType.TYPE;

public class PatchFeature {
    Map<String, Integer> exec(Node original, Node revised) {
        final AstDiff astDiff = new AstDiff();
        final NodeWithDiffType nodeWithDiffType = astDiff.createRevisedAstWithDiffType(original, revised);
        return this.check(nodeWithDiffType);
    }

    Map<String, Integer> check(NodeWithDiffType nodeWithDiffType) {
        final Map<String, Integer> featureVec = new HashMap<String, Integer>();
        featureVec.put("InsertControl", 0);
        featureVec.put("InsertGuard",   0);
        featureVec.put("ReplaceCond",   0);
        featureVec.put("ReplaceStmt",   0);
        featureVec.put("InsertStmt",    0);

        Node node = nodeWithDiffType.getNode();
        TYPE type = nodeWithDiffType.getDiffType();
        if(type == TYPE.INSERT) {
            if(node instanceof Statement) {
                featureVec.replace("InsertStmt", featureVec.get("InsertStmt") + 1); 
            }
            if(node instanceof IfStmt) {
                nodeWithDiffType.getChildNodes().forEach(child -> {
                    if(child.getDiffType() != TYPE.SAME) {
                        featureVec.replace("InsertGuard", featureVec.get("InsertGuard") + 1);
                    }
                    if(child.getNode() instanceof BreakStmt || child.getNode() instanceof ReturnStmt) {
                        featureVec.replace("InsertControl", featureVec.get("InsertControl") + 1);
                    }
                });       
            }
        }
        if(type == TYPE.CHANGE) {
            if(node instanceof IfStmt) {
                featureVec.replace("ReplaceCond", featureVec.get("ReplaceCond") + 1); 
            }
            else if(node instanceof Statement) {
                featureVec.replace("ReplaceStmt", featureVec.get("ReplaceStmt") + 1); 
            }
        }
        if(nodeWithDiffType.getChildNodes().size() == 0) {
            return featureVec;
        }

        return nodeWithDiffType.getChildNodes().stream()
            .map(childNode -> this.check(childNode))
            .reduce(featureVec, (accum, feature) -> {
                accum.entrySet().stream()
                    .forEach(e -> accum.replace(e.getKey(), accum.get(e.getKey()) + feature.get(e.getKey())));
                return accum;
            });
    }
}