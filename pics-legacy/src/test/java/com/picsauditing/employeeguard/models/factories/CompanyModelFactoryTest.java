package com.picsauditing.employeeguard.models.factories;

import com.picsauditing.employeeguard.models.CompanyEmployeeModel;
import com.picsauditing.employeeguard.models.CompanyModel;
import com.picsauditing.employeeguard.services.models.AccountModel;
import com.picsauditing.employeeguard.services.models.AccountType;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class CompanyModelFactoryTest {

	public static final int COMPANY_ID = 678;
	public static final String COMPANY_NAME = "Test Account";

	CompanyModelFactory companyModelFactory = new CompanyModelFactory();

	@Before
	public void setUp() {
		companyModelFactory = new CompanyModelFactory();
	}

	@Test
	public void testCreate() {
		AccountModel fakeAccountModel = buildFakeAccountModel();
		List<CompanyEmployeeModel> fakeEmployees = new ArrayList<>();

		CompanyModel companyModel = companyModelFactory.create(fakeAccountModel, fakeEmployees);

		verifyTestCreate(fakeEmployees, companyModel);
	}

	private void verifyTestCreate(List<CompanyEmployeeModel> fakeEmployees, CompanyModel companyModel) {
		assertEquals(COMPANY_ID, companyModel.getId());
		assertEquals(COMPANY_NAME, companyModel.getName());
		assertEquals(fakeEmployees, companyModel.getEmployees());
	}

	private AccountModel buildFakeAccountModel() {
		return new AccountModel.Builder()
				.id(COMPANY_ID)
				.name(COMPANY_NAME)
				.build();
	}
}
