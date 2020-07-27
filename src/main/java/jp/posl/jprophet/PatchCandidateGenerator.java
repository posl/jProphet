package jp.posl.jprophet;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;

import jp.posl.jprophet.fl.Suspiciousness;
import jp.posl.jprophet.operation.AstOperation;
import jp.posl.jprophet.patch.PatchCandidate;
import jp.posl.jprophet.patch.DefaultPatchCandidate;
import jp.posl.jprophet.patch.DiffWithType;
import jp.posl.jprophet.project.FileLocator;
import jp.posl.jprophet.project.Project;


public class PatchCandidateGenerator{
    /**
     * バグのあるソースコード群から修正パッチ候補を生成する 
     * 
     * @param project 修正パッチ候補を生成する対象のプロジェクト 
     * @param operations 適応するテンプレート
     * @param suspiciousnesses flで得られた疑惑値のリスト
     * @return 条件式が抽象化された修正パッチ候補のリスト
     */
    public List<PatchCandidate> exec(Project project, List<AstOperation> operations, List<Suspiciousness> suspiciousnesses){
        final List<FileLocator> fileLocators = project.getSrcFileLocators();
        List<PatchCandidate> candidates = new ArrayList<PatchCandidate>();
        int patchCandidateID = 1;
        for(FileLocator fileLocator : fileLocators){
            try {
                final List<String> lines = Files.readAllLines(Paths.get(fileLocator.getPath()), StandardCharsets.UTF_8);
                final String sourceCode = String.join("\n", lines);
                CompilationUnit cu = JavaParser.parse(sourceCode);
                final List<Node> targetNodes = NodeUtility.getAllDescendantNodes(cu);
                for(Node targetNode : targetNodes){
                    //疑惑値0のtargetNodeはパッチを生成しない
                    if (!findZeroSuspiciousness(fileLocator.getFqn(), targetNode, suspiciousnesses)) {
                        final List<AppliedOperationResult> appliedOperationResults = this.applyTemplate(targetNode, operations);
                        for(AppliedOperationResult result : appliedOperationResults){
                            candidates.add(new DefaultPatchCandidate(result.getDiffWithType(), fileLocator.getPath(), fileLocator.getFqn(), result.getOperation(), patchCandidateID));
                            patchCandidateID += 1;
                        }
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
     * nodeの疑惑値が0であるかを判定する
     * @param fqn nodeの基となったファイルfqn
     * @param node ノード
     * @param suspiciousnesses flで得られた疑惑値のリスト
     * @return 疑惑値0ならtrue,それ以外false
     */
    private boolean findZeroSuspiciousness(String fqn, Node node, List<Suspiciousness> suspiciousnesses) {
        try {
            final int line = node.getRange().orElseThrow().begin.line;
            final Optional<Suspiciousness> suspiciousness = suspiciousnesses.stream()
                .filter(s -> s.getFQN().equals(fqn))
                .filter(s -> s.getLineNumber() == line)
                .findAny();

            if (suspiciousness.isPresent()) {
                if(suspiciousness.get().getValue() == 0) {
                    return true; //疑惑値0の場合
                } else {
                    return false;  //疑惑値0以外の場合
                }
            } else {
                return true; //疑惑値がついてない場合
            }
            
        } catch (NoSuchElementException e){
            return true;
        }
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
        private final DiffWithType diffWithType;
        private final Class<? extends AstOperation> operation;

        public AppliedOperationResult(DiffWithType diffWithType, Class<? extends AstOperation> operation) {
            this.diffWithType = diffWithType;
            this.operation = operation;
        }

        /**
         * @return the diffWithType
         */
        public DiffWithType getDiffWithType() {
            return diffWithType;
        }

        /**
         * @return the operation
         */
        public Class<? extends AstOperation> getOperation() {
            return operation;
        }
    }

}
