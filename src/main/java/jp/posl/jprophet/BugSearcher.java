package jp.posl.jprophet;

import java.util.List;

import jp.posl.jprophet.FL.Suspiciousness;

public interface BugSearcher{
    public List<Suspiciousness> exec();
}