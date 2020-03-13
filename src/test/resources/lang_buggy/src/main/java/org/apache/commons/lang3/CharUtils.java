















package org.apache.commons.lang3;












public class CharUtils {
    
    private static final String[] CHAR_STRING_ARRAY = new String[128];
    







    public static final char LF = '\n';








    public static final char CR = '\r';
    

    static {
        for (char c = 0; c < CHAR_STRING_ARRAY.length; c++) {
            CHAR_STRING_ARRAY[c] = String.valueOf(c);
        }
    }








    public CharUtils() {
      super();
    }

    















    @Deprecated
    public static Character toCharacterObject(final char ch) {
        return Character.valueOf(ch);
    }
    

















    public static Character toCharacterObject(final String str) {
        if (StringUtils.isEmpty(str)) {
            return null;
        }
        return Character.valueOf(str.charAt(0));
    }
    
    













    public static char toChar(final Character ch) {
        if (ch == null) {
            throw new IllegalArgumentException("The Character must not be null");
        }
        return ch.charValue();
    }
    













    public static char toChar(final Character ch, final char defaultValue) {
        if (ch == null) {
            return defaultValue;
        }
        return ch.charValue();
    }
    
    















    public static char toChar(final String str) {
        if (StringUtils.isEmpty(str)) {
            throw new IllegalArgumentException("The String must not be empty");
        }
        return str.charAt(0);
    }
    















    public static char toChar(final String str, final char defaultValue) {
        if (StringUtils.isEmpty(str)) {
            return defaultValue;
        }
        return str.charAt(0);
    }
    
    















    public static int toIntValue(final char ch) {
        if (isAsciiNumeric(ch) == false) {
            throw new IllegalArgumentException("The character " + ch + " is not in the range '0' - '9'");
        }
        return ch - 48;
    }
    















    public static int toIntValue(final char ch, final int defaultValue) {
        if (isAsciiNumeric(ch) == false) {
            return defaultValue;
        }
        return ch - 48;
    }
    
















    public static int toIntValue(final Character ch) {
        if (ch == null) {
            throw new IllegalArgumentException("The character must not be null");
        }
        return toIntValue(ch.charValue());
    }
    
















    public static int toIntValue(final Character ch, final int defaultValue) {
        if (ch == null) {
            return defaultValue;
        }
        return toIntValue(ch.charValue(), defaultValue);
    }
    
    














    public static String toString(final char ch) {
        if (ch < 128) {
            return CHAR_STRING_ARRAY[ch];
        }
        return new String(new char[] {ch});
    }
    

















    public static String toString(final Character ch) {
        if (ch == null) {
            return null;
        }
        return toString(ch.charValue());
    }
    
    













    public static String unicodeEscaped(final char ch) {
        if (ch < 0x10) {
            return "\\u000" + Integer.toHexString(ch);
        } else if (ch < 0x100) {
            return "\\u00" + Integer.toHexString(ch);
        } else if (ch < 0x1000) {
            return "\\u0" + Integer.toHexString(ch);
        }
        return "\\u" + Integer.toHexString(ch);
    }
    
















    public static String unicodeEscaped(final Character ch) {
        if (ch == null) {
            return null;
        }
        return unicodeEscaped(ch.charValue());
    }
    
    















    public static boolean isAscii(final char ch) {
        return ch < 128;
    }
    















    public static boolean isAsciiPrintable(final char ch) {
        return ch >= 32 && ch < 127;
    }
    















    public static boolean isAsciiControl(final char ch) {
        return ch < 32 || ch == 127;
    }
    















    public static boolean isAsciiAlpha(final char ch) {
        return (ch >= 'A' && ch <= 'Z') || (ch >= 'a' && ch <= 'z');
    }
    















    public static boolean isAsciiAlphaUpper(final char ch) {
        return ch >= 'A' && ch <= 'Z';
    }
    















    public static boolean isAsciiAlphaLower(final char ch) {
        return ch >= 'a' && ch <= 'z';
    }
    















    public static boolean isAsciiNumeric(final char ch) {
        return ch >= '0' && ch <= '9';
    }
    















    public static boolean isAsciiAlphanumeric(final char ch) {
        return (ch >= 'A' && ch <= 'Z') || (ch >= 'a' && ch <= 'z') || (ch >= '0' && ch <= '9');
    }
    
}
