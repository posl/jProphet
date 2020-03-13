
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at

 *      http:

 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.

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


 * <p> Utility methods focusing on type inspection, particularly with regard to
 * generics. </p>

 * @since 3.0
 * @version $Id$

public class TypeUtils {

    
     * <p> TypeUtils instances should NOT be constructed in standard
     * programming. Instead, the class should be used as
     * <code>TypeUtils.isAssignable(cls, toClass)</code>. </p> <p> This
     * constructor is public to permit tools that require a JavaBean instance to
     * operate. </p>

    public TypeUtils() {
        super();
    }

    
     * <p> Checks if the subject type may be implicitly cast to the target type
     * following the Java generics rules. If both types are {@link Class}
     * objects, the method returns the result of
     * {@link ClassUtils#isAssignable(Class, Class)}. </p>
    
     * @param type the subject type to be assigned to the target type
     * @param toType the target type
     * @return <code>true</code> if <code>type</code> is assignable to <code>toType</code>.

    public static boolean isAssignable(final Type type, final Type toType) {
        return isAssignable(type, toType, null);
    }

    
     * <p> Checks if the subject type may be implicitly cast to the target type
     * following the Java generics rules. </p>
    
     * @param type the subject type to be assigned to the target type
     * @param toType the target type
     * @param typeVarAssigns optional map of type variable assignments
     * @return <code>true</code> if <code>type</code> is assignable to <code>toType</code>.

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

    
     * <p> Checks if the subject type may be implicitly cast to the target class
     * following the Java generics rules. </p>
    
     * @param type the subject type to be assigned to the target type
     * @param toClass the target class
     * @return true if <code>type</code> is assignable to <code>toClass</code>.

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

    
     * <p> Checks if the subject type may be implicitly cast to the target
     * parameterized type following the Java generics rules. </p>
    
     * @param type the subject type to be assigned to the target type
     * @param toParameterizedType the target parameterized type
     * @param typeVarAssigns a map with type variables
     * @return true if <code>type</code> is assignable to <code>toType</code>.

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

    
     * <p> Checks if the subject type may be implicitly cast to the target
     * generic array type following the Java generics rules. </p>
    
     * @param type the subject type to be assigned to the target type
     * @param toGenericArrayType the target generic array type
     * @param typeVarAssigns a map with type variables
     * @return true if <code>type</code> is assignable to
     * <code>toGenericArrayType</code>.

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

    
     * <p> Checks if the subject type may be implicitly cast to the target
     * wildcard type following the Java generics rules. </p>
    
     * @param type the subject type to be assigned to the target type
     * @param toWildcardType the target wildcard type
     * @param typeVarAssigns a map with type variables
     * @return true if <code>type</code> is assignable to
     * <code>toWildcardType</code>.

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

    
     * <p> Checks if the subject type may be implicitly cast to the target type
     * variable following the Java generics rules. </p>
    
     * @param type the subject type to be assigned to the target type
     * @param toTypeVariable the target type variable
     * @param typeVarAssigns a map with type variables
     * @return true if <code>type</code> is assignable to
     * <code>toTypeVariable</code>.

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

    
     * <p> </p>
    
     * @param type the type to be replaced
     * @param typeVarAssigns the map with type variables
     * @return the replaced type
     * @throws IllegalArgumentException if the type cannot be substituted

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

    
     * <p> Retrieves all the type arguments for this parameterized type
     * including owner hierarchy arguments such as <code>
     * Outer<K,V>.Inner<T>.DeepInner<E></code> . The arguments are returned in a
     * {@link Map} specifying the argument type for each {@link TypeVariable}.
     * </p>
    
     * @param type specifies the subject parameterized type from which to
     * harvest the parameters.
     * @return a map of the type arguments to their respective type variables.

    public static Map<TypeVariable<?>, Type> getTypeArguments(final ParameterizedType type) {
        return getTypeArguments(type, getRawType(type), null);
    }

    
     * <p> Gets the type arguments of a class/interface based on a subtype. For
     * instance, this method will determine that both of the parameters for the
     * interface {@link Map} are {@link Object} for the subtype
     * {@link java.util.Properties Properties} even though the subtype does not
     * directly implement the <code>Map</code> interface. <p> </p> This method
     * returns <code>null</code> if <code>type</code> is not assignable to
     * <code>toClass</code>. It returns an empty map if none of the classes or
     * interfaces in its inheritance hierarchy specify any type arguments. </p>
     * <p> A side-effect of this method is that it also retrieves the type
     * arguments for the classes and interfaces that are part of the hierarchy
     * between <code>type</code> and <code>toClass</code>. So with the above
     * example, this method will also determine that the type arguments for
     * {@link java.util.Hashtable Hashtable} are also both <code>Object</code>.
     * In cases where the interface specified by <code>toClass</code> is
     * (indirectly) implemented more than once (e.g. where <code>toClass</code>
     * specifies the interface {@link java.lang.Iterable Iterable} and
     * <code>type</code> specifies a parameterized type that implements both
     * {@link java.util.Set Set} and {@link java.util.Collection Collection}),
     * this method will look at the inheritance hierarchy of only one of the
     * implementations/subclasses; the first interface encountered that isn't a
     * subinterface to one of the others in the <code>type</code> to
     * <code>toClass</code> hierarchy. </p>
    
     * @param type the type from which to determine the type parameters of
     * <code>toClass</code>
     * @param toClass the class whose type parameters are to be determined based
     * on the subtype <code>type</code>
     * @return a map of the type assignments for the type variables in each type
     * in the inheritance hierarchy from <code>type</code> to
     * <code>toClass</code> inclusive.

    public static Map<TypeVariable<?>, Type> getTypeArguments(final Type type, final Class<?> toClass) {
        return getTypeArguments(type, toClass, null);
    }

    
     * <p> Return a map of the type arguments of <code>type</code> in the context of <code>toClass</code>. </p>
    
     * @param type the type in question
     * @param toClass the class
     * @param subtypeVarAssigns a map with type variables
     * @return the map with type arguments

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

    
     * <p> Return a map of the type arguments of a parameterized type in the context of <code>toClass</code>. </p>
    
     * @param parameterizedType the parameterized type
     * @param toClass the class
     * @param subtypeVarAssigns a map with type variables
     * @return the map with type arguments

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

    
     * <p> Return a map of the type arguments of a class in the context of <code>toClass</code>. </p>
    
     * @param cls the class in question
     * @param toClass the context class
     * @param subtypeVarAssigns a map with type variables
     * @return the map with type arguments

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

    
     * <p> Tries to determine the type arguments of a class/interface based on a
     * super parameterized type's type arguments. This method is the inverse of
     * {@link #getTypeArguments(Type, Class)} which gets a class/interface's
     * type arguments based on a subtype. It is far more limited in determining
     * the type arguments for the subject class's type variables in that it can
     * only determine those parameters that map from the subject {@link Class}
     * object to the supertype. </p> <p> Example: {@link java.util.TreeSet
     * TreeSet} sets its parameter as the parameter for
     * {@link java.util.NavigableSet NavigableSet}, which in turn sets the
     * parameter of {@link java.util.SortedSet}, which in turn sets the
     * parameter of {@link Set}, which in turn sets the parameter of
     * {@link java.util.Collection}, which in turn sets the parameter of
     * {@link java.lang.Iterable}. Since <code>TreeSet</code>'s parameter maps
     * (indirectly) to <code>Iterable</code>'s parameter, it will be able to
     * determine that based on the super type <code>Iterable<? extends
     * Map<Integer,? extends Collection<?>>></code>, the parameter of
     * <code>TreeSet</code> is <code>? extends Map<Integer,? extends
     * Collection<?>></code>. </p>
    
     * @param cls the class whose type parameters are to be determined
     * @param superType the super type from which <code>cls</code>'s type
     * arguments are to be determined
     * @return a map of the type assignments that could be determined for the
     * type variables in each type in the inheritance hierarchy from
     * <code>type</code> to <code>toClass</code> inclusive.

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

    
     * <p>Performs a mapping of type variables.</p>
    
     * @param <T> the generic type of the class in question
     * @param cls the class in question
     * @param parameterizedType the parameterized type
     * @param typeVarAssigns the map to be filled

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

    
     * <p> Closest parent type? Closest to what? The closest parent type to the
     * super class specified by <code>superClass</code>. </p>
    
     * @param cls the class in question
     * @param superClass the super class
     * @return the closes parent type

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

    
     * <p> Checks if the given value can be assigned to the target type
     * following the Java generics rules. </p>
    
     * @param value the value to be checked
     * @param type the target type
     * @return true of <code>value</code> is an instance of <code>type</code>.

    public static boolean isInstance(final Object value, final Type type) {
        if (type == null) {
            return false;
        }

        return value == null ? !(type instanceof Class<?>) || !((Class<?>) type).isPrimitive()
                : isAssignable(value.getClass(), type, null);
    }

    
     * <p> This method strips out the redundant upper bound types in type
     * variable types and wildcard types (or it would with wildcard types if
     * multiple upper bounds were allowed). </p> <p> Example: with the variable
     * type declaration:
    
     * <pre> &lt;K extends java.util.Collection&lt;String&gt; &amp;
     * java.util.List&lt;String&gt;&gt; </pre>
    
     * since <code>List</code> is a subinterface of <code>Collection</code>,
     * this method will return the bounds as if the declaration had been:
    
     * <pre> &lt;K extends java.util.List&lt;String&gt;&gt; </pre>
    
     * </p>
    
     * @param bounds an array of types representing the upper bounds of either
     * <code>WildcardType</code> or <code>TypeVariable</code>.
     * @return an array containing the values from <code>bounds</code> minus the
     * redundant types.

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

    
     * <p> Returns an array containing the sole type of {@link Object} if
     * {@link TypeVariable#getBounds()} returns an empty array. Otherwise, it
     * returns the result of <code>TypeVariable.getBounds()</code> passed into
     * {@link #normalizeUpperBounds}. </p>
    
     * @param typeVariable the subject type variable
     * @return a non-empty array containing the bounds of the type variable.

    public static Type[] getImplicitBounds(final TypeVariable<?> typeVariable) {
        final Type[] bounds = typeVariable.getBounds();

        return bounds.length == 0 ? new Type[] { Object.class } : normalizeUpperBounds(bounds);
    }

    
     * <p> Returns an array containing the sole value of {@link Object} if
     * {@link WildcardType#getUpperBounds()} returns an empty array. Otherwise,
     * it returns the result of <code>WildcardType.getUpperBounds()</code>
     * passed into {@link #normalizeUpperBounds}. </p>
    
     * @param wildcardType the subject wildcard type
     * @return a non-empty array containing the upper bounds of the wildcard
     * type.

    public static Type[] getImplicitUpperBounds(final WildcardType wildcardType) {
        final Type[] bounds = wildcardType.getUpperBounds();

        return bounds.length == 0 ? new Type[] { Object.class } : normalizeUpperBounds(bounds);
    }

    
     * <p> Returns an array containing a single value of <code>null</code> if
     * {@link WildcardType#getLowerBounds()} returns an empty array. Otherwise,
     * it returns the result of <code>WildcardType.getLowerBounds()</code>. </p>
    
     * @param wildcardType the subject wildcard type
     * @return a non-empty array containing the lower bounds of the wildcard
     * type.

    public static Type[] getImplicitLowerBounds(final WildcardType wildcardType) {
        final Type[] bounds = wildcardType.getLowerBounds();

        return bounds.length == 0 ? new Type[] { null } : bounds;
    }

    
     * <p> Determines whether or not specified types satisfy the bounds of their
     * mapped type variables. When a type parameter extends another (such as
     * <code><T, S extends T></code>), uses another as a type parameter (such as
     * <code><T, S extends Comparable<T></code>), or otherwise depends on
     * another type variable to be specified, the dependencies must be included
     * in <code>typeVarAssigns</code>. </p>
    
     * @param typeVarAssigns specifies the potential types to be assigned to the
     * type variables.
     * @return whether or not the types can be assigned to their respective type
     * variables.

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

    
     * <p> Transforms the passed in type to a {@code Class} object. Type-checking method of convenience. </p>
    
     * @param parameterizedType the type to be converted
     * @return the corresponding {@code Class} object
     * @throws IllegalStateException if the conversion fails

    private static Class<?> getRawType(final ParameterizedType parameterizedType) {
        final Type rawType = parameterizedType.getRawType();

        
        
        
        
        
        if (!(rawType instanceof Class<?>)) {
            throw new IllegalStateException("Wait... What!? Type of rawType: " + rawType);
        }

        return (Class<?>) rawType;
    }

    
     * <p> Get the raw type of a Java type, given its context. Primarily for use
     * with {@link TypeVariable}s and {@link GenericArrayType}s, or when you do
     * not know the runtime type of <code>type</code>: if you know you have a
     * {@link Class} instance, it is already raw; if you know you have a
     * {@link ParameterizedType}, its raw type is only a method call away. </p>
    
     * @param type to resolve
     * @param assigningType type to be resolved against
     * @return the resolved <code>Class</code> object or <code>null</code> if
     * the type could not be resolved

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

    
     * Learn whether the specified type denotes an array type.
     * @param type the type to be checked
     * @return <code>true</code> if <code>type</code> is an array class or a {@link GenericArrayType}.

    public static boolean isArrayType(final Type type) {
        return type instanceof GenericArrayType || type instanceof Class<?> && ((Class<?>) type).isArray();
    }

    
     * Get the array component type of <code>type</code>.
     * @param type the type to be checked
     * @return component type or null if type is not an array type

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
