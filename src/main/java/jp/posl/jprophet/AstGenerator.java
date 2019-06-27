package jp.posl.jprophet;

import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.visitor.VoidVisitor;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.github.javaparser.metamodel.NodeMetaModel;
import com.github.javaparser.JavaParser;
import com.github.javaparser.StringProvider;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.printer.lexicalpreservation.LexicalPreservingPrinter;

import javassist.expr.Instanceof;

import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.expr.IntegerLiteralExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ContinueStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.visitor.ModifierVisitor;

import java.io.FileInputStream;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.github.javaparser.ParseResult;
import com.github.javaparser.ParseStart;
import com.github.javaparser.ast.CompilationUnit;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class AstGenerator {

	private final String FILE_PATH = "/Users/shuto/test.java";
	//private final String CUR_DIR = System.getProperty("user.dir");
	private CompilationUnit cu;

	public AstGenerator() {
		try {
			this.cu = JavaParser.parse(new File(this.FILE_PATH));
		} catch (FileNotFoundException e) {
			System.err.println(e.getMessage());
		}
		// LexicalPreservingPrinter.setup(this.cu);

		// Node root = this.cu.findRootNode();
		// Node hoge = root.getChildNodes().get(3).getChildNodes().get(1).getChildNodes().get(0);
		// ((VariableDeclarator)hoge).setName("hoge");
        // System.out.println(LexicalPreservingPrinter.print(this.cu));
	}
	
	// NodeをラッパークラスAstNodeに変換
	// private List<AstNode> nodeToAstNode(List<Node> nodes){
	// 	List<AstNode> astNodes = new ArrayList<AstNode>();
	// 	nodes.forEach(n -> {
	// 		AstNode astNode = new AstNode(n);
	// 		astNodes.add(astNode);
	// 	});
	// 	return astNodes;
	// }

	// parentNode以下の全ての子ノードをリストにして返す
	private List<Node> collectAllChildNodes(Node parentNode){
		List<Node> nodes = new ArrayList<Node>(parentNode.getChildNodes());
		int index = 0;
		while(true){
			Node node = nodes.get(index);
			
			if(!node.getChildNodes().isEmpty()){
				//nodes.addAll(node.getChildNodes().stream().map(n -> n.clone()).collect(Collectors.toList()));
				// List<Node> childs = node.getChildNodes();
				// List<Node> childClones = new ArrayList<>();
				// childs.forEach(n -> {
				// 	Node nodeClone = n.clone();
				// 	childClones.add(nodeClone);
				// });
				nodes.addAll(node.getChildNodes());
				//nodes.addAll(childClones);
			}
			index++;
			if(index == nodes.size()) break;
		}
		return nodes;
	}

	// コンパイルユニットから与えられたハッシュ値を持つノードを見つける
	private Optional<Node> findNode(CompilationUnit cu, int hashCode){
		List<Node> nodes = new ArrayList<Node>();
		nodes.add(cu.findRootNode());
		int index = 0;
		while(true){
			Node node = nodes.get(index);
			if(node.hashCode() == hashCode) {
				return Optional.ofNullable(node);
			}
			else if(!node.getChildNodes().isEmpty()){
				nodes.addAll(node.getChildNodes());
			}
			index++;
			if(index == nodes.size()) break;
		}
		return Optional.ofNullable(null);
	}

	// 全てAstNodeを取得
	public List<AstNode> getAllAstNode(){
		Node root = this.cu.findRootNode();
		List<Node> nodes = this.collectAllChildNodes(root);
		// List<Integer> nodeHashCodes = new ArrayList<Integer>();
		// for(int i = 0; i < nodes.size(); i++){
		// 	nodeHashCodes.add(nodes.get(i).hashCode());
		// }

		List<AstNode> astNodes = new ArrayList<AstNode>();
		for(Node node : nodes){
			CompilationUnit cUnit;
			try{
				cUnit = JavaParser.parse(new File(this.FILE_PATH));
				System.out.println(LexicalPreservingPrinter.print(cUnit));
				this.findNode(cUnit, node.hashCode()).ifPresent(n -> {
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

