package jp.posl.jprophet;


public class AbstractRepairCandidate extends RepairCandidate {
    private AstNode astNode;
    public AbstractRepairCandidate(AstNode astNode){
        this.astNode = astNode;
    }

    public String toString(){
        // TODO: 仮のあれ
        return this.astNode.getSourceCode();
    }
}
