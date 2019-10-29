package jp.posl.jprophet.FL.specification_strategy;

import java.util.List;

import jp.posl.jprophet.FL.Suspiciousness;


public interface SpecificationProcess{
    public List<Suspiciousness> calculate(List<Suspiciousness> suspiciousnessList);
}