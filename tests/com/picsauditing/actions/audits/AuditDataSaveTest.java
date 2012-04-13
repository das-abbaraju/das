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
		assertEquals("0", AuditDataSave.trimWhitespaceLeadingZerosAndAllCommas("0"));
		assertEquals("0", AuditDataSave.trimWhitespaceLeadingZerosAndAllCommas("00"));
		assertEquals("0", AuditDataSave.trimWhitespaceLeadingZerosAndAllCommas("000"));
		assertEquals(".0", AuditDataSave.trimWhitespaceLeadingZerosAndAllCommas("000.0"));
		assertEquals(".00", AuditDataSave.trimWhitespaceLeadingZerosAndAllCommas("000.00"));
		assertEquals(".01", AuditDataSave.trimWhitespaceLeadingZerosAndAllCommas("0.01"));
		assertEquals("1", AuditDataSave.trimWhitespaceLeadingZerosAndAllCommas("01"));
		assertEquals("1", AuditDataSave.trimWhitespaceLeadingZerosAndAllCommas("001"));
		assertEquals("1.01", AuditDataSave.trimWhitespaceLeadingZerosAndAllCommas("  01.01"));
		assertEquals("1.01", AuditDataSave.trimWhitespaceLeadingZerosAndAllCommas("\t01.01"));
		assertEquals("ABC", AuditDataSave.trimWhitespaceLeadingZerosAndAllCommas("ABC"));
		assertEquals("ABC", AuditDataSave.trimWhitespaceLeadingZerosAndAllCommas("  ABC  "));
	}
}
