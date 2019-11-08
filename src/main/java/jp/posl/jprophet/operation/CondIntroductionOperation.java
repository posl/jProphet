package jp.posl.jprophet.operation;

import java.util.ArrayList;
import java.util.List;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;


/**
 * 対象のステートメントをif文で挟む
 */
public class CondIntroductionOperation implements AstOperation{
    public List<CompilationUnit> exec(Node node){
        List<CompilationUnit> candidates = new ArrayList<CompilationUnit>();
        return candidates;
    }
}