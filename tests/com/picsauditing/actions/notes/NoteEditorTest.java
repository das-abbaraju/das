package com.picsauditing.actions.notes;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.*;

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

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "NoteEditorTest-context.xml" })
public class NoteEditorTest {

	private NoteEditor noteEditor;
	private static final int TEST_ACCOUNT_ID = 5;
	private static final int TEST_USER_ID = 23;
	private ContractorAccountDAO contractorDAO;

	@Mock
	private ContractorAccount contractor;
	@Mock
	private Permissions permissions;
	@Mock
	private Account account;
	@Mock
	private Database databaseForTesting;

	@AfterClass
	public static void classTearDown() {
		Whitebox.setInternalState(I18nCache.class, "databaseForTesting", (Database) null);
	}

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		Whitebox.setInternalState(I18nCache.class, "databaseForTesting", databaseForTesting);

		noteEditor = new NoteEditor();

		contractorDAO = SpringUtils.getBean("ContractorAccountDAO", ContractorAccountDAO.class);
		reset(contractorDAO);

		when(contractorDAO.find(TEST_ACCOUNT_ID)).thenReturn(contractor);
		when(permissions.getUserId()).thenReturn(TEST_USER_ID);
		when(account.getId()).thenReturn(TEST_ACCOUNT_ID);
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
