package com.picsauditing.employeeguard.services;

import com.picsauditing.dao.AccountDAO;
import com.picsauditing.dao.OperatorAccountDAO;
import com.picsauditing.employeeguard.models.AccountModel;
import com.picsauditing.employeeguard.models.AccountType;
import com.picsauditing.employeeguard.services.AccountFilter;
import com.picsauditing.employeeguard.services.AccountService;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.OperatorAccount;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Mockito.when;

public class AccountServiceTest {

	private AccountService accountService;

	@Mock
	private AccountDAO accountDAO;
	@Mock
	private AccountFilter accountFilter;
	@Mock
	private OperatorAccountDAO operatorAccountDAO;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);

		accountService = new AccountService();

		Whitebox.setInternalState(accountService, "accountDAO", accountDAO);
		Whitebox.setInternalState(accountService, "accountFilter", accountFilter);
		Whitebox.setInternalState(accountService, "operatorDAO", operatorAccountDAO);
	}

	@Test
	public void testGetAccountById() {
		when(accountDAO.find(23)).thenReturn(buildAccount(45, "The Account", "Operator"));

		AccountModel result = accountService.getAccountById(23);

		assertEquals(45, result.getId());
	}

	private OperatorAccount createCorporate(int id, String name) {
		return buildOperator(id, name, "Corporate");
	}

	private Account buildAccount(int id, String name, String type) {
		Account account = new Account();
		account.setId(id);
		account.setName(name);
		account.setType(type);
		return account;
	}

	private OperatorAccount buildOperator(int id, String name, String type) {
		OperatorAccount operator = new OperatorAccount();
		operator.setId(id);
		operator.setName(name);
		operator.setType(type);
		return operator;
	}

	@Test
	public void testGetContractorMapForSite() {
		setupTestGetContractors();

		Map<Integer, AccountModel> results = accountService.getContractorMapForSite(312);

		verifyTestGetContractorMapForSite(results);
	}

	private void verifyTestGetContractorMapForSite(Map<Integer, AccountModel> results) {
		assertEquals(3, results.size());
		for (Map.Entry<Integer, AccountModel> mapEntry : results.entrySet()) {
			assertTrue(mapEntry.getKey() == mapEntry.getValue().getId());
		}
	}

	@Test
	public void testGetContractors() {
		setupTestGetContractors();

		List<AccountModel> results = accountService.getContractors(312);

		verifyTestGetContractors(results);
	}

	private void setupTestGetContractors() {
		when(operatorAccountDAO.find(312)).thenReturn(createCorporate(312, "Test Operator"));
		when(accountFilter.filterEmployeeGUARDAccounts(anyListOf(ContractorAccount.class)))
				.thenReturn(getFakeContractors());
	}

	private void verifyTestGetContractors(List<AccountModel> results) {
		for (AccountModel accountModel : results) {
			assertEquals(AccountType.CONTRACTOR, accountModel.getAccountType());
			assertEquals("Test Contractor " + accountModel.getId(), accountModel.getName());
		}
	}

	private List<ContractorAccount> getFakeContractors() {
		return Arrays.asList(buildFakeContractor(1, "Test Contractor 1"),
				buildFakeContractor(2, "Test Contractor 2"),
				buildFakeContractor(3, "Test Contractor 3"));
	}

	private ContractorAccount buildFakeContractor(final int id,
												  final String name) {
		ContractorAccount contractor = new ContractorAccount();
		contractor.setId(id);
		contractor.setName(name);
		contractor.setType("Contractor");
		return contractor;
	}
}
