package jp.posl.jprophet;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;

import jp.posl.jprophet.operation.AstOperation;
import jp.posl.jprophet.patch.PatchCandidate;
import jp.posl.jprophet.patch.DefaultPatchCandidate;
import jp.posl.jprophet.project.FileLocator;
import jp.posl.jprophet.project.Project;


public class PatchCandidateGenerator{
    /**
     * バグのあるソースコード群から修正パッチ候補を生成する 
     * 
     * @param project 修正パッチ候補を生成する対象のプロジェクト 
     * @return 条件式が抽象化された修正パッチ候補のリスト
     */
    public List<PatchCandidate> exec(Project project, List<AstOperation> operations){
        List<FileLocator> fileLocators = project.getSrcFileLocators();                
        List<PatchCandidate> candidates = new ArrayList<PatchCandidate>();
        for(FileLocator fileLocator : fileLocators){
            try {
                List<String> lines = Files.readAllLines(Paths.get(fileLocator.getPath()), StandardCharsets.UTF_8);
                String sourceCode = String.join("\n", lines);
                List<Node> targetNodes = NodeUtility.getAllNodesFromCode(sourceCode);
                for(Node targetNode : targetNodes){
                    List<AppliedOperationResult> appliedOperationResults = this.applyTemplate(targetNode, operations);
                    for(AppliedOperationResult result : appliedOperationResults){
                        candidates.add(new DefaultPatchCandidate(targetNode, result.getCompilationUnit(), fileLocator.getPath(), fileLocator.getFqn(), result.getOperation()));
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                break;
            }
        }

        return candidates;
    }

    /**
     * ASTノードに対して修正テンプレートを適用し，修正候補群を生成する．
     * 
     * @param repairUnits テンプレートを適用するASTノードのリスト
     * @return テンプレートが適用された修正候補のリスト
     */
    private List<AppliedOperationResult> applyTemplate(Node node, List<AstOperation> operations) {
        List<AppliedOperationResult> appliedOperationResults = new ArrayList<AppliedOperationResult>();

        operations.stream()
            .map(o -> o.exec(node).stream().map(c -> new AppliedOperationResult(c, o.getClass())).collect(Collectors.toList()))
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
