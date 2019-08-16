package jp.posl.jprophet.operation;

import java.util.ArrayList;
import java.util.List;

import jp.posl.jprophet.AstNode;

public class ValueReplacementOperation implements AstOperation{
    public ValueReplacementOperation(){
       
    }

    public List<AstNode> exec(AstNode astNode){
        List<AstNode> candidates = new ArrayList<AstNode>();
        return candidates;
    }
}