package jp.posl.jprophet.FL.specification_strategy;

import java.util.List;

import jp.posl.jprophet.FL.Suspiciousness;
import java.util.stream.Collectors;

public class SpecificBug implements SpecificationProcess{

    final private String fqn;
    final private int line;
    final private double value;


    public SpecificBug(String fqn, int line, double value){
        this.fqn = fqn;
        this.line = line;
        this.value = value;
    }

    public List<Suspiciousness> calculate(List<Suspiciousness> suspiciousnessList){
        List<Suspiciousness> suspiciousness = suspiciousnessList.stream()
            .filter(s -> fqn.equals(s.getPath()) && s.getLine() == line)
            .collect(Collectors.toList());
        
        if (suspiciousness.size() == 1){
            int index = suspiciousnessList.indexOf(suspiciousness.get(0));
            suspiciousnessList.set(index, new Suspiciousness(fqn, line, value));
        }
        return suspiciousnessList;
    }
}