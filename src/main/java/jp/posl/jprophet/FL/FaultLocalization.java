package jp.posl.jprophet.fl;

import java.util.List;

import jp.posl.jprophet.fl.Suspiciousness;

public interface FaultLocalization{
    public List<Suspiciousness> exec();
}