package jp.posl.jprophet.operation;

import java.util.ArrayList;
import java.util.List;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;

/**
 * 対象ステートメント中の変数を別のもので置き換える操作を行う
 */
public class MethodReplacementOperation implements AstOperation {
    public List<CompilationUnit> exec(Node targetNode) {
        List<CompilationUnit> candidates = new ArrayList<CompilationUnit>();

        return candidates;
    }
}