package jp.posl.jprophet.fl.manualspecification.strategy;

import java.util.List;

import jp.posl.jprophet.fl.Suspiciousness;


public interface SpecificationProcess{
    public void calculate(List<Suspiciousness> suspiciousnessList);
}