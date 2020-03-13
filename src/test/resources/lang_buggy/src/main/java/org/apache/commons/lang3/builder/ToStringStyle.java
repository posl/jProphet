















package org.apache.commons.lang3.builder;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Map;
import java.util.WeakHashMap;

import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.SystemUtils;






































public abstract class ToStringStyle implements Serializable {




    private static final long serialVersionUID = -2587890625525655916L;









    public static final ToStringStyle DEFAULT_STYLE = new DefaultToStringStyle();













    public static final ToStringStyle MULTI_LINE_STYLE = new MultiLineToStringStyle();










    public static final ToStringStyle NO_FIELD_NAMES_STYLE = new NoFieldNameToStringStyle();











    public static final ToStringStyle SHORT_PREFIX_STYLE = new ShortPrefixToStringStyle();









    public static final ToStringStyle SIMPLE_STYLE = new SimpleToStringStyle();







    private static final ThreadLocal<WeakHashMap<Object, Object>> REGISTRY =
        new ThreadLocal<WeakHashMap<Object,Object>>();


















    static Map<Object, Object> getRegistry() {
        return REGISTRY.get();
    }












    static boolean isRegistered(final Object value) {
        final Map<Object, Object> m = getRegistry();
        return m != null && m.containsKey(value);
    }










    static void register(final Object value) {
        if (value != null) {
            final Map<Object, Object> m = getRegistry();
            if (m == null) {
                REGISTRY.set(new WeakHashMap<Object, Object>());
            }
            getRegistry().put(value, null);
        }
    }













    static void unregister(final Object value) {
        if (value != null) {
            final Map<Object, Object> m = getRegistry();
            if (m != null) {
                m.remove(value);
                if (m.isEmpty()) {
                    REGISTRY.remove();
                }
            }
        }
    }




    private boolean useFieldNames = true;




    private boolean useClassName = true;




    private boolean useShortClassName = false;




    private boolean useIdentityHashCode = true;




    private String contentStart = "[";




    private String contentEnd = "]";




    private String fieldNameValueSeparator = "=";




    private boolean fieldSeparatorAtStart = false;




    private boolean fieldSeparatorAtEnd = false;




    private String fieldSeparator = ",";




    private String arrayStart = "{";




    private String arraySeparator = ",";




    private boolean arrayContentDetail = true;




    private String arrayEnd = "}";





    private boolean defaultFullDetail = true;




    private String nullText = "<null>";




    private String sizeStartText = "<size=";




    private String sizeEndText = ">";




    private String summaryObjectStartText = "<";




    private String summaryObjectEndText = ">";

    




    protected ToStringStyle() {
        super();
    }

    











    public void appendSuper(final StringBuffer buffer, final String superToString) {
        appendToString(buffer, superToString);
    }











    public void appendToString(final StringBuffer buffer, final String toString) {
        if (toString != null) {
            final int pos1 = toString.indexOf(contentStart) + contentStart.length();
            final int pos2 = toString.lastIndexOf(contentEnd);
            if (pos1 != pos2 && pos1 >= 0 && pos2 >= 0) {
                final String data = toString.substring(pos1, pos2);
                if (fieldSeparatorAtStart) {
                    removeLastFieldSeparator(buffer);
                }
                buffer.append(data);
                appendFieldSeparator(buffer);
            }
        }
    }







    public void appendStart(final StringBuffer buffer, final Object object) {
        if (object != null) {
            appendClassName(buffer, object);
            appendIdentityHashCode(buffer, object);
            appendContentStart(buffer);
            if (fieldSeparatorAtStart) {
                appendFieldSeparator(buffer);
            }
        }
    }








    public void appendEnd(final StringBuffer buffer, final Object object) {
        if (this.fieldSeparatorAtEnd == false) {
            removeLastFieldSeparator(buffer);
        }
        appendContentEnd(buffer);
        unregister(object);
    }







    protected void removeLastFieldSeparator(final StringBuffer buffer) {
        final int len = buffer.length();
        final int sepLen = fieldSeparator.length();
        if (len > 0 && sepLen > 0 && len >= sepLen) {
            boolean match = true;
            for (int i = 0; i < sepLen; i++) {
                if (buffer.charAt(len - 1 - i) != fieldSeparator.charAt(sepLen - 1 - i)) {
                    match = false;
                    break;
                }
            }
            if (match) {
                buffer.setLength(len - sepLen);
            }
        }
    }

    












