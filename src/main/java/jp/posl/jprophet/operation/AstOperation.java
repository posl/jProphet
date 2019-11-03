package jp.posl.jprophet.operation;

import java.util.List;

import jp.posl.jprophet.RepairUnit;

public interface AstOperation{
    public List<RepairUnit> exec(RepairUnit repairUnit);
}