package com.picsauditing.PICS;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import com.picsauditing.access.Permissions;
import com.picsauditing.dao.UserDAO;
import com.picsauditing.jpa.entities.BaseTable;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorWatch;
import com.picsauditing.jpa.entities.User;

public class ContractorWatchlistHelperTest {
	private ContractorWatchlistHelper contractorWatchlistHelper;

	@Mock
	private ContractorAccount contractor;
	@Mock
	private ContractorWatch watch;
	@Mock
	private Permissions permissions;
	@Mock
	private User user;
	@Mock
	private UserDAO userDAO;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		contractorWatchlistHelper = new ContractorWatchlistHelper();

		Whitebox.setInternalState(contractorWatchlistHelper, "userDAO", userDAO);
	}

	@Test
	public void testAddContractorToWatchList() {
		contractorWatchlistHelper.addContractorToWatchList(contractor, permissions, user);

		verify(userDAO).save(any(BaseTable.class));
	}

	@Test
	public void testRemoveContractorFromWatchList() {
		contractorWatchlistHelper.removeContractorFromWatchList(null);

		assertEquals(0, contractorWatchlistHelper.getRemovedContractorId());

		verify(userDAO, never()).remove(any(BaseTable.class));
	}

	@Test
	public void testRemoveContractorFromWatchList_ContractorWatchNotNull() {
		when(contractor.getId()).thenReturn(1);
		when(watch.getContractor()).thenReturn(contractor);

		contractorWatchlistHelper.removeContractorFromWatchList(watch);

		assertEquals(1, contractorWatchlistHelper.getRemovedContractorId());

		verify(userDAO).remove(any(BaseTable.class));
	}

	@Test
	public void testIsWatching() {
		when(user.getWatchedContractors()).thenReturn(new ArrayList<ContractorWatch>());

		assertFalse(contractorWatchlistHelper.isWatching(contractor, user));
	}

	@Test
	public void testIsWatching_ContractorIsWatched() {
		ArrayList<ContractorWatch> watches = new ArrayList<ContractorWatch>();
		watches.add(watch);

		when(user.getWatchedContractors()).thenReturn(watches);
		when(watch.getContractor()).thenReturn(contractor);

		assertTrue(contractorWatchlistHelper.isWatching(contractor, user));
	}

	@Test
	public void testGetWatchedSortedByContractorName() {
		ContractorAccount contractor2 = mock(ContractorAccount.class);
		ContractorWatch watch2 = mock(ContractorWatch.class);

		List<ContractorWatch> watches = new ArrayList<ContractorWatch>();
		watches.add(watch2);
		watches.add(watch);

		when(contractor.getName()).thenReturn("A");
		when(contractor2.getName()).thenReturn("Z");
		when(user.getWatchedContractors()).thenReturn(watches);
		when(watch.getContractor()).thenReturn(contractor);
		when(watch2.getContractor()).thenReturn(contractor2);

		List<ContractorWatch> sorted = contractorWatchlistHelper.getWatchedSortedByContractorName(user);

		assertEquals(sorted.get(0), watch);
		assertEquals(sorted.get(1), watch2);
	}
}
