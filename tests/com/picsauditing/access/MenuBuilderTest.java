package com.picsauditing.access;

import com.picsauditing.PicsTranslationTest;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
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
}
