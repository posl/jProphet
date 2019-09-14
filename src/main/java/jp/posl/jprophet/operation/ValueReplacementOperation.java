package jp.posl.jprophet.operation;

import java.util.ArrayList;
import java.util.List;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;

import jp.posl.jprophet.RepairUnit;

/**
 * 対象ステートメントの変数や関数，定数などを別のもので置き換える操作
 */
public class ValueReplacementOperation implements AstOperation {
    public List<RepairUnit> exec(RepairUnit repairUnit) {
        Node targetNode = repairUnit.getNode();

        if (targetNode instanceof AssignExpr) {
            // ((AssignExpr) targetNode).setValue(new FieldAccessExpr(new ThisExpr(),
            // "hoge"));
            // ((AssignExpr) targetNode).setValue(new NameExpr("lc"));
            Node classNode = targetNode.findParent(ClassOrInterfaceDeclaration.class).get();
            Node methodNode =  targetNode.findParent(MethodDeclaration.class).get();
            List<FieldDeclaration> fields = this.collectChild(classNode, FieldDeclaration.class);
            List<VariableDeclarationExpr> localVariables = this.collectChild(methodNode, VariableDeclarationExpr.class);
        }

        List<RepairUnit> candidates = new ArrayList<RepairUnit>();
        return candidates;
    }

    private <T extends Node> List<T> collectChild(Node scope, Class<T> nodeType){
        List<Node> nodes = this.collectAllChildNodes(scope);
        List<T> fields = new ArrayList<T>();
        for(Node node : nodes){
            if(nodeType.isAssignableFrom(node.getClass())){
                fields.add(nodeType.cast(node));
            }
        }

        return fields;
    }


	/**
	 * ASTノードの全ての子ノードをリストにして返す.
	 * 
	 * @param parentNode 探索したい子ノードを持つASTノード
	 * @return 子ノードのリスト 
	 */
	private List<Node> collectAllChildNodes(Node parentNode){
		List<Node> childNodes = new ArrayList<Node>(parentNode.getChildNodes());
		int index = 0;
		while(true){
			if(index == childNodes.size()) break;
			Node node = childNodes.get(index);
			
			if(!node.getChildNodes().isEmpty()){
				childNodes.addAll(node.getChildNodes());
			}
			index++;
		}
		return childNodes;
	}
    


}