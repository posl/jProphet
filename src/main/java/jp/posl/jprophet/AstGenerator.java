package jp.posl.jprophet;

import com.github.javaparser.ast.Node;
import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.printer.lexicalpreservation.LexicalPreservingPrinter;



import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;


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
	 * 与えられたハッシュ値を持つASTノードをコンパイルユニットの全ての子ノードの中から見つける.
	 * 
	 * @param compilationUnit 検索対象の子ノードを持つコンパイルユニット
	 * @param hashCode 検索するハッシュ値
	 * @param hashCodeCounts 
	 * @return ヒットしたASTノード
	 */
	private Optional<Node> findNode(CompilationUnit compilationUnit, int hashCode, HashMap<Integer, Integer> hashCodeCounts){
		List<Node> nodes = new ArrayList<Node>();
		nodes.add(compilationUnit.findRootNode());
		int index = 0;
		int count = 0;
		while(true){
			Node node = nodes.get(index);
			if(node.hashCode() == hashCode) {
				count++;
				if(count == hashCodeCounts.get(hashCode)){
					hashCodeCounts.put(node.hashCode(), hashCodeCounts.get(hashCode) - 1);
					return Optional.ofNullable(node);
				}
			}
			else if(!node.getChildNodes().isEmpty()){
				nodes.addAll(node.getChildNodes());
			}
			index++;
			if(index == nodes.size()) break;
		}
		return Optional.ofNullable(null);
	}


	/**
	 * 与えられたASTノードのリストに含まれるノードのハッシュ値を調べ，重複するハッシュ値を数える.
	 * 
	 * @param nodes ハッシュ値の重複数を調べるASTノードのリスト
	 * @return 引数のASTノードのリストに存在したハッシュ値をキーとしてその個数を値とした連想配列
	 */
	private HashMap<Integer, Integer> countHashCode(List<Node> nodes){
		List<Integer> codes = new ArrayList<Integer>();
		HashMap<Integer, Integer> hashCodeCounts = new HashMap<Integer, Integer>();
		for(Node node: nodes){
			codes.add(node.hashCode());
			if(hashCodeCounts.containsKey(node.hashCode())){
				int count = hashCodeCounts.get(node.hashCode());
				hashCodeCounts.put(node.hashCode(), ++count);
			}
			else{
				hashCodeCounts.put(node.hashCode(), 1);
			}
		}
		return hashCodeCounts;
	}


	/**
	 * ソースコードから全てのASTノードを抽出し，修正単位であるRepairUnitを取得する.
	 * 
	 * @return 修正対象のASTノードとコンパイルユニットを持った修正単位であるRepairUnitのリスト
	 */
	public List<RepairUnit> getAllRepairUnit(ProjectConfiguration project){
		final String filePath = project.getFilePath();
		final CompilationUnit cu;
		try {
			cu = JavaParser.parse(new File(filePath));
		} catch (FileNotFoundException e) {
			System.err.println(e.getMessage());
			return new ArrayList<>();
		}

		Node root = cu.findRootNode();
		List<Node> nodes = this.collectAllChildNodes(root);

		HashMap<Integer, Integer> hashCodeCounts = countHashCode(nodes);

		List<RepairUnit> repairUnits = new ArrayList<RepairUnit>();
		for(Node node : nodes){
			CompilationUnit compilationUnit;
			try{
				compilationUnit = JavaParser.parse(new File(filePath));
				LexicalPreservingPrinter.setup(compilationUnit);
				this.findNode(compilationUnit, node.hashCode(), hashCodeCounts).ifPresent(n -> {
					repairUnits.add(new RepairUnit(n, compilationUnit, filePath));
				});
			}
			catch (FileNotFoundException e) {
				System.err.println(e.getMessage());
				return new ArrayList<>();
			}
			
		}

		return repairUnits;
	}
	
}

