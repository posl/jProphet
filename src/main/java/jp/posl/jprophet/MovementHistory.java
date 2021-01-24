package jp.posl.jprophet;

import java.util.ArrayList;
import java.util.List;

import com.github.javaparser.ast.CompilationUnit;

public class MovementHistory {
    static class StatementDelta {
        int lineDelta;
        int columnDelta;
        public StatementDelta(int lineDelta, int columnDelta) {
            this.lineDelta = lineDelta;
            this.columnDelta = columnDelta;
        }
    }

    final List<StatementDelta> stmtDeltas;
    final CompilationUnit cu;

    public MovementHistory(CompilationUnit cu) {
        this.cu = cu;
        stmtDeltas = new ArrayList<StatementDelta>();
        for (int i = 0; i < cu.getEnd().orElseThrow().line; i++) {
            stmtDeltas.add(new StatementDelta(0, 0));
        }
    }

    public StatementDelta get(int lineNumber) {
        return this.stmtDeltas.get(lineNumber - 1);
    }

    public void addLineDelta(int targetLineHead, int targetLineTail, int delta) {
        if (targetLineHead < 1) {
            throw new IllegalArgumentException("The 'targetLineHead' must be greater than 0.");
        }
        if (targetLineTail > this.cu.getEnd().orElseThrow().line) {
            throw new IllegalArgumentException("The 'targetLineTail' must be smaller than or equal to the number of lines of code.");
        }
        if (targetLineHead > targetLineTail) {
            throw new IllegalArgumentException("The 'targetLineHead' must be smaller than or equal to the 'targetLineTail'.");
        }
        for (int i = targetLineHead; i <= targetLineTail; i++) {
            stmtDeltas.get(i - 1).lineDelta += delta;
        }
    }

    public void addColumnDelta(int targetLineHead, int targetLineTail, int delta) {
        if (targetLineHead < 1) {
            throw new IllegalArgumentException("The 'targetLineHead' must be greater than 0.");
        }
        if (targetLineTail > this.cu.getEnd().orElseThrow().line) {
            throw new IllegalArgumentException("The 'targetLineTail' must be smaller than or equal to the number of lines of code.");
        }
        if (targetLineHead > targetLineTail) {
            throw new IllegalArgumentException("The 'targetLineHead' must be smaller than or equal to the 'targetLineTail'.");
        }
        for (int i = targetLineHead; i <= targetLineTail; i++) {
            stmtDeltas.get(i - 1).columnDelta += delta;
        }
    }
}
