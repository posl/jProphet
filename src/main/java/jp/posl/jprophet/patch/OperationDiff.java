package jp.posl.jprophet.patch;

import com.github.javaparser.ast.Node;


public class OperationDiff { //TODO: クラス名改善の余地あり

    public enum ModifyType {
        NONE,
        INSERT,
        CHANGE,
    }

    private ModifyType modifyType;
    private Node targetNodeBeforeFix;
    private Node targetNodeAfterFix;

    /**
     * 修正のタイプと修正前後のノードを持つクラス
     * @param modifyType 修正のタイプ
     * @param targetNodeBeforeFix 修正前のノードで，rootノードはcompilationunit
     * @param targetNodeAfterFix 修正部分のノードで，親ノードを持たない
     */
    public OperationDiff(ModifyType modifyType, Node targetNodeBeforeFix, Node targetNodeAfterFix) {
        this.modifyType = modifyType;
        this.targetNodeBeforeFix = targetNodeBeforeFix;
        this.targetNodeAfterFix = targetNodeAfterFix;
    }

    /**
     * 修正のタイプを返す
     * @return 修正のタイプ
     */
    public ModifyType getModifyType() {
        return this.modifyType;
    }

    /**
     * 修正前の対象ノードを返す
     * rootノードはCompilatioUnit
     * @return 修正前の対象ノード
     */
    public Node getTargetNodeBeforeFix() {
        return this.targetNodeBeforeFix;
    }

    /**
     * 修正後の対象ノードを返す
     * 親ノードは持たない
     * @return 修正後の対象ノード
     */
    public Node getTargetNodeAfterFix() {
        return this.targetNodeAfterFix;
    }

}



