package com.picsauditing.access;

import com.picsauditing.PicsTranslationTest;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import java.util.Locale;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

public class MenuBuilderTest extends PicsTranslationTest {
	public static final int USER_ID = 123;
	public static final int SWITCHED_TO_ID = 456;

	@Mock
	private Permissions permissions;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);

		when(permissions.getUserId()).thenReturn(USER_ID);
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

    private void setupPermissionsForMibew() {
        when(permissions.isLoggedIn()).thenReturn(true);
        when(permissions.getName()).thenReturn("Name");
        when(permissions.getAccountName()).thenReturn("AccountName");
        when(permissions.getAccountId()).thenReturn(12345);
        when(permissions.getUserId()).thenReturn(54321);
        when(permissions.getEmail()).thenReturn("me@example.com");
    }
}
