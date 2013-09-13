package com.picsauditing.actions.contractors;

import com.opensymphony.xwork2.ActionSupport;
import com.picsauditing.EntityFactory;
import com.picsauditing.PICS.SmartFacilitySuggest;
import com.picsauditing.PicsActionTest;
import com.picsauditing.PicsTestUtil;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.OperatorAccountDAO;
import com.picsauditing.jpa.entities.*;
import com.picsauditing.search.Database;
import com.picsauditing.search.SearchEngine;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.startsWith;
import static org.mockito.Mockito.*;

public class RegistrationAddClientSiteTest extends PicsActionTest {
	public static final int CONTRACTOR_ID = 11233;

	private RegistrationAddClientSite registrationAddClientSite;
	private List<OperatorAccount> results;

	@Mock
	private ContractorAccount contractor;
	@Mock
	private Country country;
	@Mock
	private Database smartFacilityDatabase;
	@Mock
	private SearchEngine searchEngine;
	@Mock
	private OperatorAccountDAO operatorDao;
	@Mock
	private ContractorAccountDAO contractorAccountDao;

	@AfterClass
	public static void classTearDown() throws Exception {
		Whitebox.setInternalState(SmartFacilitySuggest.class, "database", (Database) null);
		PicsActionTest.classTearDown();
	}

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		registrationAddClientSite = new RegistrationAddClientSite();
		super.setUp(registrationAddClientSite);

		Whitebox.setInternalState(SmartFacilitySuggest.class, "database", smartFacilityDatabase);
		Whitebox.setInternalState(registrationAddClientSite, "searchEngineForTesting", searchEngine);

		PicsTestUtil.autowireDAOsFromDeclaredMocks(registrationAddClientSite, this);

		when(contractor.getId()).thenReturn(CONTRACTOR_ID);
		when(contractor.getZip()).thenReturn("12345");
		when(permissions.isContractor()).thenReturn(true);
		when(permissions.getAccountId()).thenReturn(CONTRACTOR_ID);
		when(permissions.getAccountStatus()).thenReturn(AccountStatus.Active);
		when(contractorAccountDao.find(CONTRACTOR_ID)).thenReturn(contractor);
		when(contractor.getCountry()).thenReturn(country);
		when(country.getIsoCode()).thenReturn("US");

		results = new ArrayList<OperatorAccount>();
		for (int i = 0; i < 3; i++) {
			results.add(EntityFactory.makeOperator());
		}
	}

	@Test
	public void testEmptySearchValue() throws Exception {
		registrationAddClientSite.setSearchValue(null);
		registrationAddClientSite.search();

		verify(operatorDao).findWhere(eq(false), startsWith("a.id IN ("));
	}

	@Test
	public void testNotEmptySearchValue_NotGlob() throws Exception {
		registrationAddClientSite.setSearchValue("Hello World");
		registrationAddClientSite.search();

		verify(operatorDao).nativeClientSiteSearch(anyString());
	}

	@Test
	public void testSearchForOperators_WithGlob() throws Exception {
		registrationAddClientSite.setSearchValue("*");
		registrationAddClientSite.search();

		verify(operatorDao).findWhere(false, null, permissions);
	}

	@Test
	public void testReturnAjax() throws Exception {
		when(request.getHeader("X-Requested-With")).thenReturn("XMLHttpRequest");
		assertEquals("ClientSiteList", registrationAddClientSite.search());
	}

	@Test
	public void testReturnSuccess_WhenNotAjax() throws Exception {
		assertEquals(ActionSupport.SUCCESS, registrationAddClientSite.search());
	}

	@Test
	public void testRemoveExistingOperatorsFromSearch() throws Exception {
		when(operatorDao.nativeClientSiteSearch(anyString())).thenReturn(results);
		List<OperatorAccount> operatorAccounts = new ArrayList<OperatorAccount>();
		operatorAccounts.addAll(results);
		when(contractor.getOperatorAccounts()).thenReturn(operatorAccounts);

		registrationAddClientSite.setSearchValue("Hello World");
		registrationAddClientSite.search();

		// registrationAddClientSite.getSearchResults() this calls
		// loadSearchResults
		List<OperatorAccount> results = Whitebox.getInternalState(registrationAddClientSite, "searchResults");

		assertTrue(results.isEmpty());
	}

	@Test
	public void testAddEmployeeGUARDIfOperatorCompetencyRequiresDocumentation_OperatorDoesNotHaveCompetencyRequiringDocumentation()
			throws Exception {
		ContractorOperator contractorOperator = mock(ContractorOperator.class);
		OperatorAccount operatorAccount = mock(OperatorAccount.class);

		List<ContractorOperator> contractorOperators = new ArrayList<>();
		contractorOperators.add(contractorOperator);

		when(contractor.getOperators()).thenReturn(contractorOperators);
		when(contractorOperator.getOperatorAccount()).thenReturn(operatorAccount);
		when(operatorAccount.hasCompetencyRequiringDocumentation()).thenReturn(false);

		registrationAddClientSite.setContractor(contractor);
		Whitebox.invokeMethod(registrationAddClientSite, "addEmployeeGUARDIfOperatorCompetencyRequiresDocumentation");

		verify(contractor, never()).setRequiresCompetencyReview(anyBoolean());
		verify(contractorAccountDao, never()).save(any(BaseTable.class));
	}

	@Test
	public void testAddEmployeeGUARDIfOperatorCompetencyRequiresDocumentation_OperatorDoesHaveCompetencyRequiringDocumentation()
			throws Exception {
		ContractorOperator contractorOperator = mock(ContractorOperator.class);
		OperatorAccount operatorAccount = mock(OperatorAccount.class);

		List<ContractorOperator> contractorOperators = new ArrayList<>();
		contractorOperators.add(contractorOperator);

		when(contractor.getOperators()).thenReturn(contractorOperators);
		when(contractorOperator.getOperatorAccount()).thenReturn(operatorAccount);
		when(operatorAccount.hasCompetencyRequiringDocumentation()).thenReturn(true);

		registrationAddClientSite.setContractor(contractor);
		Whitebox.invokeMethod(registrationAddClientSite, "addEmployeeGUARDIfOperatorCompetencyRequiresDocumentation");

		verify(contractor).setRequiresCompetencyReview(true);
		verify(contractorAccountDao).save(contractor);
	}
}