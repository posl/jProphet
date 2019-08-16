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

	private final String FILE_PATH = "./test.java";
	private CompilationUnit cu;

	public AstGenerator() {
		try {
			this.cu = JavaParser.parse(new File(this.FILE_PATH));
		} catch (FileNotFoundException e) {
			System.err.println(e.getMessage());
		}
	}
	

	// parentNode以下の全ての子ノードをリストにして返す
	private List<Node> collectAllChildNodes(Node parentNode){
		List<Node> nodes = new ArrayList<Node>(parentNode.getChildNodes());
		int index = 0;
		while(true){
			if(index == nodes.size()) break;
			Node node = nodes.get(index);
			
			if(!node.getChildNodes().isEmpty()){
				nodes.addAll(node.getChildNodes());
			}
			index++;
		}
		return nodes;
	}


	// コンパイルユニットから与えられたハッシュ値を持つノードを見つける
	private Optional<Node> findNode(CompilationUnit cu, int hashCode, HashMap<Integer, Integer> hashCodeCounts){
		List<Node> nodes = new ArrayList<Node>();
		nodes.add(cu.findRootNode());
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


	// 同一のハッシュ値を持つノードを数え上げる
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


	// 全てのAstNodeを取得
	public List<AstNode> getAllAstNode(){
		Node root = this.cu.findRootNode();
		List<Node> nodes = this.collectAllChildNodes(root);

		HashMap<Integer, Integer> hashCodeCounts = countHashCode(nodes);

		List<AstNode> astNodes = new ArrayList<AstNode>();
		for(Node node : nodes){
			CompilationUnit cUnit;
			try{
				cUnit = JavaParser.parse(new File(this.FILE_PATH));
				LexicalPreservingPrinter.setup(cUnit);
				this.findNode(cUnit, node.hashCode(), hashCodeCounts).ifPresent(n -> {
					astNodes.add(new AstNode(n, cUnit));
				});
			}
			catch (FileNotFoundException e) {
				System.err.println(e.getMessage());
			}
			
		}

		return astNodes;
	}
	
}

