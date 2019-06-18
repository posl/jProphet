package jp.posl.jprophet;

import com.github.javaparser.ast.body.MethodDeclaration;
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

import com.github.javaparser.ParseResult;
import com.github.javaparser.ParseStart;
import com.github.javaparser.ast.CompilationUnit;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AstGenerator {

	private final String FILE_PATH = "src/main/java/jp/posl/jprophet/AstNode.java";

	private CompilationUnit cu;

	public AstGenerator() {
		try {
			cu = JavaParser.parse(new File(this.FILE_PATH));
		} catch (FileNotFoundException e) {
			System.err.println(e.getMessage());
		}
		LexicalPreservingPrinter.setup(this.cu);
        //System.out.println(LexicalPreservingPrinter.print(this.cu));
	}
	
	// NodeをラッパークラスAstNodeに変換
	private List<AstNode> nodeToAstNode(List<Node> nodes){
		List<AstNode> astNodes = new ArrayList<AstNode>();
		nodes.forEach(n -> {
			AstNode astNode = new AstNode(n);
			astNodes.add(astNode);
		});
		return astNodes;
	}

	// parentNode以下の全ての子ノードをリストにして返す
	private List<Node> collectAllChildNodes(Node parentNode){
		List<Node> nodes = new ArrayList<Node>(parentNode.getChildNodes());
		int index = 0;
		while(true){
			Node node = nodes.get(index);
			if(!node.getChildNodes().isEmpty()){
				nodes.addAll(node.getChildNodes());
			}
			index++;
			if(index == nodes.size()) break;
		}
		return nodes;
	}

	// 全てAstNodeを取得
	public List<AstNode> getAllAstNode(){
		Node root = this.cu.findRootNode();
	
		List<Node> nodes = this.collectAllChildNodes(root);
		List<AstNode> astNodes = this.nodeToAstNode(nodes);

		return astNodes;
	}
	
}

