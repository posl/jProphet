package jp.posl.jprophet.patch;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import difflib.Chunk;
import difflib.Delta;
import difflib.Delta.TYPE;
import difflib.DiffUtils;
import difflib.Patch;
import java.io.BufferedReader;
import java.io.StringReader;

public class DiffCollector {
    private TYPE type;
    private Chunk<String> originalCode;
    private Chunk<String> patchedCode;

    public DiffCollector(){

    }
    
    public void collect(String beforeSource, String afterSource) {
        List<String> original = this.changeStringToList(beforeSource);
        List<String> revised  = this.changeStringToList(afterSource);
	    Patch<String> diff = DiffUtils.diff(original, revised);

	    List<Delta<String>> deltas = diff.getDeltas();
	    for (Delta<String> delta : deltas) {
            this.type = delta.getType();
            System.out.println(type);
            this.originalCode = delta.getOriginal();
            System.out.printf("original: position=%d, lines=%s%n", originalCode.getPosition() + 1, originalCode.getLines());
            this.patchedCode = delta.getRevised();
            System.out.printf("patched : position=%d, lines=%s%n", patchedCode.getPosition() + 1, patchedCode.getLines());

            StringBuilder prettySource = new StringBuilder("");
            for (String str : patchedCode.getLines()){
                prettySource.append(str + "\n");
            }
            System.out.println(prettySource.toString());
	    }
    }

    public List<String> changeStringToList(String text){
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

}