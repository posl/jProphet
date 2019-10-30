package jp.posl.jprophet.FL.specification_strategy;

import java.util.List;

import jp.posl.jprophet.FL.Suspiciousness;
import java.util.stream.Collectors;

/**
 * 一箇所の疑惑値を変更する
 */
public class SpecificBug implements SpecificationProcess{

    final private String fqn;
    final private int line;
    final private double value;

    /**
     * 一箇所の疑惑値を変更する
     * @param fqn 変更したいファイルのfqn
     * @param line 変更したいファイルの行番号
     * @param value 変更後の疑惑値
     */
    public SpecificBug(String fqn, int line, double value){
        this.fqn = fqn;
        this.line = line;
        this.value = value;
    }

    /**
     * 受け取った疑惑値リストを上書きして返す
     */
    public void calculate(List<Suspiciousness> suspiciousnessList){
        List<Suspiciousness> suspiciousness = suspiciousnessList.stream()
            .filter(s -> fqn.equals(s.getFQN()) && s.getLine() == line)
            .collect(Collectors.toList());
        
        if (suspiciousness.size() == 1){
            int index = suspiciousnessList.indexOf(suspiciousness.get(0));
            suspiciousnessList.set(index, new Suspiciousness(fqn, line, value));
        }
    }
}