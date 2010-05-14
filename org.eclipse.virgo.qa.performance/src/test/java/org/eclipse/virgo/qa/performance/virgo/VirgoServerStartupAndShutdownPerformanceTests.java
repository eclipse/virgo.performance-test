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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;

import org.eclipse.virgo.qa.performance.AbstractPerformanceTests;
import org.eclipse.virgo.qa.performance.UrlWaitLatch;
import org.junit.Test;

public class VirgoServerStartupAndShutdownPerformanceTests extends
		AbstractPerformanceTests {

	private static int NO_OF_ITERATIONS = 3;

	@Test
	public void testServerStartupPerformance() throws Exception {
		long[] duration = new long[NO_OF_ITERATIONS];
		for (int i = 0; i < NO_OF_ITERATIONS; i++) {
			long starttime=0l;
			long endtime=0l;
			long testDuration=0l;
			starttime = System.currentTimeMillis();
			new Thread(new StartUpThread()).start();
			UrlWaitLatch.waitFor(SPLASH_URL);
			endtime = System.currentTimeMillis();
			testDuration = endtime - starttime;
			duration[i] = testDuration;
			new Thread(new ShutdownThread()).start();
			UrlWaitLatch.waitForServerShutdownFully(SPLASH_URL);
		}

		Arrays.sort(duration);
		long high = duration[duration.length - 1];
		long low = duration[NO_OF_ITERATIONS / 2];
		long average = (high + low) / 2;
		try {
			record("testServerStartupPerformance", average, high, low, 27000);// Last reported  high: 24732 low: 23008 average: 23870 on Mac pro desktop
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testServerShutdownPerformance() throws Exception {
		long[] duration = new long[NO_OF_ITERATIONS];
		for (int i = 0; i < NO_OF_ITERATIONS; i++) {
			long starttime=0l;
			long endtime=0l;
			long testDuration=0l;
			new Thread(new StartUpThread()).start();
			UrlWaitLatch.waitFor(SPLASH_URL);
			starttime = System.currentTimeMillis();
			new Thread(new ShutdownThread()).start();
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
			record("testServerShutdownPerformance", average, high, low, 3100);// Last reported run of  high: 2622 low: 2391 average: 2506 on a mac pro desktop
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
