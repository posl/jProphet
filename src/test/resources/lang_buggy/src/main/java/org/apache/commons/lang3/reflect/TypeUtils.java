















package org.apache.commons.lang3.reflect;

import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.ClassUtils;








public class TypeUtils {








    public TypeUtils() {
        super();
    }











    public static boolean isAssignable(final Type type, final Type toType) {
        return isAssignable(type, toType, null);
    }










    private static boolean isAssignable(final Type type, final Type toType,
            final Map<TypeVariable<?>, Type> typeVarAssigns) {
        if (toType == null || toType instanceof Class<?>) {
            return isAssignable(type, (Class<?>) toType);
        }

        if (toType instanceof ParameterizedType) {
            return isAssignable(type, (ParameterizedType) toType, typeVarAssigns);
        }

        if (toType instanceof GenericArrayType) {
            return isAssignable(type, (GenericArrayType) toType, typeVarAssigns);
        }

        if (toType instanceof WildcardType) {
            return isAssignable(type, (WildcardType) toType, typeVarAssigns);
        }

        
        if (toType instanceof TypeVariable<?>) {
            return isAssignable(type, (TypeVariable<?>) toType, typeVarAssigns);
        }


        throw new IllegalStateException("found an unhandled type: " + toType);
    }









    private static boolean isAssignable(final Type type, final Class<?> toClass) {
        if (type == null) {
            
            return toClass == null || !toClass.isPrimitive();
        }

        
        
        if (toClass == null) {
            return false;
        }

        
        if (toClass.equals(type)) {
            return true;
        }

        if (type instanceof Class<?>) {
            
            return ClassUtils.isAssignable((Class<?>) type, toClass);
        }

        if (type instanceof ParameterizedType) {
            
            return isAssignable(getRawType((ParameterizedType) type), toClass);
        }

        
        if (type instanceof TypeVariable<?>) {
            
            
            for (final Type bound : ((TypeVariable<?>) type).getBounds()) {
                if (isAssignable(bound, toClass)) {
                    return true;
                }
            }

            return false;
        }

        
        
        if (type instanceof GenericArrayType) {
            return toClass.equals(Object.class)
                    || toClass.isArray()
                    && isAssignable(((GenericArrayType) type).getGenericComponentType(), toClass
                            .getComponentType());
        }

        
        
        if (type instanceof WildcardType) {
            return false;
        }

        throw new IllegalStateException("found an unhandled type: " + type);
    }










    private static boolean isAssignable(final Type type, final ParameterizedType toParameterizedType,
            final Map<TypeVariable<?>, Type> typeVarAssigns) {
        if (type == null) {
            return true;
        }

        
        
        if (toParameterizedType == null) {
            return false;
        }

        
        if (toParameterizedType.equals(type)) {
            return true;
        }

        
        final Class<?> toClass = getRawType(toParameterizedType);
        
        
        final Map<TypeVariable<?>, Type> fromTypeVarAssigns = getTypeArguments(type, toClass, null);

        
        if (fromTypeVarAssigns == null) {
            return false;
        }

        
        
        
        if (fromTypeVarAssigns.isEmpty()) {
            return true;
        }

        
        final Map<TypeVariable<?>, Type> toTypeVarAssigns = getTypeArguments(toParameterizedType,
                toClass, typeVarAssigns);

        
        for (final TypeVariable<?> var : toTypeVarAssigns.keySet()) {
            final Type toTypeArg = unrollVariableAssignments(var, toTypeVarAssigns);
            final Type fromTypeArg = unrollVariableAssignments(var, fromTypeVarAssigns);

            
            
            
            if (fromTypeArg != null
                    && !toTypeArg.equals(fromTypeArg)
                    && !(toTypeArg instanceof WildcardType && isAssignable(fromTypeArg, toTypeArg,
                            typeVarAssigns))) {
                return false;
            }
        }

        return true;
    }

    private static Type unrollVariableAssignments(TypeVariable<?> var, final Map<TypeVariable<?>, Type> typeVarAssigns) {
        Type result;
        do {
            result = typeVarAssigns.get(var);
            if (result instanceof TypeVariable<?> && !result.equals(var)) {
                var = (TypeVariable<?>) result;
                continue;
            }
            break;
        } while (true);
        return result;
    }











