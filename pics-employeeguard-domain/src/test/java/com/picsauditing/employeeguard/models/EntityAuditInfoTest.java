package com.picsauditing.employeeguard.models;

import com.picsauditing.PICS.DateBean;
import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.assertEquals;

public class EntityAuditInfoTest {

	@Test(expected = IllegalStateException.class)
	public void testBuilder_MissingAppUserId() {
		new EntityAuditInfo.Builder().timestamp(new Date()).build();
	}

	@Test(expected = IllegalStateException.class)
	public void testBuilder_MissingTimestamp() {
		new EntityAuditInfo.Builder().appUserId(3).build();
	}

	@Test
	public void testBuilder() {
		Date timestamp = DateBean.today();

		EntityAuditInfo entityAuditInfo = new EntityAuditInfo.Builder().appUserId(56).timestamp(timestamp).build();

		assertEquals(56, entityAuditInfo.getAppUserId());
		assertEquals(timestamp, entityAuditInfo.getTimestamp());
	}
}
