package jp.posl.jprophet.operation;

import java.util.List;

import com.github.javaparser.ast.Node;

import jp.posl.jprophet.patch.OperationDiff;


public interface AstOperation{
    /**
     * 対象ステートメントに対し修正操作を適用し，
     * 適用前後のソースコードの差分情報を返す
     * @param targetNode 対象ステートメントを表すノード
     * @return パッチ適用によるNodeの差分情報
     */
    public List<OperationDiff> exec(Node targetNode);
}