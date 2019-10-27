package jp.posl.jprophet.FL;

import java.util.List;

import jp.posl.jprophet.FL.Suspiciousness;

public class BugSpecification implements FaultLocalization{

    private List<Suspiciousness> suspiciousnessList;

    public List<Suspiciousness> exec(){
        //対象ソースコードの行数,FQNを取得
        //suspiciousnessListを0に初期化
        //任意の行の疑惑値を変更
        return suspiciousnessList;
    }

}