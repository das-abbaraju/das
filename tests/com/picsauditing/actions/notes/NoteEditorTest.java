package com.picsauditing.actions.notes;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.picsauditing.PICS.I18nCache;
import com.picsauditing.access.Permissions;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.util.SpringUtils;

@RunWith(PowerMockRunner.class)
@PrepareForTest({SpringUtils.class, I18nCache.class})
public class NoteEditorTest {

	private NoteEditor classUnderTest;
	private static final int TEST_ACCOUNT_ID = 5;
	private static final int TEST_USER_ID = 23;
	private static Set<Integer> groupSetWithoutISR;
	private static Set<Integer> groupSetWithISR; //71638

	@Mock ContractorAccountDAO mockDAO;
	@Mock ContractorAccount mockContractor;
	@Mock Permissions mockPerm;
	@Mock Account mockAccount;
	@Mock I18nCache mockCache;

	@SuppressWarnings("unchecked")
	@Before
	public void SetUp () {
		MockitoAnnotations.initMocks(this);
		PowerMockito.mockStatic(SpringUtils.class);
		PowerMockito.mockStatic(I18nCache.class);
		PowerMockito.when(I18nCache.getInstance()).thenReturn(mockCache);
		classUnderTest = new NoteEditor();
		PowerMockito.when(SpringUtils.getBean(anyString(), any(Class.class)))
			.thenReturn(mockDAO);
		when(mockDAO.find(TEST_ACCOUNT_ID)).thenReturn(mockContractor);
		when(mockPerm.getUserId()).thenReturn(TEST_USER_ID);
		when(mockAccount.getId()).thenReturn(TEST_ACCOUNT_ID);

		groupSetWithISR = new HashSet<Integer>(2);
		groupSetWithISR.add(71638);
		groupSetWithISR.add(25235);

		groupSetWithoutISR = new HashSet<Integer>(2);
		groupSetWithoutISR.add(25235);
		groupSetWithoutISR.add(55555);
	}

	@Test
	public void updateInternalSalesInfo_pass () {
		when(mockPerm.getGroups()).thenReturn(groupSetWithISR);
		when(mockAccount.isContractor()).thenReturn(true);

		classUnderTest.updateInternalSalesInfo(mockPerm, mockAccount);

		verify(mockContractor).setLastContactedByInsideSales(TEST_USER_ID);
		verify(mockDAO).find(TEST_ACCOUNT_ID);
		verify(mockDAO).save(mockContractor);
	}

	@Test
	public void updateInternalSalesInfo_fail1 () {
		when(mockPerm.getGroups()).thenReturn(groupSetWithoutISR);

		classUnderTest.updateInternalSalesInfo(mockPerm, mockAccount);

		verify(mockContractor, never()).setLastContactedByInsideSales(anyInt());
		verify(mockDAO, never()).find(anyInt());
		verify(mockDAO, never()).save((ContractorAccount)any());
	}

	@Test
	public void updateInternalSalesInfo_fail2 () {
		when(mockPerm.getGroups()).thenReturn(groupSetWithISR);
		when(mockAccount.isContractor()).thenReturn(false);

		classUnderTest.updateInternalSalesInfo(mockPerm, mockAccount);

		verify(mockContractor, never()).setLastContactedByInsideSales(anyInt());
		verify(mockDAO, never()).find(anyInt());
		verify(mockDAO, never()).save((ContractorAccount)any());
	}
}
