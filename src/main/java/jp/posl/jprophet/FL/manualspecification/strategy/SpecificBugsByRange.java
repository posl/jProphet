package jp.posl.jprophet.fl.manualspecification.strategy;

import java.util.List;

import jp.posl.jprophet.fl.Suspiciousness;
import java.util.stream.Collectors;

/**
 * ある範囲の疑惑値をまとめて変更する
 */
public class SpecificBugsByRange implements SpecificationProcess{

    final private String fqn;
    final private int startLine;
    final private int finishLine;
    final private double value;


    /**
     * ある範囲の疑惑値をまとめて変更する
     * @param fqn 変更したいファイルのfqn
     * @param startLine 変更したい範囲の始めの行番号
     * @param finishLine 変更したい範囲の終わりの行番号
     * @param value 変更後の疑惑値
     */
    public SpecificBugsByRange(String fqn, int startLine, int finishLine, double value){
        this.fqn = fqn;
        this.startLine = startLine;
        this.finishLine = finishLine;
        this.value = value;
    }

    /**
     * 受け取った疑惑値リストを上書きして返す
     */
    public void calculate(List<Suspiciousness> suspiciousnessList){
        List<Suspiciousness> startSuspiciousness = suspiciousnessList.stream()
            .filter(s -> fqn.equals(s.getFQN()) && s.getLine() == startLine)
            .collect(Collectors.toList());
        List<Suspiciousness> finishSuspiciousness = suspiciousnessList.stream()
            .filter(s -> fqn.equals(s.getFQN()) && s.getLine() == finishLine)
            .collect(Collectors.toList());
        
        if (startSuspiciousness.size() == 1 && finishSuspiciousness.size() == 1){
            int startIndex = suspiciousnessList.indexOf(startSuspiciousness.get(0));
            int finishIndex = suspiciousnessList.indexOf(finishSuspiciousness.get(0));

            for (int index = startIndex; index <= finishIndex; index++){
                suspiciousnessList.set(index, new Suspiciousness(fqn, startLine - startIndex + index, value));
            }
        }
    }
}