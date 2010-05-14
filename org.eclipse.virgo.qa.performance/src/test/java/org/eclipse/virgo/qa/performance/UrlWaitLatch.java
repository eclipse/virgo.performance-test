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

import static org.junit.Assert.fail;

import java.net.ConnectException;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.GetMethod;

public class UrlWaitLatch {

	public static final long HALF_SECOND = 500;

	public static final long TWO_MINUTES = 150 * 1000;

	public static void waitFor(String url) {
		waitFor(url, HALF_SECOND, TWO_MINUTES);
	}

	public static void waitFor(String url, String username, String password) {
		waitFor(url, username, password, HALF_SECOND, TWO_MINUTES);
	}

	public static void waitFor(String url, long interval, long duration) {
		HttpClient client = new HttpClient();
		wait(url, client, interval, duration);
	}

	public static void waitFor(String url, String username, String password,
			long interval, long duration) {
		HttpClient client = new HttpClient();
		client.getParams().setAuthenticationPreemptive(true);
		client.getState().setCredentials(AuthScope.ANY,
				new UsernamePasswordCredentials(username, password));
		wait(url, client, interval, duration);
	}

	private static void wait(String url, HttpClient client, long interval,
			long duration) {
		HttpMethod get = new GetMethod(url);

		try {
			long startTime = System.currentTimeMillis();

			int statusCode = 999;
			while (System.currentTimeMillis() - startTime < duration) {
				try {
					statusCode = client.executeMethod(get);
					if (statusCode < 400) {
						return;
					}
				} catch (ConnectException e) {
					// Swallow this and retry
				}
				Thread.sleep(interval);
			}

			fail(String.format("After %d ms, status code was %d", duration,
					statusCode));
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			get.releaseConnection();
		}
	}

	public static void waitForNotExistence(String url) {
		HttpClient client = new HttpClient();
		waitForNotExistence(url, client, HALF_SECOND, TWO_MINUTES);
	}

	private static void waitForNotExistence(String url, HttpClient client,
			long interval, long duration) {
		HttpMethod get = new GetMethod(url);

		try {
			long startTime = System.currentTimeMillis();

			int statusCode = 100;
			while (System.currentTimeMillis() - startTime < duration) {
				try {
					statusCode = client.executeMethod(get);
					if (statusCode > 400) {
						return;
					}
				} catch (ConnectException e) {
					// Swallow this and retry
				}
				Thread.sleep(interval);
			}

			fail(String.format("After %d ms, status code was %d", duration,
					statusCode));
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			get.releaseConnection();
		}
	}

	public static void waitForServerShutdownFully(String url) {
		HttpClient client = new HttpClient();
		waitForServerShutdownFully(url, client, HALF_SECOND, TWO_MINUTES);
	}

	private static void waitForServerShutdownFully(String url,
			HttpClient client, long interval, long duration) {
		HttpMethod get = new GetMethod(url);

		try {
			for (;;) {
				try {
					client.executeMethod(get);
					Thread.sleep(interval);
				} catch (ConnectException e) {
					break;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} finally {
			get.releaseConnection();
		}
	}

}
