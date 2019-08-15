package jp.posl.jprophet;

import java.util.ArrayList;
import java.util.List;

import com.github.javaparser.ast.Modifier;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.stmt.*;
import com.github.javaparser.ast.expr.*;

public class RepairCandidateGenerator{
	public RepairCandidateGenerator(List<AstNode> AstNodes) {
		for (AstNode astNode : AstNodes) {
			Node node = astNode.get();
			// if (node instanceof VariableDeclarator) {
			// 	System.out.println("----------------------------------");
			// 	((VariableDeclarator)node).setName("hoge");
			// 	System.out.println(astNode.getSourceCode());
			// }
			if(node instanceof ExpressionStmt){
				System.out.println("----------------------------------");
				//System.out.println(astNode.getSourceCode());
			}
			if(node instanceof BlockStmt){
				System.out.println("----------------------------------");
				((BlockStmt)node).addStatement("if(true){return;}");
				// 以下のように条件付きreturnを追加してもprinterでエラー
				// ((BlockStmt)node).addStatement(2, new IfStmt(null, new BooleanLiteralExpr(), new ReturnStmt(), null));
				System.out.println(astNode.getSourceCode());
			}
		}
	}

	public List<AbstractRepairCandidate> applyTemplate(AstNode astNode) {
		astNode.get();
		return new ArrayList<>();
	}
}
