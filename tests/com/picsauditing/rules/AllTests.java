package com.picsauditing.rules;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests {

	public static Test suite() {
		TestSuite suite = new TestSuite("Test for com.picsauditing.Rules");
		//$JUnit-BEGIN$
		suite.addTestSuite(RulesRowTest.class);
		suite.addTestSuite(RulesTest.class);
		suite.addTestSuite(RulesQuestionTest.class);
		//$JUnit-END$
		return suite;
	}

}
