package com.picsauditing.actions.contractors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
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
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.verifyStatic;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.naming.NoPermissionException;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.apache.commons.beanutils.BasicDynaBean;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.ResultSetMetaData;
import com.mysql.jdbc.Statement;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import com.picsauditing.EntityFactory;
import com.picsauditing.PicsTestUtil;
import com.picsauditing.PICS.BillingCalculatorSingle;
import com.picsauditing.PICS.DBBean;
import com.picsauditing.PICS.FacilityChanger;
import com.picsauditing.PICS.I18nCache;
import com.picsauditing.PICS.SmartFacilitySuggest;
import com.picsauditing.access.Permissions;
import com.picsauditing.access.RecordNotFoundException;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.jpa.entities.AccountLevel;
import com.picsauditing.jpa.entities.AccountStatus;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorOperator;
import com.picsauditing.jpa.entities.ContractorRegistrationRequest;
import com.picsauditing.jpa.entities.Facility;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.util.PermissionToViewContractor;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ ContractorFacilities.class, ActionContext.class, DBBean.class, I18nCache.class,
		SmartFacilitySuggest.class })
@PowerMockIgnore({ "javax.xml.parsers.*", "ch.qos.logback.*", "org.slf4j.*", "org.apache.xerces.*" })
public class ContractorFacilitiesTest {
	private ContractorFacilities contractorFacilities;

	@Mock
	private ActionContext actionContext;
	@Mock
	private BillingCalculatorSingle billingCalculatorSingle;
	@Mock
	private EntityManager entityManager;
	@Mock
	private FacilityChanger facilityChanger;
	@Mock
	private I18nCache i18nCache;
	@Mock
	private Permissions permissions;
	@Mock
	private PermissionToViewContractor permissionToViewContractor;
	@Mock
	private Query query;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		mockStatic(I18nCache.class);
		mockStatic(SmartFacilitySuggest.class);

		contractorFacilities = new ContractorFacilities();

		PicsTestUtil picsTestUtil = new PicsTestUtil();
		picsTestUtil.autowireEMInjectedDAOs(contractorFacilities, entityManager);

		Whitebox.setInternalState(contractorFacilities, "billingService", billingCalculatorSingle);
		Whitebox.setInternalState(contractorFacilities, "facilityChanger", facilityChanger);
		Whitebox.setInternalState(contractorFacilities, "i18nCache", i18nCache);
		Whitebox.setInternalState(contractorFacilities, "permissions", permissions);
		Whitebox.setInternalState(contractorFacilities, "permissionToViewContractor", permissionToViewContractor);

