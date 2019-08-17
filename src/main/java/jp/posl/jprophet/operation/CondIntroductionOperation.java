package jp.posl.jprophet.operation;

import java.util.ArrayList;
import java.util.List;

import jp.posl.jprophet.RepairUnit;

public class CondIntroductionOperation implements AstOperation{
    public CondIntroductionOperation(){
       
    }

    public List<RepairUnit> exec(RepairUnit repairUnit){
        List<RepairUnit> candidates = new ArrayList<RepairUnit>();
        return candidates;
    }
}