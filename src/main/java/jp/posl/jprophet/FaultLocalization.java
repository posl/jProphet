package jp.posl.jprophet;

import java.util.List;

import jp.posl.jprophet.FL.Suspiciousness;

public interface FaultLocalization{
    public List<Suspiciousness> exec();
}