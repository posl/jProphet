package jp.posl.jprophet.FL.specification_strategy;

import java.util.List;

import jp.posl.jprophet.FL.Suspiciousness;
import java.util.stream.Collectors;

public class SpecificBugsWavy implements SpecificationProcess{

    final private String fqn;
    final private int line;
    final private double value;
    final private int range;
    final private double width;


    public SpecificBugsWavy(String fqn, int line, double value, int range, double width){
        this.fqn = fqn;
        this.line = line;
        this.value = value;
        this.range = range;
        this.width = width;
    }

    public List<Suspiciousness> calculate(List<Suspiciousness> suspiciousnessList){
        List<Suspiciousness> suspiciousness = suspiciousnessList.stream()
            .filter(s -> fqn.equals(s.getPath()) && s.getLine() == line)
            .collect(Collectors.toList());
        
        if (suspiciousness.size() == 1){
            int index = suspiciousnessList.indexOf(suspiciousness.get(0));
            suspiciousnessList.set(index, new Suspiciousness(fqn, line, value));
            for (int i = 1; i <= range; i++){
                if (index + i <= suspiciousnessList.size()){
                    if (suspiciousnessList.get(index + i).getPath().equals(fqn) && value - width * i >= 0){
                        suspiciousnessList.set(index + i, new Suspiciousness(fqn, line + i, value - width * i));
                    }
                }
                if (index - i >= 0){
                    if (suspiciousnessList.get(index - i).getPath().equals(fqn) && value - width * i >= 0){
                        suspiciousnessList.set(index - i, new Suspiciousness(fqn, line - i, value - width * i));
                    }
                }
            }
        }
        return suspiciousnessList;
    }
}