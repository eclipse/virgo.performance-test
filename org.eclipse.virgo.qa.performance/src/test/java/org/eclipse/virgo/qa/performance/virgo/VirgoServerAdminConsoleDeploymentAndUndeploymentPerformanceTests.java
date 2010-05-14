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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Arrays;

import org.eclipse.virgo.qa.performance.AbstractPerformanceTests;
import org.eclipse.virgo.qa.performance.UrlWaitLatch;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.util.FileCopyUtils;

import com.gargoylesoftware.htmlunit.DefaultCredentialsProvider;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlFileInput;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlSubmitInput;

@Ignore
public class VirgoServerAdminConsoleDeploymentAndUndeploymentPerformanceTests
		extends AbstractPerformanceTests {

	HtmlPage adminPage = null;
	private static int NO_OF_ITERATIONS = 3;

	@BeforeClass
	public static void serverStartUp() throws Exception {
		copyDependentBundlesToRepository(getBundlesUsrDir());
		new Thread(new StartUpThread()).start();
		UrlWaitLatch.waitFor(SPLASH_URL);
	}

	public HtmlPage getAdminPage() {
		getTestContext().setBaseUrl(AbstractPerformanceTests.ADMIN_URL);
		getTestContext().setAuthorization(AbstractPerformanceTests.USERNAME,
				AbstractPerformanceTests.PASSWORD);

		final WebClient webClient = new WebClient();
		final DefaultCredentialsProvider credentialsProvider = (DefaultCredentialsProvider) webClient
				.getCredentialsProvider();
		credentialsProvider.addCredentials(USERNAME, PASSWORD);
		try {
			adminPage = webClient.getPage(ADMIN_URL);
		} catch (FailingHttpStatusCodeException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return adminPage;
	}

	public void deployStandardWarThroughAdminConsole() {
		try {
			byte[] byteArray = FileCopyUtils.copyToByteArray(new File(
					"./apps/formtags-war.war"));
			final HtmlForm uploadform = getAdminPage().getFormByName(
					"uploadForm");
			final HtmlFileInput applicationField = uploadform
					.getInputByName("application");
			applicationField.setData(byteArray);
			applicationField
					.setValueAttribute("./apps/formtags-war.war");
			final HtmlSubmitInput uploadButton = uploadform
					.getElementById("deploy_application_submit_button");
			uploadform.submit(uploadButton);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void undeployStandardWarThroughAdminConsole() {
		try {
			getAdminPage().getAnchorByName(
					"applications_undeploy_formtags-war.war")
					.click();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void deploySharedLibrariesWarThroughAdminConsole() {
		try {
			byte[] byteArray = FileCopyUtils.copyToByteArray(new File(
					"./apps/formtags-shared-libs.war"));
			final HtmlForm uploadform = getAdminPage().getFormByName(
					"uploadForm");
			final HtmlFileInput applicationField = uploadform
					.getInputByName("application");
			applicationField.setData(byteArray);
			applicationField
					.setValueAttribute("./apps/formtags-shared-libs.war");
			final HtmlSubmitInput uploadButton = uploadform
					.getElementById("deploy_application_submit_button");
			uploadform.submit(uploadButton);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void undeploySharedLibrariesWarThroughAdminConsole() {
		try {
			getAdminPage()
					.getAnchorByName(
							"applications_undeploy_org.springframework.showcase.formtags_shared_libs")
					.click();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void deployParThroughAdminConsole() {
		try {
			byte[] byteArray = FileCopyUtils.copyToByteArray(new File(
					"./apps/formtags-par.par"));
			final HtmlForm uploadform = getAdminPage().getFormByName(
					"uploadForm");
			final HtmlFileInput applicationField = uploadform
					.getInputByName("application");
			applicationField.setData(byteArray);
			applicationField
					.setValueAttribute("./apps/formtags-par.par");
			final HtmlSubmitInput uploadButton = uploadform
					.getElementById("deploy_application_submit_button");
			uploadform.submit(uploadButton);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void undeployParThroughAdminConsole() {
		try {
			getAdminPage()
					.getAnchorByName("applications_undeploy_formtags-par")
					.click();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void deployPlanThroughAdminConsole() {
		try {
			byte[] byteArray = FileCopyUtils
					.copyToByteArray(new File(
							"./apps/org.springframework.showcase.formtags-2.0.0.RELEASE.plan"));
			final HtmlForm uploadform = getAdminPage().getFormByName(
					"uploadForm");
			final HtmlFileInput applicationField = uploadform
					.getInputByName("application");
			applicationField.setData(byteArray);
			applicationField
					.setValueAttribute("./apps/org.springframework.showcase.formtags-2.0.0.RELEASE.plan");
			final HtmlSubmitInput uploadButton = uploadform
					.getElementById("deploy_application_submit_button");
			uploadform.submit(uploadButton);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void undeployPlanThroughAdminConsole() {
		try {
			getAdminPage()
					.getAnchorByName(
							"applications_undeploy_org.springframework.showcase.formtags.plan")
					.click();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testAdminConsoleDeploymentOfStandardWarPerformance()
			throws Exception {
		long[] duration = new long[NO_OF_ITERATIONS];

		for (int i = 0; i < NO_OF_ITERATIONS; i++) {
			long starttime = 0l;
			long endtime = 0l;
			long testDuration = 0l;

			deployStandardWarThroughAdminConsole();

			starttime = System.currentTimeMillis();
			waitForTextPresent("/formtags-war-2.0.0.RELEASE",
					"applications_modules_formtags-war.war");
			endtime = System.currentTimeMillis();
			testDuration = endtime - starttime;
			duration[i] = testDuration;
			undeployStandardWarThroughAdminConsole();
			waitForTextNotPresent("/formtags-war-2.0.0.RELEASE",
					"applications_modules_formtags-war.war");
		}

		Arrays.sort(duration);
		for (int i = 0; i < duration.length; i++) {
			System.out.println("Duration: " + duration[i]);
		}
		long high = duration[duration.length - 1];
		long low = duration[NO_OF_ITERATIONS / 2];
		long average = (high + low) / 2;
		try {
			record("testAdminConsoleDeploymentOfStandardWarPerformance", average, high, low, 5000);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testAdminConsoleUnDeploymentOfStandardWarPerformance()
			throws Exception {
		long[] duration = new long[NO_OF_ITERATIONS];

		for (int i = 0; i < NO_OF_ITERATIONS; i++) {
			long starttime = 0l;
			long endtime = 0l;
			long testDuration = 0l;

			deployStandardWarThroughAdminConsole();
			waitForTextPresent("/formtags-war-2.0.0.RELEASE",
					"applications_modules_formtags-war.war");

			undeployStandardWarThroughAdminConsole();
			starttime = System.currentTimeMillis();
			waitForTextNotPresent("/formtags-war-2.0.0.RELEASE",
					"applications_modules_formtags-war.war");
			endtime = System.currentTimeMillis();
			testDuration = endtime - starttime;
			duration[i] = testDuration;
		}

		Arrays.sort(duration);
		long high = duration[duration.length - 1];
		long low = duration[NO_OF_ITERATIONS / 2];
		long average = (high + low) / 2;
		try {
			record("testAdminConsoleUnDeploymentOfStandardWarPerformance", average, high, low, 5000);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testAdminConsoleDeploymentOfSharedLibrariesWarPerformance()
			throws Exception {
		long[] duration = new long[NO_OF_ITERATIONS];

		for (int i = 0; i < NO_OF_ITERATIONS; i++) {
			long starttime = 0l;
			long endtime = 0l;
			long testDuration = 0l;

			deploySharedLibrariesWarThroughAdminConsole();

			starttime = System.currentTimeMillis();
			waitForTextPresent("/formtags-shared-libs-2.0.0.RELEASE",
					"applications_modules_org.springframework.showcase.formtags_shared_libs");
			endtime = System.currentTimeMillis();
			testDuration = endtime - starttime;
			duration[i] = testDuration;
			undeploySharedLibrariesWarThroughAdminConsole();
			waitForTextNotPresent("/formtags-shared-libs-2.0.0.RELEASE",
					"applications_modules_org.springframework.showcase.formtags_shared_libs");
		}

		Arrays.sort(duration);
		long high = duration[duration.length - 1];
		long low = duration[NO_OF_ITERATIONS / 2];
		long average = (high + low) / 2;
		try {
			record("testAdminConsoleDeploymentOfSharedLibrariesWarPerformance", average, high, low, 5000);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	@Test
	public void testAdminConsoleUnDeploymentOfSharedLibrariesWarPerformance()
			throws Exception {
		long[] duration = new long[NO_OF_ITERATIONS];

		for (int i = 0; i < NO_OF_ITERATIONS; i++) {
			long starttime = 0l;
			long endtime = 0l;
			long testDuration = 0l;

			deploySharedLibrariesWarThroughAdminConsole();
			waitForTextPresent("/formtags-shared-libs-2.0.0.RELEASE",
					"applications_modules_org.springframework.showcase.formtags_shared_libs");
			undeploySharedLibrariesWarThroughAdminConsole();
			starttime = System.currentTimeMillis();
			waitForTextNotPresent("/formtags-shared-libs-2.0.0.RELEASE",
					"applications_modules_org.springframework.showcase.formtags_shared_libs");
			endtime = System.currentTimeMillis();
			testDuration = endtime - starttime;
			duration[i] = testDuration;
		}

		Arrays.sort(duration);
		long high = duration[duration.length - 1];
		long low = duration[NO_OF_ITERATIONS / 2];
		long average = (high + low) / 2;
		try {
			record("testAdminConsoleUnDeploymentOfSharedLibrariesWarPerformance", average, high, low, 5000);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testAdminConsoleDeploymentOfParPerformance() throws Exception {
		long[] duration = new long[NO_OF_ITERATIONS];

		for (int i = 0; i < NO_OF_ITERATIONS; i++) {
			long starttime = 0l;
			long endtime = 0l;
			long testDuration = 0l;
			deployParThroughAdminConsole();

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
			undeployParThroughAdminConsole();
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
		}

		Arrays.sort(duration);
		long high = duration[duration.length - 1];
		long low = duration[NO_OF_ITERATIONS / 2];
		long average = (high + low) / 2;
		try {
			record("testAdminConsoleDeploymentOfParPerformance", average, high, low, 5000);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testAdminConsoleUnDeploymentOfParPerformance() throws Exception {
		long[] duration = new long[NO_OF_ITERATIONS];

		for (int i = 0; i < NO_OF_ITERATIONS; i++) {
			long starttime = 0l;
			long endtime = 0l;
			long testDuration = 0l;
			deployParThroughAdminConsole();
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

			undeployParThroughAdminConsole();
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
		}

		Arrays.sort(duration);
		long high = duration[duration.length - 1];
		long low = duration[NO_OF_ITERATIONS / 2];
		long average = (high + low) / 2;
		try {
			record("testAdminConsoleUnDeploymentOfParPerformance", average, high, low, 5000);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testAdminConsoleDeploymentOfPlanPerformance() throws Exception {
		long[] duration = new long[NO_OF_ITERATIONS];

		for (int i = 0; i < NO_OF_ITERATIONS; i++) {
			long starttime = 0l;
			long endtime = 0l;
			long testDuration = 0l;
			deployPlanThroughAdminConsole();
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
			undeployPlanThroughAdminConsole();

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
		}

		Arrays.sort(duration);
		long high = duration[duration.length - 1];
		long low = duration[NO_OF_ITERATIONS / 2];
		long average = (high + low) / 2;
		try {
			record("testAdminConsoleDeploymentOfPlanPerformance", average, high, low, 5000);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testAdminConsoleUnDeloymentOfPlanPerformance() throws Exception {
		long[] duration = new long[NO_OF_ITERATIONS];

		for (int i = 0; i < NO_OF_ITERATIONS; i++) {
			long starttime = 0l;
			long endtime = 0l;
			long testDuration = 0l;
			deployPlanThroughAdminConsole();
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

			undeployPlanThroughAdminConsole();
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
		}

		Arrays.sort(duration);
		long high = duration[duration.length - 1];
		long low = duration[NO_OF_ITERATIONS / 2];
		long average = (high + low) / 2;
		try {
			record("testAdminConsoleUnDeloymentOfPlanPerformance", average, high, low, 5000);
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
