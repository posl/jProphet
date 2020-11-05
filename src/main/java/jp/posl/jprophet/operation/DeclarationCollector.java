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

/**
 * 参照可能な変数などの宣言ノードを収集するクラス
 */
public class DeclarationCollector {
    /**
     * メンバ変数を収集する
     * @param scope このノードが存在するクラスのメンバ変数を集める
     * @return メンバ変数の宣言ノードのリスト
     */
	public List<VariableDeclarator> collectFileds(Node scope) {
        ClassOrInterfaceDeclaration classNode;
        try {
            classNode = scope.findParent(ClassOrInterfaceDeclaration.class).orElseThrow();
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

    /**
     * 既に宣言されているローカル変数を収集する
     * @param scope このノードより前に宣言されたローカル変数を収集する
     * @return ローカル変数の宣言ノードのリスト
     */
	public List<VariableDeclarator> collectLocalVarsDeclared(Node scope) {
        MethodDeclaration methodNode;
        try {
            methodNode =  scope.findParent(MethodDeclaration.class).orElseThrow();
        }
        catch (NoSuchElementException e) {
            return new ArrayList<VariableDeclarator>();
        }
        final List<VariableDeclarator> localVars = methodNode.findAll(VariableDeclarator.class).stream()
            .filter(v -> {
                if (!v.getBegin().isPresent() || !scope.getBegin().isPresent()) return false;
                return v.getBegin().orElseThrow().line < scope.getBegin().orElseThrow().line;
            })
            .collect(Collectors.toList());
		return localVars;
	}

    /**
     * 仮引数を収集する
     * @param scope このノードが存在する関数の仮引数を集める
     * @return 仮引数の宣言ノードのリスト
     */
	public List<Parameter> collectParameters(Node scope) {
        MethodDeclaration methodNode;
        try {
            methodNode =  scope.findParent(MethodDeclaration.class).orElseThrow();
        }
        catch (NoSuchElementException e) {
            return new ArrayList<Parameter>();
        }
        final List<Parameter> parameters = methodNode.findAll(Parameter.class);
        return parameters;
    }
}
