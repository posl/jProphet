package jp.posl.jprophet.operation;

import java.util.ArrayList;
import java.util.List;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;

import jp.posl.jprophet.patch.OperationDiff;

/**
 * 変数の初期化文を対象ステートメントの前に挿入する
 */
public class InsertInitOperation implements AstOperation{
    public List<OperationDiff> exec(Node node){
        List<CompilationUnit> candidates = new ArrayList<CompilationUnit>();
        //return candidates;
        return new ArrayList<OperationDiff>();
    }
}