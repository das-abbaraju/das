package com.picsauditing.auditBuilder;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.picsauditing.PicsTest;

public class AuditPercentCalculatorTest extends PicsTest {
	private AuditPercentCalculator calculator = new AuditPercentCalculator();
	
	@Before
	public void setUp() throws Exception {
		
	}
	
	@Test
	public void test() throws Exception {
		autowireEMInjectedDAOs(calculator);
		// The AuditPercentCalculator has the highest Complexity score (13+) but absolutely no unit tests
		// http://cobertura.picsauditing.com/com.picsauditing.auditBuilder.AuditPercentCalculator.html
		// TODO Get this under test ASAP
	}
}
