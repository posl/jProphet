















package org.apache.commons.lang3.concurrent;

import org.apache.commons.lang3.ObjectUtils;





















public class ConstantInitializer<T> implements ConcurrentInitializer<T> {
    
    private static final String FMT_TO_STRING = "ConstantInitializer@%d [ object = %s ]";

    
    private final T object;










    public ConstantInitializer(final T obj) {
        object = obj;
    }








    public final T getObject() {
        return object;
    }








    @Override
    public T get() throws ConcurrentException {
        return getObject();
    }







    @Override
    public int hashCode() {
        return getObject() != null ? getObject().hashCode() : 0;
    }










    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof ConstantInitializer<?>)) {
            return false;
        }

        final ConstantInitializer<?> c = (ConstantInitializer<?>) obj;
        return ObjectUtils.equals(getObject(), c.getObject());
    }








    @Override
    public String toString() {
        return String.format(FMT_TO_STRING, Integer.valueOf(System.identityHashCode(this)),
                String.valueOf(getObject()));
    }
}
