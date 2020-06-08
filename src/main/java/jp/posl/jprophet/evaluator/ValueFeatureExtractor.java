package jp.posl.jprophet.evaluator;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.UnaryExpr;
import com.github.javaparser.ast.stmt.ForStmt;
import com.github.javaparser.ast.stmt.ForeachStmt;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.WhileStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.PrimitiveType;
import com.github.javaparser.ast.type.Type;
import com.github.javaparser.ast.type.PrimitiveType.Primitive;

import jp.posl.jprophet.operation.DeclarationCollector;;

/**
 * 修正パッチの変数の特徴抽出を行うクラス
 */
public class ValueFeatureExtractor {
    /**
     * プログラム中に登場する全ての変数についてその特徴を抽出する
     * @param root ソースコードのASTのルートノード
     * @return プログラム中の変数の特徴リスト
     */
    public List<ValueFeature> extract(Node root) {
        final List<ValueFeature> features = new ArrayList<>();
        final List<NameExpr> variables = root.findAll(NameExpr.class);
        for (NameExpr variable: variables) {
            final ValueFeature feature = new ValueFeature();
            findDeclarator(variable).ifPresent((declarator) -> {
                this.extractScopeFeature(declarator);
                declarator.findFirst(Type.class).ifPresent((type) -> {
                    this.extractTypeFeature(type);
                });
            });
            feature.add(this.extractContextFeature(variable));
            feature.add(this.extractOperationFeature(variable));
            features.add(feature);
        }
        final List<Node> declarators = root.findAll(VariableDeclarator.class).stream()
            .map(v -> (Node)v)
            .collect(Collectors.toList());
        declarators.addAll(root.findAll(Parameter.class).stream()
            .map(p -> (Node)p)
            .collect(Collectors.toList())
        );
        for (Node declarator : declarators) {
            final ValueFeature feature = new ValueFeature();
            feature.add(this.extractScopeFeature(declarator));
            final Type type = declarator.findFirst(Type.class).get();
            feature.add(this.extractTypeFeature(type));
            feature.add(this.extractContextFeature(declarator));
            features.add(feature);
        }
        return features;
    }

    /**
     * 変数の宣言ノードを探索する
     * @param variable 宣言ノードを探索したい変数のノード
     * @return 宣言ノード，存在しない場合empty()を返す
     */
    private Optional<Node> findDeclarator(NameExpr variable) {
        final DeclarationCollector collector = new DeclarationCollector();

        final Optional<VariableDeclarator> localVarDeclarator = collector.collectLocalVarsDeclared(variable).stream()
            .filter(var -> var.getName().equals(variable.getName()))
            .findFirst();
        if(localVarDeclarator.isPresent()) {
            return Optional.of((Node)localVarDeclarator.get());
        }
        final Optional<Parameter> parameter = collector.collectParameters(variable).stream()
            .filter(para -> para.getName().equals(variable.getName()))
            .findFirst();
        if(parameter.isPresent()) {
            return Optional.of((Node)parameter.get());
        };
        final Optional<VariableDeclarator> fieldDeclarator = collector.collectFileds(variable).stream()
            .filter(field -> field.getName().equals(variable.getName()))
            .findFirst();
        if(fieldDeclarator.isPresent()) {
            return Optional.of((Node)fieldDeclarator.get());
        }

        return Optional.empty();
    }

    /**
     * 型情報を抽出する  
     * @param type 型ノード
     * @return 変数の型の特徴
     */
    private ValueFeature extractTypeFeature(Type type) {
        final ValueFeature feature = new ValueFeature();
        if(type instanceof PrimitiveType) {
            final PrimitiveType primitive = (PrimitiveType) type;
            if(primitive.getType() == Primitive.BOOLEAN) {
                feature.boolType = true;
            }
            if(primitive.getType() == Primitive.INT || primitive.getType() == Primitive.DOUBLE ||
                    primitive.getType() == Primitive.LONG || primitive.getType() == Primitive.SHORT ||
                    primitive.getType() == Primitive.FLOAT) {
                feature.numType = true;
            }
        }
        if(type instanceof ClassOrInterfaceType) {
            feature.objectType = true;
            final ClassOrInterfaceType classOrInterface = (ClassOrInterfaceType) type;
            if(classOrInterface.getNameAsString().equals("String")) {
                feature.stringType = true;
            }
        }
        return feature;
    }

    /**
     * 変数がif文やループなど構文上においてどこに位置するかという特徴
     * @param variable 変数ノード
     * @return 変数の特徴
     */
    private ValueFeature extractContextFeature(Node variable) {
        final ValueFeature feature = new ValueFeature();
        if (variable.findParent(IfStmt.class).isPresent()) {
            feature.ifStmt = true;
            final Expression condition = variable.findParent(IfStmt.class).get().getCondition();
            if(condition.equals(variable)) {
                feature.condition = true;
            }
        }
        final boolean inForStmt = variable.findParent(ForStmt.class).isPresent();
        final boolean inForeachStmt = variable.findParent(ForeachStmt.class).isPresent();
        final boolean inWhileStmt = variable.findParent(WhileStmt.class).isPresent();
        if (inForStmt || inForeachStmt || inWhileStmt) {
            feature.loop = true;
        }
        feature.parameter = variable.findParent(MethodCallExpr.class).isPresent();
        feature.assign    = variable.findParent(AssignExpr.class).isPresent();
        return feature;
    }

    /**
     * 変数の被演算子としての特徴を抽出
     * @param variable 変数ノード
     * @return 変数の特徴
     */
    private ValueFeature extractOperationFeature(Node variable) {
        final ValueFeature feature = new ValueFeature();
        if(variable.findParent(BinaryExpr.class).isPresent()) {
            final BinaryExpr binaryExpr = variable.findParent(BinaryExpr.class).get();
            final List<String> commutativeOpRepresentations = List.of("+", "*", "==", "!=", "||", "&&");
            feature.commutativeOp = commutativeOpRepresentations.stream()
                .anyMatch(op -> binaryExpr.getOperator().asString().equals(op));

            final List<String> noncommutativeOpRepresentations = List.of("-", "/", "%", "<", ">", "<=", ">=");
            feature.noncommutativeOpL = noncommutativeOpRepresentations.stream()
                .anyMatch(op -> binaryExpr.getOperator().asString().equals(op) && binaryExpr.getLeft().equals(variable));
            feature.noncommutativeOpR = noncommutativeOpRepresentations.stream()
                .anyMatch(op -> binaryExpr.getOperator().asString().equals(op) && binaryExpr.getRight().equals(variable));
        }
        if(variable.findParent(UnaryExpr.class).isPresent()) {
            feature.unaryOp = true;
        }
        return feature;
    }

    /**
     * 宣言ノードを元に変数のスコープを特定
     * @param declarator
     * @return 変数の特徴
     */
    private ValueFeature extractScopeFeature(Node declarator) {
        final ValueFeature feature = new ValueFeature();
        if (declarator.findParent(MethodDeclaration.class).isPresent()) {
            if (declarator instanceof Parameter) {
                feature.argument = true;
            }
            else {
                feature.local = true;
            }
        }
        else {
            feature.field = true;
        }

        if (declarator.getParentNode().get().toString().startsWith("final")) {
            feature.constant = true;
        }
        return feature;
    }
}
