package jp.posl.jprophet.patch;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.printer.lexicalpreservation.LexicalPreservingPrinter;

import difflib.Chunk;
import difflib.Delta;
import difflib.Delta.TYPE;
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
            TYPE type = delta.getType();
            Chunk<String> originalCode = delta.getOriginal();
            Chunk<String> patchedCode = delta.getRevised();

            int i = 1;
            StringBuilder originalPrettySource = new StringBuilder("");
            for (String str : originalCode.getLines()){
                originalPrettySource.append((patchedCode.getPosition() + i) + "-" + str + "\n");
                i++;
            }

            i = 1;
            StringBuilder prettySource = new StringBuilder("");
            for (String str : patchedCode.getLines()){
                prettySource.append((patchedCode.getPosition() + i) + "+" + str + "\n");
                i++;
            }
            /*
            System.out.println(type);
            System.out.println(originalPrettySource.toString());
            System.out.println(prettySource.toString());
            */
            //types.add(type);
            //this.originalCodes.add(originalPrettySource.toString());
            //this.patchedCodes.add(prettySource.toString());
            sourceDiffBuilder.append(type.toString())
                .append("\n")
                .append(originalPrettySource.toString())
                .append(prettySource.toString());
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

    public String getSourceDiff(){
        return this.sourceDiff;
    }

}