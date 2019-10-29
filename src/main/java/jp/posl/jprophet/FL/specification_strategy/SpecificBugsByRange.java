package jp.posl.jprophet.FL.specification_strategy;

import java.util.List;

import jp.posl.jprophet.FL.Suspiciousness;
import java.util.stream.Collectors;

public class SpecificBugsByRange implements SpecificationProcess{

    final private String fqn;
    final private int startLine;
    final private int finishLine;
    final private double value;


    public SpecificBugsByRange(String fqn, int startLine, int finishLine, double value){
        this.fqn = fqn;
        this.startLine = startLine;
        this.finishLine = finishLine;
        this.value = value;
    }

    public List<Suspiciousness> calculate(List<Suspiciousness> suspiciousnessList){
        List<Suspiciousness> startSuspiciousness = suspiciousnessList.stream()
            .filter(s -> fqn.equals(s.getPath()) && s.getLine() == startLine)
            .collect(Collectors.toList());
        List<Suspiciousness> finishSuspiciousness = suspiciousnessList.stream()
            .filter(s -> fqn.equals(s.getPath()) && s.getLine() == finishLine)
            .collect(Collectors.toList());
        
        if (startSuspiciousness.size() == 1 && finishSuspiciousness.size() == 1){
            int startIndex = suspiciousnessList.indexOf(startSuspiciousness.get(0));
            int finishIndex = suspiciousnessList.indexOf(finishSuspiciousness.get(0));

            for (int index = startIndex; index <= finishIndex; index++){
                suspiciousnessList.set(index, new Suspiciousness(fqn, startLine - startIndex + index, value));
            }
        }
        return suspiciousnessList;
    }
}