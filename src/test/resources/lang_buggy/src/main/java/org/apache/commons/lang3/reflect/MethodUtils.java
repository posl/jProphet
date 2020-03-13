















package org.apache.commons.lang3.reflect;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.ClassUtils;






















public class MethodUtils {









    public MethodUtils() {
        super();
    }























    public static Object invokeMethod(final Object object, final String methodName,
            Object... args) throws NoSuchMethodException,
            IllegalAccessException, InvocationTargetException {
        if (args == null) {
            args = ArrayUtils.EMPTY_OBJECT_ARRAY;
        }
        final Class<?>[] parameterTypes = ClassUtils.toClass(args);
        return invokeMethod(object, methodName, args, parameterTypes);
    }




















    public static Object invokeMethod(final Object object, final String methodName,
            Object[] args, Class<?>[] parameterTypes)
            throws NoSuchMethodException, IllegalAccessException,
            InvocationTargetException {
        if (parameterTypes == null) {
            parameterTypes = ArrayUtils.EMPTY_CLASS_ARRAY;
        }
        if (args == null) {
            args = ArrayUtils.EMPTY_OBJECT_ARRAY;
        }
        final Method method = getMatchingAccessibleMethod(object.getClass(),
                methodName, parameterTypes);
        if (method == null) {
            throw new NoSuchMethodException("No such accessible method: "
                    + methodName + "() on object: "
                    + object.getClass().getName());
        }
        return method.invoke(object, args);
    }



















    public static Object invokeExactMethod(final Object object, final String methodName,
            Object... args) throws NoSuchMethodException,
            IllegalAccessException, InvocationTargetException {
        if (args == null) {
            args = ArrayUtils.EMPTY_OBJECT_ARRAY;
        }
        final Class<?>[] parameterTypes = ClassUtils.toClass(args);
        return invokeExactMethod(object, methodName, args, parameterTypes);
    }




















    public static Object invokeExactMethod(final Object object, final String methodName,
            Object[] args, Class<?>[] parameterTypes)
            throws NoSuchMethodException, IllegalAccessException,
            InvocationTargetException {
        if (args == null) {
            args = ArrayUtils.EMPTY_OBJECT_ARRAY;
        }
        if (parameterTypes == null) {
            parameterTypes = ArrayUtils.EMPTY_CLASS_ARRAY;
        }
        final Method method = getAccessibleMethod(object.getClass(), methodName,
                parameterTypes);
        if (method == null) {
            throw new NoSuchMethodException("No such accessible method: "
                    + methodName + "() on object: "
                    + object.getClass().getName());
        }
        return method.invoke(object, args);
    }




















    public static Object invokeExactStaticMethod(final Class<?> cls, final String methodName,
            Object[] args, Class<?>[] parameterTypes)
            throws NoSuchMethodException, IllegalAccessException,
            InvocationTargetException {
        if (args == null) {
            args = ArrayUtils.EMPTY_OBJECT_ARRAY;
        }
        if (parameterTypes == null) {
            parameterTypes = ArrayUtils.EMPTY_CLASS_ARRAY;
        }
        final Method method = getAccessibleMethod(cls, methodName, parameterTypes);
        if (method == null) {
            throw new NoSuchMethodException("No such accessible method: "
                    + methodName + "() on class: " + cls.getName());
        }
        return method.invoke(null, args);
    }

























    public static Object invokeStaticMethod(final Class<?> cls, final String methodName,
            Object... args) throws NoSuchMethodException,
            IllegalAccessException, InvocationTargetException {
        if (args == null) {
            args = ArrayUtils.EMPTY_OBJECT_ARRAY;
        }
        final Class<?>[] parameterTypes = ClassUtils.toClass(args);
        return invokeStaticMethod(cls, methodName, args, parameterTypes);
    }























    public static Object invokeStaticMethod(final Class<?> cls, final String methodName,
            Object[] args, Class<?>[] parameterTypes)
            throws NoSuchMethodException, IllegalAccessException,
            InvocationTargetException {
        if (parameterTypes == null) {
            parameterTypes = ArrayUtils.EMPTY_CLASS_ARRAY;
        }
        if (args == null) {
            args = ArrayUtils.EMPTY_OBJECT_ARRAY;
        }
        final Method method = getMatchingAccessibleMethod(cls, methodName,
                parameterTypes);
        if (method == null) {
            throw new NoSuchMethodException("No such accessible method: "
                    + methodName + "() on class: " + cls.getName());
        }
        return method.invoke(null, args);
    }



















