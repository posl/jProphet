package jp.posl.jprophet.operation;

import java.util.ArrayList;
import java.util.List;

import jp.posl.jprophet.RepairUnit;

/**
 * 対象のステートメントをif文で挟む
 */
public class CondIntroductionOperation implements AstOperation{
    public CondIntroductionOperation(RepairUnit repairUnit){

    }
    public List<RepairUnit> exec(){
        List<RepairUnit> candidates = new ArrayList<RepairUnit>();
        return candidates;
    }
}