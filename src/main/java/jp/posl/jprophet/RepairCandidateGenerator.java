package jp.posl.jprophet;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import jp.posl.jprophet.operation.*;


public class RepairCandidateGenerator{

    // テスト時のモック注入のためにprivate変数化
    private CondRefinementOperation condRefinementOperation = new CondRefinementOperation();
    private CondIntroductionOperation condIntroductionOperation = new CondIntroductionOperation(); 
    private CtrlFlowIntroductionOperation ctrlFlowIntroductionOperation = new CtrlFlowIntroductionOperation(); 
    private InsertInitOperation insertInitOperation = new InsertInitOperation(); 
    private ValueReplacementOperation valueReplacementOperation =  new ValueReplacementOperation();
    private CopyReplaceOperation copyReplaceOperation = new CopyReplaceOperation();

    /**
     * バグのあるソースコード群から修正パッチ候補を生成する 
     * 
     * @param project 修正パッチ候補を生成する対象のプロジェクト 
     * @return 条件式が抽象化された修正パッチ候補のリスト
     */
    public List<RepairCandidate> exec(ProjectConfiguration project){
        List<String> filePaths = project.getSourceFilePaths();                
        List<RepairCandidate> candidates = new ArrayList<RepairCandidate>();
        for(String filePath : filePaths){
            try {
                List<String> lines = Files.readAllLines(Paths.get(filePath), StandardCharsets.UTF_8);
                String souceCode = String.join("\n", lines);
                List<RepairUnit> repairUnits =  new AstGenerator().getAllRepairUnit(souceCode);
                for(RepairUnit repairUnit : repairUnits){
                    List<RepairUnit> appliedUnits = this.applyTemplate(repairUnit);
                    for(RepairUnit appliedUnit : appliedUnits){
                        candidates.add(new AbstractRepairCandidate(appliedUnit.getCompilationUnit(), new ArrayList<>(Arrays.asList(filePath))));
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
    private List<RepairUnit> applyTemplate(RepairUnit repairUnit) {
        List<AstOperation> astOperations = new ArrayList<AstOperation>(Arrays.asList(
            condRefinementOperation,
            condIntroductionOperation,
            ctrlFlowIntroductionOperation,
            insertInitOperation,
            valueReplacementOperation,
            copyReplaceOperation
        ));

        List<RepairUnit> appliedUnits = new ArrayList<RepairUnit>();
        for (AstOperation astOperation : astOperations){
            appliedUnits.addAll(astOperation.exec(repairUnit));    
        }

        return appliedUnits;
    }


}
