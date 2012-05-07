package com.picsauditing.access;

import static org.junit.Assert.*;

import org.junit.Ignore;
import org.junit.Test;

import com.picsauditing.EntityFactory;
import com.picsauditing.jpa.entities.User;

public class BetaPoolTest {
	@Test
	public void testDeveloper() throws Exception {
		Permissions permissions = EntityFactory.makePermission();
		permissions.getGroups().add(User.GROUP_STAKEHOLDER);

		assertFalse(BetaPool.isUserBetaTester(permissions, BetaPool.None));
		assertFalse(BetaPool.isUserBetaTester(permissions, BetaPool.Developer));
		assertTrue(BetaPool.isUserBetaTester(permissions, BetaPool.Stakeholder));
		assertTrue(BetaPool.isUserBetaTester(permissions, BetaPool.BetaTester));
		assertTrue(BetaPool.isUserBetaTester(permissions, BetaPool.Global));
	}

	@Ignore("Not ready to run yet.")
	@Test
	public void testGlobal() throws Exception {
		Permissions permissions = EntityFactory.makePermission();

		assertFalse(BetaPool.isUserBetaTester(permissions, BetaPool.None));
		assertFalse(BetaPool.isUserBetaTester(permissions, BetaPool.Developer));
		assertTrue(BetaPool.isUserBetaTester(permissions, BetaPool.Global));
	}

}
