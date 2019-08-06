package jp.posl.jprophet;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.JavaFileObject;
import javax.tools.SimpleJavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;


public class ProjectBuilder {

	static private final String CLASSPATH_SEPARATOR = File.pathSeparator;

	private final ProjectConfiguration projectConfig;

	public ProjectBuilder(final ProjectConfiguration projectConfig) {
		this.projectConfig = projectConfig;
	}

	/**
	 * 初期ソースコードをビルド
	 * 
	 * @param outDir
	 *            バイトコード出力ディレクトリ
	 * @return ビルドが成功すれば true，失敗すれば false
	 */
	public boolean build(final String outDir) {

		final JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		final StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null);

		final Iterable<? extends JavaFileObject> javaFileObjects;

        javaFileObjects = fileManager.getJavaFileObjectsFromStrings(
                this.projectConfig.getSourceFiles());


		final List<String> compilationOptions = new ArrayList<>();
		compilationOptions.add("-d");
		compilationOptions.add(outDir);
		compilationOptions.add("-encoding");
		compilationOptions.add("UTF-8");
		compilationOptions.add("-classpath");
		compilationOptions.add(String.join(CLASSPATH_SEPARATOR,
				this.projectConfig.getClassPaths()));

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

		return isSuccess;
	}



	public boolean buildTest(final String outDir) {

		final JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		final StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null);

		final Iterable<? extends JavaFileObject> javaFileObjects;

        javaFileObjects = fileManager.getJavaFileObjectsFromStrings(
                this.projectConfig.getTestFiles());


		final List<String> compilationOptions = new ArrayList<>();
		compilationOptions.add("-d");
		compilationOptions.add(outDir);
		compilationOptions.add("-encoding");
		compilationOptions.add("UTF-8");
		compilationOptions.add("-classpath");
		compilationOptions.add(String.join(CLASSPATH_SEPARATOR,
				this.projectConfig.getClassPaths()));

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
