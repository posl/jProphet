package jp.posl.jprophet.operation;

import java.util.ArrayList;
import java.util.List;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;


/**
 * if文における条件式の変更操作
 * 条件を狭める操作と緩める操作の二つを行う
 */
public class CondRefinementOperation implements AstOperation{
    public List<CompilationUnit> exec(Node node){
        List<CompilationUnit> candidates = new ArrayList<CompilationUnit>();
        return candidates;
    }
}