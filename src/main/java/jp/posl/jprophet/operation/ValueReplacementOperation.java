package jp.posl.jprophet.operation;

import java.util.ArrayList;
import java.util.List;

import jp.posl.jprophet.RepairUnit;

public class ValueReplacementOperation implements AstOperation{
    public ValueReplacementOperation(){
       
    }

    public List<RepairUnit> exec(RepairUnit repairUnit){
        List<RepairUnit> candidates = new ArrayList<RepairUnit>();
        return candidates;
    }
}