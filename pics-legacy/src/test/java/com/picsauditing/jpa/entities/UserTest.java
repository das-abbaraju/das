package com.picsauditing.jpa.entities;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Set;

import com.picsauditing.access.OpPerms;
import org.junit.Before;
import org.junit.Test;
import org.powermock.reflect.Whitebox;

import com.picsauditing.validator.InputValidator;

public class UserTest {

	private User user;

	@Before
	public void setUp() {
		user = new User();

		Whitebox.setInternalState(user, "inputValidator", new InputValidator());
	}

	@Test
	public void testIsLocked_DefaultIsFalse() throws Exception {
		assertFalse(user.isLocked());
	}

	@Test
	public void testIsLockedNow() throws Exception {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DAY_OF_YEAR, 1);

		user.setLockUntil(calendar.getTime());

		assertTrue(user.isLocked());
	}

	@Test
	public void testIsLockedBefore() throws Exception {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DAY_OF_YEAR, -1);

		user.setLockUntil(calendar.getTime());

		assertFalse(user.isLocked());
	}

    @Test
    public void testGetOwnedOpPerms() throws Exception {
        List<UserAccess> ownedPermissions = new ArrayList<UserAccess>();
        UserAccess userAccess1 = new UserAccess();
        userAccess1.setOpPerm(OpPerms.ContractorAdmin);
        UserAccess userAccess2 = new UserAccess();
        userAccess2.setOpPerm(OpPerms.ContractorInsurance);
        ownedPermissions.add(userAccess1);
        ownedPermissions.add(userAccess2);
        user.setOwnedPermissions(ownedPermissions);

        Set<OpPerms> opPerms = user.getOwnedOpPerms();

        assertTrue(opPerms.contains(OpPerms.ContractorAdmin));
        assertTrue(opPerms.contains(OpPerms.ContractorInsurance));
        assertFalse(opPerms.contains(OpPerms.AddContractors));
    }
}
