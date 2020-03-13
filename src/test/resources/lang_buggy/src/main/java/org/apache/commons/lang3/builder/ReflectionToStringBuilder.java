
















package org.apache.commons.lang3.builder;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.ClassUtils;


























































public class ReflectionToStringBuilder extends ToStringBuilder {























    public static String toString(final Object object) {
        return toString(object, null, false, false, null);
    }





























    public static String toString(final Object object, final ToStringStyle style) {
        return toString(object, style, false, false, null);
    }



































    public static String toString(final Object object, final ToStringStyle style, final boolean outputTransients) {
        return toString(object, style, outputTransients, false, null);
    }











































    public static String toString(final Object object, final ToStringStyle style, final boolean outputTransients, final boolean outputStatics) {
        return toString(object, style, outputTransients, outputStatics, null);
    }
















































    public static <T> String toString(
            final T object, final ToStringStyle style, final boolean outputTransients,
            final boolean outputStatics, final Class<? super T> reflectUpToClass) {
        return new ReflectionToStringBuilder(object, style, null, reflectUpToClass, outputTransients, outputStatics)
                .toString();
    }










    public static String toStringExclude(final Object object, final Collection<String> excludeFieldNames) {
        return toStringExclude(object, toNoNullStringArray(excludeFieldNames));
    }










    static String[] toNoNullStringArray(final Collection<String> collection) {
        if (collection == null) {
            return ArrayUtils.EMPTY_STRING_ARRAY;
        }
        return toNoNullStringArray(collection.toArray());
    }










    static String[] toNoNullStringArray(final Object[] array) {
        final List<String> list = new ArrayList<String>(array.length);
        for (final Object e : array) {
            if (e != null) {
                list.add(e.toString());
            }
        }
        return list.toArray(ArrayUtils.EMPTY_STRING_ARRAY);
    }











    public static String toStringExclude(final Object object, final String... excludeFieldNames) {
        return new ReflectionToStringBuilder(object).setExcludeFieldNames(excludeFieldNames).toString();
    }




    private boolean appendStatics = false;




    private boolean appendTransients = false;






    protected String[] excludeFieldNames;




    private Class<?> upToClass = null;















    public ReflectionToStringBuilder(final Object object) {
        super(object);
    }

















    public ReflectionToStringBuilder(final Object object, final ToStringStyle style) {
        super(object, style);
    }























    public ReflectionToStringBuilder(final Object object, final ToStringStyle style, final StringBuffer buffer) {
        super(object, style, buffer);
    }




















    public <T> ReflectionToStringBuilder(
            final T object, final ToStringStyle style, final StringBuffer buffer,
            final Class<? super T> reflectUpToClass, final boolean outputTransients, final boolean outputStatics) {
        super(object, style, buffer);
        this.setUpToClass(reflectUpToClass);
        this.setAppendTransients(outputTransients);
        this.setAppendStatics(outputStatics);
    }













    protected boolean accept(final Field field) {
        if (field.getName().indexOf(ClassUtils.INNER_CLASS_SEPARATOR_CHAR) != -1) {
            
            return false;
        }
        if (Modifier.isTransient(field.getModifiers()) && !this.isAppendTransients()) {
            
            return false;
        }
        if (Modifier.isStatic(field.getModifiers()) && !this.isAppendStatics()) {
            
            return false;
        }
        if (this.excludeFieldNames != null
            && Arrays.binarySearch(this.excludeFieldNames, field.getName()) >= 0) {
            
            return false;
        }
        return true;
    }














    protected void appendFieldsIn(final Class<?> clazz) {
        if (clazz.isArray()) {
            this.reflectionAppendArray(this.getObject());
            return;
        }
        final Field[] fields = clazz.getDeclaredFields();
        AccessibleObject.setAccessible(fields, true);
        for (final Field field : fields) {
            final String fieldName = field.getName();
            if (this.accept(field)) {
                try {
                    
                    
                    final Object fieldValue = this.getValue(field);
                    this.append(fieldName, fieldValue);
                } catch (final IllegalAccessException ex) {
                    
                    
                    
                    
                    throw new InternalError("Unexpected IllegalAccessException: " + ex.getMessage());
                }
            }
        }
    }




    public String[] getExcludeFieldNames() {
        return this.excludeFieldNames.clone();
    }








    public Class<?> getUpToClass() {
        return this.upToClass;
    }

















    protected Object getValue(final Field field) throws IllegalArgumentException, IllegalAccessException {
        return field.get(this.getObject());
    }









    public boolean isAppendStatics() {
        return this.appendStatics;
    }








    public boolean isAppendTransients() {
        return this.appendTransients;
    }










    public ReflectionToStringBuilder reflectionAppendArray(final Object array) {
        this.getStyle().reflectionAppendArrayDetail(this.getStringBuffer(), null, array);
        return this;
    }










    public void setAppendStatics(final boolean appendStatics) {
        this.appendStatics = appendStatics;
    }









    public void setAppendTransients(final boolean appendTransients) {
        this.appendTransients = appendTransients;
    }








    public ReflectionToStringBuilder setExcludeFieldNames(final String... excludeFieldNamesParam) {
        if (excludeFieldNamesParam == null) {
            this.excludeFieldNames = null;
        } else {
            
            this.excludeFieldNames = toNoNullStringArray(excludeFieldNamesParam);
            Arrays.sort(this.excludeFieldNames);
        }
        return this;
    }









    public void setUpToClass(final Class<?> clazz) {
        if (clazz != null) {
            final Object object = getObject();
            if (object != null && clazz.isInstance(object) == false) {
                throw new IllegalArgumentException("Specified class is not a superclass of the object");
            }
        }
        this.upToClass = clazz;
    }








    @Override
    public String toString() {
        if (this.getObject() == null) {
            return this.getStyle().getNullText();
        }
        Class<?> clazz = this.getObject().getClass();
        this.appendFieldsIn(clazz);
        while (clazz.getSuperclass() != null && clazz != this.getUpToClass()) {
            clazz = clazz.getSuperclass();
            this.appendFieldsIn(clazz);
        }
        return super.toString();
    }

}