    private static boolean isAssignable(final Type type, final GenericArrayType toGenericArrayType,
            final Map<TypeVariable<?>, Type> typeVarAssigns) {
        if (type == null) {
            return true;
        }

        
        
        if (toGenericArrayType == null) {
            return false;
        }

        
        if (toGenericArrayType.equals(type)) {
            return true;
        }

        final Type toComponentType = toGenericArrayType.getGenericComponentType();

        if (type instanceof Class<?>) {
            final Class<?> cls = (Class<?>) type;

            
            return cls.isArray()
                    && isAssignable(cls.getComponentType(), toComponentType, typeVarAssigns);
        }

        if (type instanceof GenericArrayType) {
            
            return isAssignable(((GenericArrayType) type).getGenericComponentType(),
                    toComponentType, typeVarAssigns);
        }

        if (type instanceof WildcardType) {
            
            for (final Type bound : getImplicitUpperBounds((WildcardType) type)) {
                if (isAssignable(bound, toGenericArrayType)) {
                    return true;
                }
            }

            return false;
        }

        if (type instanceof TypeVariable<?>) {
            
            
            for (final Type bound : getImplicitBounds((TypeVariable<?>) type)) {
                if (isAssignable(bound, toGenericArrayType)) {
                    return true;
                }
            }

            return false;
        }

        if (type instanceof ParameterizedType) {
            
            
            
            return false;
        }

        throw new IllegalStateException("found an unhandled type: " + type);
    }











    private static boolean isAssignable(final Type type, final WildcardType toWildcardType,
            final Map<TypeVariable<?>, Type> typeVarAssigns) {
        if (type == null) {
            return true;
        }

        
        
        if (toWildcardType == null) {
            return false;
        }

        
        if (toWildcardType.equals(type)) {
            return true;
        }

        final Type[] toUpperBounds = getImplicitUpperBounds(toWildcardType);
        final Type[] toLowerBounds = getImplicitLowerBounds(toWildcardType);

        if (type instanceof WildcardType) {
            final WildcardType wildcardType = (WildcardType) type;
            final Type[] upperBounds = getImplicitUpperBounds(wildcardType);
            final Type[] lowerBounds = getImplicitLowerBounds(wildcardType);

            for (Type toBound : toUpperBounds) {
                
                
                toBound = substituteTypeVariables(toBound, typeVarAssigns);

                
                
                
                for (final Type bound : upperBounds) {
                    if (!isAssignable(bound, toBound, typeVarAssigns)) {
                        return false;
                    }
                }
            }

            for (Type toBound : toLowerBounds) {
                
                
                toBound = substituteTypeVariables(toBound, typeVarAssigns);

                
                
                
                for (final Type bound : lowerBounds) {
                    if (!isAssignable(toBound, bound, typeVarAssigns)) {
                        return false;
                    }
                }
            }

            return true;
        }

        for (final Type toBound : toUpperBounds) {
            
            
            if (!isAssignable(type, substituteTypeVariables(toBound, typeVarAssigns),
                    typeVarAssigns)) {
                return false;
            }
        }

        for (final Type toBound : toLowerBounds) {
            
            
            if (!isAssignable(substituteTypeVariables(toBound, typeVarAssigns), type,
                    typeVarAssigns)) {
                return false;
            }
        }

        return true;
    }











    private static boolean isAssignable(final Type type, final TypeVariable<?> toTypeVariable,
            final Map<TypeVariable<?>, Type> typeVarAssigns) {
        if (type == null) {
            return true;
        }

        
        
        if (toTypeVariable == null) {
            return false;
        }

        
        if (toTypeVariable.equals(type)) {
            return true;
        }

        if (type instanceof TypeVariable<?>) {
            
            
            
            final Type[] bounds = getImplicitBounds((TypeVariable<?>) type);

            for (final Type bound : bounds) {
                if (isAssignable(bound, toTypeVariable, typeVarAssigns)) {
                    return true;
                }
            }
        }

        if (type instanceof Class<?> || type instanceof ParameterizedType
                || type instanceof GenericArrayType || type instanceof WildcardType) {
            return false;
        }

        throw new IllegalStateException("found an unhandled type: " + type);
    }









