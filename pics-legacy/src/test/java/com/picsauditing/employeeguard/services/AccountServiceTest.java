package com.picsauditing.employeeguard.services;

import com.picsauditing.dao.AccountDAO;
import com.picsauditing.dao.OperatorAccountDAO;
import com.picsauditing.employeeguard.services.external.BillingService;
import com.picsauditing.employeeguard.services.models.AccountModel;
import com.picsauditing.employeeguard.services.models.AccountType;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.OperatorAccount;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.reflect.Whitebox;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Mockito.when;

public class AccountServiceTest {

	private AccountService accountService;

	@Mock
	private AccountDAO accountDAO;
	@Mock
	private BillingService billingService;
	@Mock
	private OperatorAccountDAO operatorAccountDAO;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);

		accountService = new AccountService();

		Whitebox.setInternalState(accountService, "accountDAO", accountDAO);
		Whitebox.setInternalState(accountService, "billingService", billingService);
		Whitebox.setInternalState(accountService, "operatorDAO", operatorAccountDAO);
	}

	@Test
	public void testGetAccountById() {
		when(accountDAO.find(23)).thenReturn(buildAccount(45, "The Account", "Operator"));

		AccountModel result = accountService.getAccountById(23);

		assertEquals(45, result.getId());
	}

	@Test
	public void testTopCorporateAccounts() {
		OperatorAccount corporate1 = createCorporate(1, "Corporate 1");
		OperatorAccount corporate2 = createCorporate(2, "Corporate 2");
		OperatorAccount hub = createCorporate(3, "Hub");
		OperatorAccount site1 = createSite(4, "Site 1");

		linkChildToParent(site1, hub);
		linkChildToParent(site1, corporate2);
		linkChildToParent(hub, corporate1);

		when(billingService.filterEmployeeGUARDAccounts(anyList())).thenAnswer(new Answer<List<OperatorAccount>>() {
			@Override
			public List<OperatorAccount> answer(InvocationOnMock invocationOnMock) throws Throwable {
				return (List<OperatorAccount>) invocationOnMock.getArguments()[0];
			}
		});

		when(operatorAccountDAO.findOperators(Arrays.asList(4))).thenReturn(Arrays.asList(site1));

		List<AccountModel> topDogs = accountService.getTopmostCorporateAccounts(4);
		assertEquals("Corporate 1", topDogs.get(0).getName());
		assertEquals("Corporate 2", topDogs.get(1).getName());
	}

	@Test
	public void testTopCorporateAccounts_Cyclic() {
		OperatorAccount corporate1 = createCorporate(1, "Corporate 1");
		OperatorAccount corporate2 = createCorporate(2, "Corporate 2");
		OperatorAccount hub = createCorporate(3, "Hub");

		linkChildToParent(hub, corporate1);
		linkChildToParent(corporate1, corporate2);
		linkChildToParent(corporate2, hub);

		when(billingService.filterEmployeeGUARDAccounts(anyList())).thenAnswer(new Answer<List<OperatorAccount>>() {
			@Override
			public List<OperatorAccount> answer(InvocationOnMock invocationOnMock) throws Throwable {
				return (List<OperatorAccount>) invocationOnMock.getArguments()[0];
			}
		});
		when(operatorAccountDAO.find(3)).thenReturn(hub);

		List<AccountModel> topDogs = accountService.getTopmostCorporateAccounts(3);
		assertTrue(topDogs.isEmpty());
	}

	private OperatorAccount createCorporate(int id, String name) {
		return buildOperator(id, name, "Corporate");
	}

	private OperatorAccount createSite(int id, String name) {
		return buildOperator(id, name, "Operator");
	}

	private void linkChildToParent(OperatorAccount child, OperatorAccount parent) {
		child.getParentOperators().add(parent);
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
		when(billingService.filterEmployeeGUARDAccounts(anyListOf(ContractorAccount.class)))
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
