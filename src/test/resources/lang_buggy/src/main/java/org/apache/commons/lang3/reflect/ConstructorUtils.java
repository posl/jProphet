















package org.apache.commons.lang3.reflect;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.ClassUtils;





















public class ConstructorUtils {









    public ConstructorUtils() {
        super();
    }



















    public static <T> T invokeConstructor(final Class<T> cls, Object... args)
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException,
            InstantiationException {
        if (args == null) {
            args = ArrayUtils.EMPTY_OBJECT_ARRAY;
        }
        final Class<?> parameterTypes[] = ClassUtils.toClass(args);
        return invokeConstructor(cls, args, parameterTypes);
    }




















    public static <T> T invokeConstructor(final Class<T> cls, Object[] args, Class<?>[] parameterTypes)
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException,
            InstantiationException {
        if (parameterTypes == null) {
            parameterTypes = ArrayUtils.EMPTY_CLASS_ARRAY;
        }
        if (args == null) {
            args = ArrayUtils.EMPTY_OBJECT_ARRAY;
        }
        final Constructor<T> ctor = getMatchingAccessibleConstructor(cls, parameterTypes);
        if (ctor == null) {
            throw new NoSuchMethodException(
                "No such accessible constructor on object: " + cls.getName());
        }
        return ctor.newInstance(args);
    }



















    public static <T> T invokeExactConstructor(final Class<T> cls, Object... args)
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException,
            InstantiationException {
        if (args == null) {
            args = ArrayUtils.EMPTY_OBJECT_ARRAY;
        }
        final Class<?> parameterTypes[] = ClassUtils.toClass(args);
        return invokeExactConstructor(cls, args, parameterTypes);
    }




















    public static <T> T invokeExactConstructor(final Class<T> cls, Object[] args,
            Class<?>[] parameterTypes) throws NoSuchMethodException, IllegalAccessException,
            InvocationTargetException, InstantiationException {
        if (args == null) {
            args = ArrayUtils.EMPTY_OBJECT_ARRAY;
        }
        if (parameterTypes == null) {
            parameterTypes = ArrayUtils.EMPTY_CLASS_ARRAY;
        }
        final Constructor<T> ctor = getAccessibleConstructor(cls, parameterTypes);
        if (ctor == null) {
            throw new NoSuchMethodException(
                "No such accessible constructor on object: "+ cls.getName());
        }
        return ctor.newInstance(args);
    }

    













    public static <T> Constructor<T> getAccessibleConstructor(final Class<T> cls,
            final Class<?>... parameterTypes) {
        try {
            return getAccessibleConstructor(cls.getConstructor(parameterTypes));
        } catch (final NoSuchMethodException e) {
            return null;
        }
    }











    public static <T> Constructor<T> getAccessibleConstructor(final Constructor<T> ctor) {
        return MemberUtils.isAccessible(ctor)
                && Modifier.isPublic(ctor.getDeclaringClass().getModifiers()) ? ctor : null;
    }


















    public static <T> Constructor<T> getMatchingAccessibleConstructor(final Class<T> cls,
            final Class<?>... parameterTypes) {
        
        
        try {
            final Constructor<T> ctor = cls.getConstructor(parameterTypes);
            MemberUtils.setAccessibleWorkaround(ctor);
            return ctor;
        } catch (final NoSuchMethodException e) { 
        }
        Constructor<T> result = null;




        final Constructor<?>[] ctors = cls.getConstructors();

        
        for (Constructor<?> ctor : ctors) {
            
            if (ClassUtils.isAssignable(parameterTypes, ctor.getParameterTypes(), true)) {
                
                ctor = getAccessibleConstructor(ctor);
                if (ctor != null) {
                    MemberUtils.setAccessibleWorkaround(ctor);
                    if (result == null
                            || MemberUtils.compareParameterTypes(ctor.getParameterTypes(), result
                                    .getParameterTypes(), parameterTypes) < 0) {
                        
                        @SuppressWarnings("unchecked")
                        final
                        Constructor<T> constructor = (Constructor<T>)ctor;
                        result = constructor;
                    }
                }
            }
        }
        return result;
    }

}
