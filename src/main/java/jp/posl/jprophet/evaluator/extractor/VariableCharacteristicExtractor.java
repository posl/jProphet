package jp.posl.jprophet.evaluator.extractor;

import java.util.List;
import java.util.Optional;

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

import jp.posl.jprophet.evaluator.extractor.VariableCharacteristics;
import jp.posl.jprophet.evaluator.extractor.VariableCharacteristics.VarChar;
import jp.posl.jprophet.operation.DeclarationCollector;;

/**
 * 修正パッチの変数の種類抽出を行うクラス
 */
public class VariableCharacteristicExtractor {
    /**
     * 参照されている変数の特徴を抽出する
     * @param variable 変数のノード
     * @return 変数の特徴
     */
    public VariableCharacteristics extract(NameExpr variable) {
        final VariableCharacteristics varChars = new VariableCharacteristics();
        findDeclarator(variable).ifPresent((declarator) -> {
            varChars.add(this.extractCharsOfScope(declarator));
            declarator.findFirst(Type.class).ifPresent((type) -> {
                varChars.add(this.extractCharsOfType(type));
            });
        });
        varChars.add(this.extractCharsOfContext(variable));
        varChars.add(this.extractCharsOfOperation(variable));
        return varChars;
    }
    

    /**
     * 変数の宣言ノードを探索する
     * @param variable 宣言ノードを探索したい変数のノード
     * @return 宣言ノード，存在しない場合empty()を返す
     */
    public Optional<Node> findDeclarator(NameExpr variable) {
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
        }
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
    private VariableCharacteristics extractCharsOfType(Type type) {
        final VariableCharacteristics chars = new VariableCharacteristics();
        if(type instanceof PrimitiveType) {
            final PrimitiveType primitive = (PrimitiveType) type;
            if(primitive.getType() == Primitive.BOOLEAN) {
                chars.add(VarChar.BOOLEAN);
            }
            if(primitive.getType() == Primitive.INT || primitive.getType() == Primitive.DOUBLE ||
                    primitive.getType() == Primitive.LONG || primitive.getType() == Primitive.SHORT ||
                    primitive.getType() == Primitive.FLOAT) {
                chars.add(VarChar.NUM);
            }
        }
        if(type instanceof ClassOrInterfaceType) {
            chars.add(VarChar.OBJECT);
            final ClassOrInterfaceType classOrInterface = (ClassOrInterfaceType) type;
            if(classOrInterface.getNameAsString().equals("String")) {
                chars.add(VarChar.STRING);
            }
        }
        return chars;
    }

    /**
     * 変数がif文やループなど構文上においてどこに位置するかという特徴
     * @param variable 変数ノード
     * @return 変数の特徴
     */
    private VariableCharacteristics extractCharsOfContext(Node variable) {
        final VariableCharacteristics chars = new VariableCharacteristics();
        if (variable.findParent(IfStmt.class).isPresent()) {
            chars.add(VarChar.IN_IF_STMT);
            final Expression condition = variable.findParent(IfStmt.class).get().getCondition();
            if(condition.equals(variable)) {
                chars.add(VarChar.IN_CONDITION);
            }
        }
        final boolean inForStmt = variable.findParent(ForStmt.class).isPresent();
        final boolean inForeachStmt = variable.findParent(ForeachStmt.class).isPresent();
        final boolean inWhileStmt = variable.findParent(WhileStmt.class).isPresent();
        if (inForStmt || inForeachStmt || inWhileStmt) {
            chars.add(VarChar.IN_LOOP);
        }
        if (variable.findParent(MethodCallExpr.class).isPresent()) {
            chars.add(VarChar.PARAMETER);   
        }
        if (variable.findParent(AssignExpr.class).isPresent()) {
            chars.add(VarChar.IN_ASSIGN_STMT);   
        }
        return chars;
    }

    /**
     * 変数の被演算子としての特徴を抽出
     * @param variable 変数ノード
     * @return 変数の特徴
     */
    private VariableCharacteristics extractCharsOfOperation(Node variable) {
        final VariableCharacteristics chars = new VariableCharacteristics();
        if(variable.findParent(BinaryExpr.class).isPresent()) {
            final BinaryExpr binaryExpr = variable.findParent(BinaryExpr.class).get();
            final List<String> commutativeOpRepresentations = List.of("+", "*", "==", "!=", "||", "&&");
            final boolean isCommutativeOperand = commutativeOpRepresentations.stream()
                .anyMatch(op -> binaryExpr.getOperator().asString().equals(op));
            if (isCommutativeOperand) {
                chars.add(VarChar.COMMUTATIVE_OPERAND);
            }
            final List<String> noncommutativeOpRepresentations = List.of("-", "/", "%", "<", ">", "<=", ">=");
            final boolean isLeftNoncommutativeOperand = noncommutativeOpRepresentations.stream()
                .anyMatch(op -> binaryExpr.getOperator().asString().equals(op) && binaryExpr.getLeft().containsWithin(variable));
            if (isLeftNoncommutativeOperand) {
                chars.add(VarChar.NONCOMMUTATIVE_OPERAND_LEFT);
            }
            final boolean isRightNoncommutativeOperand = noncommutativeOpRepresentations.stream()
                .anyMatch(op -> binaryExpr.getOperator().asString().equals(op) && binaryExpr.getRight().containsWithin(variable));
            if (isRightNoncommutativeOperand) {
                chars.add(VarChar.NONCOMMUTATIVE_OPERAND_RIGHT);
            }
        }
        if(variable.findParent(UnaryExpr.class).isPresent()) {
                chars.add(VarChar.UNARY_OPERAND);
        }
        return chars;
    }

    /**
     * 宣言ノードを元に変数のスコープを特定
     * @param declarator
     * @return 変数の特徴
     */
    private VariableCharacteristics extractCharsOfScope(Node declarator) {
        final VariableCharacteristics chars = new VariableCharacteristics();
        if (declarator.findParent(MethodDeclaration.class).isPresent()) {
            if (declarator instanceof Parameter) {
                chars.add(VarChar.ARGUMENT);
            }
            else {
                chars.add(VarChar.LOCAL);
            }
        }
        else {
            chars.add(VarChar.FIELD);
        }

        if (declarator.getParentNode().get().toString().startsWith("final")) {
            chars.add(VarChar.CONSTANT);
        }
        return chars;
    }
}
