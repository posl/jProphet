package jp.posl.jprophet.trainingcase;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.github.javaparser.ParseProblemException;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;

import difflib.Delta;
import jp.posl.jprophet.NodeUtility;
import jp.posl.jprophet.PatchCandidateGenerator;
import jp.posl.jprophet.evaluator.AstDiff;
import jp.posl.jprophet.evaluator.extractor.FeatureExtractor;
import jp.posl.jprophet.evaluator.extractor.FeatureVector;
import jp.posl.jprophet.patch.LearningPatch;
import jp.posl.jprophet.patch.Patch;

public class TrainingCaseGenerator {

    /**
     * 一つのトレーニングケースを表現するクラス
     */
    static class TrainingCase {
        final public FeatureVector vectorOfCorrectPatch;
        final public List<FeatureVector> vectorsOfGeneratedPatch;

        /**
         * @param vectorOfCorrectPatch 開発者による正しいパッチ
         * @param vectorsOfGeneratedPatch 同一のプログラムに対して本jProphetの生成したパッチのリスト
         */
        public TrainingCase(FeatureVector vectorOfCorrectPatch, List<FeatureVector> vectorsOfGeneratedPatch) {
            this.vectorOfCorrectPatch = vectorOfCorrectPatch;
            this.vectorsOfGeneratedPatch = vectorsOfGeneratedPatch;
        }
    }

    /**
     * 修正前後のソースコードから特徴抽出を行いトレーニングケースを生成する
     * @param config 設定（元ファイルのディレクトリのパスなど）
     * @return トレーニングケースのリスト
     */
    public List<TrainingCase> generate(TrainingCaseConfig config) {
        final List<Patch> patches = this.generateLearningPatches(config);

        final FeatureExtractor extractor = new FeatureExtractor();
        final PatchCandidateGenerator patchGenerator = new PatchCandidateGenerator();
        final List<TrainingCase> trainingCases = new ArrayList<TrainingCase>();
        for (Patch patch : patches) {
            CompilationUnit originalCu = patch.getOriginalCompilationUnit();
            CompilationUnit fixedCu = patch.getFixedCompilationUnit();

            final List<Node> allNodesForPatchGeneration = this.getAllPatchedNodes(originalCu.findRootNode(), fixedCu.findRootNode());

            // ランダムな10個のパッチを使用 メモリオーバーの問題があるため暫定処置
            Collections.shuffle(allNodesForPatchGeneration);
            final List<Patch> generatedPatches = allNodesForPatchGeneration.stream()
                .flatMap(node -> patchGenerator.applyTemplate(node, config.getOperations()).stream())
                .map(result -> result.getCompilationUnit())
                .map(cu -> new LearningPatch(patch.getOriginalCompilationUnit(), cu))
                .limit(10)
                .collect(Collectors.toList());
            final List<FeatureVector> vectorsOfGeneratedPatches = generatedPatches.stream()
                .map(extractor::extract)
                .collect(Collectors.toList());
            trainingCases.add(new TrainingCase(extractor.extract(patch), vectorsOfGeneratedPatches));
        }
        return trainingCases;
    }

    /**
     * 修正前後のソースコードからLeaningPatchオブジェクトを生成する
     * 一つのパッチににつき以下のディレクトリ構成のものを対象とする
     * - patch (dir)
     *     - original (dir)
     *         - javafile(修正前ファイル)
     *     - fixed (dir)
     *         - javafile(修正後ファイル)
     * @param config 設定
     * @return LearningPatchのリスト
     */
    private List<Patch> generateLearningPatches(TrainingCaseConfig config) {
        final Path dirPath = Paths.get(config.getDirPathContainsPatchFiles());
        List<Path> patchFilePairPaths = new ArrayList<Path>();
        try {
            patchFilePairPaths = Files.list(dirPath).collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }
        final List<Patch> patches = new ArrayList<Patch>();

        for (Path patchFilePairPath: patchFilePairPaths) {
            if (!Files.isDirectory(patchFilePairPath)) {
                continue;
            }
            try {
                final Optional<Path> originalDirPath = Files.list(patchFilePairPath)
                    .filter(path -> Files.isDirectory(path))
                    .filter(path -> path.getFileName().toString().equals(config.getOriginalDirName()))
                    .findFirst();
                final Optional<Path> fixedDirPath = Files.list(patchFilePairPath)
                    .filter(path -> Files.isDirectory(path))
                    .filter(path -> path.getFileName().toString().equals(config.getFixedDirName()))
                    .findFirst();
                if (!originalDirPath.isPresent() || !fixedDirPath.isPresent()) {
                    continue;
                }
                final List<Path> pathesInOriginalDir = Files.list(originalDirPath.orElseThrow()).collect(Collectors.toList());
                final List<Path> pathesInFixedDir    = Files.list(fixedDirPath.orElseThrow()).collect(Collectors.toList());
                if (pathesInOriginalDir.size() != 1 || pathesInFixedDir.size() != 1) {
                    continue;
                }
                final Path originalFilePath = pathesInOriginalDir.get(0);
                final Path fixedFilePath    = pathesInFixedDir.get(0);
                final String originalSourceCode = String.join("\n", Files.readAllLines(originalFilePath));
                final String fixedSourceCode    = String.join("\n", Files.readAllLines(fixedFilePath));
                try {
                    patches.add(new LearningPatch(originalSourceCode, fixedSourceCode));
                } catch (ParseProblemException e) {
                    continue;
                }
            } catch (IOException e) {
                System.err.println(e.getMessage());
                e.printStackTrace();
                continue;
            }
        }
        return patches;
    }

    /**
     * 修正前後のASTノードからで修正が行われたノードをリストとして抽出する
     * @param original 修正前AST
     * @param revised 修正後AST
     * @return 修正されたノードのリスト
     */
    private List<Node> getAllPatchedNodes(Node original, Node revised) {
        final List<Node> allOriginalNodes = NodeUtility.getAllNodesInDepthFirstOrder(original);
        final AstDiff astDiff = new AstDiff();
        final List<Delta<String>> deltas = astDiff.diff(original, revised);
        final List<Node> targetNodes = new ArrayList<Node>();
        for (Delta<String> delta : deltas) {
            final int diffPosition = delta.getOriginal().getPosition(); 
            if (diffPosition >= allOriginalNodes.size()) {
                continue;        
            }
            targetNodes.add(allOriginalNodes.get(diffPosition));
        }
        return targetNodes;
    }
} 