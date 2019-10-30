package jp.posl.jprophet.operation;

import java.util.ArrayList;
import java.util.List;

import jp.posl.jprophet.RepairUnit;

/**
 * if文における条件式の変更操作
 * 条件を狭める操作と緩める操作の二つを行う
 */
public class CondRefinementOperation implements AstOperation{
    public CondRefinementOperation(RepairUnit repairUnit){

    }
    public List<RepairUnit> exec(){
        List<RepairUnit> candidates = new ArrayList<RepairUnit>();
        return candidates;
    }
}