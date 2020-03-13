















package org.apache.commons.lang3;

import org.apache.commons.lang3.math.NumberUtils;












public class BooleanUtils {








    public BooleanUtils() {
      super();
    }

    
    
















    public static Boolean negate(final Boolean bool) {
        if (bool == null) {
            return null;
        }
        return bool.booleanValue() ? Boolean.FALSE : Boolean.TRUE;
    }

    
    














    public static boolean isTrue(final Boolean bool) {
        return Boolean.TRUE.equals(bool);
    }















    public static boolean isNotTrue(final Boolean bool) {
        return !isTrue(bool);
    }















    public static boolean isFalse(final Boolean bool) {
        return Boolean.FALSE.equals(bool);
    }















    public static boolean isNotFalse(final Boolean bool) {
        return !isFalse(bool);
    }

    













    public static boolean toBoolean(final Boolean bool) {
        return bool != null && bool.booleanValue();
    }














    public static boolean toBooleanDefaultIfNull(final Boolean bool, final boolean valueIfNull) {
        if (bool == null) {
            return valueIfNull;
        }
        return bool.booleanValue();
    }

    
    














    public static boolean toBoolean(final int value) {
        return value != 0;
    }















    public static Boolean toBooleanObject(final int value) {
        return value == 0 ? Boolean.FALSE : Boolean.TRUE;
    }



















    public static Boolean toBooleanObject(final Integer value) {
        if (value == null) {
            return null;
        }
        return value.intValue() == 0 ? Boolean.FALSE : Boolean.TRUE;
    }

















    public static boolean toBoolean(final int value, final int trueValue, final int falseValue) {
        if (value == trueValue) {
            return true;
        }
        if (value == falseValue) {
            return false;
        }
        
        throw new IllegalArgumentException("The Integer did not match either specified value");
    }


















    public static boolean toBoolean(final Integer value, final Integer trueValue, final Integer falseValue) {
        if (value == null) {
            if (trueValue == null) {
                return true;
            }
            if (falseValue == null) {
                return false;
            }
        } else if (value.equals(trueValue)) {
            return true;
        } else if (value.equals(falseValue)) {
            return false;
        }
        
        throw new IllegalArgumentException("The Integer did not match either specified value");
    }



















    public static Boolean toBooleanObject(final int value, final int trueValue, final int falseValue, final int nullValue) {
        if (value == trueValue) {
            return Boolean.TRUE;
        }
        if (value == falseValue) {
            return Boolean.FALSE;
        }
        if (value == nullValue) {
            return null;
        }
        
        throw new IllegalArgumentException("The Integer did not match any specified value");
    }



















    public static Boolean toBooleanObject(final Integer value, final Integer trueValue, final Integer falseValue, final Integer nullValue) {
        if (value == null) {
            if (trueValue == null) {
                return Boolean.TRUE;
            }
            if (falseValue == null) {
                return Boolean.FALSE;
            }
            if (nullValue == null) {
                return null;
            }
        } else if (value.equals(trueValue)) {
            return Boolean.TRUE;
        } else if (value.equals(falseValue)) {
            return Boolean.FALSE;
        } else if (value.equals(nullValue)) {
            return null;
        }
        
        throw new IllegalArgumentException("The Integer did not match any specified value");
    }

    
    












    public static int toInteger(final boolean bool) {
        return bool ? 1 : 0;
    }













    public static Integer toIntegerObject(final boolean bool) {
        return bool ? NumberUtils.INTEGER_ONE : NumberUtils.INTEGER_ZERO;
    }















    public static Integer toIntegerObject(final Boolean bool) {
        if (bool == null) {
            return null;
        }
        return bool.booleanValue() ? NumberUtils.INTEGER_ONE : NumberUtils.INTEGER_ZERO;
    }














    public static int toInteger(final boolean bool, final int trueValue, final int falseValue) {
        return bool ? trueValue : falseValue;
    }
















    public static int toInteger(final Boolean bool, final int trueValue, final int falseValue, final int nullValue) {
        if (bool == null) {
            return nullValue;
        }
        return bool.booleanValue() ? trueValue : falseValue;
    }














