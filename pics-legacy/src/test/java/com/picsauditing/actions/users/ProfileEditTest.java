package com.picsauditing.actions.users;

import com.picsauditing.PicsActionTest;
import com.picsauditing.access.OpPerms;
import com.picsauditing.access.Permissions;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.UserDAO;
import com.picsauditing.jpa.entities.Language;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.model.i18n.LanguageModel;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import java.util.Date;
import java.util.HashSet;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ProfileEditTest extends PicsActionTest {
	public static final int USER_ID = 12345;
	private ProfileEdit profileEdit;

	@Mock
	private Permissions permissions;
	@Mock
	private User user;
	@Mock
	private UserDAO userDAO;
	@Mock
	private LanguageModel languageModel;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		super.setupMocks();

		profileEdit = new ProfileEdit();

		when(permissions.getUserId()).thenReturn(USER_ID);
		when(user.getId()).thenReturn(USER_ID);

		Whitebox.setInternalState(profileEdit, "permissions", permissions);
		Whitebox.setInternalState(profileEdit, "userDAO", userDAO);
		Whitebox.setInternalState(profileEdit, "supportedLanguages", languageModel);
	}

	@Test
	public void testVersion6Menu() throws Exception {
		profileEdit.setU(user);

		when(request.getHeader("Referer")).thenReturn("/Home.action");

		assertEquals(PicsActionSupport.REDIRECT, profileEdit.version6Menu());

		verify(user).setUsingDynamicReports(false);
		verify(user).setUsingVersion7Menus(false);
		verify(userDAO).save(user);
		verify(permissions).setUsingVersion7Menus(false);
	}

	@Test
	public void testVersion7Menu() throws Exception {
		profileEdit.setU(user);

		when(request.getHeader("Referer")).thenReturn("/Home.action");

		assertEquals(PicsActionSupport.REDIRECT, profileEdit.version7Menu());

		verify(user).setUsingDynamicReports(true);
		verify(user).setusingDynamicReportsDate(any(Date.class));
		verify(user).setUsingVersion7Menus(true);
		verify(user).setUsingVersion7MenusDate(any(Date.class));
		verify(userDAO).save(user);
		verify(permissions).setUsingVersion7Menus(true);
	}

    @Test
    public void testSave_UnsupportedLanguageError() throws Exception {
        profileEdit.setU(user);
        Whitebox.setInternalState(profileEdit, "language", "de");
        when(permissions.isLoggedIn()).thenReturn(true);
        when(permissions.hasPermission(OpPerms.EditProfile)).thenReturn(true);
        when(languageModel.getVisibleLanguages()).thenReturn(new HashSet<Language>());

        assertEquals(PicsActionSupport.INPUT_ERROR, profileEdit.save());
    }
}
