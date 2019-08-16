package jp.posl.jprophet.template;

import java.util.ArrayList;
import java.util.List;

import jp.posl.jprophet.AstNode;

public class InsertInitOperation implements AstOperation{
    public InsertInitOperation(){
       
    }

    public List<AstNode> exec(AstNode astNode){
        List<AstNode> candidates = new ArrayList<AstNode>();
        return candidates;
    }
}