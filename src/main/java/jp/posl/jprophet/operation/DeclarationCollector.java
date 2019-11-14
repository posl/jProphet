package jp.posl.jprophet.operation;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.body.VariableDeclarator;

public class DeclarationCollector {
	public List<VariableDeclarator> collectFileds(Node inTheClassThisNodeIsIn) {
        ClassOrInterfaceDeclaration classNode;
        try {
            classNode = inTheClassThisNodeIsIn.findParent(ClassOrInterfaceDeclaration.class).orElseThrow();
        }
        catch (NoSuchElementException e) {
            return new ArrayList<VariableDeclarator>();
        }
        final List<VariableDeclarator> fields = new ArrayList<VariableDeclarator>();
        classNode.findAll(FieldDeclaration.class).stream()
            .map(f -> f.getVariables().stream().collect(Collectors.toList()))
            .forEach(fields::addAll);
        
		return fields;
	}

	public List<VariableDeclarator> collectLocalVars(Node inTheMethodThisNodeIsIn) {
        MethodDeclaration methodNode;
        try {
            methodNode =  inTheMethodThisNodeIsIn.findParent(MethodDeclaration.class).orElseThrow();
        }
        catch (NoSuchElementException e) {
            return new ArrayList<VariableDeclarator>();
        }
        final List<VariableDeclarator> localVars = methodNode.findAll(VariableDeclarator.class);
		return localVars;
	}

	public List<Parameter> collectParameters(Node ofTheMethodThisNodeIsIn) {
        MethodDeclaration methodNode;
        try {
            methodNode =  ofTheMethodThisNodeIsIn.findParent(MethodDeclaration.class).orElseThrow();
        }
        catch (NoSuchElementException e) {
            return new ArrayList<Parameter>();
        }
        final List<Parameter> parameters = methodNode.findAll(Parameter.class);
        return parameters;
	}
}
