package jp.posl.jprophet.operation;

import java.util.ArrayList;
import java.util.List;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.VariableDeclarator;

import jp.posl.jprophet.AstNode;

public class CopyReplaceOperation implements AstOperation{
    public CopyReplaceOperation(){
       
    }

    public List<AstNode> exec(AstNode astNode){
        List<AstNode> candidates = new ArrayList<AstNode>();
        Node node = astNode.get();
        if(node instanceof VariableDeclarator){
            ((VariableDeclarator)node).setName("hoge");
            candidates.add(astNode);
        }
        return candidates;
    }
}