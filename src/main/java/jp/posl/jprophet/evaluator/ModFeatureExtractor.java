package jp.posl.jprophet.evaluator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.function.BinaryOperator;
import java.util.function.Predicate;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.stmt.BreakStmt;
import com.github.javaparser.ast.stmt.ContinueStmt;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.stmt.Statement;

import jp.posl.jprophet.evaluator.NodeWithDiffType.TYPE;

/**
 * 修正パッチの変更の特徴抽出を行うクラス
 */
public class ModFeatureExtractor {
    /**
     * 修正パッチの変更の特徴をソースコード中の連続する複数の変更行であるプログラムチャンクごとに抽出する
     * <p>
     * なお，InsertControlは飛び飛びの複数の行から判定されることがあるが，属するプログラムチャンクは
     * ifの存在するチャンクである
     * @param nodeWithDiffType 差分情報付きの抽出対象の修正後AST
     * @param chanks 修正差分チャンクのリスト
     * @return 変更の特徴
     */
    public Map<ProgramChank, ModFeature> extract(NodeWithDiffType nodeWithDiffType, List<ProgramChank> chanks) {
        final Node node = nodeWithDiffType.getNode();
        final TYPE type = nodeWithDiffType.getDiffType();

        final ModFeature feature = new ModFeature();
        if(type == TYPE.INSERT) {
            if(node instanceof IfStmt) {
                if(nodeWithDiffType.findAll(TYPE.SAME).size() > 0) {
                    feature.insertGuard += 1;
                }
                final List<Class<? extends Statement>> controlStmtClasses = List.of(ReturnStmt.class, BreakStmt.class, ContinueStmt.class);
                final Boolean controlInserted = controlStmtClasses.stream()    
                    .anyMatch(clazz -> {
                        return nodeWithDiffType.findAll(clazz).stream()
                            .filter(n -> n.getDiffType() == TYPE.INSERT)
                            .findAny().isPresent();
                    });
                if(controlInserted) {
                    feature.insertControl += 1;
                }
            }
            else if(node instanceof Statement) {
                feature.insertStmt += 1;
            }
        }
        if(type == TYPE.CHANGE) {
            if(node instanceof IfStmt) {
                feature.replaceCond += 1;
            }
            else if(node instanceof MethodCallExpr) {
                feature.replaceMethod += 1;
            }
            else if (node instanceof NameExpr) {
                feature.replaceVar += 1;
            }
        }

        Map<ProgramChank, ModFeature> map = new HashMap<ProgramChank, ModFeature>();
        int line;
        try {
            line = node.getBegin().orElseThrow().line;
        } catch (NoSuchElementException e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
            return map;
        }
        final Predicate<ProgramChank> lineIsInChankRange = chank -> chank.getBegin() <= line && line <= chank.getEnd();
        chanks.stream()
            .filter(lineIsInChankRange)
            .findFirst()
            .ifPresent((c) -> map.put(c, feature));
        
        final BinaryOperator<Map<ProgramChank, ModFeature>> mapAccumulator = (accum, newMap) -> {
            newMap.forEach((key, value) -> {
                accum.merge(key, value, (v1, v2) -> {
                    v1.add(v2);
                    return v1;
                });
            });
            return accum;
        };
        return nodeWithDiffType.getChildNodes().stream()
            .map(childNode -> this.extract(childNode, chanks))
            .reduce(map, mapAccumulator);
    }
}