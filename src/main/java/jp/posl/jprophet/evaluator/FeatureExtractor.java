package jp.posl.jprophet.evaluator;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.stmt.Statement;

import jp.posl.jprophet.NodeUtility;
import jp.posl.jprophet.evaluator.StatementFeature.StatementType;
import jp.posl.jprophet.evaluator.VariableFeature.VarType;
import jp.posl.jprophet.patch.PatchCandidate;

public class FeatureExtractor {

    public enum StatementPos {
        TARGET,
        PREV,
        NEXT
    }

    static class Statements {
        Map<Node, StatementType> statementMaps;
        StatementPos pos;
        public Statements(Map<Node, StatementType> statementMaps, StatementPos pos) {
            this.statementMaps = statementMaps;
            this.pos   = pos;
        }
    }

    public FeatureVector extract(PatchCandidate patch) {
        final Node originalRoot = patch.getOriginalCompilationUnit().findRootNode();
        final Node fixedRoot = patch.getCompilationUnit().findRootNode();
        final AstDiff diff = new AstDiff();
        final NodeWithDiffType nodeWithDiffType = diff.createRevisedAstWithDiffType(originalRoot, fixedRoot);
        final List<ProgramChunk> chunks = nodeWithDiffType.identifyModifiedProgramChunks();

        final ModFeatureExtractor modFeatureExtractor = new ModFeatureExtractor();
        final Map<ProgramChunk,ModFeature> modFeatureMap = modFeatureExtractor.extract(nodeWithDiffType, chunks);
        final StatementFeatureExtractor stmtFeatureExtractor = new StatementFeatureExtractor();
        final VariableFeatureExtractor varFeatureExtractor = new VariableFeatureExtractor();
        final List<NameExpr> originalVariables = originalRoot.findAll(NameExpr.class);
        final List<NameExpr> fixedVariables = fixedRoot.findAll(NameExpr.class);
        final FeatureVector vector = new FeatureVector();

        final Set<ProgramChunk> set = modFeatureMap.keySet();
        set.stream()
            .forEach(chunk -> {
				final ModFeature modFeature = modFeatureMap.get(chunk);
                final List<Node> allFixedNodes = NodeUtility.getAllNodesInDepthFirstOrder(fixedRoot);
                final Map<Node, StatementType> targetStmtMaps = allFixedNodes.stream()
                    .filter(node -> {
                        final int nodeBegin = node.getBegin().orElseThrow().line;
                        final boolean nodeWithinChunk = chunk.getBegin() <= nodeBegin && nodeBegin <= chunk.getEnd(); 
                        final boolean nodeIsStmt = stmtFeatureExtractor.extract(node).isPresent();
                        if(nodeWithinChunk && nodeIsStmt) {
                            return true;
                        }
                        return false;
                    })
                    .collect(Collectors.toMap(
                        stmt -> stmt, stmt -> stmtFeatureExtractor.extract(stmt).orElseThrow()
                    ));

                final List<Node> prevStmts = allFixedNodes.stream()
                    .filter(node -> {
                        final int nodeBegin = node.getBegin().orElseThrow().line;
                        final boolean nodeComesBeforeChunk = nodeBegin < chunk.getBegin(); 
                        final boolean nodeIsStmt = stmtFeatureExtractor.extract(node).isPresent();
                        if(nodeComesBeforeChunk && nodeIsStmt) {
                            return true;
                        }
                        return false;
                    })
                    .collect(Collectors.toList());
                Map<Node, StatementType> prev3StmtMaps;
                if (prevStmts.size() >= 3) {
                    prev3StmtMaps = prevStmts.subList(prevStmts.size() - 3, prevStmts.size()).stream()
                        .collect(Collectors.toMap(
                            stmt -> stmt, stmt -> stmtFeatureExtractor.extract(stmt).orElseThrow()
                        ));
                }
                else {
                    prev3StmtMaps = prevStmts.stream()
                        .collect(Collectors.toMap(
                            stmt -> stmt, stmt -> stmtFeatureExtractor.extract(stmt).orElseThrow()
                        ));
                }

                final List<Node> nextStmts = allFixedNodes.stream()
                    .filter(node -> {
                        final int nodeBegin = node.getBegin().orElseThrow().line;
                        final boolean nodeComesAfterChunk = chunk.getEnd() < nodeBegin; 
                        final boolean nodeIsStmt = stmtFeatureExtractor.extract(node).isPresent();
                        if(nodeComesAfterChunk && nodeIsStmt) {
                            return true;
                        }
                        return false;
                    })
                    .collect(Collectors.toList());
                Map<Node, StatementType> next3StmtMaps;
                if (nextStmts.size() > 3) {
                    next3StmtMaps = nextStmts.subList(0, 3).stream()
                        .collect(Collectors.toMap(
                            stmt -> stmt, stmt -> stmtFeatureExtractor.extract(stmt).orElseThrow()
                        ));
                }
                else {
                    next3StmtMaps = nextStmts.stream()
                        .collect(Collectors.toMap(
                            stmt -> stmt, stmt -> stmtFeatureExtractor.extract(stmt).orElseThrow()
                        ));
                }

                List<Statements> statementChunks = List.of(
                    new Statements(targetStmtMaps, StatementPos.TARGET),
                    new Statements(prev3StmtMaps,  StatementPos.PREV),
                    new Statements(next3StmtMaps,  StatementPos.NEXT)
                );
                statementChunks.stream().forEach(stmtChunk -> {
                    stmtChunk.statementMaps.forEach((stmt, stmtType) -> {
                        modFeature.getTypes().stream()
                            .forEach(modType -> vector.add(stmtChunk.pos, stmtType, modType));

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
                                VariableFeature originalVarFeature = varFeatureExtractor.extract(fixedVar);
                                VariableFeature fixedVarFeature = varFeatureExtractor.extract(sameVarAsFixedVar);
                                for (VarType originalVarType: originalVarFeature.getTypes()) {
                                    for (VarType fixedVarType: fixedVarFeature.getTypes()) {
                                        vector.add(StatementPos.TARGET, originalVarType, fixedVarType);
                                    }
                                }
                            }
                            else if(originalVarsWithSameName.size() > 0) {
                                final NameExpr sameVarAsFixedVar = originalVarsWithSameName.get(0);
                                final VariableFeature originalVarFeature = varFeatureExtractor.extract(fixedVar);
                                final VariableFeature fixedVarFeature = varFeatureExtractor.extract(sameVarAsFixedVar);
                                for (VarType originalVarType: originalVarFeature.getTypes()) {
                                    for (VarType fixedVarType: fixedVarFeature.getTypes()) {
                                        vector.add(StatementPos.TARGET, originalVarType, fixedVarType);
                                    }
                                }
                            }
                        }
                    });
                });

            });

        return vector;
    }

}