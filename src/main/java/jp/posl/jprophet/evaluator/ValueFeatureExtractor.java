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

import jp.posl.jprophet.evaluator.ValueFeatureVec.Scope;
import jp.posl.jprophet.evaluator.ValueFeatureVec.ValueType;
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

    private ValueFeatureVec extractTypeFeature(Type type, ValueFeatureVec vec) {
        if(type instanceof PrimitiveType) {
            final PrimitiveType primitive = (PrimitiveType) type;
            if(primitive.getType() == Primitive.BOOLEAN) {
                vec.type = ValueType.BOOLEAN;
            }
            if(primitive.getType() == Primitive.INT || primitive.getType() == Primitive.DOUBLE ||
                primitive.getType() == Primitive.INT || primitive.getType() == Primitive.FLOAT) {
                vec.type = ValueType.NUM;
            }
        }
        if(type instanceof ClassOrInterfaceType) {
            final ClassOrInterfaceType classOrInterface = (ClassOrInterfaceType) type;
            vec.type = ValueType.OBJECT;
            if(classOrInterface.getNameAsString().equals("String")) {
                vec.type = ValueType.STRING;
            }
        }
        return vec;
    }

    private ValueFeatureVec extractContextFeature(Node variable, ValueFeatureVec vec) {
        if (variable.findParent(IfStmt.class).isPresent()) {
            vec.condition = true;
        }
        final boolean inForStmt = variable.findParent(ForStmt.class).isPresent();
        final boolean inForeachStmt = variable.findParent(ForeachStmt.class).isPresent();
        final boolean inWhileStmt = variable.findParent(WhileStmt.class).isPresent();
        if (inForStmt || inForeachStmt || inWhileStmt) {
            vec.condition = true;
        }
        return vec;
    }

    private ValueFeatureVec extractScopeFeature(Node declarator, ValueFeatureVec vec) {
        vec.scope = Scope.FIELD;
        if (declarator.findParent(MethodDeclaration.class).isPresent()) {
            vec.scope = Scope.LOCAL;
            if (declarator instanceof Parameter) {
                vec.scope = Scope.ARGUMENT;
            }
        }

        if (declarator.getParentNode().get().toString().startsWith("final")) {
            vec.constant = true;
        }
        return vec;
    }

    public List<ValueFeatureVec> extract(Node node) {
        final List<ValueFeatureVec> vecs = new ArrayList<>();
        final List<NameExpr> nameExprs = node.findAll(NameExpr.class);
        for (NameExpr nameExpr: nameExprs) {
            final ValueFeatureVec vec = new ValueFeatureVec();
            Node declarator = findDeclarator(nameExpr).get();
            this.extractScopeFeature(declarator, vec);
            Type type = declarator.findFirst(Type.class).get();
            this.extractTypeFeature(type, vec);
            this.extractContextFeature(nameExpr, vec);
            vecs.add(vec);
        }
        final List<Node> declarators = node.findAll(VariableDeclarator.class).stream()
            .map(v -> (Node)v)
            .collect(Collectors.toList());
        declarators.addAll(node.findAll(Parameter.class).stream()
            .map(p -> (Node)p)
            .collect(Collectors.toList())
        );
        for (Node declarator : declarators) {
            final ValueFeatureVec vec = new ValueFeatureVec();
            this.extractScopeFeature(declarator, vec);
            Type type = declarator.findFirst(Type.class).get();
            this.extractTypeFeature(type, vec);
            this.extractContextFeature(declarator, vec);
            vecs.add(vec);
        }
        return vecs;
    }
}