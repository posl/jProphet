package jp.posl.jprophet.operation;

import java.util.ArrayList;
import java.util.List;

import jp.posl.jprophet.RepairUnit;

/**
 * 抽象条件式がtrueの時に実行されるような,
 * コントロールフローを制御するステートメント(return, breakなど)を
 * 対象の前に挿入する
 */
public class CtrlFlowIntroductionOperation implements AstOperation{
    public CtrlFlowIntroductionOperation(RepairUnit repairUnit){

    }
    public List<RepairUnit> exec(){
        List<RepairUnit> candidates = new ArrayList<RepairUnit>();
        return candidates;
    }
}