package jp.posl.jprophet.test.result;

import java.util.Map;


/**
 * テスト検証の結果を格納するクラス
 */
public interface TestResult {

    //mapにしたのは、結果内容が複数個の場合があるため
    //どういった形式で書き込むかはResultWriterクラスに任せたかった
    //もっといい方法は無いだろうか

    /**
     * テスト結果の詳細をMap形式で取得する
     * @return　テスト結果のMap
     */
    public Map<String, String> toStringMap();

}