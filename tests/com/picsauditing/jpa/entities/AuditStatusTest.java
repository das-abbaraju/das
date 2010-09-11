package com.picsauditing.jpa.entities;

import junit.framework.TestCase;

import org.junit.Test;

public class AuditStatusTest extends TestCase {
	@Test
	public void testStatusArray() {
		AuditStatus[] statuses = AuditStatus.valuesWithoutPendingExpired();
		assertEquals(6, statuses.length);
		assertEquals(AuditStatus.Resubmitted, statuses[2]);
	}

}
