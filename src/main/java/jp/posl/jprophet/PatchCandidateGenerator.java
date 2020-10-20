package jp.posl.jprophet;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;

import jp.posl.jprophet.fl.Suspiciousness;
import jp.posl.jprophet.operation.AstOperation;
import jp.posl.jprophet.patch.PatchCandidate;
import jp.posl.jprophet.patch.DiffWithType;
import jp.posl.jprophet.project.FileLocator;


public class PatchCandidateGenerator{
    /**
     * バグのあるソースコード群から修正パッチ候補を生成する 
     * 
     * @param project 修正パッチ候補を生成する対象のプロジェクト 
     * @param operations 適応するテンプレート
     * @param suspiciousnesses flで得られた疑惑値のリスト
     * @return 条件式が抽象化された修正パッチ候補のリスト
     */
    public List<PatchCandidate> exec(List<AstOperation> operations, List<Suspiciousness> suspiciousnesses, Map<FileLocator, CompilationUnit> fileLocatorMap){
        List<PatchCandidate> candidates = new ArrayList<PatchCandidate>();
        int patchCandidateID = 1;
        for(Map.Entry<FileLocator, CompilationUnit> entry : fileLocatorMap.entrySet()){
            final FileLocator fileLocator = entry.getKey();
            final List<Node> targetNodes = NodeUtility.getAllDescendantNodes(entry.getValue());
            for(Node targetNode : targetNodes){
                //疑惑値0のtargetNodeはパッチを生成しない
                if (!findZeroSuspiciousness(fileLocator.getFqn(), targetNode, suspiciousnesses)) {
                    final List<AppliedOperationResult> appliedOperationResults = this.applyTemplate(targetNode, operations);
                    for(AppliedOperationResult result : appliedOperationResults){
                        candidates.add(new PatchCandidate(result.getDiffWithType(), fileLocator.getPath(), fileLocator.getFqn(), result.getOperation(), patchCandidateID));
                        patchCandidateID += 1;
                    }
                }
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
    public List<AppliedOperationResult> applyTemplate(Node node, List<AstOperation> operations) {
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
