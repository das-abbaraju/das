package com.picsauditing.employeeguard.services.calculator;

import com.picsauditing.employeeguard.services.status.DocumentStatus;
import com.picsauditing.employeeguard.services.status.DocumentStatusCalculator;
import org.junit.Before;
import org.junit.Test;

import java.util.Calendar;

import static org.junit.Assert.assertEquals;

public class DocumentStatusCalculatorTest {
	private DocumentStatusCalculator documentStatusCalculator;

	@Before
	public void setUp() {
		documentStatusCalculator = new DocumentStatusCalculator();
	}

	@Test
	public void testCalculate_NotExpiringSoon() throws Exception {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.YEAR, 1);

		assertEquals(DocumentStatus.Complete, documentStatusCalculator.calculate(calendar.getTime()));
	}

	@Test
	public void testCalculate_ExpiringSoon() throws Exception {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.WEEK_OF_YEAR, 1);

		assertEquals(DocumentStatus.Expiring, documentStatusCalculator.calculate(calendar.getTime()));
	}

	@Test
	public void testCalculate_Expired() throws Exception {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DAY_OF_YEAR, -1);

		assertEquals(DocumentStatus.Expired, documentStatusCalculator.calculate(calendar.getTime()));
	}
}
