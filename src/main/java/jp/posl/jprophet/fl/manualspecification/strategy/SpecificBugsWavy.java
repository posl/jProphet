package jp.posl.jprophet.fl.manualspecification.strategy;

import java.util.List;

import jp.posl.jprophet.fl.Suspiciousness;
import java.util.stream.Collectors;

/**
 * ある行を中心にして上下何行かの疑惑値を変更
 */
public class SpecificBugsWavy implements SpecificationStrategy{

    final private String fqn;
    final private int line;
    final private double value;
    final private int range;
    final private double width;

    /**
     * ある行を中心にして上下何行かの疑惑値を変更
     * @param fqn 変更したいファイルのfqn
     * @param line 変更したいファイルの中心の行
     * @param value 中心の行の変更後の疑惑値
     * @param range 中心の行から+-何行を変更するか
     * @param width 中心から1行ずれるごとにどれだけ疑惑値が下がるか
     */
    public SpecificBugsWavy(String fqn, int line, double value, int range, double width){
        this.fqn = fqn;
        this.line = line;
        this.value = value;
        this.range = range;
        this.width = width;
    }

    /**
     * 受け取った疑惑値リストを上書きして返す
     */
    public void calculate(List<Suspiciousness> suspiciousnessList){
        List<Suspiciousness> suspiciousness = suspiciousnessList.stream()
            .filter(s -> fqn.equals(s.getFQN()) && s.getLineNumber() == line)
            .collect(Collectors.toList());
        
        if (suspiciousness.size() == 1){
            int index = suspiciousnessList.indexOf(suspiciousness.get(0));
            suspiciousnessList.set(index, new Suspiciousness(fqn, line, value));
            for (int i = 1; i <= range; i++){
                if (index + i < suspiciousnessList.size()){
                    if (suspiciousnessList.get(index + i).getFQN().equals(fqn) && value - width * i >= 0){
                        suspiciousnessList.set(index + i, new Suspiciousness(fqn, line + i, value - width * (double)i));
                    }
                }
                if (index - i >= 0){
                    if (suspiciousnessList.get(index - i).getFQN().equals(fqn) && value - width * i >= 0){
                        suspiciousnessList.set(index - i, new Suspiciousness(fqn, line - i, value - width * (double)i));
                    }
                }
            }
        }
    }
}