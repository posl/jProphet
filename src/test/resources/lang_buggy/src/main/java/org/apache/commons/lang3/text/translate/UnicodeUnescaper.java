















package org.apache.commons.lang3.text.translate;

import java.io.IOException;
import java.io.Writer;









public class UnicodeUnescaper extends CharSequenceTranslator {




    @Override
    public int translate(final CharSequence input, final int index, final Writer out) throws IOException {
        if (input.charAt(index) == '\\' && index + 1 < input.length() && input.charAt(index + 1) == 'u') {
            
            int i = 2;
            while (index + i < input.length() && input.charAt(index + i) == 'u') {
                i++;
            }

            if (index + i < input.length() && input.charAt(index + i) == '+') {
                i++;
            }

            if (index + i + 4 <= input.length()) {
                
                final CharSequence unicode = input.subSequence(index + i, index + i + 4);

                try {
                    final int value = Integer.parseInt(unicode.toString(), 16);
                    out.write((char) value);
                } catch (final NumberFormatException nfe) {
                    throw new IllegalArgumentException("Unable to parse unicode value: " + unicode, nfe);
                }
                return i + 4;
            } else {
                throw new IllegalArgumentException("Less than 4 hex digits in unicode value: '" + input.subSequence(index, input.length())
                        + "' due to end of CharSequence");
            }
        }
        return 0;
    }
}