    private static Type substituteTypeVariables(final Type type, final Map<TypeVariable<?>, Type> typeVarAssigns) {
        if (type instanceof TypeVariable<?> && typeVarAssigns != null) {
            final Type replacementType = typeVarAssigns.get(type);

            if (replacementType == null) {
                throw new IllegalArgumentException("missing assignment type for type variable "
                        + type);
            }

            return replacementType;
        }

        return type;
    }












    public static Map<TypeVariable<?>, Type> getTypeArguments(final ParameterizedType type) {
        return getTypeArguments(type, getRawType(type), null);
    }

































    public static Map<TypeVariable<?>, Type> getTypeArguments(final Type type, final Class<?> toClass) {
        return getTypeArguments(type, toClass, null);
    }









    private static Map<TypeVariable<?>, Type> getTypeArguments(final Type type, final Class<?> toClass,
            final Map<TypeVariable<?>, Type> subtypeVarAssigns) {
        if (type instanceof Class<?>) {
            return getTypeArguments((Class<?>) type, toClass, subtypeVarAssigns);
        }

        if (type instanceof ParameterizedType) {
            return getTypeArguments((ParameterizedType) type, toClass, subtypeVarAssigns);
        }

        if (type instanceof GenericArrayType) {
            return getTypeArguments(((GenericArrayType) type).getGenericComponentType(), toClass
                    .isArray() ? toClass.getComponentType() : toClass, subtypeVarAssigns);
        }

        
        
        if (type instanceof WildcardType) {
            for (final Type bound : getImplicitUpperBounds((WildcardType) type)) {
                
                if (isAssignable(bound, toClass)) {
                    return getTypeArguments(bound, toClass, subtypeVarAssigns);
                }
            }

            return null;
        }

        
        if (type instanceof TypeVariable<?>) {
            for (final Type bound : getImplicitBounds((TypeVariable<?>) type)) {
                
                if (isAssignable(bound, toClass)) {
                    return getTypeArguments(bound, toClass, subtypeVarAssigns);
                }
            }

            return null;
        }


        throw new IllegalStateException("found an unhandled type: " + type);
    }









    private static Map<TypeVariable<?>, Type> getTypeArguments(
            final ParameterizedType parameterizedType, final Class<?> toClass,
            final Map<TypeVariable<?>, Type> subtypeVarAssigns) {
        final Class<?> cls = getRawType(parameterizedType);

        
        if (!isAssignable(cls, toClass)) {
            return null;
        }

        final Type ownerType = parameterizedType.getOwnerType();
        Map<TypeVariable<?>, Type> typeVarAssigns;

        if (ownerType instanceof ParameterizedType) {
            
            final ParameterizedType parameterizedOwnerType = (ParameterizedType) ownerType;
            typeVarAssigns = getTypeArguments(parameterizedOwnerType,
                    getRawType(parameterizedOwnerType), subtypeVarAssigns);
        } else {
            
            typeVarAssigns = subtypeVarAssigns == null ? new HashMap<TypeVariable<?>, Type>()
                    : new HashMap<TypeVariable<?>, Type>(subtypeVarAssigns);
        }

        
        final Type[] typeArgs = parameterizedType.getActualTypeArguments();
        
        final TypeVariable<?>[] typeParams = cls.getTypeParameters();

        
        for (int i = 0; i < typeParams.length; i++) {
            final Type typeArg = typeArgs[i];
            typeVarAssigns.put(typeParams[i], typeVarAssigns.containsKey(typeArg) ? typeVarAssigns
                    .get(typeArg) : typeArg);
        }

        if (toClass.equals(cls)) {
            
            return typeVarAssigns;
        }

        
        return getTypeArguments(getClosestParentType(cls, toClass), toClass, typeVarAssigns);
    }









