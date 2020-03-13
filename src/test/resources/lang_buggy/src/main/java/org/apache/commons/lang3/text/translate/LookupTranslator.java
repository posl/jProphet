















package org.apache.commons.lang3.text.translate;

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;







public class LookupTranslator extends CharSequenceTranslator {

    private final HashMap<String, CharSequence> lookupMap;
    private final int shortest;
    private final int longest;











    public LookupTranslator(final CharSequence[]... lookup) {
        lookupMap = new HashMap<String, CharSequence>();
        int _shortest = Integer.MAX_VALUE;
        int _longest = 0;
        if (lookup != null) {
            for (final CharSequence[] seq : lookup) {
                this.lookupMap.put(seq[0].toString(), seq[1]);
                final int sz = seq[0].length();
                if (sz < _shortest) {
                    _shortest = sz;
                }
                if (sz > _longest) {
                    _longest = sz;
                }
            }
        }
        shortest = _shortest;
        longest = _longest;
    }




    @Override
    public int translate(final CharSequence input, final int index, final Writer out) throws IOException {
        int max = longest;
        if (index + longest > input.length()) {
            max = input.length() - index;
        }
        
        for (int i = max; i >= shortest; i--) {
            final CharSequence subSeq = input.subSequence(index, index + i);
            final CharSequence result = lookupMap.get(subSeq.toString());
            if (result != null) {
                out.write(result.toString());
                return i;
            }
        }
        return 0;
    }
}
