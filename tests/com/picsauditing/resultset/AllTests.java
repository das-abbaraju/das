package com.picsauditing.resultset;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests {

	public static Test suite() {
		TestSuite suite = new TestSuite("Test for com.picsauditing.resultset");
		//$JUnit-BEGIN$
		suite.addTestSuite(ResultSetRowTest.class);
		suite.addTestSuite(ResultSetTest.class);
		suite.addTestSuite(ResultSetQuestionTest.class);
		//$JUnit-END$
		return suite;
	}

}
