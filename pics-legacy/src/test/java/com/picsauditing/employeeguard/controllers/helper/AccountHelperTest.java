package com.picsauditing.employeeguard.controllers.helper;

import com.picsauditing.employeeguard.entities.Employee;
import com.picsauditing.employeeguard.entities.builders.EmployeeBuilder;
import com.picsauditing.employeeguard.models.AccountModel;
import com.picsauditing.employeeguard.models.AccountType;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class AccountHelperTest {

	@Test
	public void testConvertMap() {
		Map<AccountModel, Employee> result = AccountHelper.convertMap(fakeAccountModels(),
				fakeContractorEmployeeMap());

		verifyTestConvertMap(result);
	}

	private void verifyTestConvertMap(Map<AccountModel, Employee> result) {
		assertEquals(2, result.size());
		for (Map.Entry<AccountModel, Employee> entry : result.entrySet()) {
			assertEquals(entry.getKey().getId(), entry.getValue().getAccountId());
		}
	}

	private Map<Integer, AccountModel> fakeAccountModels() {
		return new HashMap<Integer, AccountModel>() {{
			put(123, new AccountModel.Builder()
					.id(123)
					.name("Cement Contractor")
					.accountType(AccountType.CONTRACTOR)
					.build());

			put(456, new AccountModel.Builder()
					.id(456)
					.name("Plumbing Contractor")
					.accountType(AccountType.CONTRACTOR)
					.build());
		}};
	}

	private Map<Integer, Employee> fakeContractorEmployeeMap() {
		return new HashMap<Integer, Employee>() {{
			put(456, new EmployeeBuilder()
					.accountId(456)
					.email("bob@plumbingwork.com")
					.build());

			put(123, new EmployeeBuilder()
					.accountId(123)
					.email("jack@cementwork.com")
					.build());
		}};
	}

}
