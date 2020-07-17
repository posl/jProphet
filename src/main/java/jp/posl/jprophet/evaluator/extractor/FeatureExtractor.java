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
import jp.posl.jprophet.evaluator.extractor.feature.*;
import jp.posl.jprophet.evaluator.extractor.feature.ModFeature.ModType;
import jp.posl.jprophet.evaluator.extractor.feature.StatementFeature.StatementType;
import jp.posl.jprophet.evaluator.extractor.feature.VariableFeature.VarType;
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

        final ModFeatureExtractor modFeatureExtractor = new ModFeatureExtractor();
        final Map<ProgramChunk,ModFeature> modFeatureMap = modFeatureExtractor.extract(nodeWithDiffType, chunks);
        final FeatureVector vector = new FeatureVector();

        final Set<Entry<ProgramChunk, ModFeature>> set = modFeatureMap.entrySet();
        set.stream()
            .forEach(entry -> {
                final ProgramChunk fixedChunk = entry.getKey();
                final ModFeature modFeature = entry.getValue();
                // 変更の特徴抽出
                this.extractModFeature(fixedChunk, modFeature, vector, fixedRoot);

                // 変数の特徴抽出
                this.extractVariableFeature(fixedChunk, modFeature, vector, originalRoot, fixedRoot);
            });

        return vector;
    }

    private void extractModFeature(ProgramChunk fixedChunk, ModFeature modFeature, FeatureVector vector, Node fixedRoot) {
        final StatementFeatureExtractor stmtFeatureExtractor = new StatementFeatureExtractor();

        modFeature.getTypes().stream()
            .forEach(modType -> vector.add(modType));

        final List<Node> allFixedNodes = NodeUtility.getAllNodesInDepthFirstOrder(fixedRoot);
        final List<Node> allStmts = allFixedNodes.stream()
            .filter(node -> stmtFeatureExtractor.extract(node).isPresent())
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
            StatementType stmtType = stmtFeatureExtractor.extract(stmt).orElseThrow();
            for (ModType modType: modFeature.getTypes()) {
                vector.add(pos, stmtType, modType);
            }
        });
    }

    private void extractVariableFeature(ProgramChunk fixedChunk, ModFeature modFeature, FeatureVector vector, Node originalRoot, Node fixedRoot) {
        final VariableFeatureExtractor varFeatureExtractor = new VariableFeatureExtractor();
        final List<NameExpr> originalVariables = originalRoot.findAll(NameExpr.class);
        final List<NameExpr> fixedVariables = fixedRoot.findAll(NameExpr.class);
        for(NameExpr fixedVar: fixedVariables) {
            if(!varFeatureExtractor.findDeclarator(fixedVar).isPresent()) {
                continue;
            }
            final Node fixedVarDec = varFeatureExtractor.findDeclarator(fixedVar).orElseThrow();
            final List<NameExpr> originalVarsWithSameName = originalVariables.stream()
                .filter(original -> original.getNameAsString().equals(fixedVar.getNameAsString()))
                .collect(Collectors.toList());
            final List<NameExpr> originalVarsWithSameScope = originalVarsWithSameName.stream()
                .filter(original-> {
                    final Optional<Node> declarator = varFeatureExtractor.findDeclarator(original);
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
        final VariableFeatureExtractor varFeatureExtractor = new VariableFeatureExtractor();
        final VariableFeature originalVarFeature = varFeatureExtractor.extract(originalVar);
        final VariableFeature fixedVarFeature = varFeatureExtractor.extract(fixedVar);
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
        for (VarType originalVarType: originalVarFeature.getTypes()) {
            for (VarType fixedVarType: fixedVarFeature.getTypes()) {
                vector.add(pos, originalVarType, fixedVarType);
            }
        }
        return vector;
    }

}