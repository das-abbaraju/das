package com.picsauditing.actions.notes;

import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

import com.picsauditing.PicsActionTest;
import com.picsauditing.PicsTest;
import com.picsauditing.PicsTestUtil;
import com.picsauditing.dao.AccountDAO;
import com.picsauditing.dao.UserSwitchDAO;
import com.picsauditing.toggle.FeatureToggle;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.picsauditing.PICS.I18nCache;
import com.picsauditing.access.Permissions;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.search.Database;
import com.picsauditing.util.SpringUtils;

import java.util.ArrayList;
import java.util.List;

public class NoteEditorTest extends PicsActionTest {

    private static final int PRIMARY_CORP_ACCOUNT_ID = 12345;
    private NoteEditor noteEditor;
	private static final int TEST_ACCOUNT_ID = 5;
	private static final int TEST_USER_ID = 23;

	@Mock
	private ContractorAccount contractor;
	@Mock
	private Account account;
	@Mock
    private ContractorAccountDAO contractorDAO;
    @Mock
    private FeatureToggle featureToggleChecker;
    @Mock
    private AccountDAO accountDAO;
    @Mock
    private UserSwitchDAO userSwitchDAO;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

        noteEditor = new NoteEditor();
        super.setUp(noteEditor);

        PicsTestUtil.autowireDAOsFromDeclaredMocks(noteEditor, this);
        Whitebox.setInternalState(noteEditor, "featureToggleChecker", featureToggleChecker);

		when(contractorDAO.find(TEST_ACCOUNT_ID)).thenReturn(contractor);
		when(permissions.getUserId()).thenReturn(TEST_USER_ID);
		when(account.getId()).thenReturn(TEST_ACCOUNT_ID);
	}

    @Test
    public void testGetFacilities_NotOperatorOrCorporate() throws Exception {
        when(permissions.isOperatorCorporate()).thenReturn(false);
        List<Account> facilities = noteEditor.getFacilities();
        verify(accountDAO).findNoteRestrictionOperators(permissions);
    }

    @Test
    public void testGetFacilities_IsOperatorSwitchToIsOff() throws Exception {
        final Account primaryCorporate = setupGetFacilities();
        when(featureToggleChecker.isFeatureEnabled(FeatureToggle.TOGGLE_DO_NOT_USE_SWITCHTO_ACCOUNTS_IN_NOTE_RESTRICTION))
                .thenReturn(true);
        when(permissions.isCorporate()).thenReturn(false);
        when(permissions.isOperator()).thenReturn(true);
        when(permissions.getAccountId()).thenReturn(TEST_ACCOUNT_ID);
        when(accountDAO.find(TEST_ACCOUNT_ID)).thenReturn(account);

        List<Account> facilities = noteEditor.getFacilities();

        assertThat("User's account should be the only one in the list", facilities.get(0), is(equalTo(account)));
        assertThat(facilities.size(), is(equalTo(1)));
    }

    @Test
    public void testGetFacilities_IsCorporateSwitchToIsOff() throws Exception {
        final Account primaryCorporate = setupGetFacilities();
        when(featureToggleChecker.isFeatureEnabled(FeatureToggle.TOGGLE_DO_NOT_USE_SWITCHTO_ACCOUNTS_IN_NOTE_RESTRICTION))
                .thenReturn(true);
        when(permissions.isCorporate()).thenReturn(true);

        List<Account> facilities = noteEditor.getFacilities();

        assertThat("Primary corporate should be first in the list", facilities.get(0), is(equalTo(primaryCorporate)));
        assertThat("Primary Corp should be only account in the list", facilities.size(), is(equalTo(1)));
    }

    @Test
    public void testGetFacilities_IsOperatorSwitchToIsOn() throws Exception {
        final Account primaryCorporate = setupGetFacilities();

        List<Account> facilities = noteEditor.getFacilities();

        assertThat("Primary corporate should be first in the list", facilities.get(0), is(equalTo(primaryCorporate)));
        assertThat("Primary Corp should only appear once", facilities.size(), is(equalTo(3)));
    }

    private Account setupGetFacilities() {
        final Account primaryCorporate = mock(Account.class);
        when(primaryCorporate.getName()).thenReturn("Primary Corporate Account");
        final Account switchToAccount1 = mock(Account.class);
        when(switchToAccount1.getName()).thenReturn("SwitchTo Account 1");
        final Account switchToAccount2 = mock(Account.class);
        when(switchToAccount2.getName()).thenReturn("SwitchTo Account 2");
        List<Account> switchToAccounts = new ArrayList<Account>() {{
            add(switchToAccount1);
            add(switchToAccount2);
            add(primaryCorporate);
        }};
        when(permissions.isOperatorCorporate()).thenReturn(true);
        when(permissions.getPrimaryCorporateAccountID()).thenReturn(PRIMARY_CORP_ACCOUNT_ID);
        when(featureToggleChecker.isFeatureEnabled(FeatureToggle.TOGGLE_DO_NOT_USE_SWITCHTO_ACCOUNTS_IN_NOTE_RESTRICTION))
                .thenReturn(false);

        when(accountDAO.find(PRIMARY_CORP_ACCOUNT_ID)).thenReturn(primaryCorporate);
        when(userSwitchDAO.findAccountsByUserId(TEST_USER_ID)).thenReturn(switchToAccounts);
        return primaryCorporate;
    }

	@Test
	public void updateInternalSalesInfo_pass () {
		when(permissions.hasGroup(User.GROUP_ISR)).thenReturn(true);
		when(account.isContractor()).thenReturn(true);

		noteEditor.updateInternalSalesInfo(permissions, account);

		verify(contractor).setLastContactedByInsideSales(TEST_USER_ID);
		verify(contractorDAO).find(TEST_ACCOUNT_ID);
		verify(contractorDAO).save(contractor);
	}

	@Test
	public void updateInternalSalesInfo_fail1 () {
		when(permissions.hasGroup(User.GROUP_ISR)).thenReturn(false);
		when(account.isContractor()).thenReturn(false);

		noteEditor.updateInternalSalesInfo(permissions, account);

		verify(contractor, never()).setLastContactedByInsideSales(anyInt());
		verify(contractorDAO, never()).find(anyInt());
		verify(contractorDAO, never()).save((ContractorAccount) any());
	}

	@Test
	public void updateInternalSalesInfo_fail2 () {
		when(permissions.hasGroup(User.GROUP_ISR)).thenReturn(true);
		when(account.isContractor()).thenReturn(false);

		noteEditor.updateInternalSalesInfo(permissions, account);

		verify(contractor, never()).setLastContactedByInsideSales(anyInt());
		verify(contractorDAO, never()).find(anyInt());
		verify(contractorDAO, never()).save((ContractorAccount) any());
	}
}
