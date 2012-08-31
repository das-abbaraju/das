package com.picsauditing.util;

import java.util.Date;

import junit.framework.ComparisonFailure;

/**
 * Additional assertion methods useful for writing tests. Only failed assertions
 * are recorded. These methods can be used directly:
 * <code>Assert.assertEqualsToTheSecond(...)</code>, however, they read better
 * if they are referenced through static import:<br/>
 * 
 * <pre>
 * import static com.picsauditing.util.Assert.*;
 *    ...
 *    assertEqualsToTheSecond(...);
 * </pre>
 */
public class Assert {
	/**
	 * Protect constructor since it is a static only class
	 */
	protected Assert() {
	}

	public static void assertEqualsToTheSecond(Date expected, Date actual) {
		if (expected.getTime() == actual.getTime())
			return;
		long difference = toSeconds(expected.getTime()) - toSeconds(actual.getTime());
		if (difference == 0)
			return;
		throw new ComparisonFailure("Dates are " + difference + " seconds apart.", expected.toString(),
				actual.toString());
	}

	private static long toSeconds(long milliseconds) {
		return Math.round(milliseconds / 1000);
	}

	public static void assertContains(String expectedSubstring, String actual) {
		String adjustedActual = simplifyWhitespace(actual);
		if (adjustedActual.contains(expectedSubstring))
			return;
		throw new ComparisonFailure("Substring comparison ", expectedSubstring, actual);
	}

	public static void assertNotContains(String expectedSubstring, String actual) {
		String adjustedActual = simplifyWhitespace(actual);
		if (!adjustedActual.contains(expectedSubstring))
			return;
		throw new ComparisonFailure("Substring comparison ", expectedSubstring, actual);
	}

	static private String simplifyWhitespace(String s) {
		return s.replace("\n", " ").replace("  ", " ");
	}

}
