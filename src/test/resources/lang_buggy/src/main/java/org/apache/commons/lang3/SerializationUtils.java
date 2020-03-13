















package org.apache.commons.lang3;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamClass;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;


















public class SerializationUtils {

    
    
    
    
    
    
    

    public SerializationUtils() {
        super();
    }

    
    
    
    
    
    
    
    
    
    
    
    
    
    
    

    public static <T extends Serializable> T clone(final T object) {
        if (object == null) {
            return null;
        }
        final byte[] objectData = serialize(object);
        final ByteArrayInputStream bais = new ByteArrayInputStream(objectData);

        ClassLoaderAwareObjectInputStream in = null;
        try {
            
            in = new ClassLoaderAwareObjectInputStream(bais, object.getClass().getClassLoader());
            
            
            
            

            @SuppressWarnings("unchecked") 
            final
            T readObject = (T) in.readObject();
            return readObject;

        } catch (final ClassNotFoundException ex) {
            throw new SerializationException("ClassNotFoundException while reading cloned object data", ex);
        } catch (final IOException ex) {
            throw new SerializationException("IOException while reading cloned object data", ex);
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (final IOException ex) {
                throw new SerializationException("IOException on closing cloned object data InputStream.", ex);
            }
        }
    }

    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    

    public static void serialize(final Serializable obj, final OutputStream outputStream) {
        if (outputStream == null) {
            throw new IllegalArgumentException("The OutputStream must not be null");
        }
        ObjectOutputStream out = null;
        try {
            
            out = new ObjectOutputStream(outputStream);
            out.writeObject(obj);

        } catch (final IOException ex) {
            throw new SerializationException(ex);
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (final IOException ex) { 
                
            }
        }
    }

    
    
    
    
    
    
    

    public static byte[] serialize(final Serializable obj) {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream(512);
        serialize(obj, baos);
        return baos.toByteArray();
    }

    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    

    public static <T> T deserialize(final InputStream inputStream) {
        if (inputStream == null) {
            throw new IllegalArgumentException("The InputStream must not be null");
        }
        ObjectInputStream in = null;
        try {
            
            in = new ObjectInputStream(inputStream);
            @SuppressWarnings("unchecked") 
            final T obj = (T) in.readObject();
            return obj;

        } catch (final ClassCastException ex) {
            throw new SerializationException(ex);
        } catch (final ClassNotFoundException ex) {
            throw new SerializationException(ex);
        } catch (final IOException ex) {
            throw new SerializationException(ex);
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (final IOException ex) { 
                
            }
        }
    }

    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    

    public static <T> T deserialize(final byte[] objectData) {
        if (objectData == null) {
            throw new IllegalArgumentException("The byte[] must not be null");
        }
        return SerializationUtils.<T>deserialize(new ByteArrayInputStream(objectData));
    }

    
    
    
    
    
    
    
    
    
    
    
    

     static class ClassLoaderAwareObjectInputStream extends ObjectInputStream {
        private static final Map<String, Class<?>> primitiveTypes = 
                new HashMap<String, Class<?>>();
        private final ClassLoader classLoader;
        
        
        
        
        
        
        

        public ClassLoaderAwareObjectInputStream(final InputStream in, final ClassLoader classLoader) throws IOException {
            super(in);
            this.classLoader = classLoader;

            primitiveTypes.put("byte", byte.class);
            primitiveTypes.put("short", short.class);
            primitiveTypes.put("int", int.class);
            primitiveTypes.put("long", long.class);
            primitiveTypes.put("float", float.class);
            primitiveTypes.put("double", double.class);
            primitiveTypes.put("boolean", boolean.class);
            primitiveTypes.put("char", char.class);
            primitiveTypes.put("void", void.class);
        }

        
        
        
        
        
        
        

        @Override
        protected Class<?> resolveClass(final ObjectStreamClass desc) throws IOException, ClassNotFoundException {
            final String name = desc.getName();
            try {
                return Class.forName(name, false, classLoader);
            } catch (final ClassNotFoundException ex) {
                try {
                    return Class.forName(name, false, Thread.currentThread().getContextClassLoader());
                } catch (final ClassNotFoundException cnfe) {
                    final Class<?> cls = primitiveTypes.get(name);
                    if (cls != null) {
                        return cls;
                    } else {
                        throw cnfe;
                    }
                }
            }
        }

    }

}
