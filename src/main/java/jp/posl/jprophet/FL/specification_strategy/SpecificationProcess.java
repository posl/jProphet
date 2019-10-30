package jp.posl.jprophet.FL.specification_strategy;

import java.util.List;

import jp.posl.jprophet.FL.Suspiciousness;


public interface SpecificationProcess{
    public void calculate(List<Suspiciousness> suspiciousnessList);
}