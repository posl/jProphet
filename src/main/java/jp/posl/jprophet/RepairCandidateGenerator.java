package jp.posl.jprophet;

import java.util.ArrayList;
import java.util.List;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.Name;
import com.github.javaparser.ast.expr.SimpleName;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.visitor.GenericVisitor;
import com.github.javaparser.ast.visitor.VoidVisitor;
import com.github.javaparser.printer.lexicalpreservation.LexicalPreservingPrinter;


import jp.posl.jprophet.RepairCandidate;

public class RepairCandidateGenerator{
	private List<Node> candidate;
	private CompilationUnit cu;

	public RepairCandidateGenerator(List<AstNode> AstNodes) {
		candidate = new ArrayList<Node>();
		for (AstNode astNode : AstNodes) {
			Node node = astNode.get();
			if (node instanceof VariableDeclarator) {
				((VariableDeclarator)node).setName("huga");
				//((VariableDeclarator)node).setParentNode(newParentNode);
				System.out.println(astNode.getSourceCode());
			}
			else if(node instanceof ClassOrInterfaceDeclaration){
				((ClassOrInterfaceDeclaration)node).addModifier(Modifier.STATIC);
				System.out.println(astNode.getSourceCode());
			}
			// else if(node instanceof BlockStmt){
			// 	// parent nodeが途中からないNodeがある
			// 	// 途中の改変によって切り離された可能性
			// 	// 正解

			// 	//cloneを使ってもダメだった
			// 	if(!candidate.isEmpty()){
			// 		boolean ret = ((BlockStmt) node).replace(new BlockStmt());
			// 		//System.out.println(ret);
			// 		//System.out.println(temp);
			// 	}
			// 	else{
			// 		((BlockStmt)node).addStatement("this.node = null;");
			// 	}
			// 	candidate.add(node);
			// 	System.out.println(astNode.getSourceCode());
			// }
			// else if(node instanceof ExpressionStmt){
			// 	Node newNode = ((ExpressionStmt)node).toIfStmt().get();
			// 	((ExpressionStmt)node).replace(newNode);
			// }
		}


	}

	

	public List<AbstractRepairCandidate> applyTemplate(AstNode astNode) {
		astNode.get();
		return new ArrayList<>();
	}

	// public void exec(AstNode astNode) {
	// 	astNode.accept(this);
	// }
}
