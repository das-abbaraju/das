package com.picsauditing.toggle;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;
import groovy.lang.Binding;

import java.util.*;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import com.picsauditing.access.BetaPool;
import com.picsauditing.access.PermissionBuilder;
import com.picsauditing.access.Permissions;
import com.picsauditing.dao.AppPropertyDAO;
import com.picsauditing.dao.UserDAO;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.AppProperty;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.jpa.entities.UserGroup;
import com.picsauditing.jpa.entities.YesNo;
import com.picsauditing.util.hierarchy.AbstractBreadthFirstSearchBuilder;
import com.picsauditing.util.hierarchy.HierarchyBuilder;

public class FeatureToggleExpressionsTest {
	private FeatureToggleExpressions featureToggleExpressions;
    private Set<Integer> inheritedGroups;

	@Mock
	private Binding binding;
	@Mock
	private AppPropertyDAO appPropertyDAO;
	@Mock
	private FeatureToggle featureToggle;
	@Mock
	private UserDAO userDAO;
    @Mock
    private Permissions permissions;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		featureToggleExpressions = new FeatureToggleExpressionsTestable();

		when(binding.getVariable("appPropertyDAO")).thenReturn(appPropertyDAO);
        when(binding.getVariable("permissions")).thenReturn(permissions);
        inheritedGroups = new HashSet<>();
        when(permissions.getAllInheritedGroupIds()).thenReturn(inheritedGroups);
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
	public void testReleaseToUserAudienceLevel_DevByLevelInt() throws Exception {
        when(permissions.hasGroup(User.GROUP_DEVELOPER)).thenReturn(true);

		assertTrue(featureToggleExpressions.releaseToUserAudienceLevel(1));
		assertTrue(featureToggleExpressions.releaseToUserAudienceLevel(2));
	}

	@Test
	public void testReleaseToUserAudienceLevel_StakeholderByLevelInt() throws Exception {
        when(permissions.hasGroup(User.GROUP_STAKEHOLDER)).thenReturn(true);

		assertFalse(featureToggleExpressions.releaseToUserAudienceLevel(1));
		assertTrue(featureToggleExpressions.releaseToUserAudienceLevel(2));
	}

	@Test
	public void testReleaseToUserAudienceLevel_DevByBetaPoolEnum() throws Exception {
        when(permissions.hasGroup(User.GROUP_DEVELOPER)).thenReturn(true);

		assertTrue(featureToggleExpressions.releaseToUserAudienceLevel(BetaPool.Developer));
		assertTrue(featureToggleExpressions.releaseToUserAudienceLevel(BetaPool.Stakeholder));
	}

	@Test
	public void testReleaseToUserAudienceLevel_StakeholderByBetaPoolEnum() throws Exception {
        when(permissions.hasGroup(User.GROUP_STAKEHOLDER)).thenReturn(true);

		assertFalse(featureToggleExpressions.releaseToUserAudienceLevel(BetaPool.Developer));
		assertTrue(featureToggleExpressions.releaseToUserAudienceLevel(BetaPool.Stakeholder));
	}

	@Test
	public void testUserIsMemberOf_NullUserGroupIsFalse() throws Exception {
		assertFalse(featureToggleExpressions.userIsMemberOf((Integer) null));
	}

	@Test
	public void testUserIsMemberOf_NullPermissionsIsFalse() throws Exception {
		when(binding.getVariable("permissions")).thenReturn(null);
		assertFalse(featureToggleExpressions.userIsMemberOf((Integer) null));
	}

	@Test
	public void testUserIsmemberOf_ById_UserIsReturnsTrue() throws Exception {
        inheritedGroups.add(1);

		assertTrue(featureToggleExpressions.userIsMemberOf(1));
	}

	@Test
	public void testUserIsMemberOf_UserIsNotMemberReturnsFalse() throws Exception {
		assertFalse(featureToggleExpressions.userIsMemberOf(4561));
	}

	@Test
	public void testUserIsMemberOfAny_UserIsReturnsTrue() throws Exception {
        inheritedGroups.add(2);

		List<Integer> groups = new ArrayList<Integer>();
		groups.add(1);
		groups.add(2);
		groups.add(3);

		assertTrue(featureToggleExpressions.userIsMemberOfAny(groups));
	}

	@Test
	public void testUserIsmemberOfAny_ByInt_UserIsReturnsTrue() throws Exception {
        inheritedGroups.add(1);
        inheritedGroups.add(2);

		List<Integer> groups = new ArrayList<Integer>();
		groups.add(1);
		groups.add(10);
		groups.add(11);

		assertTrue(featureToggleExpressions.userIsMemberOfAny(groups));
	}

	@Test
	public void testUserIsmemberOfAny_NullUserGroupsReturnsFalse() throws Exception {
		assertFalse(featureToggleExpressions.userIsMemberOfAny(null));
	}

	@Test
	public void testUserIsmemberOfAny_EmptyUserGroupsReturnsFalse() throws Exception {
		assertFalse(featureToggleExpressions.userIsMemberOfAny(new ArrayList<Integer>()));
	}

	@Test
	public void testUserIsmemberOfAny_UserIsNotReturnsFalse() throws Exception {
        inheritedGroups.add(1);
        inheritedGroups.add(2);

		List<Integer> groups = new ArrayList<Integer>();
		groups.add(10);
		groups.add(20);
		groups.add(30);

		assertFalse(featureToggleExpressions.userIsMemberOfAny(groups));
	}

	@Test
	public void testUserIsmemberOfAll_UserIsReturnsTrue() throws Exception {
        inheritedGroups.add(1);
        inheritedGroups.add(2);
        inheritedGroups.add(3);
        inheritedGroups.add(4);
        inheritedGroups.add(5);

		List<Integer> groups = new ArrayList<Integer>();
		groups.add(1);
		groups.add(2);
		groups.add(3);

		assertTrue(featureToggleExpressions.userIsMemberOfAll(groups));
	}

	@Test
	public void testUserIsmemberOfAll_ByInt_UserIsReturnsTrue() throws Exception {
        inheritedGroups.add(1);
        inheritedGroups.add(2);
        inheritedGroups.add(3);
        inheritedGroups.add(4);
        inheritedGroups.add(5);

		List<Integer> groups = new ArrayList<Integer>();
		groups.add(1);
		groups.add(2);
		groups.add(3);

		assertTrue(featureToggleExpressions.userIsMemberOfAll(groups));
	}

	@Test
	public void testUserIsmemberOfAll_UserIsNotReturnsFalse() throws Exception {
        inheritedGroups.add(1);
        inheritedGroups.add(3);
        inheritedGroups.add(4);
        inheritedGroups.add(5);

		List<Integer> groups = new ArrayList<Integer>();
		groups.add(1);
		groups.add(2);
		groups.add(3);

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

	@Test
	public void testHasPermission_Happy() throws Exception {
		assertFalse(featureToggleExpressions.hasPermission("RestApi"));
	}

	@Test(expected = FeatureToggleException.class)
	public void testHasPermission_NoSuchPermission() throws Exception {
		assertFalse(featureToggleExpressions.hasPermission("NoSuchPermission"));
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
