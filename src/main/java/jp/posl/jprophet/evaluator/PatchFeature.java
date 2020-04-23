package jp.posl.jprophet.evaluator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.Statement;

import difflib.Delta;
import jp.posl.jprophet.evaluator.NodeWithDiffType.TYPE;

public class PatchFeature {
    void exec(Node original, Node revised) {
        final AstDiff astDiff = new AstDiff();
        final NodeWithDiffType nodeWithDiffType = astDiff.createRevisedAstWithDiffType(original, revised);
        final Map<String, Integer> featureVec = new HashMap<String, Integer>();
        featureVec.put("InsertControl", 0);
        featureVec.put("InsertGuard",   0);
        featureVec.put("ReplaceCond",   0);
        featureVec.put("ReplaceStmt",   0);
        featureVec.put("InsertStmt",    0);
    }

    void check(NodeWithDiffType nodeWithDiffType, Map<String, Integer> featureVec) {
        if(nodeWithDiffType.getDiffType() == TYPE.INSERT) {
            if(nodeWithDiffType.getNode() instanceof Statement) {
                featureVec.replace("InsertStmt", featureVec.get("InsertStmt") + 1); 
            }
            // if(nodeWithDiffType.getNode() instanceof IfStmt) {
            // }
        }
        if(nodeWithDiffType.getDiffType() == TYPE.CHANGE) {
            if(nodeWithDiffType.getNode() instanceof Statement) {
                featureVec.replace("ReplaceStmt", featureVec.get("ReplaceStmt") + 1); 
            }
        }
    }
}