















package org.apache.commons.lang3.text.translate;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Locale;









public abstract class CharSequenceTranslator {













    public abstract int translate(CharSequence input, int index, Writer out) throws IOException;






    public final String translate(final CharSequence input) {
        if (input == null) {
            return null;
        }
        try {
            final StringWriter writer = new StringWriter(input.length() * 2);
            translate(input, writer);
            return writer.toString();
        } catch (final IOException ioe) {
            
            throw new RuntimeException(ioe);
        }
    }









    public final void translate(final CharSequence input, final Writer out) throws IOException {
        if (out == null) {
            throw new IllegalArgumentException("The Writer must not be null");
        }
        if (input == null) {
            return;
        }
        int pos = 0;
        final int len = input.length();
        while (pos < len) {
            final int consumed = translate(input, pos, out);
            if (consumed == 0) {
                final char[] c = Character.toChars(Character.codePointAt(input, pos));
                out.write(c);
                pos+= c.length;
                continue;
            }


            for (int pt = 0; pt < consumed; pt++) {
                pos += Character.charCount(Character.codePointAt(input, pt));
            }
        }
    }








    public final CharSequenceTranslator with(final CharSequenceTranslator... translators) {
        final CharSequenceTranslator[] newArray = new CharSequenceTranslator[translators.length + 1];
        newArray[0] = this;
        System.arraycopy(translators, 0, newArray, 1, translators.length);
        return new AggregateTranslator(newArray);
    }








    public static String hex(final int codepoint) {
        return Integer.toHexString(codepoint).toUpperCase(Locale.ENGLISH);
    }

}
