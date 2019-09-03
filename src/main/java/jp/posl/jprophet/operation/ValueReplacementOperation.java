package jp.posl.jprophet.operation;

import java.util.ArrayList;
import java.util.List;

import jp.posl.jprophet.RepairUnit;

/**
 * 対象ステートメントの変数や関数，定数などを別のもので置き換える操作
 */
public class ValueReplacementOperation implements AstOperation{
    public List<RepairUnit> exec(RepairUnit repairUnit){
        List<RepairUnit> candidates = new ArrayList<RepairUnit>();
        return candidates;
    }
}