package jp.posl.jprophet.evaluator;

import java.util.ArrayList;
import java.util.List;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.stmt.BreakStmt;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.stmt.Statement;

import jp.posl.jprophet.evaluator.NodeWithDiffType.TYPE;

/**
 * 修正パッチの特徴抽出を行うクラス
 */
public class PatchFeature {
    /**
     * 修正パッチの変更の特徴を抽出する
     * @param nodeWithDiffType 差分情報付きの抽出対象の修正後AST
     * @return 特徴ベクトル
     */
    ModFeatureVec extractModFeature(NodeWithDiffType nodeWithDiffType) {
        Node node = nodeWithDiffType.getNode();
        TYPE type = nodeWithDiffType.getDiffType();

        final ModFeatureVec vec = new ModFeatureVec();
        if(type == TYPE.INSERT) {
            if(node instanceof IfStmt) {
                Boolean insertGuard   = false;
                Boolean insertControl = false;
                for(NodeWithDiffType child: nodeWithDiffType.getChildNodes()) {
                    if(child.getDiffType() == TYPE.SAME) {
                        insertGuard = true;
                    }
                    if(node.findFirst(BreakStmt.class).isPresent() || node.findFirst(ReturnStmt.class).isPresent()) {
                        insertControl = true;
                    }
                }       
                if(insertGuard) {
                    vec.insertGuard += 1;
                }
                if(insertControl) {
                    vec.insertControl += 1;
                }
            }
            else if(node instanceof Statement) {
                vec.insertStmt += 1;
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

    List<ProgramChank> identifyModifiedProgramChank(NodeWithDiffType nodeWithDiffType) {
        if(nodeWithDiffType.getDiffType() != TYPE.SAME) {
            final int begin = nodeWithDiffType.getNode().getRange().get().begin.line;
        }
        else {
        }
        return new ArrayList<>();
    }

    static public class ProgramChank {
        final private int beginLine;
        final private int endLine;

        public ProgramChank(int beginLine, int endLine) {
            this.beginLine = beginLine;
            this.endLine = endLine;
        }
    }

    void identifyStatementKind(Node node) {
            
    }
}