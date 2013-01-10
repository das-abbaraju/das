package com.picsauditing.report.fields;

import static org.junit.Assert.assertEquals;

import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DisplayTypeTest {
	private final static Logger LOG = LoggerFactory.getLogger(DisplayTypeTest.class);

	@Test
	public void testBooleanFunctions() {
		log(DisplayType.Boolean);
		assertEquals(3, DisplayType.Boolean.getFunctions().size());
	}

	@Test
	public void testDateFunctions() {
		log(DisplayType.Date);
		assertEquals(9, DisplayType.Date.getFunctions().size());
	}

	@Test
	public void testFloatFunctions() {
		log(DisplayType.RightAlign);
		assertEquals(6, DisplayType.RightAlign.getFunctions().size());
	}

	@Test
	public void testIntFunctions() {
		log(DisplayType.RightAlign);
		assertEquals(6, DisplayType.RightAlign.getFunctions().size());
	}

	private void log(DisplayType type) {
		for (SqlFunction function : type.getFunctions()) {
			LOG.info(type + " " + function);
		}
	}
}