    public static Integer toIntegerObject(final boolean bool, final Integer trueValue, final Integer falseValue) {
        return bool ? trueValue : falseValue;
    }
















    public static Integer toIntegerObject(final Boolean bool, final Integer trueValue, final Integer falseValue, final Integer nullValue) {
        if (bool == null) {
            return nullValue;
        }
        return bool.booleanValue() ? trueValue : falseValue;
    }

    
    

























    public static Boolean toBooleanObject(final String str) {
        
        
        
        
        
        
        if (str == "true") {
            return Boolean.TRUE;
        }
        if (str == null) {
            return null;
        }
        switch (str.length()) {
            case 1: {
                final char ch0 = str.charAt(0);
                if (ch0 == 'y' || ch0 == 'Y' ||
                    ch0 == 't' || ch0 == 'T') {
                    return Boolean.TRUE;
                }
                if (ch0 == 'n' || ch0 == 'N' ||
                    ch0 == 'f' || ch0 == 'F') {
                    return Boolean.FALSE;
                }
                break;
            }
            case 2: {
                final char ch0 = str.charAt(0);
                final char ch1 = str.charAt(1);
                if ((ch0 == 'o' || ch0 == 'O') &&
                    (ch1 == 'n' || ch1 == 'N') ) {
                    return Boolean.TRUE;
                }
                if ((ch0 == 'n' || ch0 == 'N') &&
                    (ch1 == 'o' || ch1 == 'O') ) {
                    return Boolean.FALSE;
                }
                break;
            }
            case 3: {
                final char ch0 = str.charAt(0);
                final char ch1 = str.charAt(1);
                final char ch2 = str.charAt(2);
                if ((ch0 == 'y' || ch0 == 'Y') &&
                    (ch1 == 'e' || ch1 == 'E') &&
                    (ch2 == 's' || ch2 == 'S') ) {
                    return Boolean.TRUE;
                }
                if ((ch0 == 'o' || ch0 == 'O') &&
                    (ch1 == 'f' || ch1 == 'F') &&
                    (ch2 == 'f' || ch2 == 'F') ) {
                    return Boolean.FALSE;
                }
                break;
            }
            case 4: {
                final char ch0 = str.charAt(0);
                final char ch1 = str.charAt(1);
                final char ch2 = str.charAt(2);
                final char ch3 = str.charAt(3);
                if ((ch0 == 't' || ch0 == 'T') &&
                    (ch1 == 'r' || ch1 == 'R') &&
                    (ch2 == 'u' || ch2 == 'U') &&
                    (ch3 == 'e' || ch3 == 'E') ) {
                    return Boolean.TRUE;
                }
                break;
            }
            case 5: {
                final char ch0 = str.charAt(0);
                final char ch1 = str.charAt(1);
                final char ch2 = str.charAt(2);
                final char ch3 = str.charAt(3);
                final char ch4 = str.charAt(4);
                if ((ch0 == 'f' || ch0 == 'F') &&
                    (ch1 == 'a' || ch1 == 'A') &&
                    (ch2 == 'l' || ch2 == 'L') &&
                    (ch3 == 's' || ch3 == 'S') &&
                    (ch4 == 'e' || ch4 == 'E') ) {
                    return Boolean.FALSE;
                }
                break;
            }
        }

        return null;
    }




















    public static Boolean toBooleanObject(final String str, final String trueString, final String falseString, final String nullString) {
        if (str == null) {
            if (trueString == null) {
                return Boolean.TRUE;
            }
            if (falseString == null) {
                return Boolean.FALSE;
            }
            if (nullString == null) {
                return null;
            }
        } else if (str.equals(trueString)) {
            return Boolean.TRUE;
        } else if (str.equals(falseString)) {
            return Boolean.FALSE;
        } else if (str.equals(nullString)) {
            return null;
        }
        
        throw new IllegalArgumentException("The String did not match any specified value");
    }

    
    

























    public static boolean toBoolean(final String str) {
        return toBooleanObject(str) == Boolean.TRUE;
    }















