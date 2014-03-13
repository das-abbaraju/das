package com.picsauditing.access;

import com.picsauditing.PicsActionTest;
import com.picsauditing.PicsTestUtil;
import com.picsauditing.PicsTranslationTest;
import com.picsauditing.billing.PaymentServiceFactory;
import com.picsauditing.jpa.entities.AccountStatus;
import com.picsauditing.menu.MenuComponent;
import com.picsauditing.menu.builder.MenuBuilder;
import com.picsauditing.provisioning.ProductSubscriptionService;
import com.picsauditing.util.SpringUtils;
import com.picsauditing.util.URLUtils;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;
import org.springframework.context.ApplicationContext;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.when;

public class MenuBuilderTest extends PicsActionTest {
	public static final int USER_ID = 123;
	public static final int SWITCHED_TO_ID = 456;

	@Mock
	private Permissions permissions;
    @Mock
    private URLUtils urlUtils;
    @Mock
    private ApplicationContext applicationContext;
    @Mock
    private ProductSubscriptionService productSubscriptionService;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
        setupMocks();

        Map<String, Object> beans = new HashMap<>();
        beans.put(SpringUtils.PRODUCT_SUBSCRIPTION_SERVICE, productSubscriptionService);
        PicsTestUtil.setSpringUtilsBeans(beans);

        when(translationService.getText(anyString(),(Locale)any())).thenReturn("");
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
        when(translationService.getText("global.Company",Locale.ENGLISH)).thenReturn("Company");
        when(translationService.getText("menu.Reports",Locale.ENGLISH)).thenReturn("Reports");
        when(translationService.getText("menu.Manage",Locale.ENGLISH)).thenReturn("Manage");
        when(translationService.getText("menu.Configure",Locale.ENGLISH)).thenReturn("Configure");
        when(translationService.getText("menu.Dev",Locale.ENGLISH)).thenReturn("Dev");
        when(translationService.getText("menu.Support",Locale.ENGLISH)).thenReturn("Support");
        when(permissions.getAccountStatus()).thenReturn(AccountStatus.Active);

        MenuComponent menu = MenuBuilder.buildMenubar(permissions);
        assertTrue(menu.getChildren().size() == 4);
        for(MenuComponent child : menu.getChildren()) {
            assertNotSame("Configure",child.getName());
            assertNotSame("Manage",child.getName());
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

        assert(expectedURL.equals(mbewURL));
    }

    @Test
    public void testGetMibewURL_NotLoggedIn() throws Exception {
        String expectedURL = "https://chat.picsorganizer.com/client.php?locale=en&style=PICS";
        when(translationService.getText(eq("Mibew.LanguageCode"), any(Locale.class))).thenReturn("en");
        when(permissions.isLoggedIn()).thenReturn(false);

        String mbewURL = MenuBuilder.getMibewURL(Locale.ENGLISH, permissions);

        assert(expectedURL.equals(mbewURL));
    }

    @Test
    public void testBuildMenubar_OperatorClientSitesActivityWatch() throws Exception {
        MenuBuilder.setUrlUtils(urlUtils);
        when(urlUtils.getActionUrl(anyString(),anyString(),anyInt())).thenReturn("ActionLink");
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
        assertEquals("Company",companyMenu.getName());
    }

    private void setupPermissionsForMibew() {
        when(permissions.isLoggedIn()).thenReturn(true);
        when(permissions.getName()).thenReturn("Name");
        when(permissions.getAccountName()).thenReturn("AccountName");
        when(permissions.getAccountId()).thenReturn(12345);
        when(permissions.getUserId()).thenReturn(54321);
        when(permissions.getEmail()).thenReturn("me@example.com");
    }
}