    public static Object invokeExactStaticMethod(final Class<?> cls, final String methodName,
            Object... args) throws NoSuchMethodException,
            IllegalAccessException, InvocationTargetException {
        if (args == null) {
            args = ArrayUtils.EMPTY_OBJECT_ARRAY;
        }
        final Class<?>[] parameterTypes = ClassUtils.toClass(args);
        return invokeExactStaticMethod(cls, methodName, args, parameterTypes);
    }













    public static Method getAccessibleMethod(final Class<?> cls, final String methodName,
            final Class<?>... parameterTypes) {
        try {
            return getAccessibleMethod(cls.getMethod(methodName,
                    parameterTypes));
        } catch (final NoSuchMethodException e) {
            return null;
        }
    }









    public static Method getAccessibleMethod(Method method) {
        if (!MemberUtils.isAccessible(method)) {
            return null;
        }
        
        final Class<?> cls = method.getDeclaringClass();
        if (Modifier.isPublic(cls.getModifiers())) {
            return method;
        }
        final String methodName = method.getName();
        final Class<?>[] parameterTypes = method.getParameterTypes();

        
        method = getAccessibleMethodFromInterfaceNest(cls, methodName,
                parameterTypes);

        
        if (method == null) {
            method = getAccessibleMethodFromSuperclass(cls, methodName,
                    parameterTypes);
        }
        return method;
    }











    private static Method getAccessibleMethodFromSuperclass(final Class<?> cls,
            final String methodName, final Class<?>... parameterTypes) {
        Class<?> parentClass = cls.getSuperclass();
        while (parentClass != null) {
            if (Modifier.isPublic(parentClass.getModifiers())) {
                try {
                    return parentClass.getMethod(methodName, parameterTypes);
                } catch (final NoSuchMethodException e) {
                    return null;
                }
            }
            parentClass = parentClass.getSuperclass();
        }
        return null;
    }
















    private static Method getAccessibleMethodFromInterfaceNest(Class<?> cls,
            final String methodName, final Class<?>... parameterTypes) {
        Method method = null;

        
        for (; cls != null; cls = cls.getSuperclass()) {

            
            final Class<?>[] interfaces = cls.getInterfaces();
            for (int i = 0; i < interfaces.length; i++) {
                
                if (!Modifier.isPublic(interfaces[i].getModifiers())) {
                    continue;
                }
                
                try {
                    method = interfaces[i].getDeclaredMethod(methodName,
                            parameterTypes);
                } catch (final NoSuchMethodException e) { 




                }
                if (method != null) {
                    break;
                }
                
                method = getAccessibleMethodFromInterfaceNest(interfaces[i],
                        methodName, parameterTypes);
                if (method != null) {
                    break;
                }
            }
        }
        return method;
    }





















    public static Method getMatchingAccessibleMethod(final Class<?> cls,
            final String methodName, final Class<?>... parameterTypes) {
        try {
            final Method method = cls.getMethod(methodName, parameterTypes);
            MemberUtils.setAccessibleWorkaround(method);
            return method;
        } catch (final NoSuchMethodException e) { 
        }
        
        Method bestMatch = null;
        final Method[] methods = cls.getMethods();
        for (final Method method : methods) {
            
            if (method.getName().equals(methodName) && ClassUtils.isAssignable(parameterTypes, method.getParameterTypes(), true)) {
                
                final Method accessibleMethod = getAccessibleMethod(method);
                if (accessibleMethod != null && (bestMatch == null || MemberUtils.compareParameterTypes(
                            accessibleMethod.getParameterTypes(),
                            bestMatch.getParameterTypes(),
                            parameterTypes) < 0)) {
                        bestMatch = accessibleMethod;
                 }
            }
        }
        if (bestMatch != null) {
            MemberUtils.setAccessibleWorkaround(bestMatch);
        }
        return bestMatch;
    }
}
