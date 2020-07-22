package jp.posl.jprophet.learning;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.github.javaparser.ast.Node;

import difflib.Delta;
import jp.posl.jprophet.NodeUtility;
import jp.posl.jprophet.PatchCandidateGenerator;
import jp.posl.jprophet.evaluator.AstDiff;
import jp.posl.jprophet.evaluator.extractor.FeatureExtractor;
import jp.posl.jprophet.evaluator.extractor.FeatureVector;
import jp.posl.jprophet.patch.DefaultPatch;
import jp.posl.jprophet.patch.Patch;

public class Learner {

    static class TrainingCase {
        final public List<Boolean> binaryVectors;
        final public boolean isPositiveCase;

        public TrainingCase(List<Boolean> binaryVectors, boolean isPositiveCase) {
            this.binaryVectors = binaryVectors;
            this.isPositiveCase = isPositiveCase;
        }
    }

    public List<TrainingCase> generateTrainingCase(TrainingConfig config) {
        final Path dirPath = Paths.get(config.getDirPath());
        List<Path> trainingCasePaths;
        try {
            trainingCasePaths = Files.list(dirPath).collect(Collectors.toList());
        } catch (IOException e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
            return Collections.emptyList();
        }
        final List<Patch> patches = new ArrayList<Patch>();
        for (Path path: trainingCasePaths) {
            if (Files.isDirectory(path)) {
                try {
                    final Optional<Path> originalFilePath = Files.list(path)
                        .filter(filePath -> filePath.getFileName().toString().equals(config.getOriginalFileName()))
                        .findFirst();
                    final Optional<Path> fixedFilePath = Files.list(path)
                        .filter(filePath -> filePath.getFileName().toString().equals(config.getFixedFileName()))
                        .findFirst();
                    if (originalFilePath.isPresent() && !fixedFilePath.isPresent()) {
                        final String originalSourceCode = String.join("\n", Files.readAllLines(originalFilePath.orElseThrow()));
                        final String fixedSourceCode = String.join("\n", Files.readAllLines(fixedFilePath.orElseThrow()));
                        patches.add(new DefaultPatch(originalSourceCode, fixedSourceCode));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        final FeatureExtractor extractor = new FeatureExtractor();
        final PatchCandidateGenerator patchGenerator = new PatchCandidateGenerator();
        final List<TrainingCase> trainingCases = new ArrayList<TrainingCase>();
        for (Patch patch : patches) {
            trainingCases.add(new TrainingCase(extractor.extract(patch).get(), true));
            final Node originalRootNode = patch.getOriginalCompilationUnit().findRootNode();
            final Node fixedRootNode = patch.getCompilationUnit().findRootNode();
            
            final List<Node> allNodesForPatchGeneration = this.getAllPatchedNodes(originalRootNode, fixedRootNode);
            final List<Patch> allGeneratedPatches = allNodesForPatchGeneration.stream()
                .flatMap(node -> patchGenerator.applyTemplate(node, config.getOperations()).stream())
                .map(result -> result.getCompilationUnit())
                .map(cu -> new DefaultPatch(patch.getOriginalCompilationUnit(), cu))
                .collect(Collectors.toList());
            final List<FeatureVector> vectorsByGeneratedPatches = allGeneratedPatches.stream().map(extractor::extract).collect(Collectors.toList());
            vectorsByGeneratedPatches.stream().forEach(vector -> trainingCases.add(new TrainingCase(vector.get(), false)));
        }
        return trainingCases;
    }

    private List<Node> getAllPatchedNodes(Node original, Node revised) {
        final List<Node> allOriginalNodes = NodeUtility.getAllNodesInDepthFirstOrder(original);
        final AstDiff astDiff = new AstDiff();
        final List<Delta<String>> deltas = astDiff.diff(original, revised);
        final List<Node> targetNodes = new ArrayList<Node>();
        for (Delta<String> delta : deltas) {
            final int diffPosition = delta.getOriginal().getPosition(); 
            if (diffPosition > allOriginalNodes.size()) {
                continue;        
            }
            targetNodes.add(allOriginalNodes.get(diffPosition));
        }
        return targetNodes;
    }
}