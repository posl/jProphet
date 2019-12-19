package jp.posl.jprophet.patchgenerator;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;

import jp.posl.jprophet.NodeUtility;
import jp.posl.jprophet.operation.AstOperation;
import jp.posl.jprophet.patch.PatchCandidate;
import jp.posl.jprophet.patch.AstPatchCandidate;
import jp.posl.jprophet.project.FileLocator;
import jp.posl.jprophet.project.Project;


public class AstPatchCandidateGenerator implements PatchCandidateGenerator{
    private final List<AstOperation> operations;

    public AstPatchCandidateGenerator(List<AstOperation> operations) {
        this.operations = operations;
    }

    /**
     * バグのあるソースコード群から修正パッチ候補を生成する 
     * 
     * @param project 修正パッチ候補を生成する対象のプロジェクト 
     * @return 条件式が抽象化された修正パッチ候補のリスト
     */
    public List<PatchCandidate> exec(Project project){
        List<FileLocator> fileLocators = project.getSrcFileLocators();                
        List<PatchCandidate> candidates = new ArrayList<PatchCandidate>();
        for (FileLocator fileLocator : fileLocators){
            try {
                List<String> lines = Files.readAllLines(Paths.get(fileLocator.getPath()), StandardCharsets.UTF_8);
                String sourceCode = String.join("\n", lines);
                candidates.addAll(this.generatePatches(sourceCode, fileLocator));
            } catch (IOException e) {
                e.printStackTrace();
                break;
            }
        }
        return candidates;
    }

    private List<PatchCandidate> generatePatches(String sourceCode, FileLocator fileLocator) {
        List<PatchCandidate> patches = new ArrayList<PatchCandidate>();
        List<Node> targetNodes = NodeUtility.getAllNodesFromCode(sourceCode);
        for(Node targetNode : targetNodes){
            this.applyTemplate(targetNode).stream()
                .map(result -> new AstPatchCandidate(targetNode, result.getCompilationUnit(), fileLocator.getPath(), fileLocator.getFqn(), result.getOperation()))
                .forEach(patches::add);
        }
        return patches;
    }

    /**
     * ASTノードに対して修正テンプレートを適用し，修正候補群を生成する．
     * 
     * @param repairUnits テンプレートを適用するASTノードのリスト
     * @return テンプレートが適用された修正候補のリスト
     */
    public List<AppliedOperationResult> applyTemplate(Node node) {
        List<AppliedOperationResult> appliedOperationResults = new ArrayList<AppliedOperationResult>();

        this.operations.stream()
            .map(o -> o.exec(node).stream()
                .map(c -> new AppliedOperationResult(c, o.getClass()))
                .collect(Collectors.toList()))
            .forEach(appliedOperationResults::addAll);

        return appliedOperationResults;
    }


    public class AppliedOperationResult {
        private final CompilationUnit compilationUnit;
        private final Class<? extends AstOperation> operation;

        public AppliedOperationResult(CompilationUnit compilationUnit, Class<? extends AstOperation> operation) {
            this.compilationUnit = compilationUnit;
            this.operation = operation;
        }

        /**
         * @return the compilationUnit
         */
        public CompilationUnit getCompilationUnit() {
            return compilationUnit;
        }

        /**
         * @return the operation
         */
        public Class<? extends AstOperation> getOperation() {
            return operation;
        }
    }

}
