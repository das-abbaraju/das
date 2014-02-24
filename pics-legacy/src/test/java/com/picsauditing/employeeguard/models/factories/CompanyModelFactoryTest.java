package com.picsauditing.employeeguard.models.factories;

import com.picsauditing.employeeguard.services.models.AccountModel;
import com.picsauditing.employeeguard.services.models.AccountType;
import org.junit.Before;
import org.junit.Test;

public class CompanyModelFactoryTest {

	@Before
	public void setUp() {

	}

	@Test
	public void testCreate() {

	}

	private AccountModel buildFakeAccountModel() {
		return new AccountModel.Builder()
				.id(678)
				.accountType(AccountType.CONTRACTOR)
				.name("Test Account")
				.build();
	}
}