    public void append(final StringBuffer buffer, final String fieldName, final Object value, final Boolean fullDetail) {
        appendFieldStart(buffer, fieldName);

        if (value == null) {
            appendNullText(buffer, fieldName);

        } else {
            appendInternal(buffer, fieldName, value, isFullDetail(fullDetail));
        }

        appendFieldEnd(buffer, fieldName);
    }




















    protected void appendInternal(final StringBuffer buffer, final String fieldName, final Object value, final boolean detail) {
        if (isRegistered(value)
            && !(value instanceof Number || value instanceof Boolean || value instanceof Character)) {
           appendCyclicObject(buffer, fieldName, value);
           return;
        }

        register(value);

        try {
            if (value instanceof Collection<?>) {
                if (detail) {
                    appendDetail(buffer, fieldName, (Collection<?>) value);
                } else {
                    appendSummarySize(buffer, fieldName, ((Collection<?>) value).size());
                }

            } else if (value instanceof Map<?, ?>) {
                if (detail) {
                    appendDetail(buffer, fieldName, (Map<?, ?>) value);
                } else {
                    appendSummarySize(buffer, fieldName, ((Map<?, ?>) value).size());
                }

            } else if (value instanceof long[]) {
                if (detail) {
                    appendDetail(buffer, fieldName, (long[]) value);
                } else {
                    appendSummary(buffer, fieldName, (long[]) value);
                }

            } else if (value instanceof int[]) {
                if (detail) {
                    appendDetail(buffer, fieldName, (int[]) value);
                } else {
                    appendSummary(buffer, fieldName, (int[]) value);
                }

            } else if (value instanceof short[]) {
                if (detail) {
                    appendDetail(buffer, fieldName, (short[]) value);
                } else {
                    appendSummary(buffer, fieldName, (short[]) value);
                }

            } else if (value instanceof byte[]) {
                if (detail) {
                    appendDetail(buffer, fieldName, (byte[]) value);
                } else {
                    appendSummary(buffer, fieldName, (byte[]) value);
                }

            } else if (value instanceof char[]) {
                if (detail) {
                    appendDetail(buffer, fieldName, (char[]) value);
                } else {
                    appendSummary(buffer, fieldName, (char[]) value);
                }

            } else if (value instanceof double[]) {
                if (detail) {
                    appendDetail(buffer, fieldName, (double[]) value);
                } else {
                    appendSummary(buffer, fieldName, (double[]) value);
                }

            } else if (value instanceof float[]) {
                if (detail) {
                    appendDetail(buffer, fieldName, (float[]) value);
                } else {
                    appendSummary(buffer, fieldName, (float[]) value);
                }

            } else if (value instanceof boolean[]) {
                if (detail) {
                    appendDetail(buffer, fieldName, (boolean[]) value);
                } else {
                    appendSummary(buffer, fieldName, (boolean[]) value);
                }

            } else if (value.getClass().isArray()) {
                if (detail) {
                    appendDetail(buffer, fieldName, (Object[]) value);
                } else {
                    appendSummary(buffer, fieldName, (Object[]) value);
                }

            } else {
                if (detail) {
                    appendDetail(buffer, fieldName, value);
                } else {
                    appendSummary(buffer, fieldName, value);
                }
            }
        } finally {
            unregister(value);
        }
    }













    protected void appendCyclicObject(final StringBuffer buffer, final String fieldName, final Object value) {
       ObjectUtils.identityToString(buffer, value);
    }










    protected void appendDetail(final StringBuffer buffer, final String fieldName, final Object value) {
        buffer.append(value);
    }









    protected void appendDetail(final StringBuffer buffer, final String fieldName, final Collection<?> coll) {
        buffer.append(coll);
    }









    protected void appendDetail(final StringBuffer buffer, final String fieldName, final Map<?, ?> map) {
        buffer.append(map);
    }










    protected void appendSummary(final StringBuffer buffer, final String fieldName, final Object value) {
        buffer.append(summaryObjectStartText);
        buffer.append(getShortClassName(value.getClass()));
        buffer.append(summaryObjectEndText);
    }

    









