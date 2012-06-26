package com.picsauditing.access;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import java.util.Random;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.picsauditing.EntityFactory;
import com.picsauditing.jpa.entities.User;

public class BetaPoolTest {
	private Permissions permissions;
	
	@Before
	public void setUp() throws Exception {
		permissions = EntityFactory.makePermission();
	}
	
	@Test
	public void testDeveloper() throws Exception {
		permissions.getGroups().add(User.GROUP_STAKEHOLDER);

		assertFalse(BetaPool.isUserBetaTester(permissions, BetaPool.None));
		assertFalse(BetaPool.isUserBetaTester(permissions, BetaPool.Developer));
		assertTrue(BetaPool.isUserBetaTester(permissions, BetaPool.Stakeholder));
		assertTrue(BetaPool.isUserBetaTester(permissions, BetaPool.BetaTester));
		assertTrue(BetaPool.isUserBetaTester(permissions, BetaPool.Global));
	}

	@Test
	public void testGlobal() throws Exception {
		assertFalse(BetaPool.isUserBetaTester(permissions, BetaPool.None));
		assertFalse(BetaPool.isUserBetaTester(permissions, BetaPool.Developer));
		assertTrue(BetaPool.isUserBetaTester(permissions, BetaPool.Global));
	}
	
	@Test
	public void testGetBetaPoolByBetaLevel_NegativeNumbersReturnsNone() throws Exception {
		BetaPool result = BetaPool.getBetaPoolByBetaLevel(-1);
		
		assertThat(result, is(BetaPool.None));
	}

	@Test
	public void testGetBetaPoolByBetaLevel_TooLargeNumberReturnsNone() throws Exception {
		BetaPool result = BetaPool.getBetaPoolByBetaLevel(1000);
		
		assertThat(result, is(BetaPool.None));
	}

	@Test
	public void testGetBetaPoolByBetaLevel_GoodNumberNotNone() throws Exception {
		int numValues = BetaPool.values().length;
		int betaLevelToTry = new Random().nextInt(numValues);
		
		BetaPool result = BetaPool.getBetaPoolByBetaLevel(betaLevelToTry);
		
		assertNotNull(result);
	}
}
