package com.picsauditing.report.fields;

import static org.junit.Assert.assertEquals;

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
		assertEquals(3, DisplayType.Date.getFunctions().size());
	}

	@Test
	public void testFloatFunctions() {
		log(DisplayType.Float);
		assertEquals(6, DisplayType.Float.getFunctions().size());
	}

	@Test
	public void testIntFunctions() {
		log(DisplayType.Integer);
		assertEquals(5, DisplayType.Integer.getFunctions().size());
	}

	private void log(DisplayType type) {
		for (QueryMethod function : type.getFunctions()) {
			LOG.info(type + " " + function);
		}
	}
}