    public void append(final StringBuffer buffer, final String fieldName, final long value) {
        appendFieldStart(buffer, fieldName);
        appendDetail(buffer, fieldName, value);
        appendFieldEnd(buffer, fieldName);
    }









    protected void appendDetail(final StringBuffer buffer, final String fieldName, final long value) {
        buffer.append(value);
    }

    









    public void append(final StringBuffer buffer, final String fieldName, final int value) {
        appendFieldStart(buffer, fieldName);
        appendDetail(buffer, fieldName, value);
        appendFieldEnd(buffer, fieldName);
    }









    protected void appendDetail(final StringBuffer buffer, final String fieldName, final int value) {
        buffer.append(value);
    }

    









    public void append(final StringBuffer buffer, final String fieldName, final short value) {
        appendFieldStart(buffer, fieldName);
        appendDetail(buffer, fieldName, value);
        appendFieldEnd(buffer, fieldName);
    }









    protected void appendDetail(final StringBuffer buffer, final String fieldName, final short value) {
        buffer.append(value);
    }

    









    public void append(final StringBuffer buffer, final String fieldName, final byte value) {
        appendFieldStart(buffer, fieldName);
        appendDetail(buffer, fieldName, value);
        appendFieldEnd(buffer, fieldName);
    }









    protected void appendDetail(final StringBuffer buffer, final String fieldName, final byte value) {
        buffer.append(value);
    }

    









    public void append(final StringBuffer buffer, final String fieldName, final char value) {
        appendFieldStart(buffer, fieldName);
        appendDetail(buffer, fieldName, value);
        appendFieldEnd(buffer, fieldName);
    }









    protected void appendDetail(final StringBuffer buffer, final String fieldName, final char value) {
        buffer.append(value);
    }

    









    public void append(final StringBuffer buffer, final String fieldName, final double value) {
        appendFieldStart(buffer, fieldName);
        appendDetail(buffer, fieldName, value);
        appendFieldEnd(buffer, fieldName);
    }









    protected void appendDetail(final StringBuffer buffer, final String fieldName, final double value) {
        buffer.append(value);
    }

    









    public void append(final StringBuffer buffer, final String fieldName, final float value) {
        appendFieldStart(buffer, fieldName);
        appendDetail(buffer, fieldName, value);
        appendFieldEnd(buffer, fieldName);
    }









    protected void appendDetail(final StringBuffer buffer, final String fieldName, final float value) {
        buffer.append(value);
    }

    









    public void append(final StringBuffer buffer, final String fieldName, final boolean value) {
        appendFieldStart(buffer, fieldName);
        appendDetail(buffer, fieldName, value);
        appendFieldEnd(buffer, fieldName);
    }









    protected void appendDetail(final StringBuffer buffer, final String fieldName, final boolean value) {
        buffer.append(value);
    }











    public void append(final StringBuffer buffer, final String fieldName, final Object[] array, final Boolean fullDetail) {
        appendFieldStart(buffer, fieldName);

        if (array == null) {
            appendNullText(buffer, fieldName);

        } else if (isFullDetail(fullDetail)) {
            appendDetail(buffer, fieldName, array);

        } else {
            appendSummary(buffer, fieldName, array);
        }

        appendFieldEnd(buffer, fieldName);
    }

    










    protected void appendDetail(final StringBuffer buffer, final String fieldName, final Object[] array) {
        buffer.append(arrayStart);
        for (int i = 0; i < array.length; i++) {
            final Object item = array[i];
            if (i > 0) {
                buffer.append(arraySeparator);
            }
            if (item == null) {
                appendNullText(buffer, fieldName);

            } else {
                appendInternal(buffer, fieldName, item, arrayContentDetail);
            }
        }
        buffer.append(arrayEnd);
    }










    protected void reflectionAppendArrayDetail(final StringBuffer buffer, final String fieldName, final Object array) {
        buffer.append(arrayStart);
        final int length = Array.getLength(array);
        for (int i = 0; i < length; i++) {
            final Object item = Array.get(array, i);
            if (i > 0) {
                buffer.append(arraySeparator);
            }
            if (item == null) {
                appendNullText(buffer, fieldName);

            } else {
                appendInternal(buffer, fieldName, item, arrayContentDetail);
            }
        }
        buffer.append(arrayEnd);
    }










