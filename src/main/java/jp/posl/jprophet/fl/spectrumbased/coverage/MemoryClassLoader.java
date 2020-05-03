package jp.posl.jprophet.fl.spectrumbased.coverage;

import java.util.Map;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
/**
 * A class loader that loads classes from in-memory data.
 */
public class MemoryClassLoader extends URLClassLoader {
    
    /**
    * constructor
    */
    public MemoryClassLoader() {
        this(new URL[] {});
    }

    /**
     * constructor
     *
     * @param urls クラスパス
     */
    public MemoryClassLoader(final URL[] urls) {
        super(urls);
    }

    /**
    * クラス定義を表すMap． クラス名とバイト配列のペアを持つ．
    */
    private final Map<String, byte[]> definitions = new HashMap<>();

    /**
    * メモリ上のバイト配列をクラス定義に追加する．
    *
    * @param name 定義するクラス名
    * @param bytes 追加するクラス定義
    */
    public void addDefinition(final String fqn, final byte[] bytes) {
        definitions.put(fqn, bytes);
    }

    /**
    * クラスをロードする．<br>
    * {@link java.lang.ClassLoader#loadClass}のFQNエイリアス
    *
    * @param fqn ロード対象のクラスのFQN
    * @return ロードされたクラスオブジェクト
    * @throws ClassNotFoundException
    */
    public Class<?> loadClass(final String fqn) throws ClassNotFoundException {
        return super.loadClass(fqn);
    }

    /**
    * メモリ上からクラスを探す． <br>
    * まずURLClassLoaderによるファイルシステム上のクラスのロードを試み，それがなければメモリ上のクラスロードを試す．
    */
    @Override
    protected Class<?> findClass(final String name) throws ClassNotFoundException {
        Class<?> c = null;

        // try to load from classpath
        if (definitions.get(name) == null){
            try {
                c = super.findClass(name);
            } catch (final ClassNotFoundException e1) {
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

    @Override
    public InputStream getResourceAsStream(final String name) {
        final String fqn = convertStringNameToFqn(name);
        final byte[] bytes = definitions.get(fqn);
        if (null == bytes) {
            return super.getResourceAsStream(name);
        }
        return new ByteArrayInputStream(bytes);
    }

    private String convertStringNameToFqn(final String name) {
        return name.replaceAll("\\.class$", "")
            .replaceAll("\\/", ".");
    }

}