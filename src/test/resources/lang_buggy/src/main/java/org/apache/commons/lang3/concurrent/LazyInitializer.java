















package org.apache.commons.lang3.concurrent;





























































public abstract class LazyInitializer<T> implements ConcurrentInitializer<T> {
    
    private volatile T object;









    @Override
    public T get() throws ConcurrentException {
        
        
        T result = object;

        if (result == null) {
            synchronized (this) {
                result = object;
                if (result == null) {
                    object = result = initialize();
                }
            }
        }

        return result;
    }











    protected abstract T initialize() throws ConcurrentException;
}
