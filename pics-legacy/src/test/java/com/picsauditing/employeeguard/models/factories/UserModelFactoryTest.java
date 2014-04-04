package com.picsauditing.employeeguard.models.factories;

import com.picsauditing.employeeguard.models.UserModel;
import com.picsauditing.employeeguard.services.models.AccountType;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class UserModelFactoryTest {

	public static final int USER_ID = 123;
	public static final int ACCOUNT_ID = 234;
	public static final String NAME = "Name";

	UserModelFactory factory;

	@Before
	public void setUp() throws Exception {
		factory = new UserModelFactory();
	}

	@Test
	public void testCreate() throws Exception {
		UserModel result = factory.create(USER_ID, ACCOUNT_ID, NAME, AccountType.OPERATOR);

		assertNotNull(result);
		assertEquals(USER_ID, result.getUserId());
		assertEquals(ACCOUNT_ID, result.getAccountId());
		assertEquals(NAME, result.getName());
		assertEquals(AccountType.OPERATOR, result.getType());
	}
}
