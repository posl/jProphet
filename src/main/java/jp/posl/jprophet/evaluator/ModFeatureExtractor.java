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
 * 修正パッチの特徴抽出を行うクラス
 */
public class ModFeatureExtractor {
    /**
     * 修正パッチの変更の特徴をソースコード中の連続する複数の変更行であるプログラムチャンクごとに抽出する
     * <p>
     * なお，InsertControlは飛び飛びの複数の行から判定されることがあるが，属するプログラムチャンクは
     * ifの存在するチャンクである
     * @param nodeWithDiffType 差分情報付きの抽出対象の修正後AST
     * @param chanks 修正差分チャンクのリスト
     * @return 特徴ベクトル
     */
    public Map<ProgramChank, ModFeatureVec> extract(NodeWithDiffType nodeWithDiffType, List<ProgramChank> chanks) {
        final Node node = nodeWithDiffType.getNode();
        final TYPE type = nodeWithDiffType.getDiffType();

        final ModFeatureVec vec = new ModFeatureVec();
        if(type == TYPE.INSERT) {
            if(node instanceof IfStmt) {
                if(nodeWithDiffType.findAll(TYPE.SAME).size() > 0) {
                    vec.insertGuard += 1;
                }
                final List<Class<? extends Statement>> controlStmtClasses = List.of(ReturnStmt.class, BreakStmt.class, ContinueStmt.class);
                final Boolean controlInserted = controlStmtClasses.stream()    
                    .anyMatch(clazz -> {
                        return nodeWithDiffType.findAll(clazz).stream()
                            .filter(n -> n.getDiffType() == TYPE.INSERT)
                            .findAny().isPresent();
                    });
                if(controlInserted) {
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
            .ifPresent((c) -> map.put(c, vec));
        
        final BinaryOperator<Map<ProgramChank, ModFeatureVec>> mapAccumulator = (accum, newMap) -> {
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