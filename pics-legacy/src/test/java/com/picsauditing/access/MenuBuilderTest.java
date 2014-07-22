package com.picsauditing.access;

import com.picsauditing.PicsActionTest;
import com.picsauditing.PicsTestUtil;
import com.picsauditing.access.user.UserMode;
import com.picsauditing.employeeguard.services.ProfileService;
import com.picsauditing.featuretoggle.Features;
import com.picsauditing.jpa.entities.AccountStatus;
import com.picsauditing.menu.MenuComponent;
import com.picsauditing.menu.builder.MenuBuilder;
import com.picsauditing.provisioning.ProductSubscriptionService;
import com.picsauditing.util.SpringUtils;
import com.picsauditing.util.URLUtils;
import org.approvaltests.Approvals;
import org.approvaltests.reporters.DiffReporter;
import org.approvaltests.reporters.UseReporter;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;
import org.togglz.junit.TogglzRule;

import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.when;

@UseReporter(DiffReporter.class)
public class MenuBuilderTest extends PicsActionTest {

	public static final int USER_ID = 123;
	public static final int SWITCHED_TO_ID = 456;
	public static final int APP_USER_ID = 54321;
	public static final String ACTION_LINK = "ActionLink";

	@Mock
	private Permissions permissions;
	@Mock
	private URLUtils urlUtils;
	@Mock
	private ProductSubscriptionService productSubscriptionService;
	@Mock
	private ProfileService profileService;

	@Rule
	public TogglzRule togglzRule = TogglzRule.allEnabled(Features.class);

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		setupMocks();

		Map<String, Object> beans = new HashMap<>();
		beans.put(SpringUtils.PRODUCT_SUBSCRIPTION_SERVICE, productSubscriptionService);
		beans.put(SpringUtils.PROFILE_SERVICE, profileService);
		PicsTestUtil.setSpringUtilsBeans(beans);

