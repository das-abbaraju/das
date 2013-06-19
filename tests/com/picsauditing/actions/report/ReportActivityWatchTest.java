package com.picsauditing.actions.report;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.persistence.EntityManager;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import com.picsauditing.PicsTestUtil;
import com.picsauditing.PicsTranslationTest;
import com.picsauditing.PICS.ContractorWatchlistHelper;
import com.picsauditing.access.NoRightsException;
import com.picsauditing.access.OpPerms;
import com.picsauditing.access.Permissions;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorWatch;
import com.picsauditing.jpa.entities.User;

public class ReportActivityWatchTest extends PicsTranslationTest {
	private ReportActivityWatch reportActivityWatch;

	@Mock
	private ContractorAccount contractor;
	@Mock
	private ContractorWatch watch;
	@Mock
	private ContractorWatchlistHelper helper;
	@Mock
	private EntityManager entityManager;
	@Mock
	private Permissions permissions;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		super.resetTranslationService();

		reportActivityWatch = new ReportActivityWatch();
		PicsTestUtil testUtil = new PicsTestUtil();
		testUtil.autowireEMInjectedDAOs(reportActivityWatch, entityManager);

		Whitebox.setInternalState(reportActivityWatch, "contractorWatchlistHelper", helper);
		Whitebox.setInternalState(reportActivityWatch, "permissions", permissions);

		// This would otherwise call run(sql) and instantiate a new Database
		// object
		Whitebox.setInternalState(reportActivityWatch, "runReport", false);

		when(permissions.isAdmin()).thenReturn(true);
	}

	@Test(expected = NoRightsException.class)
	public void testCheckPermissions() throws Exception {
		when(permissions.isAdmin()).thenReturn(false);

		reportActivityWatch.checkPermissions();
	}

	@Test
	public void testCheckPermissions_Admin() throws Exception {
		reportActivityWatch.checkPermissions();
	}

	@Test
	public void testCheckPermissions_HasContractorWatch() throws Exception {
		when(permissions.hasPermission(OpPerms.ContractorWatch)).thenReturn(true);

		reportActivityWatch.checkPermissions();
	}

	@Test
	public void testExecute() throws Exception {
		assertEquals(PicsActionSupport.SUCCESS, reportActivityWatch.execute());
		assertFalse(reportActivityWatch.hasActionErrors());
	}

	@Test
	public void testExecute_AllOptionsDeselected() throws Exception {
		reportActivityWatch.setLogin(false);
		reportActivityWatch.setNotesAndEmail(false);
		reportActivityWatch.setAudits(false);
		reportActivityWatch.setFlagColorChange(false);
		reportActivityWatch.setFlagCriteria(false);

		assertEquals(PicsActionSupport.SUCCESS, reportActivityWatch.execute());
		assertTrue(reportActivityWatch.hasActionErrors());
	}

	@Test
	public void testAdd() throws Exception {
		assertEquals(PicsActionSupport.SUCCESS, reportActivityWatch.add());
		assertTrue(reportActivityWatch.hasActionErrors());
		assertFalse(reportActivityWatch.hasActionMessages());

		verify(helper, never()).isWatching(any(ContractorAccount.class), any(User.class));
		verify(helper, never())
				.addContractorToWatchList(any(ContractorAccount.class), eq(permissions), any(User.class));
	}

	@Test
	public void testAdd_ContractorWatched() throws Exception {
		reportActivityWatch.setContractor(contractor);

		when(helper.isWatching(eq(contractor), any(User.class))).thenReturn(true);

		assertEquals(PicsActionSupport.SUCCESS, reportActivityWatch.add());
		assertTrue(reportActivityWatch.hasActionErrors());
		assertFalse(reportActivityWatch.hasActionMessages());

		verify(helper).isWatching(any(ContractorAccount.class), any(User.class));
		verify(helper, never())
				.addContractorToWatchList(any(ContractorAccount.class), eq(permissions), any(User.class));
	}

	@Test
	public void testAdd_NewContractorWatch() throws Exception {
		reportActivityWatch.setContractor(contractor);

		when(helper.isWatching(eq(contractor), any(User.class))).thenReturn(false);

		assertEquals(PicsActionSupport.SUCCESS, reportActivityWatch.add());
		assertFalse(reportActivityWatch.hasActionErrors());
		assertTrue(reportActivityWatch.hasActionMessages());

		verify(helper).isWatching(any(ContractorAccount.class), any(User.class));
		verify(helper).addContractorToWatchList(any(ContractorAccount.class), eq(permissions), any(User.class));
	}

	@Test
	public void testRemove() throws Exception {
		assertEquals(PicsActionSupport.SUCCESS, reportActivityWatch.remove());
		assertTrue(reportActivityWatch.hasActionErrors());

		verify(helper, never()).removeContractorFromWatchList(any(ContractorWatch.class));
	}

	@Test
	public void testRemove_WatchSet() throws Exception {
		reportActivityWatch.setContractorWatch(watch);

		assertEquals(PicsActionSupport.REDIRECT, reportActivityWatch.remove());
		assertFalse(reportActivityWatch.hasActionErrors());

		verify(helper).removeContractorFromWatchList(any(ContractorWatch.class));
	}
}