    protected void appendSummary(final StringBuffer buffer, final String fieldName, final Object[] array) {
        appendSummarySize(buffer, fieldName, array.length);
    }

    











    public void append(final StringBuffer buffer, final String fieldName, final long[] array, final Boolean fullDetail) {
        appendFieldStart(buffer, fieldName);

        if (array == null) {
            appendNullText(buffer, fieldName);

        } else if (isFullDetail(fullDetail)) {
            appendDetail(buffer, fieldName, array);

        } else {
            appendSummary(buffer, fieldName, array);
        }

        appendFieldEnd(buffer, fieldName);
    }










    protected void appendDetail(final StringBuffer buffer, final String fieldName, final long[] array) {
        buffer.append(arrayStart);
        for (int i = 0; i < array.length; i++) {
            if (i > 0) {
                buffer.append(arraySeparator);
            }
            appendDetail(buffer, fieldName, array[i]);
        }
        buffer.append(arrayEnd);
    }










    protected void appendSummary(final StringBuffer buffer, final String fieldName, final long[] array) {
        appendSummarySize(buffer, fieldName, array.length);
    }

    











    public void append(final StringBuffer buffer, final String fieldName, final int[] array, final Boolean fullDetail) {
        appendFieldStart(buffer, fieldName);

        if (array == null) {
            appendNullText(buffer, fieldName);

        } else if (isFullDetail(fullDetail)) {
            appendDetail(buffer, fieldName, array);

        } else {
            appendSummary(buffer, fieldName, array);
        }

        appendFieldEnd(buffer, fieldName);
    }










    protected void appendDetail(final StringBuffer buffer, final String fieldName, final int[] array) {
        buffer.append(arrayStart);
        for (int i = 0; i < array.length; i++) {
            if (i > 0) {
                buffer.append(arraySeparator);
            }
            appendDetail(buffer, fieldName, array[i]);
        }
        buffer.append(arrayEnd);
    }










    protected void appendSummary(final StringBuffer buffer, final String fieldName, final int[] array) {
        appendSummarySize(buffer, fieldName, array.length);
    }

    











    public void append(final StringBuffer buffer, final String fieldName, final short[] array, final Boolean fullDetail) {
        appendFieldStart(buffer, fieldName);

        if (array == null) {
            appendNullText(buffer, fieldName);

        } else if (isFullDetail(fullDetail)) {
            appendDetail(buffer, fieldName, array);

        } else {
            appendSummary(buffer, fieldName, array);
        }

        appendFieldEnd(buffer, fieldName);
    }










    protected void appendDetail(final StringBuffer buffer, final String fieldName, final short[] array) {
        buffer.append(arrayStart);
        for (int i = 0; i < array.length; i++) {
            if (i > 0) {
                buffer.append(arraySeparator);
            }
            appendDetail(buffer, fieldName, array[i]);
        }
        buffer.append(arrayEnd);
    }










    protected void appendSummary(final StringBuffer buffer, final String fieldName, final short[] array) {
        appendSummarySize(buffer, fieldName, array.length);
    }

    











    public void append(final StringBuffer buffer, final String fieldName, final byte[] array, final Boolean fullDetail) {
        appendFieldStart(buffer, fieldName);

        if (array == null) {
            appendNullText(buffer, fieldName);

        } else if (isFullDetail(fullDetail)) {
            appendDetail(buffer, fieldName, array);

        } else {
            appendSummary(buffer, fieldName, array);
        }

        appendFieldEnd(buffer, fieldName);
    }










    protected void appendDetail(final StringBuffer buffer, final String fieldName, final byte[] array) {
        buffer.append(arrayStart);
        for (int i = 0; i < array.length; i++) {
            if (i > 0) {
                buffer.append(arraySeparator);
            }
            appendDetail(buffer, fieldName, array[i]);
        }
        buffer.append(arrayEnd);
    }










    protected void appendSummary(final StringBuffer buffer, final String fieldName, final byte[] array) {
        appendSummarySize(buffer, fieldName, array.length);
    }

    











