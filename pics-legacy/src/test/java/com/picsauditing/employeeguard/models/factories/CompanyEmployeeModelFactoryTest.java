package com.picsauditing.employeeguard.models.factories;

import com.picsauditing.employeeguard.models.CompanyEmployeeModel;
import com.picsauditing.employeeguard.services.models.AccountModel;
import com.picsauditing.employeeguard.services.models.AccountType;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class CompanyEmployeeModelFactoryTest {

	CompanyEmployeeModelFactory companyEmployeeModelFactory;

	@Before
	public void setUp() throws Exception {
		companyEmployeeModelFactory = new CompanyEmployeeModelFactory();
	}

	@Test
	public void testCreate() throws Exception {
		List<CompanyEmployeeModel> fakeEmployees = new ArrayList<>();
		
 	}

	private AccountModel buildFakeAccountModel() {
		return new AccountModel.Builder()
				.id(67)
				.name("Test Contractor")
				.accountType(AccountType.CONTRACTOR)
				.build();
	}
}
