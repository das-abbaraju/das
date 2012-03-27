package com.picsauditing.actions.audits;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class AuditDataSaveTest {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testTrimWhitespaceLeadingZerosAndAllCommas() throws Exception {
		assertEquals("10.10", AuditDataSave.trimWhitespaceLeadingZerosAndAllCommas("10.10"));
		assertEquals("10.10", AuditDataSave.trimWhitespaceLeadingZerosAndAllCommas("  10.10"));
		assertEquals("10.10", AuditDataSave.trimWhitespaceLeadingZerosAndAllCommas("10.10  "));
		assertEquals("10.10", AuditDataSave.trimWhitespaceLeadingZerosAndAllCommas("010.10"));
		assertEquals("10.10", AuditDataSave.trimWhitespaceLeadingZerosAndAllCommas("00010.10"));
		assertEquals("10.10", AuditDataSave.trimWhitespaceLeadingZerosAndAllCommas(",010.10"));
		assertEquals("10.10", AuditDataSave.trimWhitespaceLeadingZerosAndAllCommas("0,010.10"));
	}
}
