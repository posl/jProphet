package jp.posl.jprophet.operation;

import java.util.List;

import jp.posl.jprophet.AstNode;

public interface AstOperation{
    public List<AstNode> exec(AstNode astNode);
}