		when(translationService.getText(anyString(), (Locale) any())).thenReturn("");
		when(permissions.isLoggedIn()).thenReturn(true);
		when(permissions.getUserId()).thenReturn(USER_ID);
		when(permissions.getLocale()).thenReturn(Locale.ENGLISH);
		when(permissions.getName()).thenReturn("John Doe");
		when(permissions.getAccountName()).thenReturn("John Doe Construction");
		when(permissions.getAccountId()).thenReturn(USER_ID);
		when(permissions.getEmail()).thenReturn("tester@picsauditing.com");
	}

	@AfterClass
	public static void tearDown() throws Exception {
		PicsActionTest.classTearDown();
		PicsTestUtil.resetSpringUtilsBeans();
	}

	@Test
	public void testBuildMenubar_noPermissions() throws Exception {
		MenuBuilder.setUrlUtils(urlUtils);
		when(translationService.getText("global.Company", Locale.ENGLISH)).thenReturn("Company");
		when(translationService.getText("menu.Reports", Locale.ENGLISH)).thenReturn("Reports");
		when(translationService.getText("menu.Manage", Locale.ENGLISH)).thenReturn("Manage");
		when(translationService.getText("menu.Configure", Locale.ENGLISH)).thenReturn("Configure");
		when(translationService.getText("menu.Dev", Locale.ENGLISH)).thenReturn("Dev");
		when(translationService.getText("menu.Support", Locale.ENGLISH)).thenReturn("Support");
		when(permissions.getAccountStatus()).thenReturn(AccountStatus.Active);

		when(permissions.getAccountStatus()).thenReturn(AccountStatus.Demo);

		MenuComponent menu = MenuBuilder.buildMenubar(permissions);
		assertTrue(menu.getChildren().size() == 4);
		for (MenuComponent child : menu.getChildren()) {
			assertNotSame("Configure", child.getName());
			assertNotSame("Manage", child.getName());
		}
	}

	@Test
	public void testSwitchedToAnotherUser_AdminIdNotSet() throws Exception {
		boolean switchedToAnotherUser = Whitebox.invokeMethod(MenuBuilder.class, "switchedToAnotherUser", permissions);
		assertFalse(switchedToAnotherUser);
	}

	@Test
	public void testSwitchedToAnotherUser_AdminIdDoesNotEqualUserId() throws Exception {
		when(permissions.getAdminID()).thenReturn(USER_ID);
		when(permissions.getUserId()).thenReturn(SWITCHED_TO_ID);

		boolean switchedToAnotherUser = Whitebox.invokeMethod(MenuBuilder.class, "switchedToAnotherUser", permissions);
		assertTrue(switchedToAnotherUser);
	}

	@Test
	public void testSwitchedToAnotherUser_AdminIdEqualsUserId() throws Exception {
		when(permissions.getAdminID()).thenReturn(USER_ID);
		when(permissions.getUserId()).thenReturn(USER_ID);

		boolean switchedToAnotherUser = Whitebox.invokeMethod(MenuBuilder.class, "switchedToAnotherUser", permissions);
		assertFalse(switchedToAnotherUser);
	}

	@Test
	public void testGetMibewURL_LoggedIn() throws Exception {
		String expectedURL = "https://chat.picsorganizer.com/client.php?locale=en&style=PICS&name=Name&accountName=AccountName&accountId=12345&userId=54321&email=me%40example.com";
		when(translationService.getText(eq("Mibew.LanguageCode"), any(Locale.class))).thenReturn("en");
		setupPermissionsForMibew();

		String mbewURL = MenuBuilder.getMibewURL(Locale.ENGLISH, permissions);

		assert (expectedURL.equals(mbewURL));
	}

	@Test
	public void testGetMibewURL_NotLoggedIn() throws Exception {
		String expectedURL = "https://chat.picsorganizer.com/client.php?locale=en&style=PICS";
		when(translationService.getText(eq("Mibew.LanguageCode"), any(Locale.class))).thenReturn("en");
		when(permissions.isLoggedIn()).thenReturn(false);

		String mbewURL = MenuBuilder.getMibewURL(Locale.ENGLISH, permissions);

		assert (expectedURL.equals(mbewURL));
	}

	@Test
	public void testBuildMenubar_OperatorClientSitesActivityWatch() throws Exception {
		MenuBuilder.setUrlUtils(urlUtils);
		when(urlUtils.getActionUrl(anyString(), anyString(), anyInt())).thenReturn(ACTION_LINK);
		when(permissions.isOperatorCorporate()).thenReturn(true);
		when(permissions.getAccountStatus()).thenReturn(AccountStatus.Active);
		when(permissions.isShowClientSitesLink()).thenReturn(true);
		when(permissions.isAdmin()).thenReturn(true);

		when(translationService.getText(eq("global.Company"), any(Locale.class))).thenReturn("Company");
		when(translationService.getText(eq("global.Facilities"), any(Locale.class))).thenReturn("Client Sites");
		when(translationService.getText(eq("ReportActivityWatch.title"), any(Locale.class))).thenReturn("Activity Watch");
		MenuComponent menu = MenuBuilder.buildMenubar(permissions);
		assertTrue(menu.getChildren().size() == 5);
		MenuComponent companyMenu = menu.getChildren().get(0);
		assertEquals("Company", companyMenu.getName());
	}

	@Test
	public void testBuildMenubar_ReportsMenu_GetStarted() throws Exception {
		MenuBuilder.setUrlUtils(urlUtils);
		when(urlUtils.getActionUrl(anyString(), anyString(), anyInt())).thenReturn(ACTION_LINK);
		when(permissions.getAccountStatus()).thenReturn(AccountStatus.Active);
		when(permissions.isShowClientSitesLink()).thenReturn(true);
		when(permissions.isAdmin()).thenReturn(true);

		when(translationService.getText(eq("menu.Reports"), any(Locale.class))).thenReturn("Reports");
		when(translationService.getText(eq("menu.ReportsManager.GettingStarted"), any(Locale.class))).thenReturn("Get Started");
		MenuComponent menu = MenuBuilder.buildMenubar(permissions);
		assertTrue(menu.getChildren().size() == 5);
		MenuComponent companyMenu = menu.getChildren().get(1);
		assertEquals("Reports", companyMenu.getName());
		assertTrue(companyMenu.getChildren().size() == 1);
		MenuComponent getStartedMenu = companyMenu.getChildren().get(0);
		assertEquals("Get Started", getStartedMenu.getName());
		assertEquals("/ManageReports!getStarted.action", getStartedMenu.getUrl());
	}

	private void setupPermissionsForMibew() {
		when(permissions.isLoggedIn()).thenReturn(true);
		when(permissions.getName()).thenReturn("Name");
		when(permissions.getAccountName()).thenReturn("AccountName");
		when(permissions.getAccountId()).thenReturn(12345);
		when(permissions.getUserId()).thenReturn(54321);
		when(permissions.getEmail()).thenReturn("me@example.com");
	}

	@Test
	public void testBuildMenubar_ManageMenu_GetStarted() throws Exception {
		MenuBuilder.setUrlUtils(urlUtils);
		when(urlUtils.getActionUrl(anyString(), anyString(), anyInt())).thenReturn(ACTION_LINK);
		when(permissions.getAccountStatus()).thenReturn(AccountStatus.Active);
		when(permissions.isShowClientSitesLink()).thenReturn(true);
		when(permissions.isAdmin()).thenReturn(true);
		when(permissions.isOperatorCorporate()).thenReturn(true);
		when(permissions.getAccountId()).thenReturn(12345);
		when(permissions.getUserId()).thenReturn(54321);
		when(permissions.hasPermission(OpPerms.SearchContractors)).thenReturn(true);
		when(translationService.getText(eq("menu.Manage"), any(Locale.class))).thenReturn("Manage");
		when(translationService.getText(eq("NewContractorSearch.title"), any(Locale.class))).thenReturn("SearchForNew");

		MenuComponent menu = MenuBuilder.buildMenubar(permissions);

		assertTrue(menu.getChildren().size() == 5);
		MenuComponent companyMenu = menu.getChildren().get(2);
		assertEquals("Manage", companyMenu.getName());
	}

	@Test
	public void testBuildMenubar_ManageMenu_SubMenu_SearchForNew() throws Exception {
		MenuBuilder.setUrlUtils(urlUtils);
		when(urlUtils.getActionUrl(anyString(), anyString(), anyInt())).thenReturn(ACTION_LINK);
		when(permissions.getAccountStatus()).thenReturn(AccountStatus.Active);
		when(permissions.isShowClientSitesLink()).thenReturn(true);
		when(permissions.isAdmin()).thenReturn(true);
		when(permissions.isOperatorCorporate()).thenReturn(true);
		when(permissions.getAccountId()).thenReturn(12345);
		when(permissions.getUserId()).thenReturn(54321);
		when(permissions.hasPermission(OpPerms.SearchContractors)).thenReturn(true);
		when(translationService.getText(eq("menu.Manage"), any(Locale.class))).thenReturn("Manage");
		when(translationService.getText(eq("NewContractorSearch.title"), any(Locale.class))).thenReturn("Search For New");

		MenuComponent menu = MenuBuilder.buildMenubar(permissions);

		assertTrue(menu.getChildren().size() == 5);
		MenuComponent companyMenu = menu.getChildren().get(2);
		assertEquals("Manage", companyMenu.getName());
		MenuComponent searchForNew = companyMenu.getChildren().get(0);
		assertEquals("Search For New", searchForNew.getName());

	}

	@Test
	public void testBuildMenubar_ManageMenu_SubMenu_CompanyFinder() throws Exception {
		MenuBuilder.setUrlUtils(urlUtils);
		when(urlUtils.getActionUrl(anyString(), anyString(), anyInt())).thenReturn(ACTION_LINK);
		when(permissions.getAccountStatus()).thenReturn(AccountStatus.Active);
		when(permissions.isShowClientSitesLink()).thenReturn(true);
		when(permissions.isAdmin()).thenReturn(true);
		when(permissions.isOperatorCorporate()).thenReturn(true);
		when(permissions.getAccountId()).thenReturn(12345);
		when(permissions.getUserId()).thenReturn(54321);
		when(permissions.hasPermission(OpPerms.SearchContractors)).thenReturn(true);
		when(translationService.getText(eq("menu.Manage"), any(Locale.class))).thenReturn("Manage");
		when(translationService.getText(eq("CompanyFinder.title"), any(Locale.class))).thenReturn("Company Finder");

		MenuComponent menu = MenuBuilder.buildMenubar(permissions);

		assertTrue(menu.getChildren().size() == 5);
		MenuComponent companyMenu = menu.getChildren().get(2);
		assertEquals("Manage", companyMenu.getName());
		MenuComponent companyFinder = companyMenu.getChildren().get(1);
		assertEquals("Company Finder", companyFinder.getName());

	}

	@Test
	public void testBuildMenubar_ManageMenu_SubMenu_CompanyFinder_TogglzDisabled() throws Exception {
		togglzRule.disable(Features.COMPANY_FINDER);

		MenuBuilder.setUrlUtils(urlUtils);
		when(urlUtils.getActionUrl(anyString(), anyString(), anyInt())).thenReturn(ACTION_LINK);
		when(permissions.getAccountStatus()).thenReturn(AccountStatus.Active);
		when(permissions.isShowClientSitesLink()).thenReturn(true);
		when(permissions.isAdmin()).thenReturn(true);
		when(permissions.isOperatorCorporate()).thenReturn(true);
		when(permissions.getAccountId()).thenReturn(12345);
		when(permissions.getUserId()).thenReturn(54321);
		when(permissions.hasPermission(OpPerms.SearchContractors)).thenReturn(true);
		when(translationService.getText(eq("menu.Manage"), any(Locale.class))).thenReturn("Manage");
		when(translationService.getText(eq("CompanyFinder.title"), any(Locale.class))).thenReturn("Company Finder");

		MenuComponent menu = MenuBuilder.buildMenubar(permissions);

		assertTrue(menu.getChildren().size() == 5);
		MenuComponent companyMenu = menu.getChildren().get(2);
		assertEquals("Manage", companyMenu.getName());
		MenuComponent companyFinder = companyMenu.getChildren().get(1);
		assertNotSame("Company Finder", companyFinder.getName());
	}

	@Test
	public void testBuildMenubar_EmployeeGUARD_EmployeeUser() throws Exception {
		MenuComponent menu = setupTestBuildMenubar_EmployeeGUARD_EmployeeUser();

		Approvals.verify(menu.getChildren().toString());
	}

	private MenuComponent setupTestBuildMenubar_EmployeeGUARD_EmployeeUser() {
		MenuBuilder.setUrlUtils(urlUtils);
		when(urlUtils.getActionUrl(anyString(), anyString(), anyInt())).thenReturn(ACTION_LINK);
		when(permissions.getAppUserID()).thenReturn(APP_USER_ID);
		when(permissions.getCurrentMode()).thenReturn(UserMode.EMPLOYEE);

		super.setupEchoTranslationService();

		return MenuBuilder.buildMenubar(permissions);
	}

	@Test
	public void testBuildMenubar_EmployeeGUARD_ContractorUser_InEmployeeMode() throws Exception {
		MenuComponent menu = setupTestBuildMenubar_EmployeeGUARD_ContractorUser_InEmployeeMode();

		Approvals.verify(menu.getChildren().toString());
	}

	private MenuComponent setupTestBuildMenubar_EmployeeGUARD_ContractorUser_InEmployeeMode() {
		MenuBuilder.setUrlUtils(urlUtils);
		when(urlUtils.getActionUrl(anyString(), anyString(), anyInt())).thenReturn(ACTION_LINK);
		when(permissions.getUserId()).thenReturn(USER_ID);
		when(permissions.getAppUserID()).thenReturn(APP_USER_ID);
		when(permissions.getCurrentMode()).thenReturn(UserMode.EMPLOYEE);
		when(permissions.getAvailableUserModes()).thenReturn(new HashSet<>(Arrays.asList(UserMode.EMPLOYEE,
				UserMode.ADMIN)));

		super.setupEchoTranslationService();

		return MenuBuilder.buildMenubar(permissions);
	}

	@Test
	public void testBuildMenubar_EmployeeGUARD_ContractorUser_InAdminMode() throws Exception {
		MenuComponent menu = setupTestBuildMenubar_EmployeeGUARD_ContractorUser_InAdminMode();

		Approvals.verify(menu.getChildren().toString());
	}

	private MenuComponent setupTestBuildMenubar_EmployeeGUARD_ContractorUser_InAdminMode() {
		MenuBuilder.setUrlUtils(urlUtils);
		when(urlUtils.getActionUrl(anyString(), anyString(), anyInt())).thenReturn(ACTION_LINK);
		when(permissions.getUserId()).thenReturn(USER_ID);
		when(permissions.getAppUserID()).thenReturn(APP_USER_ID);
		when(permissions.getCurrentMode()).thenReturn(UserMode.ADMIN);
		when(permissions.getAvailableUserModes()).thenReturn(new HashSet<>(Arrays.asList(UserMode.EMPLOYEE,
				UserMode.ADMIN)));

		super.setupEchoTranslationService();

		return MenuBuilder.buildMenubar(permissions);
	}
}
