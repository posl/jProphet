package jp.posl.jprophet.evaluator;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.stmt.BreakStmt;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.stmt.Statement;

import jp.posl.jprophet.evaluator.NodeWithDiffType.TYPE;

public class PatchFeature {
    ModFeatureVec extractModFeature(NodeWithDiffType nodeWithDiffType) {
        Node node = nodeWithDiffType.getNode();
        TYPE type = nodeWithDiffType.getDiffType();

        final ModFeatureVec vec = new ModFeatureVec();
        if(type == TYPE.INSERT) {
            if(node instanceof Statement) {
                vec.insertStmt += 1;
            }
            if(node instanceof IfStmt) {
                nodeWithDiffType.getChildNodes().forEach(child -> {
                    if(child.getDiffType() != TYPE.SAME) {
                        vec.insertGuard += 1;
                    }
                    if(child.getNode() instanceof BreakStmt || child.getNode() instanceof ReturnStmt) {
                        vec.insertControl += 1;
                    }
                });       
            }
        }
        if(type == TYPE.CHANGE) {
            if(node instanceof IfStmt) {
                vec.replaceCond += 1;
            }
            else if(node instanceof Statement) {
                vec.replaceStmt += 1;
            }
        }

        if(nodeWithDiffType.getChildNodes().size() == 0) {
            return vec;
        }
        return nodeWithDiffType.getChildNodes().stream()
            .map(childNode -> this.extractModFeature(childNode))
            .reduce(vec, (accum, newVec) -> {
                accum.add(newVec);
                return accum;
            });
    }
}