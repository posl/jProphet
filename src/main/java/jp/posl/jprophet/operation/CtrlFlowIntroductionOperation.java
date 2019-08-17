package jp.posl.jprophet.operation;

import java.util.ArrayList;
import java.util.List;

import jp.posl.jprophet.RepairUnit;

public class CtrlFlowIntroductionOperation implements AstOperation{
    public CtrlFlowIntroductionOperation(){
       
    }

    public List<RepairUnit> exec(RepairUnit repairUnit){
        List<RepairUnit> candidates = new ArrayList<RepairUnit>();
        return candidates;
    }
}