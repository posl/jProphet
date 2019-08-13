package jp.posl.jprophet;

import java.util.ArrayList;
import java.util.List;

import com.github.javaparser.ast.Modifier;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;

public class RepairCandidateGenerator{
	public RepairCandidateGenerator(List<AstNode> AstNodes) {
		for (AstNode astNode : AstNodes) {
			Node node = astNode.get();
			if (node instanceof VariableDeclarator) {
				System.out.println("----------------------------------");
				((VariableDeclarator)node).setName("hoge");
				System.out.println(astNode.getSourceCode());
			}
			else if(node instanceof ClassOrInterfaceDeclaration){
				System.out.println("----------------------------------");
				((ClassOrInterfaceDeclaration)node).addModifier(Modifier.STATIC);
				System.out.println(astNode.getSourceCode());
			}
		}
	}

	public List<AbstractRepairCandidate> applyTemplate(AstNode astNode) {
		astNode.get();
		return new ArrayList<>();
	}
}
