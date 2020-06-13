package jp.posl.jprophet.evaluator;

import java.util.List;
import java.util.Map;

import com.github.javaparser.ast.Node;

import jp.posl.jprophet.patch.DefaultPatchCandidate;

public class FeatureExtractor {
    public FeatureVector extract(DefaultPatchCandidate patch) {
        Node originalRoot = patch.getOriginalCompilationUnit().findRootNode();
        Node fixedRoot = patch.getCompilationUnit().findRootNode();
        final AstDiff diff = new AstDiff();
        final NodeWithDiffType nodeWithDiffType = diff.createRevisedAstWithDiffType(originalRoot, fixedRoot);
        final List<ProgramChank> chanks = nodeWithDiffType.identifyModifiedProgramChanks();

        final ModFeatureExtractor modFeatureExtractor = new ModFeatureExtractor();
        final Map<ProgramChank,ModFeature> modFeatureMap = modFeatureExtractor.extract(nodeWithDiffType, chanks);
        final StatementFeatureExtractor stmtFeatureExtractorForOriginal = new StatementFeatureExtractor(originalRoot);
        final StatementFeatureExtractor stmtFeatureExtractorForFixed = new StatementFeatureExtractor(fixedRoot);

        return new FeatureVector();
    }
}