    private static Map<TypeVariable<?>, Type> getTypeArguments(Class<?> cls, final Class<?> toClass,
            final Map<TypeVariable<?>, Type> subtypeVarAssigns) {
        
        if (!isAssignable(cls, toClass)) {
            return null;
        }

        
        if (cls.isPrimitive()) {
            
            if (toClass.isPrimitive()) {
                
                
                return new HashMap<TypeVariable<?>, Type>();
            }

            
            cls = ClassUtils.primitiveToWrapper(cls);
        }

        
        final HashMap<TypeVariable<?>, Type> typeVarAssigns = subtypeVarAssigns == null ? new HashMap<TypeVariable<?>, Type>()
                : new HashMap<TypeVariable<?>, Type>(subtypeVarAssigns);

        
        if (toClass.equals(cls)) {
            return typeVarAssigns;
        }

        
        return getTypeArguments(getClosestParentType(cls, toClass), toClass, typeVarAssigns);
    }




























    public static Map<TypeVariable<?>, Type> determineTypeArguments(final Class<?> cls,
            final ParameterizedType superType) {
        final Class<?> superClass = getRawType(superType);

        
        if (!isAssignable(cls, superClass)) {
            return null;
        }

        if (cls.equals(superClass)) {
            return getTypeArguments(superType, superClass, null);
        }

        
        final Type midType = getClosestParentType(cls, superClass);

        
        if (midType instanceof Class<?>) {
            return determineTypeArguments((Class<?>) midType, superType);
        }

        final ParameterizedType midParameterizedType = (ParameterizedType) midType;
        final Class<?> midClass = getRawType(midParameterizedType);
        
        
        final Map<TypeVariable<?>, Type> typeVarAssigns = determineTypeArguments(midClass, superType);
        
        mapTypeVariablesToArguments(cls, midParameterizedType, typeVarAssigns);

        return typeVarAssigns;
    }









    private static <T> void mapTypeVariablesToArguments(final Class<T> cls,
            final ParameterizedType parameterizedType, final Map<TypeVariable<?>, Type> typeVarAssigns) {
        
        final Type ownerType = parameterizedType.getOwnerType();

        if (ownerType instanceof ParameterizedType) {
            
            mapTypeVariablesToArguments(cls, (ParameterizedType) ownerType, typeVarAssigns);
        }

        
        
        
        
        final Type[] typeArgs = parameterizedType.getActualTypeArguments();

        
        
        final TypeVariable<?>[] typeVars = getRawType(parameterizedType).getTypeParameters();

        
        final List<TypeVariable<Class<T>>> typeVarList = Arrays.asList(cls
                .getTypeParameters());

        for (int i = 0; i < typeArgs.length; i++) {
            final TypeVariable<?> typeVar = typeVars[i];
            final Type typeArg = typeArgs[i];

            
            if (typeVarList.contains(typeArg)
            
                    
                    && typeVarAssigns.containsKey(typeVar)) {
                
                typeVarAssigns.put((TypeVariable<?>) typeArg, typeVarAssigns.get(typeVar));
            }
        }
    }









    private static Type getClosestParentType(final Class<?> cls, final Class<?> superClass) {
        
        if (superClass.isInterface()) {
            
            final Type[] interfaceTypes = cls.getGenericInterfaces();
            
            Type genericInterface = null;

            
            for (final Type midType : interfaceTypes) {
                Class<?> midClass = null;

                if (midType instanceof ParameterizedType) {
                    midClass = getRawType((ParameterizedType) midType);
                } else if (midType instanceof Class<?>) {
                    midClass = (Class<?>) midType;
                } else {
                    throw new IllegalStateException("Unexpected generic"
                            + " interface type found: " + midType);
                }

                
                
                if (isAssignable(midClass, superClass)
                        && isAssignable(genericInterface, (Type) midClass)) {
                    genericInterface = midType;
                }
            }

            
            if (genericInterface != null) {
                return genericInterface;
            }
        }

        
        
        return cls.getGenericSuperclass();
    }









    public static boolean isInstance(final Object value, final Type type) {
        if (type == null) {
            return false;
        }

        return value == null ? !(type instanceof Class<?>) || !((Class<?>) type).isPrimitive()
                : isAssignable(value.getClass(), type, null);
    }






















