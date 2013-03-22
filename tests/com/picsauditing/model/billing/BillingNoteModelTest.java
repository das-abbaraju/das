package com.picsauditing.model.billing;


import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import com.picsauditing.access.Permissions;
import com.picsauditing.dao.UserDAO;
import com.picsauditing.jpa.entities.User;

public class BillingNoteModelTest {

	// class under test
	BillingNoteModel billingNoteModel;

	private static final int USER_ID = 787;
	private static final int SWITCHED_TO_USER_ID = 457;

	@Mock
	private Permissions permissions;
	@Mock
	private User user;
	@Mock
	private User switchedToUser;
	@Mock
	private UserDAO userDAO;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		billingNoteModel = new BillingNoteModel();

		when(user.getId()).thenReturn(USER_ID);
		when(switchedToUser.getId()).thenReturn(SWITCHED_TO_USER_ID);

		Whitebox.setInternalState(billingNoteModel, "userDAO", userDAO);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testFindUserForPaymentNote_NullPermissionsObject() throws Exception {
		billingNoteModel.findUserForPaymentNote(null);
	}

	@Test
	public void testFindUserForPaymentNote_NotUsingSwitchTo() throws Exception {
		setupForTest(USER_ID, user);

		User userForNote = billingNoteModel.findUserForPaymentNote(permissions);

		assertEquals(userForNote.getId(), USER_ID);
	}

	private void setupForTest(int userId, User user) {
		when(permissions.getUserId()).thenReturn(userId);
		when(userDAO.find(userId)).thenReturn(user);
	}

	@Test
	public void testFindUserForPaymentNote_SwitchedToUser() throws Exception {
		when(permissions.getUserId()).thenReturn(USER_ID);
		setupForTest(SWITCHED_TO_USER_ID, switchedToUser);

		User userForNote = billingNoteModel.findUserForPaymentNote(permissions);

		assertEquals(userForNote.getId(), SWITCHED_TO_USER_ID);
	}

}
