package com.picsauditing.actions.contractors;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import javax.persistence.EntityManager;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import com.picsauditing.EntityFactory;
import com.picsauditing.PicsTestUtil;
import com.picsauditing.PICS.I18nCache;
import com.picsauditing.access.NoRightsException;
import com.picsauditing.access.Permissions;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.jpa.entities.AccountStatus;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorOperator;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.mail.EmailBuilder;
import com.picsauditing.search.Database;

public class RequestNewContractorAccountTest {
	private RequestNewContractorAccount requestNewContractorAccount;
	private PicsTestUtil picsTestUtil;

	private ContractorAccount contractor;

	@Mock
	private ContractorOperator relationship;
	@Mock
	private Database database;
	@Mock
	private EmailBuilder emailBuilder;
	@Mock
	private EntityManager entityManager;
	@Mock
	private Permissions permissions;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		Whitebox.setInternalState(I18nCache.class, "databaseForTesting", database);

		requestNewContractorAccount = new RequestNewContractorAccount();
		picsTestUtil = new PicsTestUtil();
		picsTestUtil.autowireEMInjectedDAOs(requestNewContractorAccount, entityManager);

		contractor = EntityFactory.makeContractor();

		Whitebox.setInternalState(requestNewContractorAccount, "emailBuilder", emailBuilder);
		Whitebox.setInternalState(requestNewContractorAccount, "permissions", permissions);
	}

	@AfterClass
	public static void classTearDown() {
		Whitebox.setInternalState(I18nCache.class, "databaseForTesting", (Database) null);
	}

	// If we're logged in as the operator, find the my relationship with
	// this request

	// If I'm a corporate, find all the relationships this contractor has
	// with my children and give me a table to add/remove relationships

	// If I'm a PICS employee, find all the relationships and list them in a
	// table with add/remove abilities

	// load tags
	// TODO How do we show tags for corporate and PICS users?

	@Test(expected = NoRightsException.class)
	public void testExecute_Contractor() throws Exception {
		when(permissions.isContractor()).thenReturn(true);

		requestNewContractorAccount.execute();
	}

	@Test
	public void testExecute_PicsEmployee() throws Exception {
		when(permissions.isPicsEmployee()).thenReturn(true);

		assertEquals(PicsActionSupport.SUCCESS, requestNewContractorAccount.execute());
	}

	@Test
	public void testExecute_OperatorCorporate() throws Exception {
		when(permissions.isOperatorCorporate()).thenReturn(true);

		assertEquals(PicsActionSupport.SUCCESS, requestNewContractorAccount.execute());
	}

	@Test
	public void testExecute_DefaultStatus() throws Exception {
		when(permissions.isPicsEmployee()).thenReturn(true);

		assertEquals(PicsActionSupport.SUCCESS, requestNewContractorAccount.execute());
		assertEquals(AccountStatus.Requested, requestNewContractorAccount.getRequestedContractor().getStatus());
	}

	@Test
	public void testExecute_ExistingStatus() throws Exception {
		contractor.setStatus(AccountStatus.Deactivated);

		when(permissions.isPicsEmployee()).thenReturn(true);

		requestNewContractorAccount.setRequestedContractor(contractor);

		assertEquals(PicsActionSupport.SUCCESS, requestNewContractorAccount.execute());
		assertEquals(AccountStatus.Deactivated, requestNewContractorAccount.getRequestedContractor().getStatus());
	}

	@Test
	public void testExecute_OperatorExistingRequest() throws Exception {
		OperatorAccount operator = EntityFactory.makeOperator();
		OperatorAccount otherOperator = EntityFactory.makeOperator();

		contractor.setRequestedBy(otherOperator);
		contractor.setStatus(AccountStatus.Requested);

		when(entityManager.find(OperatorAccount.class, operator.getId())).thenReturn(operator);
		when(permissions.getAccountId()).thenReturn(operator.getId());
		when(permissions.isOperatorCorporate()).thenReturn(true);

		requestNewContractorAccount.setRequestedContractor(contractor);

		assertEquals(PicsActionSupport.SUCCESS, requestNewContractorAccount.execute());
		assertEquals(operator, requestNewContractorAccount.getRequestRelationship().getOperatorAccount());
		assertEquals(otherOperator, requestNewContractorAccount.getRequestedContractor().getRequestedBy());
	}

	@Test
	public void testExecute_FindOperatorRelationship() throws Exception {
		OperatorAccount operator = EntityFactory.makeOperator();
		ContractorOperator contractorOperator = EntityFactory.addContractorOperator(contractor, operator);

		when(permissions.getAccountId()).thenReturn(operator.getId());
		when(permissions.isOperator()).thenReturn(true);
		when(permissions.isOperatorCorporate()).thenReturn(true);

		requestNewContractorAccount.setRequestedContractor(contractor);

		assertEquals(PicsActionSupport.SUCCESS, requestNewContractorAccount.execute());
		assertEquals(contractorOperator, requestNewContractorAccount.getRequestRelationship());
	}
}