		when(entityManager.createQuery(anyString())).thenReturn(query);
		when(I18nCache.getInstance()).thenReturn(i18nCache);
		when(i18nCache.getText(anyString(), any(Locale.class), any())).thenReturn("Text");
		when(permissions.isContractor()).thenReturn(true);
		when(permissions.getAccountStatus()).thenReturn(AccountStatus.Active);
		when(permissionToViewContractor.check(anyBoolean())).thenReturn(true);
		when(SmartFacilitySuggest.getFirstFacility(any(ContractorAccount.class), any(Permissions.class))).thenReturn(
				new ArrayList<BasicDynaBean>());
	}

	@Test
	public void testExecute() throws Exception {
		mockActionContext();
		initializeContractor();

		assertEquals(ActionSupport.SUCCESS, contractorFacilities.execute());

		verify(entityManager).merge(any(ContractorAccount.class));
	}

	@Test
	public void testExecute_Admin() throws Exception {
		mockActionContext();
		ContractorAccount contractorAccount = initializeContractor();

		when(permissions.isContractor()).thenReturn(false);
		when(permissions.isAdmin()).thenReturn(true);

		contractorFacilities.setId(contractorAccount.getId());

		assertEquals(ActionSupport.SUCCESS, contractorFacilities.execute());

		verify(entityManager, never()).merge(any(ContractorAccount.class));
	}

	@Test
	public void testExecute_PendingContractor() throws Exception {
		mockActionContext();

		ContractorAccount contractorAccount = initializeContractor();
		contractorAccount.setStatus(AccountStatus.Pending);

		assertEquals(ActionSupport.SUCCESS, contractorFacilities.execute());

		verify(entityManager).merge(any(ContractorAccount.class));
	}

	@Test
	public void testExecute_ContractorWithOneOperator() throws Exception {
		mockActionContext();

		ContractorAccount contractorAccount = initializeContractor();
		OperatorAccount operatorAccount = EntityFactory.makeOperator();
		EntityFactory.addContractorOperator(contractorAccount, operatorAccount);

		assertEquals(ActionSupport.SUCCESS, contractorFacilities.execute());

		verify(entityManager).merge(any(ContractorAccount.class));
	}

	@Test(expected = RecordNotFoundException.class)
	public void testExecute_OperatorWithoutID() throws Exception {
		mockActionContext();

		when(entityManager.find(eq(ContractorAccount.class), anyInt())).thenReturn(null);
		when(permissions.isContractor()).thenReturn(false);
		when(permissions.isOperator()).thenReturn(true);

		contractorFacilities.execute();
	}

	@Test(expected = NoPermissionException.class)
	public void testExecute_OperatorWithID() throws Exception {
		mockActionContext();

		ContractorAccount contractorAccount = EntityFactory.makeContractor();

		when(entityManager.find(ContractorAccount.class, contractorAccount.getId())).thenReturn(contractorAccount);
		when(permissions.isContractor()).thenReturn(false);
		when(permissions.isOperator()).thenReturn(true);

		contractorFacilities.setId(contractorAccount.getId());
		contractorFacilities.execute();
	}

	@Test
	public void testExecute_RequestWithoutTags() throws Exception {
		mockActionContext();

		ContractorAccount contractorAccount = mock(ContractorAccount.class);
		when(contractorAccount.getId()).thenReturn(1);

		OperatorAccount operatorAccount = EntityFactory.makeOperator();
		ContractorRegistrationRequest request = new ContractorRegistrationRequest();
		request.setId(1);
		request.setRequestedBy(operatorAccount);

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("requestID", 1);

		when(actionContext.getSession()).thenReturn(map);
		when(entityManager.find(eq(ContractorAccount.class), anyInt())).thenReturn(contractorAccount);
		when(entityManager.find(ContractorRegistrationRequest.class, 1)).thenReturn(request);
		when(permissions.getAccountId()).thenReturn(1);

		assertEquals(ActionSupport.SUCCESS, contractorFacilities.execute());
		assertTrue(actionContext.getSession().isEmpty());

		verify(contractorAccount).syncBalance();
		verify(entityManager, times(2)).merge(any(ContractorAccount.class));
		verify(facilityChanger).add();
	}

	@Test
	public void testExecute_RequestWithTags() throws Exception {
		mockActionContext();

		ContractorAccount contractorAccount = spy(EntityFactory.makeContractor());

		OperatorAccount operatorAccount = EntityFactory.makeOperator();
		ContractorRegistrationRequest request = new ContractorRegistrationRequest();
		request.setId(1);
		request.setOperatorTags("1");
		request.setRequestedBy(operatorAccount);

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("requestID", 1);

		when(actionContext.getSession()).thenReturn(map);
		doNothing().when(contractorAccount).syncBalance();
		when(entityManager.find(eq(ContractorAccount.class), anyInt())).thenReturn(contractorAccount);
		when(entityManager.find(ContractorRegistrationRequest.class, 1)).thenReturn(request);
		when(permissions.getAccountId()).thenReturn(1);

		assertEquals(ActionSupport.SUCCESS, contractorFacilities.execute());
		assertTrue(actionContext.getSession().isEmpty());
		assertFalse(contractorAccount.getOperatorTags().isEmpty());

		verify(contractorAccount).syncBalance();
		verify(entityManager, times(3)).merge(any(ContractorAccount.class));
		verify(facilityChanger).add();
	}

	@Test
	public void testExecute_ButtonEmpty() throws Exception {
		mockActionContext();
		initializeContractor();

		contractorFacilities.setButton("");

		assertEquals(ActionSupport.SUCCESS, contractorFacilities.execute());

		verify(entityManager).merge(any(ContractorAccount.class));
	}

	@Test
	public void testExecute_SearchOperatorNameProvided() throws Exception {
		mockActionContext();
		initializeContractor();

		contractorFacilities.setButton("search");
		contractorFacilities.setOperator(new OperatorAccount());
		contractorFacilities.getOperator().setName("Test");

		when(entityManager.createQuery(anyString())).thenReturn(query);
		when(query.getResultList()).thenReturn(new ArrayList<OperatorAccount>());

		assertEquals("search", contractorFacilities.execute());
		assertNotNull(contractorFacilities.getSearchResults());

		verify(entityManager).merge(any(ContractorAccount.class));
	}

	@Test
	public void testExecute_SearchStateProvided() throws Exception {
		mockActionContext();
		initializeContractor();

		contractorFacilities.setButton("search");
		contractorFacilities.setState("State");
		contractorFacilities.setOperator(new OperatorAccount());

		when(entityManager.createQuery(anyString())).thenReturn(query);
		when(query.getResultList()).thenReturn(new ArrayList<OperatorAccount>());

		assertEquals("search", contractorFacilities.execute());
		assertNotNull(contractorFacilities.getSearchResults());

		verify(entityManager).merge(any(ContractorAccount.class));
	}

	@Test
	public void testExecute_SearchStateEmpty() throws Exception {
		mockActionContext();
		initializeContractor();

		contractorFacilities.setButton("search");
		contractorFacilities.setState("");
		contractorFacilities.setOperator(new OperatorAccount());

		when(entityManager.createQuery(anyString())).thenReturn(query);
		when(query.getResultList()).thenReturn(new ArrayList<OperatorAccount>());

		assertEquals("search", contractorFacilities.execute());
		assertNotNull(contractorFacilities.getSearchResults());

		verify(entityManager).merge(any(ContractorAccount.class));
	}

	@Test
	public void testExecute_SearchResults() throws Exception {
		mockActionContext();
		ContractorAccount contractorAccount = initializeContractor();

		contractorFacilities.setButton("search");
		contractorFacilities.setState("State");
		contractorFacilities.setOperator(new OperatorAccount());

		OperatorAccount operatorResult = EntityFactory.makeOperator();
		operatorResult.setOnsiteServices(true);

		OperatorAccount operatorResult2 = EntityFactory.makeOperator();
		operatorResult2.setOnsiteServices(false);
		operatorResult2.setOffsiteServices(true);

		OperatorAccount operatorResult3 = EntityFactory.makeOperator();
		operatorResult3.setOnsiteServices(true);

		EntityFactory.addContractorOperator(contractorAccount, operatorResult3);

		ArrayList<OperatorAccount> operators = new ArrayList<OperatorAccount>();
		operators.add(operatorResult);
		operators.add(operatorResult2);
		operators.add(operatorResult3);

		when(entityManager.createQuery(anyString())).thenReturn(query);
		when(query.getResultList()).thenReturn(contractorAccount.getOperators()).thenReturn(operators);

		assertEquals("search", contractorFacilities.execute());
		assertNotNull(contractorFacilities.getSearchResults());
		assertEquals(1, contractorFacilities.getSearchResults().size());

		verify(entityManager).merge(any(ContractorAccount.class));
	}

	@Test
	public void testExecute_SearchNonCorporateOperatorsSizeZero() throws Exception {
		mockActionContext();
		initializeContractor();

		contractorFacilities.setButton("search");
		contractorFacilities.setOperator(new OperatorAccount());

		assertEquals("search", contractorFacilities.execute());

		verify(entityManager).merge(any(ContractorAccount.class));
		verifyStatic();
		SmartFacilitySuggest.getFirstFacility(any(ContractorAccount.class), any(Permissions.class));
	}

	@Test
	public void testExecute_SearchNonCorporateOperatorsSizeZeroWithResult() throws Exception {
		mockActionContext();
		initializeContractor();

		BasicDynaBean basicDynaBean = mock(BasicDynaBean.class);
		List<BasicDynaBean> data = new ArrayList<BasicDynaBean>();
		data.add(basicDynaBean);

		when(basicDynaBean.get("onsiteServices")).thenReturn(1);
		when(basicDynaBean.get("offsiteServices")).thenReturn(0);
		when(basicDynaBean.get("materialSupplier")).thenReturn(0);
		when(basicDynaBean.get("opID")).thenReturn(1);
		when(basicDynaBean.get("name")).thenReturn("Test Operator");
		when(basicDynaBean.get("status")).thenReturn("Active");
		when(SmartFacilitySuggest.getFirstFacility(any(ContractorAccount.class), any(Permissions.class))).thenReturn(
				data);

		contractorFacilities.setButton("search");
		contractorFacilities.setOperator(new OperatorAccount());

		assertEquals("search", contractorFacilities.execute());
		assertFalse(contractorFacilities.getSearchResults().isEmpty());

		verify(entityManager).merge(any(ContractorAccount.class));
		verifyStatic();
		SmartFacilitySuggest.getFirstFacility(any(ContractorAccount.class), any(Permissions.class));
	}

	@Test
	public void testExecute_SearchSimilarOperatorsNonCorporate() throws Exception {
		mockActionContext();
		ContractorAccount contractorAccount = initializeContractor();
		OperatorAccount operatorAccount = EntityFactory.makeOperator();
		EntityFactory.addContractorOperator(contractorAccount, operatorAccount);

		when(SmartFacilitySuggest.getSimilarOperators(any(ContractorAccount.class), anyInt())).thenReturn(
				new ArrayList<BasicDynaBean>());

		contractorFacilities.setButton("search");
		contractorFacilities.setOperator(new OperatorAccount());

		assertEquals("search", contractorFacilities.execute());
		assertTrue(contractorFacilities.hasActionMessages());

		verify(entityManager).merge(any(ContractorAccount.class));
		verifyStatic();
		SmartFacilitySuggest.getSimilarOperators(any(ContractorAccount.class), anyInt());
	}

	@Test
	public void testExecute_SearchSimilarOperatorCorporateNoSharedOperators() throws Exception {
		mockActionContext();

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

		contractorFacilities.setButton("search");
		contractorFacilities.setId(contractor.getId());
		contractorFacilities.setOperator(new OperatorAccount());

		assertEquals("search", contractorFacilities.execute());
		assertNotNull(contractorFacilities.getSearchResults());
		assertFalse(contractorFacilities.getSearchResults().isEmpty());

		verify(entityManager).find(OperatorAccount.class, corporate.getId());
	}

	@Test
	public void testExecute_SearchSimilarOperatorCorporateSharedOperators() throws Exception {
		mockActionContext();
		mockActionContext();

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

		contractorFacilities.setButton("search");
		contractorFacilities.setId(contractor.getId());
		contractorFacilities.setOperator(new OperatorAccount());

		assertEquals("search", contractorFacilities.execute());
		assertNotNull(contractorFacilities.getSearchResults());
		assertTrue(contractorFacilities.getSearchResults().isEmpty());

		verify(entityManager).find(OperatorAccount.class, corporate.getId());
	}

	@Test
	public void testExecute_SearchShowAll() throws Exception {
		mockActionContext();
		initializeContractor();

		mockStatic(DBBean.class);
		Connection connection = mock(Connection.class);
		Statement statement = mock(Statement.class);
		ResultSet resultSet = mock(ResultSet.class);
		ResultSetMetaData metaData = mock(ResultSetMetaData.class);

		when(DBBean.getDBConnection()).thenReturn(connection);
		when(connection.createStatement(anyInt(), anyInt())).thenReturn(statement);
		when(statement.executeQuery(anyString())).thenReturn(resultSet);
		when(resultSet.getMetaData()).thenReturn(metaData);

		contractorFacilities.setButton("searchShowAll");
		assertEquals("search", contractorFacilities.execute());

		verify(entityManager).merge(any(ContractorAccount.class));
	}

	@Test
	public void testExecute_ValidateBidOnly() throws Exception {
		mockActionContext();
		initializeContractor();

		OperatorAccount operatorAccount = EntityFactory.makeOperator();
		when(entityManager.find(eq(OperatorAccount.class), anyInt())).thenReturn(operatorAccount);

		contractorFacilities.setOperator(operatorAccount);

		contractorFacilities.setButton("validateBidOnly");
		assertEquals(PicsActionSupport.JSON, contractorFacilities.execute());
	}

	@Test
	public void testExecute_Load() throws Exception {
		mockActionContext();
		initializeContractor();

		when(entityManager.createQuery(anyString())).thenReturn(query);
		when(query.getResultList()).thenReturn(new ArrayList<ContractorOperator>());

		contractorFacilities.setButton("load");
		assertEquals("load", contractorFacilities.execute());
	}

	@Test
	public void testExecute_Request() throws Exception {
		mockActionContext();
		initializeContractor();

		contractorFacilities.setOperator(new OperatorAccount());

		contractorFacilities.setButton("request");
		assertEquals(PicsActionSupport.JSON, contractorFacilities.execute());

		verify(entityManager).merge(any(ContractorAccount.class));
	}

	@Test
	public void testExecute_RequestWithOperator() throws Exception {
		mockActionContext();
		ContractorAccount contractor = initializeContractor();

		contractorFacilities.setOperator(EntityFactory.makeOperator());

		contractorFacilities.setButton("request");

		assertEquals(PicsActionSupport.JSON, contractorFacilities.execute());
		assertEquals(contractorFacilities.getOperator(), contractor.getRequestedBy());

		verify(entityManager, times(2)).merge(any(ContractorAccount.class));
	}

	@Test
	public void testExecute_RequestWithOperatorBidOnly() throws Exception {
		mockActionContext();

		ContractorAccount contractorAccount = initializeContractor(true);
		contractorAccount.setAccountLevel(AccountLevel.BidOnly);

		OperatorAccount operatorAccount = EntityFactory.makeOperator();
		operatorAccount.setAcceptsBids(false);

		contractorFacilities.setOperator(operatorAccount);

		contractorFacilities.setButton("request");

		assertEquals(PicsActionSupport.JSON, contractorFacilities.execute());
		assertTrue(contractorAccount.isRenew());
		assertEquals(AccountLevel.BidOnly, contractorAccount.getAccountLevel());

		verify(entityManager, times(2)).merge(any(ContractorAccount.class));
	}

	@Test
	public void testExecute_SwitchToTrialAccount() throws Exception {
		mockActionContext();

		ContractorAccount contractorAccount = initializeContractor(true);

		contractorFacilities.setButton("SwitchToTrialAccount");

		assertEquals(ActionSupport.SUCCESS, contractorFacilities.execute());
		assertFalse(contractorAccount.isRenew());
		assertEquals(AccountLevel.BidOnly, contractorAccount.getAccountLevel());

		verify(entityManager, times(2)).merge(any(ContractorAccount.class));
	}

	@Test
	public void testExecute_AddOperatorServiceMismatch() throws Exception {
		mockActionContext();
		initializeContractor();

		OperatorAccount operatorAccount = EntityFactory.makeOperator();
		operatorAccount.setOffsiteServices(true);
		operatorAccount.setOnsiteServices(false);
		contractorFacilities.setOperator(operatorAccount);

		when(entityManager.createQuery(anyString())).thenReturn(query);
		when(query.getResultList()).thenReturn(new ArrayList<ContractorOperator>());

		contractorFacilities.setButton("addOperator");

		assertEquals(PicsActionSupport.JSON, contractorFacilities.execute());
		assertTrue(contractorFacilities.hasActionErrors());

		verify(entityManager).merge(any(ContractorAccount.class));
	}

	@Test
	public void testExecute_AddOperatorServiceMatch() throws Exception {
		mockActionContext();

		ContractorAccount contractorAccount = initializeContractor(true);

		OperatorAccount operatorAccount = EntityFactory.makeOperator();
		operatorAccount.setOnsiteServices(true);
		contractorFacilities.setOperator(operatorAccount);

		contractorFacilities.setButton("addOperator");

		assertEquals(PicsActionSupport.JSON, contractorFacilities.execute());
		assertTrue(contractorAccount.isRenew());

		verify(entityManager, times(2)).merge(any(ContractorAccount.class));
	}

	@Test
	public void testExecute_RemoveOperatorPendingContractorNoOperators() throws Exception {
		mockActionContext();

		ContractorAccount contractorAccount = initializeContractor(true);
		contractorAccount.setStatus(AccountStatus.Pending);

		OperatorAccount operatorAccount = EntityFactory.makeOperator();
		contractorAccount.setRequestedBy(operatorAccount);
		contractorFacilities.setOperator(operatorAccount);

		contractorFacilities.setButton("removeOperator");

		assertEquals(PicsActionSupport.JSON, contractorFacilities.execute());
		assertNull(contractorAccount.getRequestedBy());

		verify(entityManager, times(2)).merge(any(ContractorAccount.class));
	}

	@Test
	public void testExecute_RemoveOperatorPendingContractorOneOperator() throws Exception {
		mockActionContext();

		OperatorAccount nonCorporate = EntityFactory.makeOperator();

		ContractorAccount contractorAccount = initializeContractor(true);
		contractorAccount.setStatus(AccountStatus.Pending);
		EntityFactory.addContractorOperator(contractorAccount, nonCorporate);

		OperatorAccount operatorAccount = EntityFactory.makeOperator();
		contractorAccount.setRequestedBy(operatorAccount);
		contractorFacilities.setOperator(operatorAccount);

		contractorFacilities.setButton("removeOperator");

		assertEquals(PicsActionSupport.JSON, contractorFacilities.execute());
		assertEquals(nonCorporate, contractorAccount.getRequestedBy());

		verify(entityManager, times(3)).merge(any(ContractorAccount.class));
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

	private void mockActionContext() {
		mockStatic(ActionContext.class);
		when(ActionContext.getContext()).thenReturn(actionContext);
		when(actionContext.getSession()).thenReturn(new HashMap<String, Object>());
	}

	private ContractorAccount initializeContractor() {
		return initializeContractor(false);
	}

	private ContractorAccount initializeContractor(boolean spy) {
		ContractorAccount contractorAccount = EntityFactory.makeContractor();
		int contractorID = contractorAccount.getId();

		if (spy) {
			contractorAccount = spy(contractorAccount);
			doNothing().when(contractorAccount).syncBalance();
		}

		when(entityManager.find(ContractorAccount.class, contractorID)).thenReturn(contractorAccount);
		when(permissions.getAccountId()).thenReturn(contractorID);

		return contractorAccount;
	}
}
