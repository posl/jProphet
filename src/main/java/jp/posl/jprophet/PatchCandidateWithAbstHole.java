package jp.posl.jprophet;

import java.util.Optional;

import com.github.javaparser.ast.CompilationUnit;

/**
 * StagedProgramrRepairにおける条件内部の具体的なコードが生成される前の状態の
 * 修正パッチ候補を持つ
 */
public class PatchCandidateWithAbstHole implements PatchCandidate {
    private PatchCandidateImpl patchCandidate;
    public PatchCandidateWithAbstHole(RepairUnit repairUnit, String fixedFilePath, String fixedFileFQN) {
        this.patchCandidate = new PatchCandidateImpl(repairUnit, fixedFilePath, fixedFileFQN);
    }

    /**
     * {@inheritDoc}
     */
    public String getFilePath(){
        return this.patchCandidate.getFilePath();
    }

    /**
     * {@inheritDoc}
     */
    public String getFqn(){
        return this.patchCandidate.getFqn();
    }

    /**
     * {@inheritDoc}
     */
    public CompilationUnit getCompilationUnit(){
        return this.patchCandidate.getCompilationUnit();
    }

    /**
     * {@inheritDoc}
     */
    public Optional<Integer> getLineNumber() {
        return this.patchCandidate.getLineNumber();
    }

    /**
     * {@inheritDoc}
     */
    public String toString(){
        return this.patchCandidate.toString();
    }
}
