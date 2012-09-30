package com.picsauditing.toggle;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import groovy.lang.Binding;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.picsauditing.access.BetaPool;
import com.picsauditing.access.Permissions;
import com.picsauditing.dao.AppPropertyDAO;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.AppProperty;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.jpa.entities.UserGroup;
import com.picsauditing.jpa.entities.YesNo;

public class FeatureToggleExpressionsTest {
	private FeatureToggleExpressions featureToggleExpressions;
	private List<UserGroup> groups;
	private Permissions permissions;

	@Mock
	private Binding binding;
	@Mock
	private User currentUser;
	@Mock
	private Account account;
	@Mock
	private AppPropertyDAO appPropertyDAO;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		featureToggleExpressions = new FeatureToggleExpressionsTestable();

		groups = new ArrayList<UserGroup>();

		when(currentUser.getGroups()).thenReturn(groups);
		when(currentUser.getId()).thenReturn(941);
		when(currentUser.getName()).thenReturn("Test Group");
		when(currentUser.getAccount()).thenReturn(account);
		when(currentUser.getLocale()).thenReturn(Locale.ENGLISH);

		permissions = new Permissions();
		when(binding.getVariable("permissions")).thenReturn(permissions);
		when(binding.getVariable("appPropertyDAO")).thenReturn(appPropertyDAO);
	}

	private void addUserGroup(int id, String name) {
		UserGroup group = new UserGroup();
		User user = new User();
		user.setId(id);
		user.setName(name);
		user.setIsGroup(YesNo.Yes);
		group.setGroup(user);
		groups.add(group);
	}

	@Test
	public void testReleaseToApplicationAudienceLevel_NobodyLevelIsFalse() throws Exception {
		when(appPropertyDAO.getProperty(AppProperty.BETA_LEVEL)).thenReturn("3");

		assertFalse(featureToggleExpressions.releaseToApplicationAudienceLevel(0));
	}

	@Test
	public void testReleaseToApplicationAudienceLevel_StakeHolderByInt_TrueEquals() throws Exception {
		when(appPropertyDAO.getProperty(AppProperty.BETA_LEVEL)).thenReturn("3");

		assertTrue(featureToggleExpressions.releaseToApplicationAudienceLevel(3));
	}

	@Test
	public void testReleaseToApplicationAudienceLevel_StakeHolderByInt_TrueGreater() throws Exception {
		when(appPropertyDAO.getProperty(AppProperty.BETA_LEVEL)).thenReturn("4");

		assertTrue(featureToggleExpressions.releaseToApplicationAudienceLevel(3));
	}

	@Test
	public void testReleaseToApplicationAudienceLevel_DevByBetaPool_TrueEquals() throws Exception {
		when(appPropertyDAO.getProperty(AppProperty.BETA_LEVEL)).thenReturn("1");

		assertTrue(featureToggleExpressions.releaseToApplicationAudienceLevel(BetaPool.Developer));
	}

	@Test
	public void testReleaseToApplicationAudienceLevel_DevByBetaPool_TrueGreater() throws Exception {
		when(appPropertyDAO.getProperty(AppProperty.BETA_LEVEL)).thenReturn("2");

		assertTrue(featureToggleExpressions.releaseToApplicationAudienceLevel(BetaPool.Developer));
	}

	@Test
	public void testReleaseToApplicationAudienceLevel_FalseUnparseableLevel() throws Exception {
		when(appPropertyDAO.getProperty(AppProperty.BETA_LEVEL)).thenReturn("NAN");

		assertFalse(featureToggleExpressions.releaseToApplicationAudienceLevel(BetaPool.Developer));
	}

	@Test
	public void testReleaseToApplicationAudienceLevel_DevByLevelInt() throws Exception {

	}

	@Test
	public void testReleaseToUserAudienceLevel_DevByLevelInt() throws Exception {
		addUserGroup(User.GROUP_DEVELOPER, "Developer");
		permissions.login(currentUser);

		assertTrue(featureToggleExpressions.releaseToUserAudienceLevel(1));
		assertTrue(featureToggleExpressions.releaseToUserAudienceLevel(2));
	}

	@Test
	public void testReleaseToUserAudienceLevel_StakeholderByLevelInt() throws Exception {
		addUserGroup(User.GROUP_STAKEHOLDER, "Stakeholder");
		permissions.login(currentUser);

		assertFalse(featureToggleExpressions.releaseToUserAudienceLevel(1));
		assertTrue(featureToggleExpressions.releaseToUserAudienceLevel(2));
	}

	@Test
	public void testReleaseToUserAudienceLevel_DevByBetaPoolEnum() throws Exception {
		addUserGroup(User.GROUP_DEVELOPER, "Developer");
		permissions.login(currentUser);

		assertTrue(featureToggleExpressions.releaseToUserAudienceLevel(BetaPool.Developer));
		assertTrue(featureToggleExpressions.releaseToUserAudienceLevel(BetaPool.Stakeholder));
	}

	@Test
	public void testReleaseToUserAudienceLevel_StakeholderByBetaPoolEnum() throws Exception {
		addUserGroup(User.GROUP_STAKEHOLDER, "Stakeholder");
		permissions.login(currentUser);

		assertFalse(featureToggleExpressions.releaseToUserAudienceLevel(BetaPool.Developer));
		assertTrue(featureToggleExpressions.releaseToUserAudienceLevel(BetaPool.Stakeholder));
	}

	@Test
	public void testUserIsMemberOf_NullUserGroupIsFalse() throws Exception {
		assertFalse(featureToggleExpressions.userIsMemberOf((String) null));
		assertFalse(featureToggleExpressions.userIsMemberOf((Integer) null));
	}

	@Test
	public void testUserIsMemberOf_NullPermissionsIsFalse() throws Exception {
		when(binding.getVariable("permissions")).thenReturn(null);
		assertFalse(featureToggleExpressions.userIsMemberOf((String) null));
	}

	@Test
	public void testUserIsmemberOf_UserIsReturnsTrue() throws Exception {
		addUserGroup(1, "test1");
		permissions.login(currentUser);

		assertTrue(featureToggleExpressions.userIsMemberOf("test1"));
	}

	@Test
	public void testUserIsmemberOf_ById_UserIsReturnsTrue() throws Exception {
		addUserGroup(1, "test1");
		permissions.login(currentUser);

		assertTrue(featureToggleExpressions.userIsMemberOf(1));
	}

	@Test
	public void testUserIsmemberOf_UserIsNotMemberReturnsFalse() throws Exception {
		permissions.login(currentUser);

		assertFalse(featureToggleExpressions.userIsMemberOf("NOT A GOOD GROUP"));
	}

	@Test
	public void testUserIsmemberOfAny_UserIsReturnsTrue() throws Exception {
		addUserGroup(2, "test2");
		permissions.login(currentUser);

		List<String> groups = new ArrayList<String>();
		groups.add("no");
		groups.add("test2");
		groups.add("nope");

		assertTrue(featureToggleExpressions.userIsMemberOfAny(groups));
	}

	@Test
	public void testUserIsmemberOfAny_ByInt_UserIsReturnsTrue() throws Exception {
		addUserGroup(1, "test1");
		addUserGroup(2, "test2");
		permissions.login(currentUser);

		List<Integer> groups = new ArrayList<Integer>();
		groups.add(1);
		groups.add(10);
		groups.add(11);

		assertTrue(featureToggleExpressions.userIsMemberOfAny(groups));
	}

	@Test
	public void testUserIsmemberOfAny_MixedCollection_UserIsReturnsTrue() throws Exception {
		addUserGroup(1, "test1");
		addUserGroup(2, "test2");
		permissions.login(currentUser);

		List<Object> groups = new ArrayList<Object>();
		groups.add(1);
		groups.add("test2");
		groups.add("nope");

		assertTrue(featureToggleExpressions.userIsMemberOfAny(groups));
	}

	@Test
	public void testUserIsmemberOfAny_NullUserGroupsReturnsFalse() throws Exception {
		assertFalse(featureToggleExpressions.userIsMemberOfAny(null));
	}

	@Test
	public void testUserIsmemberOfAny_EmptyUserGroupsReturnsFalse() throws Exception {
		assertFalse(featureToggleExpressions.userIsMemberOfAny(new ArrayList<String>()));
	}

	@Test
	public void testUserIsmemberOfAny_UserIsNotReturnsFalse() throws Exception {
		addUserGroup(1, "test1");
		addUserGroup(2, "test2");
		permissions.login(currentUser);

		List<String> groups = new ArrayList<String>();
		groups.add("no");
		groups.add("huh uh");
		groups.add("nope");

		assertFalse(featureToggleExpressions.userIsMemberOfAny(groups));
	}

	@Test
	public void testUserIsmemberOfAll_UserIsReturnsTrue() throws Exception {
		addUserGroup(1, "test1");
		addUserGroup(2, "test2");
		addUserGroup(3, "test3");
		addUserGroup(4, "test4");
		addUserGroup(5, "test5");
		permissions.login(currentUser);

		List<String> groups = new ArrayList<String>();
		groups.add("test1");
		groups.add("test2");
		groups.add("test3");

		assertTrue(featureToggleExpressions.userIsMemberOfAll(groups));
	}

	@Test
	public void testUserIsmemberOfAll_ByInt_UserIsReturnsTrue() throws Exception {
		addUserGroup(1, "test1");
		addUserGroup(2, "test2");
		addUserGroup(3, "test3");
		addUserGroup(4, "test4");
		addUserGroup(5, "test5");
		permissions.login(currentUser);

		List<Integer> groups = new ArrayList<Integer>();
		groups.add(1);
		groups.add(2);
		groups.add(3);

		assertTrue(featureToggleExpressions.userIsMemberOfAll(groups));
	}

	@Test
	public void testUserIsmemberOfAll_MixedCollection_UserIsReturnsTrue() throws Exception {
		addUserGroup(1, "test1");
		addUserGroup(2, "test2");
		addUserGroup(3, "test3");
		addUserGroup(4, "test4");
		addUserGroup(5, "test5");
		permissions.login(currentUser);

		List<Object> groups = new ArrayList<Object>();
		groups.add(1);
		groups.add("test2");
		groups.add(3);

		assertTrue(featureToggleExpressions.userIsMemberOfAll(groups));
	}

	@Test
	public void testUserIsmemberOfAll_UserIsNotReturnsFalse() throws Exception {
		addUserGroup(1, "test1");
		addUserGroup(3, "test3");
		addUserGroup(4, "test4");
		addUserGroup(5, "test5");
		permissions.login(currentUser);

		List<String> groups = new ArrayList<String>();
		groups.add("test1");
		groups.add("test2");
		groups.add("test3");

		assertFalse(featureToggleExpressions.userIsMemberOfAll(groups));
	}

	@Test(expected = FeatureToggleException.class)
	public void testVersionOf_BadSystemStringThrowsException() throws Exception {
		featureToggleExpressions.versionOf("BAD_ROBOT");
	}

	@Test(expected = FeatureToggleException.class)
	public void testVersionOfBackProcs_NonNumericVersionThrowsException() throws Exception {
		when(appPropertyDAO.getProperty("VERSION.BPROC")).thenReturn("NAN");

		featureToggleExpressions.versionOfBackProcs();
	}

	@Test
	public void testApplicationBetaLevel_Happy() throws Exception {
		when(appPropertyDAO.getProperty(AppProperty.BETA_LEVEL)).thenReturn("3");
		assertThat(featureToggleExpressions.applicationBetaLevel(), is(equalTo(3f)));
	}

	@Test(expected = FeatureToggleException.class)
	public void testApplicationBetaLevel_NonNumericBetaLevelThrowsException() throws Exception {
		when(appPropertyDAO.getProperty(AppProperty.BETA_LEVEL)).thenReturn("NAN");
		featureToggleExpressions.applicationBetaLevel();
	}

	private class FeatureToggleExpressionsTestable extends FeatureToggleExpressions {

		@Override
		public Object run() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Binding getBinding() {
			return binding;
		}

	}
}
