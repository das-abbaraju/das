package com.picsauditing.actions.contractors;

import static org.hamcrest.Matchers.hasItem;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.naming.NoPermissionException;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.picsauditing.PICS.FeeService;
import com.picsauditing.service.contractor.TopLevelOperatorFinder;
import com.picsauditing.contractor.service.ContractorFacilitiesService;
import org.apache.commons.beanutils.BasicDynaBean;
import org.hamcrest.Matchers;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import com.picsauditing.EntityFactory;
import com.picsauditing.PicsActionTest;
import com.picsauditing.PicsTestUtil;
import com.picsauditing.PICS.BillingService;
import com.picsauditing.PICS.FacilityChanger;
import com.picsauditing.PICS.SmartFacilitySuggest;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.jpa.entities.AccountLevel;
import com.picsauditing.jpa.entities.AccountStatus;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorOperator;
import com.picsauditing.jpa.entities.ContractorRegistrationRequest;
import com.picsauditing.jpa.entities.Facility;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.report.RecordNotFoundException;
import com.picsauditing.search.Database;
import com.picsauditing.util.PermissionToViewContractor;

@SuppressWarnings("deprecation")
public class ContractorFacilitiesTest extends PicsActionTest {
	private ContractorFacilities contractorFacilities;

	@Mock
	private BasicDynaBean basicDynaBean;
	@Mock
	private BillingService billingService;
    @Mock
    private FeeService feeService;
	@Mock
	private EntityManager entityManager;
	@Mock
	private FacilityChanger facilityChanger;
	@Mock
	private PermissionToViewContractor permissionToViewContractor;
	@Mock
	private Query query;
	@Mock
	private Database database;
    @Mock
    private TopLevelOperatorFinder topLevelOperatorFinder;
    @Mock
    private ContractorFacilitiesService contractorFacilitiesService;

	@AfterClass
	public static void tearDown() throws Exception {
		Whitebox.setInternalState(SmartFacilitySuggest.class, "database", (Database) null);
	}

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		contractorFacilities = new ContractorFacilities();
		super.setUp(contractorFacilities);

		Whitebox.setInternalState(SmartFacilitySuggest.class, "database", database);

		PicsTestUtil picsTestUtil = new PicsTestUtil();
		picsTestUtil.autowireEMInjectedDAOs(contractorFacilities, entityManager);

		Whitebox.setInternalState(contractorFacilities, "billingService", billingService);
        Whitebox.setInternalState(contractorFacilities, "feeService", feeService);
		Whitebox.setInternalState(contractorFacilities, "facilityChanger", facilityChanger);
		Whitebox.setInternalState(contractorFacilities, "permissionToViewContractor", permissionToViewContractor);
		Whitebox.setInternalState(contractorFacilities, "database", database);
        Whitebox.setInternalState(contractorFacilities, "topLevelOperatorFinder", topLevelOperatorFinder);
		Whitebox.setInternalState(contractorFacilities, "contractorFacilitiesService", contractorFacilitiesService);

