package jp.posl.jprophet.evaluator.extractor;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.NameExpr;

import jp.posl.jprophet.NodeUtility;
import jp.posl.jprophet.evaluator.AstDiff;
import jp.posl.jprophet.evaluator.NodeWithDiffType;
import jp.posl.jprophet.evaluator.ProgramChunk;
import jp.posl.jprophet.evaluator.extractor.StatementKindExtractor.StatementType;
import jp.posl.jprophet.evaluator.extractor.feature.*;
import jp.posl.jprophet.evaluator.extractor.feature.ModKinds.ModKind;
import jp.posl.jprophet.evaluator.extractor.feature.VariableKinds.VarKind;
import jp.posl.jprophet.patch.PatchCandidate;

public class FeatureExtractor {

    public enum StatementPos {
        TARGET,
        PREV,
        NEXT
    }

    public FeatureVector extract(PatchCandidate patch) {
        final Node originalRoot = patch.getOriginalCompilationUnit().findRootNode();
        final Node fixedRoot = patch.getCompilationUnit().findRootNode();
        final AstDiff diff = new AstDiff();
        final NodeWithDiffType nodeWithDiffType = diff.createRevisedAstWithDiffType(originalRoot, fixedRoot);
        final List<ProgramChunk> chunks = nodeWithDiffType.identifyModifiedProgramChunks();

        final ModKindExtractor modKindExtractor = new ModKindExtractor();
        final Map<ProgramChunk,ModKinds> modKindMap = modKindExtractor.extract(nodeWithDiffType, chunks);
        final FeatureVector vector = new FeatureVector();

        final Set<Entry<ProgramChunk, ModKinds>> set = modKindMap.entrySet();
        set.stream()
            .forEach(entry -> {
                final ProgramChunk fixedChunk = entry.getKey();
                final ModKinds modKind = entry.getValue();
                // 変更の特徴抽出
                this.extractModFeature(fixedChunk, modKind, vector, fixedRoot);

                // 変数の特徴抽出
                this.extractVariableFeature(fixedChunk, modKind, vector, originalRoot, fixedRoot);
            });

        return vector;
    }

    private void extractModFeature(ProgramChunk fixedChunk, ModKinds modKind, FeatureVector vector, Node fixedRoot) {
        final StatementKindExtractor stmtKindExtractor = new StatementKindExtractor();

        modKind.getTypes().stream()
            .forEach(modType -> vector.add(modType));

        final List<Node> allFixedNodes = NodeUtility.getAllNodesInDepthFirstOrder(fixedRoot);
        final List<Node> allStmts = allFixedNodes.stream()
            .filter(node -> stmtKindExtractor.extract(node).isPresent())
            .collect(Collectors.toList());

        allStmts.stream().forEach(stmt -> {
            final int stmtBegin = stmt.getBegin().orElseThrow().line;
            final boolean nodeComesBeforeChunk = stmtBegin < fixedChunk.getBegin(); 
            final boolean nodeComesAfterChunk = fixedChunk.getEnd() < stmtBegin; 
            StatementPos pos = StatementPos.TARGET;
            if (nodeComesBeforeChunk) {
                pos = StatementPos.PREV;
            }
            if (nodeComesAfterChunk) {
                pos = StatementPos.NEXT;
            }
            StatementType stmtType = stmtKindExtractor.extract(stmt).orElseThrow();
            for (ModKind modType: modKind.getTypes()) {
                vector.add(pos, stmtType, modType);
            }
        });
    }

    private void extractVariableFeature(ProgramChunk fixedChunk, ModKinds modKind, FeatureVector vector, Node originalRoot, Node fixedRoot) {
        final VariableKindExtractor varKindExtractor = new VariableKindExtractor();
        final List<NameExpr> originalVariables = originalRoot.findAll(NameExpr.class);
        final List<NameExpr> fixedVariables = fixedRoot.findAll(NameExpr.class);
        for(NameExpr fixedVar: fixedVariables) {
            if(!varKindExtractor.findDeclarator(fixedVar).isPresent()) {
                continue;
            }
            final Node fixedVarDec = varKindExtractor.findDeclarator(fixedVar).orElseThrow();
            final List<NameExpr> originalVarsWithSameName = originalVariables.stream()
                .filter(original -> original.getNameAsString().equals(fixedVar.getNameAsString()))
                .collect(Collectors.toList());
            final List<NameExpr> originalVarsWithSameScope = originalVarsWithSameName.stream()
                .filter(original-> {
                    final Optional<Node> declarator = varKindExtractor.findDeclarator(original);
                    if(!declarator.isPresent()) {
                        return false;
                    }
                    final Node originalVarDec = declarator.orElseThrow();
                    final boolean originalVarIsNotField = originalVarDec.findParent(MethodDeclaration.class).isPresent();
                    final boolean fixedVarIsNotField = fixedVarDec.findParent(MethodDeclaration.class).isPresent();
                    final boolean bothIsField = !originalVarIsNotField && !fixedVarIsNotField;
                    if(bothIsField) return true;
                    if(!bothIsField) {
                        final String methodNameWhereOriginalVarWasDeclared = originalVarDec.findParent(MethodDeclaration.class).orElseThrow().getNameAsString();
                        final String methodNameWhereFixedlVarWasDeclared = fixedVarDec.findParent(MethodDeclaration.class).orElseThrow().getNameAsString();
                        if(methodNameWhereOriginalVarWasDeclared.equals(methodNameWhereFixedlVarWasDeclared)) {
                            return true;
                        }
                    } 
                    return false;
                })
                .collect(Collectors.toList());
            if(originalVarsWithSameScope.size() > 0) {
                final NameExpr sameVarAsFixedVar = originalVarsWithSameScope.get(0);
                this.hoge(fixedChunk, sameVarAsFixedVar, fixedVar, vector);
            }
            else if(originalVarsWithSameName.size() > 0) {
                final NameExpr sameVarAsFixedVar = originalVarsWithSameName.get(0);
                this.hoge(fixedChunk, sameVarAsFixedVar, fixedVar, vector);
            }
        }
    }

    private FeatureVector hoge(ProgramChunk fixedChunk, NameExpr originalVar, NameExpr fixedVar, FeatureVector vector) {
        final VariableKindExtractor varKindExtractor = new VariableKindExtractor();
        final VariableKinds originalVarKind = varKindExtractor.extract(originalVar);
        final VariableKinds fixedVarKind = varKindExtractor.extract(fixedVar);
        final int nodeBegin = fixedVar.getBegin().orElseThrow().line;
        final boolean nodeComesBeforeChunk = nodeBegin < fixedChunk.getBegin(); 
        final boolean nodeComesAfterChunk = fixedChunk.getEnd() < nodeBegin; 
        StatementPos pos = StatementPos.TARGET;
        if (nodeComesBeforeChunk) {
            pos = StatementPos.PREV;
        }
        if (nodeComesAfterChunk) {
            pos = StatementPos.NEXT;
        }
        for (VarKind originalVarType: originalVarKind.getTypes()) {
            for (VarKind fixedVarType: fixedVarKind.getTypes()) {
                vector.add(pos, originalVarType, fixedVarType);
            }
        }
        return vector;
    }

}