    public void append(final StringBuffer buffer, final String fieldName, final char[] array, final Boolean fullDetail) {
        appendFieldStart(buffer, fieldName);

        if (array == null) {
            appendNullText(buffer, fieldName);

        } else if (isFullDetail(fullDetail)) {
            appendDetail(buffer, fieldName, array);

        } else {
            appendSummary(buffer, fieldName, array);
        }

        appendFieldEnd(buffer, fieldName);
    }










    protected void appendDetail(final StringBuffer buffer, final String fieldName, final char[] array) {
        buffer.append(arrayStart);
        for (int i = 0; i < array.length; i++) {
            if (i > 0) {
                buffer.append(arraySeparator);
            }
            appendDetail(buffer, fieldName, array[i]);
        }
        buffer.append(arrayEnd);
    }










    protected void appendSummary(final StringBuffer buffer, final String fieldName, final char[] array) {
        appendSummarySize(buffer, fieldName, array.length);
    }

    











    public void append(final StringBuffer buffer, final String fieldName, final double[] array, final Boolean fullDetail) {
        appendFieldStart(buffer, fieldName);

        if (array == null) {
            appendNullText(buffer, fieldName);

        } else if (isFullDetail(fullDetail)) {
            appendDetail(buffer, fieldName, array);

        } else {
            appendSummary(buffer, fieldName, array);
        }

        appendFieldEnd(buffer, fieldName);
    }










    protected void appendDetail(final StringBuffer buffer, final String fieldName, final double[] array) {
        buffer.append(arrayStart);
        for (int i = 0; i < array.length; i++) {
            if (i > 0) {
                buffer.append(arraySeparator);
            }
            appendDetail(buffer, fieldName, array[i]);
        }
        buffer.append(arrayEnd);
    }










    protected void appendSummary(final StringBuffer buffer, final String fieldName, final double[] array) {
        appendSummarySize(buffer, fieldName, array.length);
    }

    











    public void append(final StringBuffer buffer, final String fieldName, final float[] array, final Boolean fullDetail) {
        appendFieldStart(buffer, fieldName);

        if (array == null) {
            appendNullText(buffer, fieldName);

        } else if (isFullDetail(fullDetail)) {
            appendDetail(buffer, fieldName, array);

        } else {
            appendSummary(buffer, fieldName, array);
        }

        appendFieldEnd(buffer, fieldName);
    }










    protected void appendDetail(final StringBuffer buffer, final String fieldName, final float[] array) {
        buffer.append(arrayStart);
        for (int i = 0; i < array.length; i++) {
            if (i > 0) {
                buffer.append(arraySeparator);
            }
            appendDetail(buffer, fieldName, array[i]);
        }
        buffer.append(arrayEnd);
    }










    protected void appendSummary(final StringBuffer buffer, final String fieldName, final float[] array) {
        appendSummarySize(buffer, fieldName, array.length);
    }

    











    public void append(final StringBuffer buffer, final String fieldName, final boolean[] array, final Boolean fullDetail) {
        appendFieldStart(buffer, fieldName);

        if (array == null) {
            appendNullText(buffer, fieldName);

        } else if (isFullDetail(fullDetail)) {
            appendDetail(buffer, fieldName, array);

        } else {
            appendSummary(buffer, fieldName, array);
        }

        appendFieldEnd(buffer, fieldName);
    }










    protected void appendDetail(final StringBuffer buffer, final String fieldName, final boolean[] array) {
        buffer.append(arrayStart);
        for (int i = 0; i < array.length; i++) {
            if (i > 0) {
                buffer.append(arraySeparator);
            }
            appendDetail(buffer, fieldName, array[i]);
        }
        buffer.append(arrayEnd);
    }










    protected void appendSummary(final StringBuffer buffer, final String fieldName, final boolean[] array) {
        appendSummarySize(buffer, fieldName, array.length);
    }

    







    protected void appendClassName(final StringBuffer buffer, final Object object) {
        if (useClassName && object != null) {
            register(object);
            if (useShortClassName) {
                buffer.append(getShortClassName(object.getClass()));
            } else {
                buffer.append(object.getClass().getName());
            }
        }
    }







