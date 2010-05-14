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

package org.eclipse.virgo.qa.performance;

import static org.junit.Assert.assertFalse;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.util.Map;

import net.sourceforge.jwebunit.junit.WebTester;
import net.sourceforge.jwebunit.util.TestContext;

import org.springframework.util.FileCopyUtils;


public class AbstractPerformanceTests {

	protected static final String SPLASH_URL = "http://localhost:8080/splash";
	protected static final String ADMIN_URL = "http://localhost:8080/admin/web/info/overview.htm";
	protected static final String USERNAME = "admin";
	protected static final String PASSWORD = "springsource";
	private static String binDir = null;
	private static String pickupDir = null;
	private static String bundlesUsrDir = null;
	private static final String BUNDLES_DIR = "./bundles";
	private static String[] bundleNames = new String[] {
			"org.springframework.showcase.formtags.domain.jar",
			"org.springframework.showcase.formtags.service.jar",
			"org.springframework.showcase.formtags.web.war" };

	private static Process process = null;
	private static ProcessBuilder pb = null;
	private static File startup = null;
	private static File startupURI = null;
	private static File shutdownURI = null;
	private static String startupFileName = null;
	private static File shutdown = null;
	private static String shutdownFileName = null;
	private static OperatingSystemMXBean os = ManagementFactory
			.getOperatingSystemMXBean();

	public static final long HALF_SECOND = 500;

	public static final long TWO_MINUTES = 120 * 1000;

	private final WebTester tester = new WebTester();

	protected final TestContext getTestContext() {
		return this.tester.getTestContext();
	}

	protected final WebTester getTester() {
		return this.tester;
	}

	protected static String getServerBinDir() throws IOException {
		if (binDir == null) {
			File testExpanded = new File("./target/test-expanded/");
			for (File candidate : testExpanded.listFiles()) {
				if (candidate.isDirectory()) {
					binDir = new File(candidate, "bin").getCanonicalPath();
					break;
				}
			}
		}
		return binDir;
	}

	protected static String getPickupDir() throws IOException {
		if (pickupDir == null) {
			File testExpanded = new File("./target/test-expanded/");
			for (File candidate : testExpanded.listFiles()) {
				if (candidate.isDirectory()) {
					pickupDir = new File(candidate, "pickup")
							.getCanonicalPath();
					break;
				}
			}
		}
		return pickupDir;
	}

	protected static String getBundlesUsrDir() throws IOException {
		if (bundlesUsrDir == null) {
			File testExpanded = new File("./target/test-expanded/");
			for (File mainDir : testExpanded.listFiles()) {
				if (mainDir.isDirectory()) {
					File repositoryDir = new File(mainDir, "repository")
							.getCanonicalFile();
                    bundlesUsrDir = new File(repositoryDir, "usr")
							.getCanonicalPath();
                }
			}
		}
		return bundlesUsrDir;
	}

	protected static void copyApplicationsToPickup(String pickupDir,
			String appsDir, String[] applicationNames) throws IOException,
			InterruptedException {
		for (String applicationName : applicationNames) {

			FileCopyUtils.copy(new File(appsDir, applicationName), new File(
					pickupDir, applicationName));
			Thread.sleep(1500);
		}
	}

	protected static void deleteApplicationsFromPickup(String pickupDir,
			String[] applicationNames) throws IOException {
		for (String applicationName : applicationNames) {
			new File(pickupDir, applicationName).delete();
		}
	}

	protected static void copyDependentBundlesToRepository(String bundlesUsrDir)
			throws Exception {
      for (String bundleName : bundleNames) {

			FileCopyUtils.copy(new File(BUNDLES_DIR, bundleName), new File(
					bundlesUsrDir, bundleName));
		}
	}

	protected void record(String name, long average, long high, long low, long limit) throws FileNotFoundException, IOException {
	    
	    assertFalse(String.format("Test %s took too long, high: %d, low: %d, average: %d, limit:%d", name, high, low, average, limit), average > limit);
	    
//      FOR REPORTING THE ACTUAL TEST TIMES
//		File report = new File("target/" + this.getClass().getName() + "-" + name + ".txt");
//		FileWriter reportWriter;
//		if(!report.exists()){
//		    report.createNewFile();
//	        reportWriter = new FileWriter(report);
//	        reportWriter.write("Test Timing report");
//		} else {
//	        reportWriter = new FileWriter(report);   
//		}
//		reportWriter.write(String.format("%nTest '%s': high: %d low: %d average: %d", this.getClass().getName(), high, low, average));
//		reportWriter.close();
	}

	public void waitForTextPresent(String text, String tableId) {
		waitForTextPresent(text, tableId, 500, 30 * 1000);
	}

	public void waitForTextNotPresent(String text, String tableId) {
		waitForTextNotPresent(text, tableId, 500, 30 * 1000);
	}

	private void waitForTextPresent(String text, String tableId, long interval,
			long duration) {
		long startTime = System.currentTimeMillis();
		while (System.currentTimeMillis() - startTime < duration) {
			UrlWaitLatch.waitFor(ADMIN_URL, USERNAME, PASSWORD);
			getTestContext().setBaseUrl(ADMIN_URL);
			getTestContext().setAuthorization(USERNAME, PASSWORD);
			getTester().beginAt(ADMIN_URL);
			if (getTester().getTestingEngine().hasTable(tableId)) {
				return;
			}
			try {
				Thread.sleep(interval);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	private void waitForTextNotPresent(String text, String tableId,
			long interval, long duration) {
		long startTime = System.currentTimeMillis();
		while (System.currentTimeMillis() - startTime < duration) {
			UrlWaitLatch.waitFor(ADMIN_URL, USERNAME, PASSWORD);
			getTestContext().setBaseUrl(ADMIN_URL);
			getTestContext().setAuthorization(USERNAME, PASSWORD);
			getTester().beginAt(ADMIN_URL);
			if (!getTester().getTestingEngine().hasTable(tableId)) {
				return;
			}
			try {
				Thread.sleep(interval);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	protected static class StartUpThread implements Runnable {
		public StartUpThread() {
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
				args = new String[] { startupFileName, "-clean" };
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

	protected static class ShutdownThread implements Runnable {
		public ShutdownThread() {
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
				args = new String[] { shutdownFileName };
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
