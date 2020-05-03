package jp.posl.jprophet.fl.spectrumbased.coverage;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class SkippingMemoryClassLoader extends MemoryClassLoader {
    
    final private ClassLoader delegationClassLoader;

    public SkippingMemoryClassLoader(URL[] urls) {
        super(urls);
        delegationClassLoader = findDelegationClassLoader(getClass().getClassLoader());
    }

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
                    c = findClass(name);
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
}