    public static Type[] normalizeUpperBounds(final Type[] bounds) {
        
        if (bounds.length < 2) {
            return bounds;
        }

        final Set<Type> types = new HashSet<Type>(bounds.length);

        for (final Type type1 : bounds) {
            boolean subtypeFound = false;

            for (final Type type2 : bounds) {
                if (type1 != type2 && isAssignable(type2, type1, null)) {
                    subtypeFound = true;
                    break;
                }
            }

            if (!subtypeFound) {
                types.add(type1);
            }
        }

        return types.toArray(new Type[types.size()]);
    }










    public static Type[] getImplicitBounds(final TypeVariable<?> typeVariable) {
        final Type[] bounds = typeVariable.getBounds();

        return bounds.length == 0 ? new Type[] { Object.class } : normalizeUpperBounds(bounds);
    }











    public static Type[] getImplicitUpperBounds(final WildcardType wildcardType) {
        final Type[] bounds = wildcardType.getUpperBounds();

        return bounds.length == 0 ? new Type[] { Object.class } : normalizeUpperBounds(bounds);
    }










    public static Type[] getImplicitLowerBounds(final WildcardType wildcardType) {
        final Type[] bounds = wildcardType.getLowerBounds();

        return bounds.length == 0 ? new Type[] { null } : bounds;
    }














    public static boolean typesSatisfyVariables(final Map<TypeVariable<?>, Type> typeVarAssigns) {
        
        
        for (final Map.Entry<TypeVariable<?>, Type> entry : typeVarAssigns.entrySet()) {
            final TypeVariable<?> typeVar = entry.getKey();
            final Type type = entry.getValue();

            for (final Type bound : getImplicitBounds(typeVar)) {
                if (!isAssignable(type, substituteTypeVariables(bound, typeVarAssigns),
                        typeVarAssigns)) {
                    return false;
                }
            }
        }

        return true;
    }








    private static Class<?> getRawType(final ParameterizedType parameterizedType) {
        final Type rawType = parameterizedType.getRawType();

        
        
        
        
        
        if (!(rawType instanceof Class<?>)) {
            throw new IllegalStateException("Wait... What!? Type of rawType: " + rawType);
        }

        return (Class<?>) rawType;
    }













    public static Class<?> getRawType(final Type type, final Type assigningType) {
        if (type instanceof Class<?>) {
            
            return (Class<?>) type;
        }

        if (type instanceof ParameterizedType) {
            
            return getRawType((ParameterizedType) type);
        }

        if (type instanceof TypeVariable<?>) {
            if (assigningType == null) {
                return null;
            }

            
            final Object genericDeclaration = ((TypeVariable<?>) type).getGenericDeclaration();

            
            
            if (!(genericDeclaration instanceof Class<?>)) {
                return null;
            }

            
            
            final Map<TypeVariable<?>, Type> typeVarAssigns = getTypeArguments(assigningType,
                    (Class<?>) genericDeclaration);

            
            
            if (typeVarAssigns == null) {
                return null;
            }

            
            final Type typeArgument = typeVarAssigns.get(type);

            if (typeArgument == null) {
                return null;
            }

            
            return getRawType(typeArgument, assigningType);
        }

        if (type instanceof GenericArrayType) {
            
            final Class<?> rawComponentType = getRawType(((GenericArrayType) type)
                    .getGenericComponentType(), assigningType);

            
            return Array.newInstance(rawComponentType, 0).getClass();
        }

        
        if (type instanceof WildcardType) {
            return null;
        }

        throw new IllegalArgumentException("unknown type: " + type);
    }






    public static boolean isArrayType(final Type type) {
        return type instanceof GenericArrayType || type instanceof Class<?> && ((Class<?>) type).isArray();
    }






    public static Type getArrayComponentType(final Type type) {
        if (type instanceof Class<?>) {
            final Class<?> clazz = (Class<?>) type;
            return clazz.isArray() ? clazz.getComponentType() : null;
        }
        if (type instanceof GenericArrayType) {
            return ((GenericArrayType) type).getGenericComponentType();
        }
        return null;
    }

}
