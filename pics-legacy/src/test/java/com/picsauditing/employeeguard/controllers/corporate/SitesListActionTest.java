package com.picsauditing.employeeguard.controllers.corporate;

import com.picsauditing.PicsActionTest;
import com.picsauditing.access.NoRightsException;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.employeeguard.services.external.AccountService;
import com.picsauditing.employeeguard.services.factory.AccountServiceFactory;
import org.approvaltests.Approvals;
import org.approvaltests.reporters.DiffReporter;
import org.approvaltests.reporters.UseReporter;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

@UseReporter(DiffReporter.class)
public class SitesListActionTest extends PicsActionTest {

	public static final int CONTRACTOR_ID = 234;
	public static final int SITE_ID = 123;

	SitesListAction sitesListAction;

	private AccountService accountService = AccountServiceFactory.getAccountService();

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		sitesListAction = new SitesListAction();
		super.setUp(sitesListAction);

		when(permissions.getAccountId()).thenReturn(SITE_ID);
		when(permissions.getAccountName()).thenReturn("Site Name");

		Whitebox.setInternalState(sitesListAction, "accountService", accountService);
	}

	@Test(expected = NoRightsException.class)
	public void testList_Site() throws Exception {
		when(permissions.isContractor()).thenReturn(true);

		sitesListAction.list();
	}

	@Test
	public void testList_Corporate() throws Exception {
		when(permissions.isCorporate()).thenReturn(true);

		assertEquals(PicsActionSupport.JSON_STRING, sitesListAction.list());
		assertNotNull(sitesListAction.getJsonString());
		Approvals.verify(sitesListAction.getJsonString());
	}
}
