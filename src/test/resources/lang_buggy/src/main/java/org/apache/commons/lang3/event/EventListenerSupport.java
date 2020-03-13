
















package org.apache.commons.lang3.event;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.commons.lang3.Validate;
































public class EventListenerSupport<L> implements Serializable {

    
    private static final long serialVersionUID = 3593265990380473632L;






    private List<L> listeners = new CopyOnWriteArrayList<L>();





    private transient L proxy;




    private transient L[] prototypeArray;

















    public static <T> EventListenerSupport<T> create(final Class<T> listenerInterface) {
        return new EventListenerSupport<T>(listenerInterface);
    }













    public EventListenerSupport(final Class<L> listenerInterface) {
        this(listenerInterface, Thread.currentThread().getContextClassLoader());
    }














    public EventListenerSupport(final Class<L> listenerInterface, final ClassLoader classLoader) {
        this();
        Validate.notNull(listenerInterface, "Listener interface cannot be null.");
        Validate.notNull(classLoader, "ClassLoader cannot be null.");
        Validate.isTrue(listenerInterface.isInterface(), "Class {0} is not an interface",
                listenerInterface.getName());
        initializeTransientFields(listenerInterface, classLoader);
    }





    private EventListenerSupport() {
    }









    public L fire() {
        return proxy;
    }













    public void addListener(final L listener) {
        Validate.notNull(listener, "Listener object cannot be null.");
        listeners.add(listener);
    }






    int getListenerCount() {
        return listeners.size();
    }









    public void removeListener(final L listener) {
        Validate.notNull(listener, "Listener object cannot be null.");
        listeners.remove(listener);
    }







    public L[] getListeners() {
        return listeners.toArray(prototypeArray);
    }






    private void writeObject(final ObjectOutputStream objectOutputStream) throws IOException {
        final ArrayList<L> serializableListeners = new ArrayList<L>();

        
        ObjectOutputStream testObjectOutputStream = new ObjectOutputStream(new ByteArrayOutputStream());
        for (final L listener : listeners) {
            try {
                testObjectOutputStream.writeObject(listener);
                serializableListeners.add(listener);
            } catch (final IOException exception) {
                
                testObjectOutputStream = new ObjectOutputStream(new ByteArrayOutputStream());
            }
        }




        objectOutputStream.writeObject(serializableListeners.toArray(prototypeArray));
    }







    private void readObject(final ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        @SuppressWarnings("unchecked") 
        final
        L[] listeners = (L[]) objectInputStream.readObject();

        this.listeners = new CopyOnWriteArrayList<L>(listeners);

        @SuppressWarnings("unchecked") 
        final
        Class<L> listenerInterface = (Class<L>) listeners.getClass().getComponentType();

        initializeTransientFields(listenerInterface, Thread.currentThread().getContextClassLoader());
    }






    private void initializeTransientFields(final Class<L> listenerInterface, final ClassLoader classLoader) {
        @SuppressWarnings("unchecked") 
        final
        L[] array = (L[]) Array.newInstance(listenerInterface, 0);
        this.prototypeArray = array;
        createProxy(listenerInterface, classLoader);
    }






    private void createProxy(final Class<L> listenerInterface, final ClassLoader classLoader) {
        proxy = listenerInterface.cast(Proxy.newProxyInstance(classLoader,
                new Class[] { listenerInterface }, createInvocationHandler()));
    }






    protected InvocationHandler createInvocationHandler() {
        return new ProxyInvocationHandler();
    }




    protected class ProxyInvocationHandler implements InvocationHandler {













        @Override
        public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
            for (final L listener : listeners) {
                method.invoke(listener, args);
            }
            return null;
        }
    }
}
