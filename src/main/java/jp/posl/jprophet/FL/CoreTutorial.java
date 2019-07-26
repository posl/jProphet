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
import java.util.HashMap;
import java.util.Map;

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

/**
 * Example usage of the JaCoCo core API. In this tutorial a single target class
 * will be instrumented and executed. Finally the coverage information will be
 * dumped.
 */
public final class CoreTutorial {

	private final PrintStream out;

	/**
	 * Creates a new example instance printing to the given stream.
	 * 
	 * @param out
	 *            stream for outputs
	 */
	public CoreTutorial(final PrintStream out) {
		this.out = out;
	}

	/**
	 * Run this example.
	 * 
	 * @throws Exception
	 *             in case of errors
	 */
	public void execute() throws Exception {
		final String targetName = TestTarget.class.getName();
		System.out.println(targetName);

		// For instrumentation and runtime we need a IRuntime instance
		// to collect execution data:
		final IRuntime runtime = new LoggerRuntime();

		// The Instrumenter creates a modified version of our test target class
		// that contains additional probes for execution data recording:
		final Instrumenter instr = new Instrumenter(runtime);
		InputStream original = getTargetClass(targetName);
		final byte[] instrumented = instr.instrument(original, targetName);
		original.close();

		// Now we're ready to run our instrumented class and need to startup the
		// runtime first:
		final RuntimeData data = new RuntimeData();
		runtime.startup(data);

		// In this tutorial we use a special class loader to directly load the
		// instrumented class definition from a byte[] instances.
		final MemoryClassLoader memoryClassLoader = new MemoryClassLoader();
		memoryClassLoader.addDefinition(targetName, instrumented);
		final Class<?> targetClass = memoryClassLoader.loadClass(targetName);

		// Here we execute our test target class through its Runnable interface:
		final Runnable targetInstance = (Runnable) targetClass.newInstance();
		targetInstance.run();

		// At the end of test execution we collect execution data and shutdown
		// the runtime:
		final ExecutionDataStore executionData = new ExecutionDataStore();
		final SessionInfoStore sessionInfos = new SessionInfoStore();
		data.collect(executionData, sessionInfos, false);
		runtime.shutdown();

		// Together with the original class definition we can calculate coverage
		// information:
		final CoverageBuilder coverageBuilder = new CoverageBuilder();
		final Analyzer analyzer = new Analyzer(executionData, coverageBuilder);
		original = getTargetClass(targetName);
		analyzer.analyzeClass(original, targetName);
		original.close();

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
		System.out.println("resource: " + resource);
		System.out.println("getResourceAsStream: " + getClass().getResourceAsStream(resource));
		
		
		return getClass().getResourceAsStream(resource);
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

	/**
	 * Entry point to run this examples as a Java application.
	 * 
	 * @param args
	 *            list of program arguments
	 * @throws Exception
	 *             in case of errors
	 */

}