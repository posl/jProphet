/*******************************************************************************
 * Copyright (c) 2009, 2019 Mountainminds GmbH & Co. KG and Contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Marc R. Hoffmann - initial API and implementation
 *    
 *******************************************************************************/
package jp.posl.jprophet.FL;

import java.io.InputStream;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import org.jacoco.core.analysis.Analyzer;
import org.jacoco.core.analysis.CoverageBuilder;
import org.jacoco.core.analysis.IClassCoverage;
import org.jacoco.core.analysis.ICounter;
import org.jacoco.core.data.ExecutionDataStore;
import org.jacoco.core.data.SessionInfoStore;
import org.jacoco.core.instr.Instrumenter;
import org.jacoco.core.runtime.IRuntime;
import org.jacoco.core.runtime.LoggerRuntime;
import org.jacoco.core.runtime.RuntimeData;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;

import jp.posl.jprophet.ProjectConfiguration;

/**
 * Example usage of the JaCoCo core API. In this tutorial a single target class
 * will be instrumented and executed. Finally the coverage information will be
 * dumped.
 */
public final class CoverageProject {
	private final PrintStream out;
	private MemoryClassLoader memoryClassLoader;
	private final IRuntime runtime;
	private final Instrumenter instrumenter;

	/**
	 * Creates a new example instance printing to the given stream.
	 * 
	 * @param out
	 *            stream for outputs
	 */
	public CoverageProject(final PrintStream out) {
		this.out = out;
		this.runtime = new LoggerRuntime();
		this.instrumenter = new Instrumenter(runtime);
		this.memoryClassLoader = null;
		// ここであらかじめoutputディレクトリにビルド済みのクラスファイルをクラスローダーが読み込んでおく
		try {
			this.memoryClassLoader = new MemoryClassLoader(new URL[] { new URL("file:./Ftmp/") });
		} catch (MalformedURLException e){
			System.err.println(e.getMessage());
		}
	}

	/**
	 * Run this example.
	 * 
	 * @throws Exception
	 *             in case of errors
	 */
	public void execute(List<String> SourceClasses, List<String> TestClasses) throws Exception {
		// 対象のソースファイルとそのテストクラスファイルの完全修飾ドメイン名(FQDN)
		
		for (final String targetName : SourceClasses) {
			System.out.println(targetName);
			final Instrumenter instr = new Instrumenter(runtime);
			InputStream original = this.getTargetClassInputStream(targetName);
			final byte[] instrumented = instr.instrument(original, targetName);original.close();
			this.memoryClassLoader.addDefinition(targetName, instrumented);
			//ここがエラー
			//loadClass(targetName, instrument(targetName));
		}

		final RuntimeData runtimeData = new RuntimeData();
		this.runtime.startup(runtimeData);
		
		final JUnitCore junitCore = new JUnitCore();
		for (final String targetName : TestClasses) {
			final Class<?> junitClass = this.memoryClassLoader.loadClass(targetName);
			final Result result = junitCore.run(junitClass);
			//TestResults.add(result);
			System.out.println("Failure count: " + result.getFailureCount() + " (" + targetName);
		}

		System.out.println("finish junitcore");

		
		final ExecutionDataStore executionData = new ExecutionDataStore();
		final SessionInfoStore sessionInfos = new SessionInfoStore();
		runtimeData.collect(executionData, sessionInfos, false);
		runtime.shutdown();

		System.out.println("finish runtime.shutdown");

		final CoverageBuilder coverageBuilder = new CoverageBuilder();
		final Analyzer analyzer = new Analyzer(executionData, coverageBuilder);
		for(final String targetName : SourceClasses){
			InputStream original = getTargetClassInputStream(targetName);
			analyzer.analyzeClass(original, targetName);
			original.close();
		}


		// Let's dump some metrics and line coverage information:
		for (final IClassCoverage cc : coverageBuilder.getClasses()) {
			out.printf("Coverage of class %s%n", cc.getName());

			printCounter("instructions", cc.getInstructionCounter());
			printCounter("branches", cc.getBranchCounter());
			printCounter("lines", cc.getLineCounter());
			printCounter("methods", cc.getMethodCounter());
			printCounter("complexity", cc.getComplexityCounter());

			for (int i = cc.getFirstLine(); i <= cc.getLastLine(); i++) {
				out.printf("Line %s: %s%n", Integer.valueOf(i),
						getColor(cc.getLine(i).getStatus()));
			}
		}
	}

	private InputStream getTargetClass(final String name) {
		final String resource = '/' + name.replace('.', '/') + ".class";
		
		return getClass().getResourceAsStream(resource);
	}

	private InputStream getTargetClassInputStream(final String name) {
		final String resource = name.replace('.', '/') + ".class";

		InputStream is = this.memoryClassLoader.getResourceAsStream(resource);
		return is;
	}

	private void printCounter(final String unit, final ICounter counter) {
		final Integer missed = Integer.valueOf(counter.getMissedCount());
		final Integer total = Integer.valueOf(counter.getTotalCount());
		out.printf("%s of %s %s missed%n", missed, total, unit);
	}

	private String getColor(final int status) {
		switch (status) {
		case ICounter.NOT_COVERED:
			return "red";
		case ICounter.PARTLY_COVERED:
			return "yellow";
		case ICounter.FULLY_COVERED:
			return "green";
		}
		return "";
	}

	private byte[] instrument(final String targetName) throws Exception {
		return this.instrumenter.instrument(getTargetClass(targetName), "");
	}
	
	private Class<?> loadClass(final String targetName, final byte[] bytes) throws ClassNotFoundException {
		this.memoryClassLoader.addDefinition(targetName, bytes);
		return this.memoryClassLoader.loadClass(targetName); // force load instrumented class.
	}

	/**
	 * Entry point to run this examples as a Java application.
	 * 
	 * @param args
	 *            list of program arguments
	 * @throws Exception
	 *             in case of errors
	 */

}