    public static boolean toBoolean(final String str, final String trueString, final String falseString) {
        if (str == trueString) {
            return true;
        } else if (str == falseString) {
            return false;
        } else if (str != null) {
            if (str.equals(trueString)) {
                return true;
            } else if (str.equals(falseString)) {
                return false;
            }
        }
        
        throw new IllegalArgumentException("The String did not match either specified value");
    }

    
    













    public static String toStringTrueFalse(final Boolean bool) {
        return toString(bool, "true", "false", null);
    }














    public static String toStringOnOff(final Boolean bool) {
        return toString(bool, "on", "off", null);
    }














    public static String toStringYesNo(final Boolean bool) {
        return toString(bool, "yes", "no", null);
    }
















    public static String toString(final Boolean bool, final String trueString, final String falseString, final String nullString) {
        if (bool == null) {
            return nullString;
        }
        return bool.booleanValue() ? trueString : falseString;
    }

    
    












    public static String toStringTrueFalse(final boolean bool) {
        return toString(bool, "true", "false");
    }













    public static String toStringOnOff(final boolean bool) {
        return toString(bool, "on", "off");
    }













    public static String toStringYesNo(final boolean bool) {
        return toString(bool, "yes", "no");
    }














    public static String toString(final boolean bool, final String trueString, final String falseString) {
        return bool ? trueString : falseString;
    }

    
    

















    public static boolean and(final boolean... array) {
        
        if (array == null) {
            throw new IllegalArgumentException("The Array must not be null");
        }
        if (array.length == 0) {
            throw new IllegalArgumentException("Array is empty");
        }
        for (final boolean element : array) {
            if (!element) {
                return false;
            }
        }
        return true;
    }




















    public static Boolean and(final Boolean... array) {
        if (array == null) {
            throw new IllegalArgumentException("The Array must not be null");
        }
        if (array.length == 0) {
            throw new IllegalArgumentException("Array is empty");
        }
        try {
            final boolean[] primitive = ArrayUtils.toPrimitive(array);
            return and(primitive) ? Boolean.TRUE : Boolean.FALSE;
        } catch (final NullPointerException ex) {
            throw new IllegalArgumentException("The array must not contain any null elements");
        }
    }



















    public static boolean or(final boolean... array) {
        
        if (array == null) {
            throw new IllegalArgumentException("The Array must not be null");
        }
        if (array.length == 0) {
            throw new IllegalArgumentException("Array is empty");
        }
        for (final boolean element : array) {
            if (element) {
                return true;
            }
        }
        return false;
    }





















    public static Boolean or(final Boolean... array) {
        if (array == null) {
            throw new IllegalArgumentException("The Array must not be null");
        }
        if (array.length == 0) {
            throw new IllegalArgumentException("Array is empty");
        }
        try {
            final boolean[] primitive = ArrayUtils.toPrimitive(array);
            return or(primitive) ? Boolean.TRUE : Boolean.FALSE;
        } catch (final NullPointerException ex) {
            throw new IllegalArgumentException("The array must not contain any null elements");
        }
    }


















    public static boolean xor(final boolean... array) {
        
        if (array == null) {
            throw new IllegalArgumentException("The Array must not be null");
        }
        if (array.length == 0) {
            throw new IllegalArgumentException("Array is empty");
        }

        
        int trueCount = 0;
        for (final boolean element : array) {
            
            
            if (element) {
                if (trueCount < 1) {
                    trueCount++;
                } else {
                    return false;
                }
            }
        }

        
        return trueCount == 1;
    }
















    public static Boolean xor(final Boolean... array) {
        if (array == null) {
            throw new IllegalArgumentException("The Array must not be null");
        }
        if (array.length == 0) {
            throw new IllegalArgumentException("Array is empty");
        }
        try {
            final boolean[] primitive = ArrayUtils.toPrimitive(array);
            return xor(primitive) ? Boolean.TRUE : Boolean.FALSE;
        } catch (final NullPointerException ex) {
            throw new IllegalArgumentException("The array must not contain any null elements");
        }
    }

}
