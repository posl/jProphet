package jp.posl.jprophet.fl.spectrumbased.coverage;

import java.util.Map;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
/**
 * A class loader that loads classes from in-memory data.
 */
public class MemoryClassLoader extends URLClassLoader {
    final private ClassLoader delegationClassLoader;

    public MemoryClassLoader(URL[] urls) {
        super(urls);
        delegationClassLoader = findDelegationClassLoader(getClass().getClassLoader());
    }
    private final Map<String, byte[]> definitions = new HashMap<String, byte[]>();

    /**
     * クラスローダの親子関係を探索して，委譲先となるExtension/PlatformClassLoaderを探す．
     * 
     * @param cl
     * @return
     */
    private ClassLoader findDelegationClassLoader(final ClassLoader cl) {
        if (null == cl) {
        throw new RuntimeException("Cannot find extension class loader.");
        }
        // (#600) patch for greater than jdk9
        // 対象の名前が ExtensionClassLoader (jdk8) or PlatformClassLoader(>jdk9) ならOK．
        final String name = cl.toString();
        if (name.contains("$ExtClassLoader@") || //
            name.contains("$PlatformClassLoader@")) {
        return cl;
        }
        // さもなくば再帰的に親を探す
        return findDelegationClassLoader(cl.getParent());
    }

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

    // @Override
    // public Class<?> loadClass(final String name, final boolean resolve)
    //         throws ClassNotFoundException {
    //     Class<?> c = findLoadedClass(name);
    //     if (c == null) {
    //         final byte[] bytes = definitions.get(name);
    //         if (bytes != null) {
    //             c = defineClass(name, bytes, 0, bytes.length);
    //         }
    //         c = super.loadClass(name, resolve);
    //     }
    //     if (c == null) {
    //         throw new ClassNotFoundException(name);
    //     }
    //     return c;
    // }

    /**
     * メモリ上からクラスを探す． <br>
     * まずURLClassLoaderによるファイルシステム上のクラスのロードを試み，それがなければメモリ上のクラスロードを試す．
     * 
     */
    @Override
    protected Class<?> findClass(final String name) throws ClassNotFoundException {
        Class<?> c = null;

        // try to load from classpath
        
        if (definitions.get(name) == null){
            try {
                c = super.findClass(name);
            } catch (final ClassNotFoundException | NoClassDefFoundError e1) {
                // ignore
            }
        }
        

        // if fails, try to load from memory
        if (null == c) {
            final byte[] bytes = definitions.get(name);
            if (bytes != null) {
                try {
                    c = defineClass(name, bytes, 0, bytes.length);
                } catch (final ClassFormatError e) {
                    throw e;
                }
            }
        }

        // otherwise, class not found
        if (null == c) {
            throw new ClassNotFoundException(name);
        }
        return c;
    }

    public Class<?> loadClass(final String name, final boolean resolve) throws ClassNotFoundException {
        // JUnit関係のクラスのみロードを通常の委譲関係に任す．これがないとJUnitが期待通りに動かない．
        if (name.startsWith("org.junit.") || name.startsWith("junit.") || name.startsWith("org.hamcrest.")) {
            return getParent().loadClass(name);
        }
        
        // 委譲処理．java.lang.ClassLoader#loadClassを参考に作成．
        synchronized (getClassLoadingLock(name)) {
            // First, check if the class has already been loaded
            Class<?> c = findLoadedClass(name);
            
            if (null == c) {
                try {
                    // Second, try to load using extension class loader
                    c = delegationClassLoader.loadClass(name);
                    //c = super.loadClass(name, resolve);
                } catch (final ClassNotFoundException e) {
                    // ignore
                }
            }
            
            if (null == c) {
                try {
                    // Finally, try to load from memory
                    c = this.findClass(name);
                } catch (final ClassNotFoundException e) {
                    // ignore
                }
            }

            if (null == c) {
                throw new ClassNotFoundException(name);
            }
            if (resolve) {
                resolveClass(c);
            }
            return c;
        }
    }

    /*
    @Override
    public InputStream getResourceAsStream(final String name) {
        final String fqn = convertStringNameToNameToFqn(name);
        final byte[] bytes = definitions.get(fqn);
        if (null == bytes) {
            return super.getResourceAsStream(name);
        }
        return new ByteArrayInputStream(bytes);
    }
    */
    
    private String convertStringNameToNameToFqn(final String name) { 
        return name.replaceAll("\\.class$", "").replaceAll("\\/", ".");
    }

}