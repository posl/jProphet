package jp.posl.jprophet.test;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Map;
/**
 * A class loader that loads classes from in-memory data.
 */
public class MemoryClassLoader extends URLClassLoader {

    public MemoryClassLoader(URL[] urls) {
        super(urls);
    }
    private final Map<String, byte[]> definitions = new HashMap<String, byte[]>();

    /**
     * Add a in-memory representation of a class.
     *
     * @param name
     *            name of the class
     * @param bytes
     *            class definition
     */
    public void addDefinition(final String name, final byte[] bytes) {
        definitions.put(name, bytes);
    }

    @Override
    public Class<?> loadClass(final String name, final boolean resolve)
            throws ClassNotFoundException {
        final byte[] bytes = definitions.get(name);
        if (bytes != null) {
            return defineClass(name, bytes, 0, bytes.length);
        }
        return super.loadClass(name, resolve);
    }

}