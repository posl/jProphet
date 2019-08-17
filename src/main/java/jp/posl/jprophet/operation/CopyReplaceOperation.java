package jp.posl.jprophet.operation;

import java.util.ArrayList;
import java.util.List;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.VariableDeclarator;

import jp.posl.jprophet.RepairUnit;

public class CopyReplaceOperation implements AstOperation{
    public CopyReplaceOperation(){
       
    }

    public List<RepairUnit> exec(RepairUnit repairUnit){
        List<RepairUnit> candidates = new ArrayList<RepairUnit>();
        Node node = repairUnit.getNode();
        if(node instanceof VariableDeclarator){
            ((VariableDeclarator)node).setName("hoge");
            candidates.add(repairUnit);
        }
        return candidates;
    }
}