package jp.posl.jprophet;

import com.github.javaparser.ast.Node;
import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.printer.lexicalpreservation.LexicalPreservingPrinter;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;


public class AstGenerator {

	/**
	 * ノードの子ノードを幅優先で探索し，与えられたインデックスのノードを返す
	 * 
	 * @param node 検索対象の親ノード
	 * @param targetIndex レベル順（幅優先）のインデックス
	 * @return レベル順でtargetIndex番目のノード
	 */
	public static Optional<Node> findByLevelOrderIndex(Node node, int targetIndex){
		List<Node> childNodes = new LinkedList<Node>(node.getChildNodes());
		for(int i = 0;;i++){
			if(childNodes.isEmpty()){ 
				return Optional.empty();
			}
			Node head = childNodes.remove(0);
			if(i == targetIndex){
				return Optional.of(head); 
			}
			childNodes.addAll(head.getChildNodes());
		}
	}


	/**
	 * ソースコードから全てのASTノードを抽出し，修正単位であるRepairUnitを取得する.
	 * 
	 * @param sourceCode AST抽出対象のソースコード
	 * @return 修正対象のASTノードとコンパイルユニットを持った修正単位であるRepairUnitのリスト
	 */
	public List<RepairUnit> getAllRepairUnit(String sourceCode){
		List<RepairUnit> repairUnits = new ArrayList<RepairUnit>();
		for(int i = 0; ; i++){
			CompilationUnit compilationUnit;   //RepairUnitごとに新しいインスタンスの生成
			compilationUnit = JavaParser.parse(sourceCode);

			// TODO なんかここ良い書き方にしたい．やってることがただのnullチェックif分岐
			Optional<Node> optNode = AstGenerator.findByLevelOrderIndex(compilationUnit.findRootNode(), i); 
			if(optNode.isPresent()){
				repairUnits.add(new RepairUnit(optNode.orElseThrow(), i, compilationUnit)); 
			}
			else{
				return repairUnits;
			}
		}
	}
}

