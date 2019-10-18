package jp.posl.jprophet.operation;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.expr.SimpleName;
import com.github.javaparser.ast.expr.ThisExpr;

import jp.posl.jprophet.RepairUnit;

/**
 * 対象ステートメント中の変数を別のもので置き換える操作
 */
public class VariableReplacementOperation implements AstOperation {
    private final RepairUnit repairUnit;
    private final Node targetNode;
    private final int varNameIndexInSimpleName = 1;
    private final List<String> fieldNames;
    private final List<String> localVarNames;

    public VariableReplacementOperation(final RepairUnit repairUnit){
        this.repairUnit = repairUnit;
        this.targetNode = repairUnit.getTargetNode();
        this.fieldNames = collectFieldNames(repairUnit);
        this.localVarNames = collectLocalVarNames(repairUnit);
    }
    

    private List<String> collectFieldNames(final RepairUnit repairUnit){
        ClassOrInterfaceDeclaration classNode;
        try {
            classNode = this.targetNode.findParent(ClassOrInterfaceDeclaration.class).orElseThrow();
        }
        catch (NoSuchElementException e) {
            return new ArrayList<String>();
        }
        final List<FieldDeclaration> fields = classNode.findAll(FieldDeclaration.class);
        final List<String> fieldNames = fields.stream()
                                              .map(field -> field.findAll(SimpleName.class).get(varNameIndexInSimpleName).asString())
                                              .collect(Collectors.toList());
        
        return fieldNames;
    }

    private List<String> collectLocalVarNames(final RepairUnit repairUnit){
        MethodDeclaration methodNode;
        try {
            methodNode =  this.targetNode.findParent(MethodDeclaration.class).orElseThrow();
        }
        catch (NoSuchElementException e) {
            return new ArrayList<String>();
        }
        final List<VariableDeclarationExpr> localVars = methodNode.findAll(VariableDeclarationExpr.class);
        final List<String> localVarNames = localVars.stream()
                                                    .map(localVar -> localVar.findAll(SimpleName.class).get(varNameIndexInSimpleName).asString())
                                                    .collect(Collectors.toList());
        return localVarNames;
    }

    private List<RepairUnit> replaceAssignExprWith(List<String> varNames, Function<String, Expression> constructExpr){
        List<RepairUnit> candidates = new ArrayList<RepairUnit>();
        for(String varName: varNames){
            RepairUnit newCandidate = RepairUnit.copy(this.repairUnit);
            ((AssignExpr) newCandidate.getTargetNode()).setValue(constructExpr.apply(varName));
            candidates.add(newCandidate);
        }

        return candidates;        
    }


    private List<RepairUnit> replaceArgsWith(List<String> varNames, Function<String, Expression> constructExpr){
        final int argc = ((MethodCallExpr)(this.targetNode)).getArguments().size(); 
        List<RepairUnit> candidates = new ArrayList<RepairUnit>();

        for(String varName: varNames){
            for(int i = 0; i < argc; i++){
                RepairUnit newCandidate = RepairUnit.copy(this.repairUnit);
                MethodCallExpr methodCallExpr = (MethodCallExpr)newCandidate.getTargetNode();
                methodCallExpr.setArgument(i, constructExpr.apply(varName));
                candidates.add(newCandidate);
            }
        }

        return candidates; 
    }


    public List<RepairUnit> exec() {
        List<RepairUnit> candidates = new ArrayList<RepairUnit>();
        Function<String, Expression> constructField = fieldName -> new FieldAccessExpr(new ThisExpr(), fieldName);
        Function<String, Expression> constructLocalVar = localVarName -> new NameExpr(localVarName);

        if (targetNode instanceof AssignExpr) {
            List<RepairUnit> candidatesWithAssignExprReplacedByFields = this.replaceAssignExprWith(this.fieldNames, constructField);
            List<RepairUnit> candidatesWithAssignExprReplacedByLocalVars = this.replaceAssignExprWith(this.localVarNames, constructLocalVar);
            candidates.addAll(candidatesWithAssignExprReplacedByFields);
            candidates.addAll(candidatesWithAssignExprReplacedByLocalVars);
        }
        if (targetNode instanceof MethodCallExpr){
            List<RepairUnit> candidatesWithArgsReplacedByFields = this.replaceArgsWith(this.fieldNames, constructField);
            List<RepairUnit> candidatesWithArgsReplacedByLocalVars = this.replaceArgsWith(this.localVarNames, constructLocalVar);
            candidates.addAll(candidatesWithArgsReplacedByFields);
            candidates.addAll(candidatesWithArgsReplacedByLocalVars);
        }

        return candidates;
    }
}