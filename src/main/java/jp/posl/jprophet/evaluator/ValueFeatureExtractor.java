package jp.posl.jprophet.evaluator;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ForStmt;
import com.github.javaparser.ast.stmt.ForeachStmt;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.WhileStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.PrimitiveType;
import com.github.javaparser.ast.type.Type;
import com.github.javaparser.ast.type.PrimitiveType.Primitive;

import jp.posl.jprophet.evaluator.ValueFeature.Scope;
import jp.posl.jprophet.evaluator.ValueFeature.ValueType;
import jp.posl.jprophet.operation.DeclarationCollector;;

public class ValueFeatureExtractor {
    private Optional<Node> findDeclarator(NameExpr nameExpr) {
        final DeclarationCollector collector = new DeclarationCollector();
        Optional<VariableDeclarator> localVarDeclarator = collector.collectLocalVarsDeclared(nameExpr).stream()
            .filter(var -> var.getName().equals(nameExpr.getName()))
            .findFirst();
        if(localVarDeclarator.isPresent()) {
            return Optional.of(localVarDeclarator.get());
        }
        Optional<Parameter> parameter = collector.collectParameters(nameExpr).stream()
            .filter(para -> para.getName().equals(nameExpr.getName()))
            .findFirst();
        if(parameter.isPresent()) {
            return Optional.of(parameter.get());
        };
        Optional<VariableDeclarator> fieldDeclarator = collector.collectFileds(nameExpr).stream()
            .filter(field -> field.getName().equals(nameExpr.getName()))
            .findFirst();
        if(fieldDeclarator.isPresent()) {
            return Optional.of(fieldDeclarator.get());
        }
        return Optional.empty();
    }

    private ValueFeature extractTypeFeature(Type type, ValueFeature feature) {
        if(type instanceof PrimitiveType) {
            final PrimitiveType primitive = (PrimitiveType) type;
            if(primitive.getType() == Primitive.BOOLEAN) {
                feature.type = ValueType.BOOLEAN;
            }
            if(primitive.getType() == Primitive.INT || primitive.getType() == Primitive.DOUBLE ||
                primitive.getType() == Primitive.INT || primitive.getType() == Primitive.FLOAT) {
                feature.type = ValueType.NUM;
            }
        }
        if(type instanceof ClassOrInterfaceType) {
            final ClassOrInterfaceType classOrInterface = (ClassOrInterfaceType) type;
            feature.type = ValueType.OBJECT;
            if(classOrInterface.getNameAsString().equals("String")) {
                feature.type = ValueType.STRING;
            }
        }
        return feature;
    }

    private ValueFeature extractContextFeature(Node variable, ValueFeature feature) {
        if (variable.findParent(IfStmt.class).isPresent()) {
            feature.condition = true;
        }
        final boolean inForStmt = variable.findParent(ForStmt.class).isPresent();
        final boolean inForeachStmt = variable.findParent(ForeachStmt.class).isPresent();
        final boolean inWhileStmt = variable.findParent(WhileStmt.class).isPresent();
        if (inForStmt || inForeachStmt || inWhileStmt) {
            feature.condition = true;
        }
        return feature;
    }

    private ValueFeature extractScopeFeature(Node declarator, ValueFeature feature) {
        feature.scope = Scope.FIELD;
        if (declarator.findParent(MethodDeclaration.class).isPresent()) {
            feature.scope = Scope.LOCAL;
            if (declarator instanceof Parameter) {
                feature.scope = Scope.ARGUMENT;
            }
        }

        if (declarator.getParentNode().get().toString().startsWith("final")) {
            feature.constant = true;
        }
        return feature;
    }

    public List<ValueFeature> extract(Node node) {
        final List<ValueFeature> features = new ArrayList<>();
        final List<NameExpr> nameExprs = node.findAll(NameExpr.class);
        for (NameExpr nameExpr: nameExprs) {
            final ValueFeature feature = new ValueFeature();
            Node declarator = findDeclarator(nameExpr).get();
            this.extractScopeFeature(declarator, feature);
            Type type = declarator.findFirst(Type.class).get();
            this.extractTypeFeature(type, feature);
            this.extractContextFeature(nameExpr, feature);
            features.add(feature);
        }
        final List<Node> declarators = node.findAll(VariableDeclarator.class).stream()
            .map(v -> (Node)v)
            .collect(Collectors.toList());
        declarators.addAll(node.findAll(Parameter.class).stream()
            .map(p -> (Node)p)
            .collect(Collectors.toList())
        );
        for (Node declarator : declarators) {
            final ValueFeature feature = new ValueFeature();
            this.extractScopeFeature(declarator, feature);
            Type type = declarator.findFirst(Type.class).get();
            this.extractTypeFeature(type, feature);
            this.extractContextFeature(declarator, feature);
            features.add(feature);
        }
        return features;
    }
}