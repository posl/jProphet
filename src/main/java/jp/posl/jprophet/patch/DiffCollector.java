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
    private Chunk<String> oc;
    private Chunk<String> rc;

    public DiffCollector(){

    }
    
    public void exec(String beforeSource, String afterSource) {
        List<String> original = this.changeStringToList(beforeSource);
        List<String> revised  = this.changeStringToList(afterSource);
	    Patch<String> diff = DiffUtils.diff(original, revised);

	    List<Delta<String>> deltas = diff.getDeltas();
	    for (Delta<String> delta : deltas) {
            this.type = delta.getType();
            System.out.println(type);
            this.oc = delta.getOriginal();
            System.out.printf("del: position=%d, lines=%s%n", oc.getPosition() + 1, oc.getLines());
            this.rc = delta.getRevised();
            System.out.printf("add: position=%d, lines=%s%n", rc.getPosition() + 1, rc.getLines());
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