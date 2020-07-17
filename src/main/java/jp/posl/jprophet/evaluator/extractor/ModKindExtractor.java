package jp.posl.jprophet.evaluator.extractor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.function.BinaryOperator;
import java.util.function.Predicate;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.SimpleName;
import com.github.javaparser.ast.stmt.BreakStmt;
import com.github.javaparser.ast.stmt.ContinueStmt;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.stmt.Statement;

import jp.posl.jprophet.evaluator.NodeWithDiffType;
import jp.posl.jprophet.evaluator.ProgramChunk;
import jp.posl.jprophet.evaluator.extractor.feature.ModKinds.ModKind;
import jp.posl.jprophet.evaluator.NodeWithDiffType.TYPE;
import jp.posl.jprophet.evaluator.extractor.feature.ModKinds;

/**
 * 修正パッチの変更の特徴抽出を行うクラス
 */
public class ModKindExtractor {
    /**
     * 修正パッチの変更の特徴をソースコード中の連続する複数の変更行であるプログラムチャンクごとに抽出する
     * <p>
     * なお，InsertControlは飛び飛びの複数の行から判定されることがあるが，属するプログラムチャンクは
     * ifの存在するチャンクである
     * @param nodeWithDiffType 差分情報付きの抽出対象の修正後AST
     * @param chunks 修正差分チャンクのリスト
     * @return 変更の特徴
     */
    public Map<ProgramChunk, ModKinds> extract(NodeWithDiffType nodeWithDiffType, List<ProgramChunk> chunks) {
        final Node node = nodeWithDiffType.getNode();
        final TYPE type = nodeWithDiffType.getDiffType();

        final ModKinds kinds = new ModKinds();
        if(type == TYPE.INSERT) {
            if(node instanceof IfStmt) {
                if(nodeWithDiffType.findAll(TYPE.SAME).size() > 0) {
                    kinds.add(ModKind.INSERT_GUARD);
                }
                final List<Class<? extends Statement>> controlStmtClasses = List.of(ReturnStmt.class, BreakStmt.class, ContinueStmt.class);
                final Boolean controlInserted = controlStmtClasses.stream()    
                    .anyMatch(clazz -> {
                        return nodeWithDiffType.findAll(clazz).stream()
                            .filter(n -> n.getDiffType() == TYPE.INSERT)
                            .findAny().isPresent();
                    });
                if(controlInserted) {
                    kinds.add(ModKind.INSERT_CONTROL);
                }
            }
            else if(node instanceof Statement) {
                kinds.add(ModKind.INSERT_STMT);
            }
        }
        if(type == TYPE.CHANGE) {
            if(node instanceof IfStmt) {
                kinds.add(ModKind.REPLACE_COND);
            }
            else if (node instanceof NameExpr) {
                kinds.add(ModKind.REPLACE_VAR);
            }
            else if(node instanceof SimpleName) {
                if(node.getParentNode().isPresent()) {
                    if(node.getParentNode().orElseThrow() instanceof MethodCallExpr) {
                        kinds.add(ModKind.REPLACE_METHOD);
                    }
                }
            }
        }

        Map<ProgramChunk, ModKinds> map = new HashMap<ProgramChunk, ModKinds>();
        int line;
        try {
            line = node.getBegin().orElseThrow().line;
        } catch (NoSuchElementException e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
            return map;
        }
        final Predicate<ProgramChunk> lineIsInChunkRange = chunk -> chunk.getBegin() <= line && line <= chunk.getEnd();
        chunks.stream()
            .filter(lineIsInChunkRange)
            .findFirst()
            .ifPresent((c) -> map.put(c, kinds));
        
        final BinaryOperator<Map<ProgramChunk, ModKinds>> mapAccumulator = (accum, newMap) -> {
            newMap.forEach((key, value) -> {
                accum.merge(key, value, (v1, v2) -> {
                    v1.add(v2);
                    return v1;
                });
            });
            return accum;
        };
        return nodeWithDiffType.getChildNodes().stream()
            .map(childNode -> this.extract(childNode, chunks))
            .reduce(map, mapAccumulator);
    }
}