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

public class DiffCollector {
    private String sourceBeforeFix;
    private String fixedSource;
    private String sourceDiff;

    public DiffCollector(Node targetNodeBeforeFix, CompilationUnit fixedCompilationUnit){     
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
        List<String> original = this.changeStringToList(beforeSource);
        List<String> revised  = this.changeStringToList(afterSource);
	    Patch<String> diff = DiffUtils.diff(original, revised);

        List<Delta<String>> deltas = diff.getDeltas();
        StringBuilder sourceDiffBuilder = new StringBuilder("");

	    for (Delta<String> delta : deltas) {
            Chunk<String> originalCode = delta.getOriginal();
            Chunk<String> patchedCode = delta.getRevised();

            int i = 1;
            StringBuilder originalPrettySource = new StringBuilder("");
            for (String str : originalCode.getLines()){
                //ソースコードの行は6桁まで
                originalPrettySource.append(String.format("%-6s", (patchedCode.getPosition() + i)) + "-" + str + "\n");
                i++;
            }

            i = 1;
            StringBuilder prettySource = new StringBuilder("");
            for (String str : patchedCode.getLines()){
                //ソースコードの行は6桁まで
                prettySource.append(String.format("%-6s", (patchedCode.getPosition() + i)) + "+" + str + "\n");
                i++;
            }

            sourceDiffBuilder.append("")
                .append(originalPrettySource.toString())
                .append("\n")
                .append(prettySource.toString())
                .append("\n");
        }
        this.sourceDiff = sourceDiffBuilder.toString();
    }

    /**
     * Stringを行ごとに分割してリストにする
     * @param text
     * @return
     */
    private List<String> changeStringToList(String text){
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
    public String getSourceDiff(){
        return this.sourceDiff;
    }

}