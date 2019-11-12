package jp.posl.jprophet.operation;

import java.util.ArrayList;
import java.util.List;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;

/**
 * 対象ステートメントの以前に現れているステートメントを，
 * 対象ステートメントの直前に挿入し，さらに置換操作(ValueReplacementOperation)を適用する．
 */
public class CopyReplaceOperation implements AstOperation{
    public List<CompilationUnit> exec(Node node){
        List<CompilationUnit> candidates = new ArrayList<CompilationUnit>();
        return candidates;
    }
}