















package org.apache.commons.lang3.concurrent;

import java.util.concurrent.atomic.AtomicReference;















































public abstract class AtomicInitializer<T> implements ConcurrentInitializer<T> {
    
    private final AtomicReference<T> reference = new AtomicReference<T>();










    @Override
    public T get() throws ConcurrentException {
        T result = reference.get();

        if (result == null) {
            result = initialize();
            if (!reference.compareAndSet(null, result)) {
                
                result = reference.get();
            }
        }

        return result;
    }












    protected abstract T initialize() throws ConcurrentException;
}
