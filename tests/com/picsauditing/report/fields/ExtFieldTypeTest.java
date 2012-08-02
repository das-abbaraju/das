package com.picsauditing.report.fields;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExtFieldTypeTest {
	private final static Logger LOG = LoggerFactory.getLogger(ExtFieldTypeTest.class);

	@Test
	public void testGetAutoFunctions() {
		log(ExtFieldType.Auto);
		assertEquals(0, ExtFieldType.Auto.getFunctions().size());
	}

	@Test
	public void testBooleanFunctions() {
		log(ExtFieldType.Boolean);
		assertEquals(5, ExtFieldType.Boolean.getFunctions().size());
	}

	@Test
	public void testDateFunctions() {
		log(ExtFieldType.Date);
		assertEquals(11, ExtFieldType.Date.getFunctions().size());
	}

	@Test
	public void testFloatFunctions() {
		log(ExtFieldType.Float);
		assertEquals(8, ExtFieldType.Float.getFunctions().size());
	}

	@Test
	public void testIntFunctions() {
		log(ExtFieldType.Int);
		assertEquals(7, ExtFieldType.Int.getFunctions().size());
	}

	private void log(ExtFieldType type) {
		for (QueryMethod function : type.getFunctions()) {
			LOG.info(type + " " + function);
		}
	}
}