    protected void appendIdentityHashCode(final StringBuffer buffer, final Object object) {
        if (this.isUseIdentityHashCode() && object!=null) {
            register(object);
            buffer.append('@');
            buffer.append(Integer.toHexString(System.identityHashCode(object)));
        }
    }






    protected void appendContentStart(final StringBuffer buffer) {
        buffer.append(contentStart);
    }






    protected void appendContentEnd(final StringBuffer buffer) {
        buffer.append(contentEnd);
    }









    protected void appendNullText(final StringBuffer buffer, final String fieldName) {
        buffer.append(nullText);
    }






    protected void appendFieldSeparator(final StringBuffer buffer) {
        buffer.append(fieldSeparator);
    }







    protected void appendFieldStart(final StringBuffer buffer, final String fieldName) {
        if (useFieldNames && fieldName != null) {
            buffer.append(fieldName);
            buffer.append(fieldNameValueSeparator);
        }
    }







    protected void appendFieldEnd(final StringBuffer buffer, final String fieldName) {
        appendFieldSeparator(buffer);
    }
















    protected void appendSummarySize(final StringBuffer buffer, final String fieldName, final int size) {
        buffer.append(sizeStartText);
        buffer.append(size);
        buffer.append(sizeEndText);
    }















    protected boolean isFullDetail(final Boolean fullDetailRequest) {
        if (fullDetailRequest == null) {
            return defaultFullDetail;
        }
        return fullDetailRequest.booleanValue();
    }










    protected String getShortClassName(final Class<?> cls) {
        return ClassUtils.getShortClassName(cls);
    }

    
    
    
    






    protected boolean isUseClassName() {
        return useClassName;
    }






    protected void setUseClassName(final boolean useClassName) {
        this.useClassName = useClassName;
    }

    







    protected boolean isUseShortClassName() {
        return useShortClassName;
    }







    protected void setUseShortClassName(final boolean useShortClassName) {
        this.useShortClassName = useShortClassName;
    }

    






    protected boolean isUseIdentityHashCode() {
        return useIdentityHashCode;
    }






    protected void setUseIdentityHashCode(final boolean useIdentityHashCode) {
        this.useIdentityHashCode = useIdentityHashCode;
    }

    






    protected boolean isUseFieldNames() {
        return useFieldNames;
    }






    protected void setUseFieldNames(final boolean useFieldNames) {
        this.useFieldNames = useFieldNames;
    }

    







    protected boolean isDefaultFullDetail() {
        return defaultFullDetail;
    }







    protected void setDefaultFullDetail(final boolean defaultFullDetail) {
        this.defaultFullDetail = defaultFullDetail;
    }

    






    protected boolean isArrayContentDetail() {
        return arrayContentDetail;
    }






    protected void setArrayContentDetail(final boolean arrayContentDetail) {
        this.arrayContentDetail = arrayContentDetail;
    }

    






    protected String getArrayStart() {
        return arrayStart;
    }









    protected void setArrayStart(String arrayStart) {
        if (arrayStart == null) {
            arrayStart = "";
        }
        this.arrayStart = arrayStart;
    }

    






    protected String getArrayEnd() {
        return arrayEnd;
    }









    protected void setArrayEnd(String arrayEnd) {
        if (arrayEnd == null) {
            arrayEnd = "";
        }
        this.arrayEnd = arrayEnd;
    }

    






    protected String getArraySeparator() {
        return arraySeparator;
    }









    protected void setArraySeparator(String arraySeparator) {
        if (arraySeparator == null) {
            arraySeparator = "";
        }
        this.arraySeparator = arraySeparator;
    }

    






    protected String getContentStart() {
        return contentStart;
    }









    protected void setContentStart(String contentStart) {
        if (contentStart == null) {
            contentStart = "";
        }
        this.contentStart = contentStart;
    }

    






    protected String getContentEnd() {
        return contentEnd;
    }









    protected void setContentEnd(String contentEnd) {
        if (contentEnd == null) {
            contentEnd = "";
        }
        this.contentEnd = contentEnd;
    }

    






    protected String getFieldNameValueSeparator() {
        return fieldNameValueSeparator;
    }









    protected void setFieldNameValueSeparator(String fieldNameValueSeparator) {
        if (fieldNameValueSeparator == null) {
            fieldNameValueSeparator = "";
        }
        this.fieldNameValueSeparator = fieldNameValueSeparator;
    }

    






