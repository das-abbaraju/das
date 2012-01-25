package com.picsauditing.jpa.entities;

import junit.framework.TestCase;

import org.junit.Test;

public class AuditStatusTest extends TestCase {
	@Test
	public void testStatusArray() {
		AuditStatus[] statuses = AuditStatus.valuesWithoutPending();
		assertEquals(6, statuses.length);
		assertEquals(AuditStatus.Resubmitted, statuses[3]);
	}

}
