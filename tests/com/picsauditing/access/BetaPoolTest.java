package com.picsauditing.access;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import java.util.Random;

import org.junit.Before;
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

	// if user is a developer, then features meant for anybody will be visible
	@Test
	public void testIsUserBetaTester_UserIsGroupDeveloper() throws Exception {
		permissions.getGroups().add(User.GROUP_DEVELOPER);

		nobodySeesNoneAndEverybodySeesGlobal();
		
		assertTrue(BetaPool.isUserBetaTester(permissions, BetaPool.Developer));
		assertTrue(BetaPool.isUserBetaTester(permissions, BetaPool.Stakeholder));
		assertTrue(BetaPool.isUserBetaTester(permissions, BetaPool.BetaTester));
	}

	// if user is a stakeholder, then features meant for developers should not
	// be visible to them
	// however, things meant for beta testers and for global will be visible
	@Test
	public void testIsUserBetaTester_UserIsGroupStakeHolder() throws Exception {
		permissions.getGroups().add(User.GROUP_STAKEHOLDER);

		nobodySeesNoneAndEverybodySeesGlobal();

		assertFalse(BetaPool.isUserBetaTester(permissions, BetaPool.Developer));
		assertTrue(BetaPool.isUserBetaTester(permissions, BetaPool.Stakeholder));
		assertTrue(BetaPool.isUserBetaTester(permissions, BetaPool.BetaTester));
	}

	// if user is a betatester, then things meant for developers and
	// stakeholders are not visible
	@Test
	public void testIsUserBetaTester_UserIsGroupBetaTest() throws Exception {
		permissions.getGroups().add(User.GROUP_BETATESTER);

		nobodySeesNoneAndEverybodySeesGlobal();

		assertFalse(BetaPool.isUserBetaTester(permissions, BetaPool.Developer));
		assertFalse(BetaPool.isUserBetaTester(permissions, BetaPool.Stakeholder));
		assertTrue(BetaPool.isUserBetaTester(permissions, BetaPool.BetaTester));
	}

	@Test
	public void testIsUserBetaTester_NotAMemberOfAnyBetaGroup() throws Exception {
		// only global should be true
		nobodySeesNoneAndEverybodySeesGlobal();
		assertFalse(BetaPool.isUserBetaTester(permissions, BetaPool.Developer));
		assertFalse(BetaPool.isUserBetaTester(permissions, BetaPool.Stakeholder));
	}
	
	private void nobodySeesNoneAndEverybodySeesGlobal() {
		assertFalse(BetaPool.isUserBetaTester(permissions, BetaPool.None));
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

}
