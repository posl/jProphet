package jp.posl.jprophet;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import jp.posl.jprophet.operation.AstOperation;
import jp.posl.jprophet.patch.PatchCandidate;
import jp.posl.jprophet.patch.PatchCandidateWithAbstHole;
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
                String souceCode = String.join("\n", lines);
                List<RepairUnit> repairUnits =  new AstGenerator().getAllRepairUnit(souceCode);
                for(RepairUnit repairUnit : repairUnits){
                    List<RepairUnit> appliedUnits = this.applyTemplate(repairUnit, operations);
                    for(RepairUnit appliedUnit : appliedUnits){
                        candidates.add(new PatchCandidateWithAbstHole(appliedUnit, fileLocator.getPath(), fileLocator.getFqn()));
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
    private List<RepairUnit> applyTemplate(RepairUnit repairUnit, List<AstOperation> operations) {
        List<RepairUnit> appliedUnits = new ArrayList<RepairUnit>();

        operations.stream()
            .map(o -> o.exec(repairUnit))
            .forEach(appliedUnits::addAll);

        return appliedUnits;
    }
}
