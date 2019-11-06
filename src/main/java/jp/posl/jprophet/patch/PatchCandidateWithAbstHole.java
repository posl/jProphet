package jp.posl.jprophet.patch;

import java.util.Optional;

import com.github.javaparser.ast.CompilationUnit;

import jp.posl.jprophet.RepairUnit;

/**
 * StagedProgramrRepairにおける条件内部の具体的なコードが生成される前の状態の
 * 修正パッチ候補を持つ
 */
public class PatchCandidateWithAbstHole implements PatchCandidate {
    private DefaultPatchCandidate patchCandidate;
    public PatchCandidateWithAbstHole(RepairUnit repairUnit, String fixedFilePath, String fixedFileFQN) {
        this.patchCandidate = new DefaultPatchCandidate(repairUnit, fixedFilePath, fixedFileFQN);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getFilePath(){
        return this.patchCandidate.getFilePath();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getFqn(){
        return this.patchCandidate.getFqn();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CompilationUnit getCompilationUnit(){
        return this.patchCandidate.getCompilationUnit();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<Integer> getLineNumber() {
        return this.patchCandidate.getLineNumber();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString(){
        return this.patchCandidate.toString();
    }
}
