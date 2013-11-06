package com.picsauditing.employeeguard.services;

import com.picsauditing.dao.AccountDAO;
import com.picsauditing.dao.OperatorAccountDAO;
import com.picsauditing.employeeguard.services.external.BillingService;
import com.picsauditing.employeeguard.services.models.AccountModel;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.OperatorAccount;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.reflect.Whitebox;

import java.util.List;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyList;
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
		when(operatorAccountDAO.find(4)).thenReturn(site1);

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
}
