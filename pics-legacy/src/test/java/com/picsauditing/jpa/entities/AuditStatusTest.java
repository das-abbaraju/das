package com.picsauditing.jpa.entities;

import junit.framework.TestCase;

import org.junit.Test;

public class AuditStatusTest extends TestCase {
	@Test
	public void testStatusArray() {
		AuditStatus[] statuses = AuditStatus.activeStatusesBeyondPending();
		assertEquals(7, statuses.length);
		assertEquals(AuditStatus.Incomplete, statuses[0]);
		assertEquals(AuditStatus.Approved, statuses[6]);
	}

}
