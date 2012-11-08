package com.picsauditing.access;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.picsauditing.EntityFactory;
import com.picsauditing.jpa.entities.AccountStatus;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.jpa.entities.User;

public class UserIndexPageTest {
	
	@Test
	public void testNotLoggedIn() throws Exception {
		assertEquals("Login.action", UserIndexPage.getIndexURL(null));
		Permissions permissions = new Permissions();
		assertEquals("Login.action", UserIndexPage.getIndexURL(permissions));
	}

	@Test
	public void testContractorLoggedIn() throws Exception {
		Permissions permissions = createPermissions(createContractorUser());
		assertEquals("ContractorView.action", UserIndexPage.getIndexURL(permissions));
	}

	@Test
	public void testPendingContractorLoggedIn() throws Exception {
		Permissions permissions = createPermissions(createPendingContractorUser());
		assertEquals("RegistrationMakePayment.action", UserIndexPage.getIndexURL(permissions));
	}

	@Test
	public void testClientSiteLoggedIn() throws Exception {
		Permissions permissions = createPermissions(EntityFactory.makeUser(OperatorAccount.class));
		assertEquals("Home.action", UserIndexPage.getIndexURL(permissions));
		permissions = createPermissions(EntityFactory.makeUser());
		assertEquals("Home.action", UserIndexPage.getIndexURL(permissions));
	}

	private User createContractorUser() throws Exception {
		return EntityFactory.makeUser(ContractorAccount.class);
	}

	private User createPendingContractorUser() throws Exception {
		User user = createContractorUser();
		user.getAccount().setStatus(AccountStatus.Pending);
		return user;
	}

	private Permissions createPermissions(User user) throws Exception {
		return EntityFactory.makePermission(user);
	}
}
