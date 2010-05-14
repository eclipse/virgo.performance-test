/*******************************************************************************
 * Copyright (c) 2008, 2010 VMware Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   VMware Inc. - initial contribution
 *******************************************************************************/

package org.eclipse.virgo.qa.performance.virgo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.util.Arrays;
import java.util.Map;

import org.eclipse.virgo.qa.performance.AbstractPerformanceTests;
import org.eclipse.virgo.qa.performance.UrlWaitLatch;
import org.junit.Test;


public class VirgoServerImmediateShutdownPerformanceTests extends
		AbstractPerformanceTests {

	private static Process process = null;
	private static ProcessBuilder pb = null;
	private static File shutdownURI = null;
	private static File shutdown = null;
	private static String shutdownFileName = null;
	private static OperatingSystemMXBean os = ManagementFactory
			.getOperatingSystemMXBean();
	private static int NO_OF_ITERATIONS = 3;

	@Test
	public void testServerImmediateShutdownPerformance() throws Exception {
		long[] duration = new long[NO_OF_ITERATIONS];
		for (int i = 0; i < NO_OF_ITERATIONS; i++) {
			long starttime=0l;
			long endtime=0l;
			long testDuration=0l;
			new Thread(new StartUpThread()).start();
			UrlWaitLatch.waitFor(SPLASH_URL);
			starttime = System.currentTimeMillis();
			new Thread(new ImmediateShutdownThread()).start();
			UrlWaitLatch.waitForServerShutdownFully(SPLASH_URL);
			endtime = System.currentTimeMillis();
			testDuration = endtime - starttime;
			duration[i] = testDuration;
		}

		Arrays.sort(duration);
		long high = duration[duration.length - 1];
		long low = duration[NO_OF_ITERATIONS / 2];
		long average = (high + low) / 2;
		try {
			record("testServerImmediateShutdownPerformance", average, high, low, 2600);// Last reported run high: 1935 low: 1811 average: 1873 on Mac pro desktop
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	protected static class ImmediateShutdownThread implements Runnable {
		public ImmediateShutdownThread() {
		}

		public void run() {
			String[] args = null;
			try {
				if (os.getName().contains("Windows")) {
					shutdown = new File(getServerBinDir(), "shutdown.bat");
					shutdownURI = new File(shutdown.toURI());
					shutdownFileName = shutdownURI.getCanonicalPath();
				} else {
					shutdown = new File(getServerBinDir(), "shutdown.sh");
					shutdownURI = new File(shutdown.toURI());
					shutdownFileName = shutdownURI.getCanonicalPath();
				}
				args = new String[] { shutdownFileName, "-immediate" };
				pb = new ProcessBuilder(args);
				pb.redirectErrorStream(true);
				Map<String, String> env = pb.environment();
				env.put("JAVA_HOME", System.getProperty("java.home"));

				process = pb.start();

				InputStream is = process.getInputStream();
				InputStreamReader isr = new InputStreamReader(is);
				BufferedReader br = new BufferedReader(isr);
				String line;
				while ((line = br.readLine()) != null) {
					System.out.println(line);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
