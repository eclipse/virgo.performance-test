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

public class VirgoServerDebugModeStartupPerformanceTests extends
		AbstractPerformanceTests {

	private static Process process = null;
	private static ProcessBuilder pb = null;
	private static File startup = null;
	private static File startupURI = null;
	private static String startupFileName = null;
	private static OperatingSystemMXBean os = ManagementFactory
			.getOperatingSystemMXBean();
	private static int NO_OF_ITERATIONS = 3;

	@Test
	public void testServerDebugModeStartupPerformance() throws Exception {
		long[] duration = new long[NO_OF_ITERATIONS];
		for (int i = 0; i < NO_OF_ITERATIONS; i++) {
			long starttime=0l;
			long endtime=0l;
			long testDuration=0l;
			starttime = System.currentTimeMillis();
			new Thread(new DebugModeStartUpThread()).start();
			UrlWaitLatch.waitFor(SPLASH_URL);
			endtime = System.currentTimeMillis();
			testDuration = endtime - starttime;
			duration[i] = testDuration;
			new Thread(new ShutdownThread()).start();
			UrlWaitLatch.waitForServerShutdownFully(SPLASH_URL);
		}

		Arrays.sort(duration);
		for (int i = 0; i < duration.length; i++) {
			System.out.println("Duration: " + duration[i]);
		}
		long high = duration[duration.length - 1];
		long low = duration[NO_OF_ITERATIONS / 2];
		long average = (high + low) / 2;
		try {
			record("testServerDebugModeStartupPerformance", average, high, low, 35000);// Last reported run of  high: 26965 low: 24852 average: 25908 on Mac pro desktop
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static class DebugModeStartUpThread implements Runnable {
		public DebugModeStartUpThread() {
		}

		public void run() {
			String[] args = null;
			try {
				if (os.getName().contains("Windows")) {
					startup = new File(getServerBinDir(), "startup.bat");
					startupURI = new File(startup.toURI());
					startupFileName = startupURI.getCanonicalPath();

				} else {
					startup = new File(getServerBinDir(), "startup.sh");
					startupURI = new File(startup.toURI());
					startupFileName = startupURI.getCanonicalPath();
				}
				args = new String[] { startupFileName, "-debug" };
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
