package jp.posl.jprophet.test.result;

import java.util.Map;

public interface TestResult {

    public boolean getIsSuccess();



    //mapにしたのは、結果内容が複数個の場合があるため
    //どういった形式で書き込むかはResultWriterクラスに任せたかった
    //もっといい方法は無いだろうか
    public Map<String, String> toStringMap();

}