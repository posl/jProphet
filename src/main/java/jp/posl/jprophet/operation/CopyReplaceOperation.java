package jp.posl.jprophet.operation;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import java.util.Collections;


import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.BreakStmt;

import jp.posl.jprophet.RepairUnit;

/**
 * 対象ステートメントの以前に現れているステートメントを，
 * 対象ステートメントの直前に挿入し，さらに置換操作(ValueReplacementOperation)を適用する．
 */
public class CopyReplaceOperation implements AstOperation{

    private final RepairUnit repairUnit;
    private final Node targetNode;

    public CopyReplaceOperation(RepairUnit repairUnit){
        this.repairUnit = repairUnit;
        this.targetNode = this.repairUnit.getTargetNode();
    }
    public List<RepairUnit> exec(){
        List<RepairUnit> candidates = new ArrayList<RepairUnit>();
        //修正対象のステートメントの属するメソッドノードを取得
        //メソッド内のステートメント(修正対象のステートメントより前のもの)を収集
        List<Statement> statements = collectLocalStatements();
        //修正対象のステートメントの直前に,収集したステートメントのNodeを追加
        return candidates;
    }

    private List<Statement> collectLocalStatements(){
        MethodDeclaration methodNode;
        try {
            methodNode =  this.targetNode.findParent(MethodDeclaration.class).orElseThrow();
        }
        catch (NoSuchElementException e) {
            return new ArrayList<Statement>();
        }

        List<Statement> localStatements = methodNode.findAll(Statement.class);

        //this.targetStatementを含むそれより後ろ(インデックスが大きい)の要素を全て消す
        //TODO stream()とかでできないか?
        if (localStatements.contains(this.targetNode)){
            final int targetIndex = localStatements.indexOf(this.targetNode);
            final int targetSize = localStatements.size();
            for (int i = targetIndex; i < targetSize; i++){
                localStatements.remove(targetIndex);
            }
        }


        //BlockStmtを全て除外する
        return localStatements.stream()
            .filter(s -> (s instanceof BlockStmt) == false)
            .collect(Collectors.toList());

        /*
        List<Statement> removeSet = new ArrayList<Statement>();
        removeSet.addAll(methodNode.findAll(BlockStmt.class));
        Collections.addAll(removeSet);
        localStatements.removeAll(removeSet);
        return localStatements;
        */
    }
}