    protected String getFieldSeparator() {
        return fieldSeparator;
    }









    protected void setFieldSeparator(String fieldSeparator) {
        if (fieldSeparator == null) {
            fieldSeparator = "";
        }
        this.fieldSeparator = fieldSeparator;
    }

    








    protected boolean isFieldSeparatorAtStart() {
        return fieldSeparatorAtStart;
    }








    protected void setFieldSeparatorAtStart(final boolean fieldSeparatorAtStart) {
        this.fieldSeparatorAtStart = fieldSeparatorAtStart;
    }

    








    protected boolean isFieldSeparatorAtEnd() {
        return fieldSeparatorAtEnd;
    }








    protected void setFieldSeparatorAtEnd(final boolean fieldSeparatorAtEnd) {
        this.fieldSeparatorAtEnd = fieldSeparatorAtEnd;
    }

    






    protected String getNullText() {
        return nullText;
    }









    protected void setNullText(String nullText) {
        if (nullText == null) {
            nullText = "";
        }
        this.nullText = nullText;
    }

    









    protected String getSizeStartText() {
        return sizeStartText;
    }












    protected void setSizeStartText(String sizeStartText) {
        if (sizeStartText == null) {
            sizeStartText = "";
        }
        this.sizeStartText = sizeStartText;
    }

    









    protected String getSizeEndText() {
        return sizeEndText;
    }












    protected void setSizeEndText(String sizeEndText) {
        if (sizeEndText == null) {
            sizeEndText = "";
        }
        this.sizeEndText = sizeEndText;
    }

    









    protected String getSummaryObjectStartText() {
        return summaryObjectStartText;
    }












    protected void setSummaryObjectStartText(String summaryObjectStartText) {
        if (summaryObjectStartText == null) {
            summaryObjectStartText = "";
        }
        this.summaryObjectStartText = summaryObjectStartText;
    }

    









    protected String getSummaryObjectEndText() {
        return summaryObjectEndText;
    }












    protected void setSummaryObjectEndText(String summaryObjectEndText) {
        if (summaryObjectEndText == null) {
            summaryObjectEndText = "";
        }
        this.summaryObjectEndText = summaryObjectEndText;
    }

    







    private static final class DefaultToStringStyle extends ToStringStyle {






        private static final long serialVersionUID = 1L;






        DefaultToStringStyle() {
            super();
        }






        private Object readResolve() {
            return ToStringStyle.DEFAULT_STYLE;
        }

    }

    








    private static final class NoFieldNameToStringStyle extends ToStringStyle {

        private static final long serialVersionUID = 1L;






        NoFieldNameToStringStyle() {
            super();
            this.setUseFieldNames(false);
        }






        private Object readResolve() {
            return ToStringStyle.NO_FIELD_NAMES_STYLE;
        }

    }

    








    private static final class ShortPrefixToStringStyle extends ToStringStyle {

        private static final long serialVersionUID = 1L;






        ShortPrefixToStringStyle() {
            super();
            this.setUseShortClassName(true);
            this.setUseIdentityHashCode(false);
        }





        private Object readResolve() {
            return ToStringStyle.SHORT_PREFIX_STYLE;
        }

    }








    private static final class SimpleToStringStyle extends ToStringStyle {

        private static final long serialVersionUID = 1L;






        SimpleToStringStyle() {
            super();
            this.setUseClassName(false);
            this.setUseIdentityHashCode(false);
            this.setUseFieldNames(false);
            this.setContentStart("");
            this.setContentEnd("");
        }





        private Object readResolve() {
            return ToStringStyle.SIMPLE_STYLE;
        }

    }

    







    private static final class MultiLineToStringStyle extends ToStringStyle {

        private static final long serialVersionUID = 1L;






        MultiLineToStringStyle() {
            super();
            this.setContentStart("[");
            this.setFieldSeparator(SystemUtils.LINE_SEPARATOR + "  ");
            this.setFieldSeparatorAtStart(true);
            this.setContentEnd(SystemUtils.LINE_SEPARATOR + "]");
        }






        private Object readResolve() {
            return ToStringStyle.MULTI_LINE_STYLE;
        }

    }

}
