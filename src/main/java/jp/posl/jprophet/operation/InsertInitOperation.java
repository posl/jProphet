package jp.posl.jprophet.operation;

import java.util.ArrayList;
import java.util.List;

import jp.posl.jprophet.RepairUnit;

/**
 * 変数の初期化文を対象ステートメントの前に挿入する
 */
public class InsertInitOperation implements AstOperation{
    public InsertInitOperation(RepairUnit repairUnit){

    }

    public List<RepairUnit> exec(){
        List<RepairUnit> candidates = new ArrayList<RepairUnit>();
        return candidates;
    }
}