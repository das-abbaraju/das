package com.picsauditing.model.account;


import static org.mockito.Mockito.*;

import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import com.picsauditing.access.Permissions;
import com.picsauditing.dao.AccountDAO;
import com.picsauditing.dao.NoteDAO;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.AccountStatus;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.Note;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.jpa.entities.User;

public class AccountStatusChangesTest {

	AccountStatusChanges accountStatusChanges;

	@Mock
	private OperatorAccount clientSite;
	@Mock
	private ContractorAccount contractor;
	@Mock
	private Permissions permissions;
	@Mock
	private AccountDAO accountDAO;
	@Mock
	private NoteDAO noteDAO;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		accountStatusChanges = new AccountStatusChanges();

		Whitebox.setInternalState(accountStatusChanges, "accountDAO", accountDAO);
		Whitebox.setInternalState(accountStatusChanges, "noteDAO", noteDAO);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testValidation_NullClientSite() throws Exception {
		Account account = null;

		Whitebox.invokeMethod(accountStatusChanges, "validate", account, "Some Reason.");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testValidation_NoDeactivationReason() throws Exception {
		Account account = new Account();

		Whitebox.invokeMethod(accountStatusChanges, "validate", account, null);
	}

	@Test
	public void testDeactivateClientSite() {
		accountStatusChanges.deactivateClientSite(clientSite, permissions, AccountStatusChanges.DOES_NOT_WORK_FOR_OPERATOR_REASON,
                "No longer with PICS.");

		verifyDeactivation(clientSite);
	}

	@Test
	public void testDeactivateContractor() {
		accountStatusChanges.deactivateContractor(contractor, permissions,AccountStatusChanges.PAYMENTS_NOT_CURRENT_REASON,
                "Delinquent account.");

		verify(contractor, times(1)).setRenew(false);
		verifyDeactivation(contractor);
	}

	private void verifyDeactivation(Account account) {
		verify(account, times(1)).setStatus(AccountStatus.Deactivated);
        verify(account, times(1)).setReason(anyString());
		verify(account, times(1)).setDeactivatedBy(any(User.class));
		verify(account, times(1)).setDeactivationDate(any(Date.class));
		verify(noteDAO, times(1)).save(any(Note.class));
		verify(accountDAO, times(1)).save(account);
	}

}
