package com.picsauditing.actions.contractors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.Set;

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
import com.picsauditing.jpa.entities.BaseTable;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorOperator;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.jpa.entities.OperatorForm;
import com.picsauditing.mail.EmailBuilder;
import com.picsauditing.search.Database;

public class RequestNewContractorAccountTest {
	private RequestNewContractorAccount requestNewContractorAccount;
	private PicsTestUtil picsTestUtil;

	@Mock
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

		Whitebox.setInternalState(requestNewContractorAccount, "emailBuilder", emailBuilder);
		Whitebox.setInternalState(requestNewContractorAccount, "permissions", permissions);
	}

	@AfterClass
	public static void classTearDown() {
		Whitebox.setInternalState(I18nCache.class, "databaseForTesting", (Database) null);
	}

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
	public void testExecute_DefaultStatus() throws Exception {
		when(permissions.isPicsEmployee()).thenReturn(true);

		assertEquals(PicsActionSupport.SUCCESS, requestNewContractorAccount.execute());
		assertEquals(AccountStatus.Requested, requestNewContractorAccount.getRequestedContractor().getStatus());
	}

	@Test
	public void testExecute_ExistingStatus() throws Exception {
		when(permissions.isPicsEmployee()).thenReturn(true);

		ContractorAccount contractor = EntityFactory.makeContractor();
		contractor.setStatus(AccountStatus.Deactivated);

		requestNewContractorAccount.setRequestedContractor(contractor);

		assertEquals(PicsActionSupport.SUCCESS, requestNewContractorAccount.execute());
		assertEquals(AccountStatus.Deactivated, requestNewContractorAccount.getRequestedContractor().getStatus());
	}

	@Test
	public void testExecute_OperatorNewRequest() throws Exception {
		OperatorAccount operator = EntityFactory.makeOperator();

		when(entityManager.find(OperatorAccount.class, operator.getId())).thenReturn(operator);
		when(permissions.getAccountId()).thenReturn(operator.getId());
		when(permissions.isOperatorCorporate()).thenReturn(true);

		assertEquals(PicsActionSupport.SUCCESS, requestNewContractorAccount.execute());
		assertEquals(operator, requestNewContractorAccount.getRequestRelationship().getOperatorAccount());
	}

	@Test(expected = NoRightsException.class)
	public void testExecute_OperatorExistingRequestNotVisibleAccount() throws Exception {
		ContractorAccount contractor = EntityFactory.makeContractor();
		OperatorAccount operator = EntityFactory.makeOperator();
		OperatorAccount anotherOperator = EntityFactory.makeOperator();
		Set<Integer> visibleAccounts = Collections.emptySet();

		contractor.setRequestedBy(operator);

		when(entityManager.find(OperatorAccount.class, operator.getId())).thenReturn(operator);
		when(permissions.getAccountId()).thenReturn(anotherOperator.getId());
		when(permissions.getVisibleAccounts()).thenReturn(visibleAccounts);
		when(permissions.isOperator()).thenReturn(true);
		when(permissions.isOperatorCorporate()).thenReturn(true);

		requestNewContractorAccount.setRequestedContractor(contractor);
		requestNewContractorAccount.execute();
	}

	@Test
	public void testSave_MissingContractorSpecificFields() throws Exception {
		assertEquals(PicsActionSupport.SUCCESS, requestNewContractorAccount.save());
		assertTrue(requestNewContractorAccount.hasActionErrors());

		verify(entityManager, never()).merge(any(BaseTable.class));
		verify(entityManager, never()).persist(any(BaseTable.class));
	}

	@Test
	public void testSave_MissingOperatorSpecificFields() throws Exception {
		// TODO: Set contractor fields here
		assertEquals(PicsActionSupport.SUCCESS, requestNewContractorAccount.save());
		assertTrue(requestNewContractorAccount.hasActionErrors());

		verify(entityManager, never()).merge(any(BaseTable.class));
		verify(entityManager, never()).persist(any(BaseTable.class));
	}

	@Test
	public void testGetEmailPreview() throws Exception {
		verify(emailBuilder).build();
	}

	@Test
	public void testGetContractorLetter() throws Exception {
		assertNull(Whitebox.invokeMethod(requestNewContractorAccount, "getContractorLetter"));
	}

	@Test
	public void testGetContractorLetter_OperatorSet() throws Exception {
		OperatorAccount operator = EntityFactory.makeOperator();
		OperatorForm form = new OperatorForm();
		form.setFormName("* Contractor Letter");
		operator.getOperatorForms().add(form);

		ContractorAccount contractor = EntityFactory.makeContractor();
		contractor.setRequestedBy(operator);

		requestNewContractorAccount.setRequestedContractor(contractor);

		OperatorForm contractorLetter = Whitebox.invokeMethod(requestNewContractorAccount, "getContractorLetter");
		assertNotNull(contractorLetter);
		assertEquals(form, contractorLetter);
	}
}
