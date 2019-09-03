package jp.posl.jprophet;

import com.github.javaparser.ast.Node;
import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.printer.lexicalpreservation.LexicalPreservingPrinter;
import java.util.ArrayList;
import java.util.List;


public class AstGenerator {

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


	/**
	 * ソースコードから全てのASTノードを抽出し，修正単位であるRepairUnitを取得する.
	 * 
	 * @param source AST抽出対象のソースコード
	 * @return 修正対象のASTノードとコンパイルユニットを持った修正単位であるRepairUnitのリスト
	 */
	public List<RepairUnit> getAllRepairUnit(String sourceCode){
		final CompilationUnit cu;
		cu = JavaParser.parse(sourceCode);

		Node root = cu.findRootNode();
		List<Node> nodes = this.collectAllChildNodes(root);

		List<RepairUnit> repairUnits = new ArrayList<RepairUnit>();
		for(int i = 0; i < nodes.size(); i++){
			CompilationUnit compilationUnit;
			compilationUnit = JavaParser.parse(sourceCode);
			LexicalPreservingPrinter.setup(compilationUnit);
			repairUnits.add(new RepairUnit(this.collectAllChildNodes(compilationUnit.findRootNode()).get(i), i, compilationUnit));
		}

		return repairUnits;
	}
	
}

