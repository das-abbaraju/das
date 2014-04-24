package com.picsauditing.employeeguard.services.factory;

import com.picsauditing.employeeguard.services.AccountService;
import com.picsauditing.employeeguard.models.AccountModel;
import com.picsauditing.employeeguard.models.AccountType;
import com.picsauditing.jpa.entities.Account;
import org.mockito.Mockito;

import java.util.Arrays;

import static org.mockito.Matchers.anyCollection;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.when;

public class AccountServiceFactory {
	public static final int CONTRACTOR_ID = 12345;
	private static AccountService accountService = Mockito.mock(AccountService.class);

	public static AccountService getAccountService() {
		Mockito.reset(accountService);

		AccountModel operator = new AccountModel.Builder().id(Account.PicsID).accountType(AccountType.OPERATOR).name("Operator").build();
		AccountModel corporate = new AccountModel.Builder().id(Account.PICS_CORPORATE_ID).accountType(AccountType.CORPORATE).name("Corporate").build();
		AccountModel contractor = new AccountModel.Builder().id(CONTRACTOR_ID).accountType(AccountType.CONTRACTOR).name("Contractor").build();

		when(accountService.getAccountById(anyInt())).thenReturn(operator);
		when(accountService.getAccountsByIds(anyCollection())).thenReturn(Arrays.asList(operator, corporate));
		when(accountService.getChildOperatorIds(anyInt())).thenReturn(Arrays.asList(Account.PicsID));
		when(accountService.getChildOperators(anyInt())).thenReturn(Arrays.asList(operator));
		when(accountService.getContractors(anyInt())).thenReturn(Arrays.asList(contractor));
		when(accountService.getTopmostCorporateAccountIds(anyInt())).thenReturn(Arrays.asList(Account.PICS_CORPORATE_ID));
		when(accountService.extractParentAccounts(anyInt())).thenReturn(Arrays.asList(corporate));

		return accountService;
	}
}
