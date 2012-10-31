package com.picsauditing.jpa.entities;

import java.util.Calendar;

import org.junit.Assert;
import org.junit.Test;

public class UserTest {

	@Test
	public void testIsLocked() throws Exception {
		User user = new User();
		Assert.assertFalse(user.isLocked());
	}

	@Test
	public void testIsLockedNow() throws Exception {
		User user = new User();
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DAY_OF_YEAR, 1);
		user.setLockUntil(calendar.getTime());
		Assert.assertTrue(user.isLocked());
	}

	@Test
	public void testIsLockedBefore() throws Exception {
		User user = new User();
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DAY_OF_YEAR, -1);
		user.setLockUntil(calendar.getTime());
		Assert.assertFalse(user.isLocked());
	}
}
