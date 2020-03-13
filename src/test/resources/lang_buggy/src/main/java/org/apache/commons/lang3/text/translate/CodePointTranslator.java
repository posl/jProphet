















package org.apache.commons.lang3.text.translate;

import java.io.IOException;
import java.io.Writer;








public abstract class CodePointTranslator extends CharSequenceTranslator {





    @Override
    public final int translate(final CharSequence input, final int index, final Writer out) throws IOException {
        final int codepoint = Character.codePointAt(input, index);
        final boolean consumed = translate(codepoint, out);
        if (consumed) {
            return 1;
        } else {
            return 0;
        }
    }









    public abstract boolean translate(int codepoint, Writer out) throws IOException;

}
