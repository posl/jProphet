package jp.posl.jprophet;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaCompiler.CompilationTask;

import org.apache.commons.io.FileUtils;

import jp.posl.jprophet.project.Project;

import javax.tools.JavaFileObject;
import javax.tools.SimpleJavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;


public class ProjectBuilder {

    static private final String CLASSPATH_SEPARATOR = File.pathSeparator;

    /**
     * プロジェクトのソースコードとテストクラスをビルド
     * 
     * @param config 対象プロジェクトの設定
     * @return ビルドが成功すれば true，失敗すれば false
     */
    public boolean build(RepairConfiguration config) {
        final Project project = config.getTargetProject();
        List<String> filePaths = Stream.concat(project.getSrcFilePaths().stream(), project.getTestFilePaths().stream()).collect(Collectors.toList());
        final JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        final StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null);

        final Iterable<? extends JavaFileObject> javaFileObjects;

        javaFileObjects = fileManager.getJavaFileObjectsFromStrings(
                filePaths);


        final List<String> compilationOptions = new ArrayList<>();
        compilationOptions.add("-d");
        compilationOptions.add(config.getBuildPath());
        compilationOptions.add("-source");
        compilationOptions.add("1.8");
        compilationOptions.add("-target");
        compilationOptions.add("1.8");
        compilationOptions.add("-encoding");
        compilationOptions.add("UTF-8");
        compilationOptions.add("-classpath");
        compilationOptions.add(String.join(CLASSPATH_SEPARATOR,
                project.getClassPaths()));

        final DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();
        final CompilationTask task = compiler.getTask(null, fileManager, diagnostics, compilationOptions, null,
                javaFileObjects);

        final boolean isSuccess = task.call();

        for (Diagnostic<?> diagnostic : diagnostics.getDiagnostics()) {
            System.err.println(diagnostic.getCode());
            System.err.println(diagnostic.getKind());
            System.err.println(diagnostic.getPosition());
            System.err.println(diagnostic.getStartPosition());
            System.err.println(diagnostic.getEndPosition());
            System.err.println(diagnostic.getSource());
            System.err.println(diagnostic.getMessage(null));

        }

        try {
            fileManager.close();
        } catch (final IOException e) {
            e.printStackTrace();
        }

        //resources内のファイルをbuildPath直下にコピペする
        try {
            File mainResourcesDir = new File(project.getRootPath() + "/src/main/resources");
            File testResourcesDir = new File(project.getRootPath() + "/src/test/resources");
            if (mainResourcesDir.listFiles() != null) {
                FileUtils.copyDirectory(mainResourcesDir,new File(config.getBuildPath()));
            }
            if (testResourcesDir.listFiles() != null) {
                FileUtils.copyDirectory(testResourcesDir,new File(config.getBuildPath()));
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    
        return isSuccess;
    }

}

class JavaSourceFromString extends SimpleJavaFileObject {

    final String code;

    JavaSourceFromString(String name, String code) {
        super(URI.create("string:///" + name.replace('.', '/') + Kind.SOURCE.extension), Kind.SOURCE);
        this.code = code;
    }

    @Override
    public CharSequence getCharContent(boolean ignoreEncodingErrors) {
        return code;
    }
}
