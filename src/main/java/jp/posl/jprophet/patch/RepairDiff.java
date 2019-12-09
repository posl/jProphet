package jp.posl.jprophet.patch;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.printer.lexicalpreservation.LexicalPreservingPrinter;

import difflib.Chunk;
import difflib.Delta;
import difflib.DiffUtils;
import difflib.Patch;
import java.io.BufferedReader;
import java.io.StringReader;

/**
 * 修正前と修正後のdiffを集める
 */
public class RepairDiff {
    private String sourceBeforeFix;
    private String fixedSource;
    private String sourceDiff;

    /**
     * targetNodeBeforeFixとfixedCompilationUnitのdiffを生成する
     * @param targetNodeBeforeFix 修正前のNode
     * @param fixedCompilationUnit 修正後のCompilationUnit
     */
    public RepairDiff(Node targetNodeBeforeFix, CompilationUnit fixedCompilationUnit){     
        LexicalPreservingPrinter.setup(targetNodeBeforeFix.findCompilationUnit().orElseThrow());
        this.sourceBeforeFix =LexicalPreservingPrinter.print(targetNodeBeforeFix.findCompilationUnit().orElseThrow());

        LexicalPreservingPrinter.setup(fixedCompilationUnit);
        this.fixedSource = LexicalPreservingPrinter.print(fixedCompilationUnit);

        this.collect(sourceBeforeFix, fixedSource);
    }
    
    /**
     * diffの情報を集める
     * @param beforeSource
     * @param afterSource
     */
    private void collect(String beforeSource, String afterSource) {
        List<String> original = this.splitTextByLine(beforeSource);
        List<String> revised  = this.splitTextByLine(afterSource);
	    Patch<String> diff = DiffUtils.diff(original, revised);

        List<Delta<String>> deltas = diff.getDeltas();
        StringBuilder sourceDiffBuilder = new StringBuilder("");

	    for (Delta<String> delta : deltas) {
            Chunk<String> originalCode = delta.getOriginal();
            Chunk<String> patchedCode = delta.getRevised();

            int lineNum = 1;
            final int around = 2; //前後何行を含むか
            StringBuilder originalDiffSource = new StringBuilder("");

            for (int i = around; i >= 1; i--) {
                try {
                    originalDiffSource.append(String.format("%-6s", (originalCode.getPosition() + lineNum - i)) + "  " + original.get(originalCode.getPosition() - i) + "\n");
                } catch (IndexOutOfBoundsException e){}
            }

            for (String str : originalCode.getLines()){
                //ソースコードの行は6桁まで
                originalDiffSource.append(String.format("%-6s", (originalCode.getPosition() + lineNum)) + "- " + str + "\n");
                lineNum++;
            }

            for (int i = 0; i < around; i++) {
                try {
                originalDiffSource.append(String.format("%-6s", (originalCode.getPosition() + lineNum + i)) + "  " + original.get(originalCode.getPosition() + i + lineNum - 1) + "\n");
                } catch (IndexOutOfBoundsException e){}
            }

            lineNum = 1;
            StringBuilder patchedDiffSource = new StringBuilder("");

            for (int i = around; i >= 1; i--) {
                try {
                patchedDiffSource.append(String.format("%-6s", (patchedCode.getPosition() + lineNum - i)) + "  " + revised.get(patchedCode.getPosition() - i) + "\n");
                } catch (IndexOutOfBoundsException e){}
            }

            for (String str : patchedCode.getLines()){
                //ソースコードの行は6桁まで
                patchedDiffSource.append(String.format("%-6s", (patchedCode.getPosition() + lineNum)) + "+ " + str + "\n");
                lineNum++;
            }

            for (int i = 0; i < around; i++) {
                try {
                patchedDiffSource.append(String.format("%-6s", (patchedCode.getPosition() + lineNum + i)) + "  " + revised.get(patchedCode.getPosition() + i + lineNum - 1) + "\n");
                } catch (IndexOutOfBoundsException e){}
            }

            sourceDiffBuilder.append("")
                .append(originalDiffSource.toString())
                .append("\n")
                .append(patchedDiffSource.toString())
                .append("\n");
        }
        this.sourceDiff = sourceDiffBuilder.toString();
    }

    /**
     * Stringを行ごとに分割してリストにする
     * @param text
     * @return
     */
    private List<String> splitTextByLine(String text){
        List<String> textList = new ArrayList<String>();
        try (BufferedReader reader = new BufferedReader(new StringReader(text))) {
            String line = null;
            while ((line = reader.readLine()) != null) {
                textList.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return textList;
    }

    /**
     * ソースコードのdiffを取得する
     * @return
     */
    public String toString(){
        return this.sourceDiff;
    }

}