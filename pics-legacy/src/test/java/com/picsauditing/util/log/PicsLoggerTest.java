package com.picsauditing.util.log;

import junit.framework.TestCase;

public class PicsLoggerTest extends TestCase {
	public PicsLoggerTest(String name) {
		super(name);
	}

	public void testLog() {
		PicsLogger.log("test");
	}
}
