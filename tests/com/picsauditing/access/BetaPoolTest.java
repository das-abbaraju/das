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
	public void testBetaPoolNumbers() throws Exception {
		// It is important that these numbers do not change, because we currently rely on being able to refer to these levels by number in the app_properties table.
		assertEquals(BetaPool.None,BetaPool.getBetaPoolByBetaLevel(0));
		assertEquals(BetaPool.Developer,BetaPool.getBetaPoolByBetaLevel(1));
		assertEquals(BetaPool.Stakeholder,BetaPool.getBetaPoolByBetaLevel(2));
		assertEquals(BetaPool.BetaTester,BetaPool.getBetaPoolByBetaLevel(3));
		assertEquals(BetaPool.Global,BetaPool.getBetaPoolByBetaLevel(4));
	}
	@Test
	public void testDeveloper() throws Exception {
		// FIXME Why is this test called testDeveloper()?  There is not anything specifically Developer about it.
		permissions.getGroups().add(User.GROUP_STAKEHOLDER);

		assertFalse(BetaPool.isUserBetaTester(permissions, BetaPool.None));
		assertFalse(BetaPool.isUserBetaTester(permissions, BetaPool.Developer));
		assertTrue(BetaPool.isUserBetaTester(permissions, BetaPool.Stakeholder));
		assertTrue(BetaPool.isUserBetaTester(permissions, BetaPool.BetaTester));
		assertTrue(BetaPool.isUserBetaTester(permissions, BetaPool.Global));
	}

	@Test
	public void testGlobal() throws Exception {
		// FIXME How is this test different from testDeveloper()?  Other than doing less?  
		// FIXME Why is this test called testGlobal()?  There is not anything specifically Global about it.
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
		// FIXME - doing unit tests against random numbers is not a good practice.  Better would be to test the first and last values in the range (if not all of them)
		int betaLevelToTry = new Random().nextInt(numValues);
		
		BetaPool result = BetaPool.getBetaPoolByBetaLevel(betaLevelToTry);
		
		assertNotNull(result);
	}
}
