package jp.posl.jprophet.project;

/**
 * ファイルのパスとFQNを表す
 */
public class FileLocator {
    private final String path;
    private final String fqn;

    public FileLocator(String path, String fqn) {
        this.path = path;
        this.fqn = fqn;
    }

    public String getPath() {
        return this.path;
    }

    public String getFqn() {
        return this.fqn;
    }
}