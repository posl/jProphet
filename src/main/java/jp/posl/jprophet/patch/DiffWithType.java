package jp.posl.jprophet.patch;

import com.github.javaparser.ast.Node;


public class DiffWithType { //TODO: クラス名改善の余地あり

    public enum ModifyType {
        NONE,
        INSERT,
        CHANGE,
    }
    
    private ModifyType modifyType;
    private Node targetNodeBeforeFix;
    private Node targetNodeAfterFix;

    /**
     * コンストラクタ
     */
    public DiffWithType(ModifyType modifyType, Node targetNodeBeforeFix, Node targetNodeAfterFix) {
        this.modifyType = modifyType;
        this.targetNodeBeforeFix = targetNodeBeforeFix;
        this.targetNodeAfterFix = targetNodeAfterFix;
    }

    public ModifyType getModifyType() {
        return this.modifyType;
    }

    public Node getTargetNodeBeforeFix() {
        return this.targetNodeBeforeFix;
    }

    public Node getTargetNodeAfterFix() {
        return this.targetNodeAfterFix;
    }

}