		when(permissions.isContractor()).thenReturn(true);
		when(permissions.getAccountStatus()).thenReturn(AccountStatus.Active);
		when(permissionToViewContractor.check(anyBoolean())).thenReturn(true);
		when(entityManager.createQuery(anyString())).thenReturn(query);
		when(database.select(anyString(), anyBoolean())).thenReturn(new ArrayList<BasicDynaBean>());
        when(topLevelOperatorFinder.findAllTopLevelOperators(any(ContractorAccount.class))).thenReturn(null);
	}

	@Test
	public void testExecute() throws Exception {
		initializeContractor();

		assertEquals(PicsActionSupport.SUCCESS, contractorFacilities.execute());

		verify(entityManager).merge(any(ContractorAccount.class));
	}

	@Test
	public void testExecute_Admin() throws Exception {
		ContractorAccount contractorAccount = initializeContractor();

		when(permissions.isContractor()).thenReturn(false);
		when(permissions.isAdmin()).thenReturn(true);

		contractorFacilities.setId(contractorAccount.getId());

		assertEquals(PicsActionSupport.SUCCESS, contractorFacilities.execute());

		verify(entityManager, never()).merge(any(ContractorAccount.class));
	}

	@Test
	public void testExecute_PendingContractor() throws Exception {
		ContractorAccount contractorAccount = initializeContractor();
		contractorAccount.setStatus(AccountStatus.Pending);

		assertEquals(PicsActionSupport.SUCCESS, contractorFacilities.execute());

		verify(entityManager).merge(any(ContractorAccount.class));
	}

	@Test
	public void testExecute_ContractorWithOneOperator() throws Exception {
		ContractorAccount contractorAccount = initializeContractor();
		OperatorAccount operatorAccount = EntityFactory.makeOperator();
		EntityFactory.addContractorOperator(contractorAccount, operatorAccount);

		assertEquals(PicsActionSupport.SUCCESS, contractorFacilities.execute());

		verify(entityManager).merge(any(ContractorAccount.class));
	}

	@Test(expected = RecordNotFoundException.class)
	public void testExecute_OperatorWithoutID() throws Exception {
		when(entityManager.find(eq(ContractorAccount.class), anyInt())).thenReturn(null);
		when(permissions.isContractor()).thenReturn(false);
		when(permissions.isOperator()).thenReturn(true);

		contractorFacilities.execute();
	}

	@Test(expected = NoPermissionException.class)
	public void testExecute_OperatorWithID() throws Exception {
		ContractorAccount contractorAccount = EntityFactory.makeContractor();

		when(entityManager.find(ContractorAccount.class, contractorAccount.getId())).thenReturn(contractorAccount);
		when(permissions.isContractor()).thenReturn(false);
		when(permissions.isOperator()).thenReturn(true);

		contractorFacilities.setId(contractorAccount.getId());
		contractorFacilities.execute();
	}

	@Test
	public void testExecute_RequestWithoutTags() throws Exception {
		ContractorAccount contractorAccount = mock(ContractorAccount.class);
		when(contractorAccount.getId()).thenReturn(1);

		OperatorAccount operatorAccount = EntityFactory.makeOperator();
		ContractorRegistrationRequest request = new ContractorRegistrationRequest();
		request.setId(1);
		request.setRequestedBy(operatorAccount);

		session.put("requestID", 1);

		when(entityManager.find(eq(ContractorAccount.class), anyInt())).thenReturn(contractorAccount);
		when(entityManager.find(ContractorRegistrationRequest.class, 1)).thenReturn(request);
		when(permissions.getAccountId()).thenReturn(1);

		assertEquals(PicsActionSupport.SUCCESS, contractorFacilities.execute());
		assertTrue(actionContext.getSession().get("requestID") == null);

		verify(billingService).syncBalance(contractorAccount);
		verify(entityManager, times(2)).merge(any(ContractorAccount.class));
		verify(facilityChanger).add();
	}

	@Test
	public void testExecute_RequestWithTags() throws Exception {
		ContractorAccount contractorAccount = spy(EntityFactory.makeContractor());

		OperatorAccount operatorAccount = EntityFactory.makeOperator();
		ContractorRegistrationRequest request = new ContractorRegistrationRequest();
		request.setId(1);
		request.setOperatorTags("1");
		request.setRequestedBy(operatorAccount);

		session.put("requestID", 1);

		doNothing().when(billingService).syncBalance(contractorAccount);
		when(entityManager.find(eq(ContractorAccount.class), anyInt())).thenReturn(contractorAccount);
		when(entityManager.find(ContractorRegistrationRequest.class, 1)).thenReturn(request);
		when(permissions.getAccountId()).thenReturn(1);

		assertEquals(PicsActionSupport.SUCCESS, contractorFacilities.execute());
		assertTrue(actionContext.getSession().get("requestID") == null);
		assertFalse(contractorAccount.getOperatorTags().isEmpty());

		verify(billingService).syncBalance(contractorAccount);
		verify(entityManager, times(3)).merge(any(ContractorAccount.class));
		verify(facilityChanger).add();
	}

	@Test
	public void testExecute_SearchOperatorNameProvided() throws Exception {
		initializeContractor();

		contractorFacilities.setOperator(new OperatorAccount());
		contractorFacilities.getOperator().setName("Test");

		when(entityManager.createQuery(anyString())).thenReturn(query);
		when(query.getResultList()).thenReturn(new ArrayList<OperatorAccount>());

		assertEquals("search", contractorFacilities.search());
		assertNotNull(contractorFacilities.getSearchResults());

		verify(entityManager, never()).merge(any(ContractorAccount.class));
	}

	@Test
	public void testExecute_SearchValueProvided() throws Exception {
		initializeContractor();

		contractorFacilities.setSearch("CountrySubdivision");

		ArgumentCaptor<String> sqlCaptor = ArgumentCaptor.forClass(String.class);

		assertEquals("search", contractorFacilities.search());
		assertNotNull(contractorFacilities.getSearchResults());

		verify(database).select(sqlCaptor.capture(), anyBoolean());
		verify(entityManager).createQuery(anyString());
		verify(entityManager, never()).merge(any(ContractorAccount.class));
		verify(query).getResultList();

		assertTrue(sqlCaptor.getValue().contains("countrySubdivision LIKE '%CountrySubdivision%'"));
		assertTrue(sqlCaptor.getValue().contains("msgValue LIKE '%CountrySubdivision%'"));
	}

	@Test
	public void testExecute_SearchValueEmpty() throws Exception {
		initializeContractor();

		contractorFacilities.setSearch("");
		contractorFacilities.setOperator(new OperatorAccount());

		when(entityManager.createQuery(anyString())).thenReturn(query);
		when(query.getResultList()).thenReturn(new ArrayList<OperatorAccount>());

		assertEquals("search", contractorFacilities.search());
		assertNotNull(contractorFacilities.getSearchResults());

		verify(entityManager, never()).merge(any(ContractorAccount.class));
	}

	@Test
	public void testSearch_WithResults() throws Exception {
		ContractorAccount contractorAccount = initializeContractor();

		contractorFacilities.setSearch("CountrySubdivision");

		OperatorAccount operatorResult = EntityFactory.makeOperator();
		operatorResult.setOnsiteServices(true);

		OperatorAccount operatorResult2 = EntityFactory.makeOperator();
		operatorResult2.setOnsiteServices(false);
		operatorResult2.setOffsiteServices(true);

		OperatorAccount operatorResult3 = EntityFactory.makeOperator();
		operatorResult3.setOnsiteServices(true);

		EntityFactory.addContractorOperator(contractorAccount, operatorResult3);

		ArrayList<OperatorAccount> operators = new ArrayList<>();
		operators.add(operatorResult);
		operators.add(operatorResult2);
		operators.add(operatorResult3);

		List<BasicDynaBean> basicDynaBeans = new ArrayList<>();
		basicDynaBeans.add(basicDynaBean);

		when(basicDynaBean.get("opID")).thenReturn(operatorResult3.getId());
		when(basicDynaBean.get("name")).thenReturn(operatorResult3.getName());
		when(basicDynaBean.get("status")).thenReturn(operatorResult3.getStatus());
		when(basicDynaBean.get("onsiteServices")).thenReturn(operatorResult3.isOnsiteServices() ? 1 : 0);
		when(basicDynaBean.get("offsiteServices")).thenReturn(operatorResult3.isOnsiteServices() ? 1 : 0);
		when(basicDynaBean.get("materialSupplier")).thenReturn(operatorResult3.isOnsiteServices() ? 1 : 0);
		when(basicDynaBean.get("transportationServices")).thenReturn(operatorResult3.isOnsiteServices() ? 1 : 0);
		when(database.select(anyString(), anyBoolean())).thenReturn(basicDynaBeans);
		when(entityManager.createQuery(anyString())).thenReturn(query);
		when(query.getResultList()).thenReturn(contractorAccount.getOperators());

		assertEquals("search", contractorFacilities.search());
		assertNotNull(contractorFacilities.getSearchResults());

		verify(entityManager).createQuery(anyString());
		verify(query).getResultList();
	}

	@Test
	public void testSearch_NonCorporateOperatorsSizeZero_NoDataSetsActionMessageOnly() throws Exception {
		String testMessage = "Test Message";
		initializeContractor();
		when(translationService.hasKey(anyString(), any(Locale.class))).thenReturn(true);
		when(
				translationService.getText(eq("ContractorFacilities.message.FacilitiesBasedLocation"),
						eq(Locale.ENGLISH), any())).thenReturn(testMessage);

		contractorFacilities.setOperator(new OperatorAccount());

		assertEquals("search", contractorFacilities.search());

		assertThat(contractorFacilities.getActionMessages(), hasItem(testMessage));
	}

	@Test
	public void testSearch_NonCorporateOperatorsSizeZero_WithResult() throws Exception {
		initializeContractor();

		BasicDynaBean basicDynaBean = mock(BasicDynaBean.class);
		List<BasicDynaBean> data = new ArrayList<BasicDynaBean>();
		data.add(basicDynaBean);

		when(basicDynaBean.get("total")).thenReturn(1);
		when(basicDynaBean.get("onsiteServices")).thenReturn(1);
		when(basicDynaBean.get("offsiteServices")).thenReturn(0);
		when(basicDynaBean.get("materialSupplier")).thenReturn(0);
		when(basicDynaBean.get("transportationServices")).thenReturn(0);
		when(basicDynaBean.get("opID")).thenReturn(1);
		when(basicDynaBean.get("name")).thenReturn("Test Operator");
		when(basicDynaBean.get("status")).thenReturn("Active");

		when(database.select(anyString(), anyBoolean())).thenReturn(data);

		contractorFacilities.setOperator(new OperatorAccount());

		assertEquals("search", contractorFacilities.search());
		assertFalse(contractorFacilities.getSearchResults().isEmpty());
		assertEquals("Test Operator", contractorFacilities.getSearchResults().get(0).getName());
	}

	@Test
	public void testSearch_SimilarOperatorsNonCorporate() throws Exception {
		ContractorAccount contractorAccount = initializeContractor();
		OperatorAccount operatorAccount = EntityFactory.makeOperator();
		EntityFactory.addContractorOperator(contractorAccount, operatorAccount);

		contractorFacilities.setOperator(new OperatorAccount());

		assertEquals("search", contractorFacilities.search());
		assertTrue(contractorFacilities.hasActionMessages());
	}

	@Test
	public void testSearch_SimilarOperatorCorporateNoSharedOperators() throws Exception {
		ContractorAccount contractor = EntityFactory.makeContractor();
		when(entityManager.find(ContractorAccount.class, contractor.getId())).thenReturn(contractor);
		EntityFactory.addContractorOperator(contractor, EntityFactory.makeOperator());

		OperatorAccount corporate = EntityFactory.makeOperator();
		corporate.setType("Corporate");

		when(entityManager.find(OperatorAccount.class, corporate.getId())).thenReturn(corporate);
		when(permissions.getAccountId()).thenReturn(corporate.getId());
		when(permissions.isContractor()).thenReturn(false);
		when(permissions.isCorporate()).thenReturn(true);

		OperatorAccount operator = EntityFactory.makeOperator();

		Facility facility = new Facility();
		facility.setOperator(operator);
		facility.setCorporate(corporate);

		corporate.getOperatorFacilities().add(facility);

		contractorFacilities.setId(contractor.getId());
		contractorFacilities.setOperator(new OperatorAccount());

		assertEquals("search", contractorFacilities.search());
		assertNotNull(contractorFacilities.getSearchResults());
		assertFalse(contractorFacilities.getSearchResults().isEmpty());

		verify(entityManager).find(OperatorAccount.class, corporate.getId());
	}

	@Test
	public void testSearch_SimilarOperatorCorporateSharedOperators() throws Exception {
		OperatorAccount operator = EntityFactory.makeOperator();

		ContractorAccount contractor = EntityFactory.makeContractor();
		when(entityManager.find(ContractorAccount.class, contractor.getId())).thenReturn(contractor);
		EntityFactory.addContractorOperator(contractor, operator);

		OperatorAccount corporate = EntityFactory.makeOperator();
		corporate.setType("Corporate");

		when(entityManager.find(OperatorAccount.class, corporate.getId())).thenReturn(corporate);
		when(permissions.getAccountId()).thenReturn(corporate.getId());
		when(permissions.isContractor()).thenReturn(false);
		when(permissions.isCorporate()).thenReturn(true);

		Facility facility = new Facility();
		facility.setOperator(operator);
		facility.setCorporate(corporate);

		corporate.getOperatorFacilities().add(facility);

		contractorFacilities.setId(contractor.getId());
		contractorFacilities.setOperator(new OperatorAccount());

		assertEquals("search", contractorFacilities.search());
		assertNotNull(contractorFacilities.getSearchResults());
		assertTrue(contractorFacilities.getSearchResults().isEmpty());

		verify(entityManager).find(OperatorAccount.class, corporate.getId());
	}

	@Test
	public void testSearchShowAll() throws Exception {
		initializeContractor();

		assertEquals("search", contractorFacilities.searchShowAll());

		verify(entityManager, never()).merge(any(ContractorAccount.class));
	}

	@Test
	public void testValidateBidOnly() throws Exception {
		ContractorAccount contractor = initializeContractor();

		OperatorAccount operator = EntityFactory.makeOperator();
		when(entityManager.find(eq(OperatorAccount.class), anyInt())).thenReturn(operator);

		contractorFacilities.setContractor(contractor);
		contractorFacilities.setOperator(operator);

		assertEquals(PicsActionSupport.JSON, contractorFacilities.validateBidOnly());
	}

	@Test
	public void testLoad() throws Exception {
		initializeContractor();

		when(entityManager.createQuery(anyString())).thenReturn(query);
		when(query.getResultList()).thenReturn(new ArrayList<ContractorOperator>());

		assertEquals("load", contractorFacilities.load());
	}

	@Test
	public void testSetRequestedBy() throws Exception {
		ContractorAccount contractor = initializeContractor();

		contractorFacilities.setContractor(contractor);
		contractorFacilities.setOperator(new OperatorAccount());

		assertEquals(PicsActionSupport.JSON, contractorFacilities.setRequestedBy());

		verify(entityManager, never()).merge(any(ContractorAccount.class));
	}

	@Test
	public void testSetRequestedBy_WithOperator() throws Exception {
		ContractorAccount contractor = initializeContractor();

		contractorFacilities.setContractor(contractor);
		contractorFacilities.setOperator(EntityFactory.makeOperator());

		assertEquals(PicsActionSupport.JSON, contractorFacilities.setRequestedBy());
		assertEquals(contractorFacilities.getOperator(), contractor.getRequestedBy());

		verify(entityManager).merge(any(ContractorAccount.class));
	}

	@Test
	public void testSetRequestedBy_WithOperatorBidOnly() throws Exception {
		ContractorAccount contractor = initializeContractor(true);
		contractor.setAccountLevel(AccountLevel.BidOnly);

		OperatorAccount operator = EntityFactory.makeOperator();
		operator.setAcceptsBids(false);

		contractorFacilities.setContractor(contractor);
		contractorFacilities.setOperator(operator);

		assertEquals(PicsActionSupport.JSON, contractorFacilities.setRequestedBy());
		assertTrue(contractor.isRenew());
		assertEquals(AccountLevel.BidOnly, contractor.getAccountLevel());

		verify(entityManager).merge(any(ContractorAccount.class));
	}

	@Test
	public void testSwitchToTrialAccount() throws Exception {
		ContractorAccount contractor = initializeContractor(true);

		contractorFacilities.setContractor(contractor);

		assertEquals(PicsActionSupport.BLANK, contractorFacilities.switchToTrialAccount());
		assertFalse(contractor.isRenew());
		assertEquals(AccountLevel.BidOnly, contractor.getAccountLevel());

		verify(entityManager).merge(any(ContractorAccount.class));
	}

	@Test
	public void testAdd_OperatorServiceMismatch() throws Exception {
		ContractorAccount contractor = initializeContractor();

		OperatorAccount operator = EntityFactory.makeOperator();
		operator.setOffsiteServices(true);
		operator.setOnsiteServices(false);

		contractorFacilities.setContractor(contractor);
		contractorFacilities.setOperator(operator);

		when(entityManager.createQuery(anyString())).thenReturn(query);
		when(query.getResultList()).thenReturn(new ArrayList<ContractorOperator>());

		assertEquals(PicsActionSupport.JSON, contractorFacilities.add());
		assertTrue(contractorFacilities.hasActionErrors());

		verify(entityManager, never()).merge(any(ContractorAccount.class));
	}

	@Test
	public void testAdd_OperatorServiceMatch() throws Exception {
		ContractorAccount contractor = initializeContractor(true);

		OperatorAccount operatorAccount = EntityFactory.makeOperator();
		operatorAccount.setOnsiteServices(true);

		contractorFacilities.setContractor(contractor);
		contractorFacilities.setOperator(operatorAccount);

		assertEquals(PicsActionSupport.JSON, contractorFacilities.add());
		assertTrue(contractor.isRenew());

		verify(entityManager).merge(any(ContractorAccount.class));
	}

	@Test
	public void testRemove_OperatorPendingContractorNoOperators() throws Exception {
		ContractorAccount contractor = initializeContractor(true);
		contractor.setStatus(AccountStatus.Pending);

		OperatorAccount operator = EntityFactory.makeOperator();
		contractor.setRequestedBy(operator);

		contractorFacilities.setContractor(contractor);
		contractorFacilities.setOperator(operator);

		assertEquals(PicsActionSupport.JSON, contractorFacilities.remove());
		assertNull(contractor.getRequestedBy());

		verify(entityManager, never()).merge(any(ContractorAccount.class));
	}

	@Test
	public void testRemove_OperatorPendingContractorOneOperator() throws Exception {
		OperatorAccount nonCorporate = EntityFactory.makeOperator();

		ContractorAccount contractor = initializeContractor(true);
		contractor.setStatus(AccountStatus.Pending);
		EntityFactory.addContractorOperator(contractor, nonCorporate);

		OperatorAccount operator = EntityFactory.makeOperator();
		contractor.setRequestedBy(operator);

		contractorFacilities.setContractor(contractor);
		contractorFacilities.setOperator(operator);

		assertEquals(PicsActionSupport.JSON, contractorFacilities.remove());
		assertEquals(nonCorporate, contractor.getRequestedBy());

		verify(entityManager).merge(any(ContractorAccount.class));
	}

	@Test
	public void testIsTrialContractor() {
		ContractorAccount contractorAccount = EntityFactory.makeContractor();
		contractorFacilities.setContractor(contractorAccount);

		assertFalse(contractorFacilities.isTrialContractor());

		OperatorAccount operatorAccount = EntityFactory.makeOperator();
		EntityFactory.addContractorOperator(contractorAccount, operatorAccount);
		assertFalse(contractorFacilities.isTrialContractor());

		contractorAccount.getOperators().clear();
		operatorAccount.setAcceptsBids(true);
		EntityFactory.addContractorOperator(contractorAccount, operatorAccount);
		assertFalse(contractorFacilities.isTrialContractor());

		contractorAccount.setStatus(AccountStatus.Pending);
		contractorAccount.setAccountLevel(AccountLevel.BidOnly);
		assertFalse(contractorFacilities.isTrialContractor());

		contractorAccount.setAccountLevel(AccountLevel.Full);
		assertTrue(contractorFacilities.isTrialContractor());
	}

	@Test
	public void testGetTypeCount() {
		ContractorAccount contractor = EntityFactory.makeContractor();
		OperatorAccount operator = EntityFactory.makeOperator();
		operator.setOnsiteServices(false);
		operator.setOffsiteServices(false);
		operator.setMaterialSupplier(false);

		contractorFacilities.setContractor(contractor);

		assertEquals(0, contractorFacilities.getTypeCount(operator));

		operator.setOnsiteServices(true);
		assertEquals(1, contractorFacilities.getTypeCount(operator));

		operator.setOffsiteServices(true);
		assertEquals(1, contractorFacilities.getTypeCount(operator));

		contractor.setOffsiteServices(true);
		assertEquals(2, contractorFacilities.getTypeCount(operator));

		contractor.setMaterialSupplier(true);
		assertEquals(2, contractorFacilities.getTypeCount(operator));

		operator.setMaterialSupplier(true);
		assertEquals(3, contractorFacilities.getTypeCount(operator));
	}

	private ContractorAccount initializeContractor() {
		return initializeContractor(false);
	}

	private ContractorAccount initializeContractor(boolean spy) {
		ContractorAccount contractorAccount = EntityFactory.makeContractor();
		int contractorID = contractorAccount.getId();

		if (spy) {
			contractorAccount = spy(contractorAccount);
			doNothing().when(billingService).syncBalance(contractorAccount);
		}

		when(entityManager.find(ContractorAccount.class, contractorID)).thenReturn(contractorAccount);
		when(permissions.getAccountId()).thenReturn(contractorID);

		return contractorAccount;
	}
}
