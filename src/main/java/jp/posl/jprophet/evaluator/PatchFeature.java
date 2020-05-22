package jp.posl.jprophet.evaluator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
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
    public ModFeatureVec extractModFeature(NodeWithDiffType nodeWithDiffType) {
        Node node = nodeWithDiffType.getNode();
        TYPE type = nodeWithDiffType.getDiffType();

        final ModFeatureVec vec = new ModFeatureVec();
        if(type == TYPE.INSERT) {
            if(node instanceof IfStmt) {
                Boolean insertGuard   = false;
                Boolean insertControl = false;
                if(nodeWithDiffType.findAll(TYPE.SAME).size() > 0) {
                    insertGuard = true;
                }

                final boolean insertedBreak = nodeWithDiffType.findAll(BreakStmt.class).stream()
                    .filter(n -> n.getDiffType() == TYPE.INSERT)
                    .findAny().isPresent();
                final boolean insertedReturn = nodeWithDiffType.findAll(ReturnStmt.class).stream()
                    .filter(n -> n.getDiffType() == TYPE.INSERT)
                    .findAny().isPresent();
                if(insertedBreak || insertedReturn) {
                    insertControl = true;
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
            else if(node instanceof MethodCallExpr) {
                vec.replaceMethod += 1;
            }
            else if (node instanceof NameExpr) {
                vec.replaceVar += 1;
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


    public Map<ProgramChank, ModFeatureVec> extractModFeature2(NodeWithDiffType nodeWithDiffType, List<ProgramChank> chanks) {
        Node node = nodeWithDiffType.getNode();
        TYPE type = nodeWithDiffType.getDiffType();

        final ModFeatureVec vec = new ModFeatureVec();
        if(type == TYPE.INSERT) {
            if(node instanceof IfStmt) {
                Boolean insertGuard   = false;
                Boolean insertControl = false;
                if(nodeWithDiffType.findAll(TYPE.SAME).size() > 0) {
                    insertGuard = true;
                }

                final boolean insertedBreak = nodeWithDiffType.findAll(BreakStmt.class).stream()
                    .filter(n -> n.getDiffType() == TYPE.INSERT)
                    .findAny().isPresent();
                final boolean insertedReturn = nodeWithDiffType.findAll(ReturnStmt.class).stream()
                    .filter(n -> n.getDiffType() == TYPE.INSERT)
                    .findAny().isPresent();
                if(insertedBreak || insertedReturn) {
                    insertControl = true;
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
            else if(node instanceof MethodCallExpr) {
                vec.replaceMethod += 1;
            }
            else if (node instanceof NameExpr) {
                vec.replaceVar += 1;
            }
        }

        Map<ProgramChank, ModFeatureVec> map = new HashMap<ProgramChank, ModFeatureVec>();
        final int line = node.getRange().get().begin.line;
        chanks.stream()
            .filter(c -> c.getBegin() <= line && line <= c.getEnd())
            .findFirst()
            .ifPresent((c) -> map.put(c, vec));
        if(nodeWithDiffType.getChildNodes().size() == 0) {
            return map;
        }
        return nodeWithDiffType.getChildNodes().stream()
            .map(childNode -> this.extractModFeature2(childNode, chanks))
            .reduce(map, (accum, newMap) -> {
                newMap.forEach((key, value) -> {
                    accum.merge(key, value, (v1, v2) -> {
                        v1.add(v2);
                        return v1;
                    });
                });
                return accum;
            });
    }

    public List<ProgramChank> identifyModifiedProgramChank(NodeWithDiffType nodeWithDiffType) {
        List<NodeWithDiffType> nodesWithDiffType = this.convertTreeToList(nodeWithDiffType);
        List<ProgramChank> chanks = new ArrayList<ProgramChank>();
        int beginLine = 0;
        int previousLine = 0;
        boolean counting = false;
        for(NodeWithDiffType node: nodesWithDiffType) {
            final int line = node.getNode().getRange().get().begin.line;
            if(!counting && node.getDiffType() != TYPE.SAME) {
                beginLine = line;
                counting = true;
            }
            if(counting && node.getDiffType() == TYPE.SAME && line != previousLine) {;
                chanks.add(new ProgramChank(beginLine, previousLine));
                counting = false;
            }
            previousLine = line;
        }
        if(counting) {
            chanks.add(new ProgramChank(beginLine, previousLine));
        }
        return chanks;
    }

    private List<NodeWithDiffType> convertTreeToList(NodeWithDiffType root) {
        List<NodeWithDiffType> descendantNodes = new ArrayList<NodeWithDiffType>();
        descendantNodes.add(root);
        root.getChildNodes().stream()
            .map(childNode -> convertTreeToList(childNode))
            .forEach(descendantNodes::addAll);
        return descendantNodes;
    }


}