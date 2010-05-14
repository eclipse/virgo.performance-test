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
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

@Ignore
public class VirgoServerPickupDeploymentAndUndeploymentPerformanceTests extends
		AbstractPerformanceTests {

	private static final String APPS_DIR = "./apps";
	private static final String[] APPLICATION_NAME_WAR = new String[] { "formtags-war.war" };
	private static final String[] APPLICATION_NAME_SHARED_LIBRARIES_WAR = new String[] { "formtags-shared-libs.war" };
	private static final String[] APPLICATION_NAME_SHARED_SERVICES_WAR = new String[] {
			"formtags-shared-services-service.jar",
			"formtags-shared-services-war.war" };
	private static final String[] APPLICATION_NAME_PAR = new String[] { "formtags-par.par" };
	private static final String[] APPLICATION_NAME_PLAN = new String[] { "org.springframework.showcase.formtags-2.0.0.RELEASE.plan" };
	private static int NO_OF_ITERATIONS = 3;

	@BeforeClass
	public static void serverStartUp() throws Exception {
		copyDependentBundlesToRepository(getBundlesUsrDir());
		new Thread(new StartUpThread()).start();
		UrlWaitLatch.waitFor(SPLASH_URL);
		UrlWaitLatch.waitFor(ADMIN_URL, USERNAME, PASSWORD);
	}

	@Before
	public void testContextSetup() throws Exception {
		getTestContext().setBaseUrl(AbstractPerformanceTests.ADMIN_URL);
		getTestContext().setAuthorization(AbstractPerformanceTests.USERNAME,
				AbstractPerformanceTests.PASSWORD);
	}

	@Test
	public void testPickupDeploymentOfStandardWarPerformance() throws Exception {
		long[] duration = new long[NO_OF_ITERATIONS];

		for (int i = 0; i < NO_OF_ITERATIONS; i++) {
			long starttime = 0l;
			long endtime = 0l;
			long testDuration = 0l;
			copyApplicationsToPickup(getPickupDir(), APPS_DIR,
					APPLICATION_NAME_WAR);
			starttime = System.currentTimeMillis();

			waitForTextPresent("/formtags-war-2.0.0.RELEASE",
					"applications_modules_formtags-war.war");
			endtime = System.currentTimeMillis();
			testDuration = endtime - starttime;
			duration[i] = testDuration;

			deleteApplicationsFromPickup(getPickupDir(), APPLICATION_NAME_WAR);
			waitForTextNotPresent("/formtags-war-2.0.0.RELEASE",
					"applications_modules_formtags-war.war");
			Thread.sleep(2000);
		}

		Arrays.sort(duration);
		long high = duration[duration.length - 1];
		long low = duration[NO_OF_ITERATIONS / 2];
		long average = (high + low) / 2;
		try {
			record("testPickupDeploymentOfStandardWarPerformance", average, high, low, 5000);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testPickupUnDeploymentOfStandardWarPerformance()
			throws Exception {
		long[] duration = new long[NO_OF_ITERATIONS];

		for (int i = 0; i < NO_OF_ITERATIONS; i++) {
			long starttime = 0l;
			long endtime = 0l;
			long testDuration = 0l;
			copyApplicationsToPickup(getPickupDir(), APPS_DIR,
					APPLICATION_NAME_WAR);
			waitForTextPresent("/formtags-war-2.0.0.RELEASE",
					"applications_modules_formtags-war.war");
			deleteApplicationsFromPickup(getPickupDir(), APPLICATION_NAME_WAR);
			starttime = System.currentTimeMillis();
			waitForTextNotPresent("/formtags-war-2.0.0.RELEASE",
					"applications_modules_formtags-war.war");
			endtime = System.currentTimeMillis();
			testDuration = endtime - starttime;
			duration[i] = testDuration;
			Thread.sleep(2000);

		}

		Arrays.sort(duration);
		long high = duration[duration.length - 1];
		long low = duration[NO_OF_ITERATIONS / 2];
		long average = (high + low) / 2;
		try {
			record("testPickupUnDeploymentOfStandardWarPerformance", average, high, low, 5000);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testPickupDeploymentOfSharedLibrariesWarPerformance()
			throws Exception {
		long[] duration = new long[NO_OF_ITERATIONS];

		for (int i = 0; i < NO_OF_ITERATIONS; i++) {
			long starttime = 0l;
			long endtime = 0l;
			long testDuration = 0l;
			copyApplicationsToPickup(getPickupDir(), APPS_DIR,
					APPLICATION_NAME_SHARED_LIBRARIES_WAR);
			starttime = System.currentTimeMillis();
			waitForTextPresent("/formtags-shared-libs-2.0.0.RELEASE",
					"applications_modules_org.springframework.showcase.formtags_shared_libs");
			endtime = System.currentTimeMillis();
			testDuration = endtime - starttime;
			duration[i] = testDuration;
			deleteApplicationsFromPickup(getPickupDir(),
					APPLICATION_NAME_SHARED_LIBRARIES_WAR);
			waitForTextNotPresent("/formtags-shared-libs-2.0.0.RELEASE",
					"applications_modules_org.springframework.showcase.formtags_shared_libs");
			Thread.sleep(2000);
		}

		Arrays.sort(duration);
		long high = duration[duration.length - 1];
		long low = duration[NO_OF_ITERATIONS / 2];
		long average = (high + low) / 2;
		try {
			record("testPickupDeploymentOfSharedLibrariesWarPerformance", average, high, low, 5000);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testPickupUnDeploymentOfSharedLibrariesWarPerformance()
			throws Exception {
		long[] duration = new long[NO_OF_ITERATIONS];

		for (int i = 0; i < NO_OF_ITERATIONS; i++) {
			long starttime = 0l;
			long endtime = 0l;
			long testDuration = 0l;
			copyApplicationsToPickup(getPickupDir(), APPS_DIR,
					APPLICATION_NAME_SHARED_LIBRARIES_WAR);
			waitForTextPresent("/formtags-shared-libs-2.0.0.RELEASE",
					"applications_modules_org.springframework.showcase.formtags_shared_libs");
			deleteApplicationsFromPickup(getPickupDir(),
					APPLICATION_NAME_SHARED_LIBRARIES_WAR);
			starttime = System.currentTimeMillis();
			waitForTextNotPresent("/formtags-shared-libs-2.0.0.RELEASE",
					"applications_modules_org.springframework.showcase.formtags_shared_libs");
			endtime = System.currentTimeMillis();
			testDuration = endtime - starttime;
			duration[i] = testDuration;
			Thread.sleep(2000);

		}

		Arrays.sort(duration);
		long high = duration[duration.length - 1];
		long low = duration[NO_OF_ITERATIONS / 2];
		long average = (high + low) / 2;
		try {
			record("testPickupUnDeploymentOfSharedLibrariesWarPerformance", average, high, low, 5000);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Ignore
	@Test
	public void testPickupDeploymentOfSharedServicesWarPerformance()
			throws Exception {
		long[] duration = new long[NO_OF_ITERATIONS];

		for (int i = 0; i < NO_OF_ITERATIONS; i++) {
			long starttime = 0l;
			long endtime = 0l;
			long testDuration = 0l;
			copyApplicationsToPickup(getPickupDir(), APPS_DIR,
					APPLICATION_NAME_SHARED_SERVICES_WAR);
			starttime = System.currentTimeMillis();
			waitForTextPresent(
					"/formtags-shared-services-war-2.0.0.RELEASE",
					"applications_modules_applications_modules_org.springframework.showcase.formtags.web_shared_services");
			endtime = System.currentTimeMillis();
			testDuration = endtime - starttime;
			duration[i] = testDuration;
			deleteApplicationsFromPickup(getPickupDir(),
					APPLICATION_NAME_SHARED_SERVICES_WAR);
			waitForTextNotPresent(
					"/formtags-shared-services-war-2.0.0.RELEASE",
					"applications_modules_applications_modules_org.springframework.showcase.formtags.web_shared_services");
			Thread.sleep(2000);
		}
		Arrays.sort(duration);
		long high = duration[duration.length - 1];
		long low = duration[NO_OF_ITERATIONS / 2];
		long average = (high + low) / 2;
		try {
			record("testPickupDeploymentOfSharedServicesWarPerformance", average, high, low, 5000);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Ignore
	@Test
	public void testPickupUnDeploymentOfSharedServicesWarPerformance()
			throws Exception {
		long[] duration = new long[NO_OF_ITERATIONS];

		for (int i = 0; i < NO_OF_ITERATIONS; i++) {
			long starttime = 0l;
			long endtime = 0l;
			long testDuration = 0l;
			copyApplicationsToPickup(getPickupDir(), APPS_DIR,
					APPLICATION_NAME_SHARED_SERVICES_WAR);
			waitForTextPresent(
					"/formtags-shared-services-war-2.0.0.RELEASE",
					"applications_modules_applications_modules_org.springframework.showcase.formtags.web_shared_services");
			deleteApplicationsFromPickup(getPickupDir(),
					APPLICATION_NAME_SHARED_SERVICES_WAR);
			starttime = System.currentTimeMillis();
			waitForTextNotPresent(
					"/formtags-shared-services-war-2.0.0.RELEASE",
					"applications_modules_applications_modules_org.springframework.showcase.formtags.web_shared_services");
			endtime = System.currentTimeMillis();
			testDuration = endtime - starttime;
			duration[i] = testDuration;
			Thread.sleep(2000);
		}

		Arrays.sort(duration);
		long high = duration[duration.length - 1];
		long low = duration[NO_OF_ITERATIONS / 2];
		long average = (high + low) / 2;
		try {
			record("testPickupUnDeploymentOfSharedServicesWarPerformance", average, high, low, 5000);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testPickupDeploymentOfParPerformance() throws Exception {
		long[] duration = new long[NO_OF_ITERATIONS];

		for (int i = 0; i < NO_OF_ITERATIONS; i++) {
			long starttime = 0l;
			long endtime = 0l;
			long testDuration = 0l;
			copyApplicationsToPickup(getPickupDir(), APPS_DIR,
					APPLICATION_NAME_PAR);
			starttime = System.currentTimeMillis();
			waitForTextPresent("formtags-par",
					"applications_modules_formtags-par");
			waitForTextPresent("formtags-par-synthetic.context",
					"applications_modules_formtags-par-synthetic.context");
			waitForTextPresent("org.springframework.showcase.formtags.web_par",
					"applications_modules_org.springframework.showcase.formtags.web_par");
			waitForTextPresent(
					"org.springframework.showcase.formtags.service_par",
					"applications_modules_org.springframework.showcase.formtags.service_par");
			waitForTextPresent(
					"org.springframework.showcase.formtags.domain_par",
					"applications_modules_org.springframework.showcase.formtags.domain_par");

			endtime = System.currentTimeMillis();
			testDuration = endtime - starttime;
			duration[i] = testDuration;
			deleteApplicationsFromPickup(getPickupDir(), APPLICATION_NAME_PAR);
			waitForTextNotPresent("formtags-par",
					"applications_modules_formtags-par");
			waitForTextNotPresent("formtags-par-synthetic.context",
					"applications_modules_formtags-par-synthetic.context");
			waitForTextNotPresent(
					"org.springframework.showcase.formtags.web_par",
					"applications_modules_org.springframework.showcase.formtags.web_par");
			waitForTextNotPresent(
					"org.springframework.showcase.formtags.service_par",
					"applications_modules_org.springframework.showcase.formtags.service_par");
			waitForTextNotPresent(
					"org.springframework.showcase.formtags.domain_par",
					"applications_modules_org.springframework.showcase.formtags.domain_par");

			Thread.sleep(2000);
		}

		Arrays.sort(duration);
		long high = duration[duration.length - 1];
		long low = duration[NO_OF_ITERATIONS / 2];
		long average = (high + low) / 2;
		try {
			record("testPickupDeploymentOfParPerformance", average, high, low, 5000);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testPickupUnDeploymentOfParPerformance() throws Exception {
		long[] duration = new long[NO_OF_ITERATIONS];

		for (int i = 0; i < NO_OF_ITERATIONS; i++) {
			long starttime = 0l;
			long endtime = 0l;
			long testDuration = 0l;
			copyApplicationsToPickup(getPickupDir(), APPS_DIR,
					APPLICATION_NAME_PAR);
			waitForTextPresent("formtags-par",
					"applications_modules_formtags-par");
			waitForTextPresent("formtags-par-synthetic.context",
					"applications_modules_formtags-par-synthetic.context");
			waitForTextPresent("org.springframework.showcase.formtags.web_par",
					"applications_modules_org.springframework.showcase.formtags.web_par");
			waitForTextPresent(
					"org.springframework.showcase.formtags.service_par",
					"applications_modules_org.springframework.showcase.formtags.service_par");
			waitForTextPresent(
					"org.springframework.showcase.formtags.domain_par",
					"applications_modules_org.springframework.showcase.formtags.domain_par");

			deleteApplicationsFromPickup(getPickupDir(), APPLICATION_NAME_PAR);
			starttime = System.currentTimeMillis();
			waitForTextNotPresent("formtags-par",
					"applications_modules_formtags-par");
			waitForTextNotPresent("formtags-par-synthetic.context",
					"applications_modules_formtags-par-synthetic.context");
			waitForTextNotPresent(
					"org.springframework.showcase.formtags.web_par",
					"applications_modules_org.springframework.showcase.formtags.web_par");
			waitForTextNotPresent(
					"org.springframework.showcase.formtags.service_par",
					"applications_modules_org.springframework.showcase.formtags.service_par");
			waitForTextNotPresent(
					"org.springframework.showcase.formtags.domain_par",
					"applications_modules_org.springframework.showcase.formtags.domain_par");

			endtime = System.currentTimeMillis();
			testDuration = endtime - starttime;
			duration[i] = testDuration;
			Thread.sleep(2000);
		}

		Arrays.sort(duration);
		long high = duration[duration.length - 1];
		long low = duration[NO_OF_ITERATIONS / 2];
		long average = (high + low) / 2;
		try {
			record("testPickupUnDeploymentOfParPerformance", average, high, low, 5000);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testPickupDeploymentOfPlanPerformance() throws Exception {
		long[] duration = new long[NO_OF_ITERATIONS];

		for (int i = 0; i < NO_OF_ITERATIONS; i++) {
			long starttime = 0l;
			long endtime = 0l;
			long testDuration = 0l;
			copyApplicationsToPickup(getPickupDir(), APPS_DIR,
					APPLICATION_NAME_PLAN);
			starttime = System.currentTimeMillis();
			waitForTextPresent("org.springframework.showcase.formtags.plan",
					"applications_modules_org.springframework.showcase.formtags.plan");
			waitForTextPresent(
					"org.springframework.showcase.formtags.plan-synthetic.context",
					"applications_modules_org.springframework.showcase.formtags.plan-synthetic.context");
			waitForTextPresent("org.springframework.showcase.formtags.web_par",
					"applications_modules_org.springframework.showcase.formtags.web_par");
			waitForTextPresent(
					"org.springframework.showcase.formtags.service_par",
					"applications_modules_org.springframework.showcase.formtags.service_par");
			waitForTextPresent(
					"org.springframework.showcase.formtags.domain_par",
					"applications_modules_org.springframework.showcase.formtags.domain_par");

			endtime = System.currentTimeMillis();
			testDuration = endtime - starttime;
			duration[i] = testDuration;
			deleteApplicationsFromPickup(getPickupDir(), APPLICATION_NAME_PLAN);
			waitForTextNotPresent("org.springframework.showcase.formtags.plan",
					"applications_modules_org.springframework.showcase.formtags.plan");
			waitForTextNotPresent(
					"org.springframework.showcase.formtags.plan-synthetic.context",
					"applications_modules_org.springframework.showcase.formtags.plan-synthetic.context");
			waitForTextNotPresent(
					"org.springframework.showcase.formtags.web_par",
					"applications_modules_org.springframework.showcase.formtags.web_par");
			waitForTextNotPresent(
					"org.springframework.showcase.formtags.service_par",
					"applications_modules_org.springframework.showcase.formtags.service_par");
			waitForTextNotPresent(
					"org.springframework.showcase.formtags.domain_par",
					"applications_modules_org.springframework.showcase.formtags.domain_par");

			Thread.sleep(2000);
		}

		Arrays.sort(duration);
		long high = duration[duration.length - 1];
		long low = duration[NO_OF_ITERATIONS / 2];
		long average = (high + low) / 2;
		try {
			record("testPickupDeploymentOfPlanPerformance", average, high, low, 5000);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testPickupUnDeloymentOfPlanPerformance() throws Exception {
		long[] duration = new long[NO_OF_ITERATIONS];

		for (int i = 0; i < NO_OF_ITERATIONS; i++) {
			long starttime = 0l;
			long endtime = 0l;
			long testDuration = 0l;
			copyApplicationsToPickup(getPickupDir(), APPS_DIR,
					APPLICATION_NAME_PLAN);
			waitForTextPresent("org.springframework.showcase.formtags.plan",
					"applications_modules_org.springframework.showcase.formtags.plan");
			waitForTextPresent(
					"org.springframework.showcase.formtags.plan-synthetic.context",
					"applications_modules_org.springframework.showcase.formtags.plan-synthetic.context");
			waitForTextPresent("org.springframework.showcase.formtags.web_par",
					"applications_modules_org.springframework.showcase.formtags.web_par");
			waitForTextPresent(
					"org.springframework.showcase.formtags.service_par",
					"applications_modules_org.springframework.showcase.formtags.service_par");
			waitForTextPresent(
					"org.springframework.showcase.formtags.domain_par",
					"applications_modules_org.springframework.showcase.formtags.domain_par");

			deleteApplicationsFromPickup(getPickupDir(), APPLICATION_NAME_PLAN);
			starttime = System.currentTimeMillis();
			waitForTextNotPresent("org.springframework.showcase.formtags.plan",
					"applications_modules_org.springframework.showcase.formtags.plan");
			waitForTextNotPresent(
					"org.springframework.showcase.formtags.plan-synthetic.context",
					"applications_modules_org.springframework.showcase.formtags.plan-synthetic.context");
			waitForTextNotPresent(
					"org.springframework.showcase.formtags.web_par",
					"applications_modules_org.springframework.showcase.formtags.web_par");
			waitForTextNotPresent(
					"org.springframework.showcase.formtags.service_par",
					"applications_modules_org.springframework.showcase.formtags.service_par");
			waitForTextNotPresent(
					"org.springframework.showcase.formtags.domain_par",
					"applications_modules_org.springframework.showcase.formtags.domain_par");

			endtime = System.currentTimeMillis();
			testDuration = endtime - starttime;
			duration[i] = testDuration;
			Thread.sleep(2000);
		}

		Arrays.sort(duration);
		long high = duration[duration.length - 1];
		long low = duration[NO_OF_ITERATIONS / 2];
		long average = (high + low) / 2;
		try {
			record("testPickupUnDeloymentOfPlanPerformance", average, high, low, 5000);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@AfterClass
	public static void serverShutdown() throws Exception {
		new Thread(new ShutdownThread()).start();
		UrlWaitLatch.waitForServerShutdownFully(SPLASH_URL);
	}

}
