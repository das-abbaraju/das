package com.picsauditing.actions.employees;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.Query;

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
import com.picsauditing.access.OpPerms;
import com.picsauditing.access.Permissions;
import com.picsauditing.access.RecordNotFoundException;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.jpa.entities.BaseTable;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.EmployeeRole;
import com.picsauditing.jpa.entities.JobCompetency;
import com.picsauditing.jpa.entities.JobRole;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.jpa.entities.OperatorCompetency;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.search.Database;

public class ManageJobRolesTest {
	private ManageJobRoles manageJobRoles;

	@Mock
	private Database database;
	@Mock
	private EntityManager entityManager;
	@Mock
	private Permissions permissions;
	@Mock
	private Query query;

	private final String ROLE = "role";
	private final String COMPETENCIES = "competencies";

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		Whitebox.setInternalState(I18nCache.class, "databaseForTesting", database);

		manageJobRoles = new ManageJobRoles();
		PicsTestUtil testUtil = new PicsTestUtil();
		testUtil.autowireEMInjectedDAOs(manageJobRoles, entityManager);

		Whitebox.setInternalState(manageJobRoles, "permissions", permissions);

		when(entityManager.createQuery(anyString())).thenReturn(query);
	}

	@AfterClass
	public static void classTearDown() {
		Whitebox.setInternalState(I18nCache.class, "databaseForTesting", (Database) null);
	}

	@Test
	public void testExecute() throws Exception {
		setLoggedInAsContractorAdmin();

		assertEquals(PicsActionSupport.SUCCESS, manageJobRoles.execute());
	}

	@Test(expected = RecordNotFoundException.class)
	public void testFindAccount_Null() throws Exception {
		Whitebox.invokeMethod(manageJobRoles, "findAccount");
	}

	@Test
	public void testFindAccount_Audit() throws Exception {
		ContractorAccount contractor = EntityFactory.makeContractor();
		ContractorAudit audit = EntityFactory.makeContractorAudit(AuditType.HSE_COMPETENCY, contractor);

		manageJobRoles.setAudit(audit);
		Whitebox.invokeMethod(manageJobRoles, "findAccount");

		assertEquals(contractor, manageJobRoles.getAccount());
	}

	@Test
	public void testFindAccount_Role() throws Exception {
		ContractorAccount contractor = EntityFactory.makeContractor();
		JobRole role = new JobRole();
		role.setAccount(contractor);

		manageJobRoles.setRole(role);
		Whitebox.invokeMethod(manageJobRoles, "findAccount");

		assertEquals(contractor, manageJobRoles.getAccount());
	}

	@Test
	public void testFindAccount_Contractor() throws Exception {
		ContractorAccount contractor = setLoggedInAsContractorAdmin();

		Whitebox.invokeMethod(manageJobRoles, "findAccount");

		assertEquals(contractor, manageJobRoles.getAccount());

		verify(entityManager).find(Account.class, contractor.getId());
	}

	@Test
	public void testFindAccount_ID() throws Exception {
		ContractorAccount contractor = EntityFactory.makeContractor();

		when(entityManager.find(Account.class, contractor.getId())).thenReturn(contractor);

		manageJobRoles.setId(contractor.getId());
		Whitebox.invokeMethod(manageJobRoles, "findAccount");

		assertEquals(contractor, manageJobRoles.getAccount());

		verify(entityManager).find(Account.class, contractor.getId());
	}

	@Test
	public void testGet_NoParameters() throws Exception {
		setLoggedInAsContractorAdmin();

		assertEquals(ROLE, manageJobRoles.get());
		assertTrue(manageJobRoles.getRole().getId() == 0);
	}

	@Test
	public void testGet_Role() throws Exception {
		setLoggedInAsContractorAdmin();

		JobRole role = new JobRole();
		role.setId(1);
		role.setName("Role");

		manageJobRoles.setRole(role);

		assertEquals(ROLE, manageJobRoles.get());
		assertEquals(role, manageJobRoles.getRole());
	}

	@Test
	public void testSave_RoleMissingName() throws Exception {
		setLoggedInAsContractorAdmin();

		JobRole role = new JobRole();
		manageJobRoles.setRole(role);

		assertEquals(PicsActionSupport.SUCCESS, manageJobRoles.save());
		assertTrue(manageJobRoles.hasActionErrors());

		verify(entityManager, never()).merge(any(BaseTable.class));
		verify(entityManager, never()).persist(any(BaseTable.class));
	}

	@Test
	public void testSave_RoleMissingAccount() throws Exception {
		ContractorAccount contractor = setLoggedInAsContractorAdmin();

		JobRole role = new JobRole();
		role.setName("Role");

		manageJobRoles.setAccount(contractor);
		manageJobRoles.setRole(role);

		assertEquals(PicsActionSupport.REDIRECT, manageJobRoles.save());
		assertFalse(manageJobRoles.hasActionErrors());

		verify(entityManager, never()).merge(any(BaseTable.class));
		verify(entityManager).persist(any(BaseTable.class));
	}

	@Test
	public void testDelete_Unused() throws Exception {
		ContractorAccount contractor = setLoggedInAsContractorAdmin();

		when(entityManager.createQuery(anyString())).thenReturn(query);
		when(query.getResultList()).thenReturn(Collections.emptyList());

		JobRole role = new JobRole();

		manageJobRoles.setAccount(contractor);
		manageJobRoles.setRole(role);

		assertEquals(PicsActionSupport.REDIRECT, manageJobRoles.delete());

		verify(entityManager).remove(any(BaseTable.class));
	}

	@Test
	public void testDelete_Used() throws Exception {
		ContractorAccount contractor = setLoggedInAsContractorAdmin();

		List<EmployeeRole> employeeRoles = new ArrayList<EmployeeRole>();
		employeeRoles.add(new EmployeeRole());

		when(query.getResultList()).thenReturn(employeeRoles);

		JobRole role = new JobRole();
		role.setActive(true);

		manageJobRoles.setAccount(contractor);
		manageJobRoles.setRole(role);

		assertEquals(PicsActionSupport.REDIRECT, manageJobRoles.delete());
		assertFalse(manageJobRoles.getRole().isActive());

		verify(entityManager, never()).remove(any(BaseTable.class));
	}

	@Test
	public void testGetUrlOptions() throws Exception {
		ContractorAccount contractor = setLoggedInAsContractorAdmin();
		ContractorAudit audit = EntityFactory.makeContractorAudit(AuditType.HSE_COMPETENCY, contractor);

		manageJobRoles.setAccount(contractor);
		assertEquals("account=" + contractor.getId(), Whitebox.invokeMethod(manageJobRoles, "getUrlOptions"));

		manageJobRoles.setAudit(audit);
		assertEquals("audit=" + audit.getId(), Whitebox.invokeMethod(manageJobRoles, "getUrlOptions"));

		manageJobRoles.setQuestionId(1);
		assertEquals(String.format("audit=%d&questionId=%d", audit.getId(), 1),
				Whitebox.invokeMethod(manageJobRoles, "getUrlOptions"));
	}

	@Test
	public void testAddCompetency_CompetencyMissing() throws Exception {
		setLoggedInAsContractorAdmin();

		assertEquals(COMPETENCIES, manageJobRoles.addCompetency());
		assertTrue(manageJobRoles.hasActionErrors());

		verify(entityManager, never()).merge(any(BaseTable.class));
		verify(entityManager, never()).persist(any(BaseTable.class));
	}

	@Test
	public void testAddCompetency_CompetencyExisting() throws Exception {
		setLoggedInAsContractorAdmin();

		OperatorCompetency competency = new OperatorCompetency();

		JobCompetency jobCompetency = new JobCompetency();
		jobCompetency.setId(1);
		jobCompetency.setCompetency(competency);

		JobRole role = new JobRole();
		role.setId(1);

		role.getJobCompetencies().add(jobCompetency);

		manageJobRoles.setCompetency(competency);
		manageJobRoles.setRole(role);

		assertEquals(COMPETENCIES, manageJobRoles.addCompetency());
		assertTrue(manageJobRoles.hasActionErrors());

		verify(entityManager, never()).merge(any(BaseTable.class));
		verify(entityManager, never()).persist(any(BaseTable.class));
	}

	@Test
	public void testAddCompetency_CompetencyNew() throws Exception {
		setLoggedInAsContractorAdmin();

		OperatorCompetency competency = new OperatorCompetency();

		JobRole role = new JobRole();
		role.setId(1);

		manageJobRoles.setCompetency(competency);
		manageJobRoles.setRole(role);

		assertEquals(COMPETENCIES, manageJobRoles.addCompetency());
		assertFalse(manageJobRoles.hasActionErrors());

		verify(entityManager).merge(any(BaseTable.class));
		verify(entityManager).persist(any(BaseTable.class));
	}

	@Test
	public void testRemoveCompetency_CompetencyMissing() throws Exception {
		setLoggedInAsContractorAdmin();

		assertEquals(COMPETENCIES, manageJobRoles.removeCompetency());
		assertTrue(manageJobRoles.hasActionErrors());

		verify(entityManager, never()).remove(any(BaseTable.class));
	}

	@Test
	public void testRemoveCompetency_CompetencyExisting() throws Exception {
		setLoggedInAsContractorAdmin();

		OperatorCompetency competency = new OperatorCompetency();

		JobCompetency jobCompetency = new JobCompetency();
		jobCompetency.setId(1);
		jobCompetency.setCompetency(competency);

		JobRole role = new JobRole();
		role.setId(1);

		role.getJobCompetencies().add(jobCompetency);

		manageJobRoles.setCompetency(competency);
		manageJobRoles.setRole(role);

		assertEquals(COMPETENCIES, manageJobRoles.removeCompetency());
		assertFalse(manageJobRoles.hasActionErrors());
		assertTrue(manageJobRoles.getRole().getJobCompetencies().isEmpty());

		verify(entityManager).remove(any(BaseTable.class));
	}

	@Test
	public void testGetJobRoles_Lookup() {
		ContractorAccount contractor = setLoggedInAsContractorAdmin();

		when(query.getResultList()).thenReturn(Collections.emptyList());

		manageJobRoles.setAccount(contractor);

		assertNotNull(manageJobRoles.getJobRoles());
	}

	@Test
	public void testGetJobRoles_Existing() {
		Whitebox.setInternalState(manageJobRoles, "jobRoles", Collections.emptyList());

		assertNotNull(manageJobRoles.getJobRoles());

		verify(query, never()).getResultList();
	}

	@Test
	public void testGetUsedCount() {
		JobRole role = new JobRole();

		List<JobRole> roles = new ArrayList<JobRole>();
		roles.add(role);
		roles.add(role);

		when(query.getResultList()).thenReturn(roles);

		assertEquals(2, manageJobRoles.getUsedCount(role));
	}

	@Test(expected = NoRightsException.class)
	public void testCheckPermissions_ContractorWithoutPermissions() throws Exception {
		when(permissions.isContractor()).thenReturn(true);

		Whitebox.invokeMethod(manageJobRoles, "checkPermissions");
	}

	@Test(expected = NoRightsException.class)
	public void testCheckPermissions_OperatorWithoutDefineRoles() throws Exception {
		Permissions permissions = new Permissions();
		permissions.login(EntityFactory.makeUser(OperatorAccount.class));

		Whitebox.setInternalState(manageJobRoles, "permissions", permissions);

		Whitebox.invokeMethod(manageJobRoles, "checkPermissions");
	}

	@Test(expected = NoRightsException.class)
	public void testCheckPermissions_OperatorWithoutAllOperators() throws Exception {
		Permissions permissions = new Permissions();
		OperatorAccount operator = EntityFactory.makeOperator();
		User user = EntityFactory.makeUser(OperatorAccount.class);
		user.addOwnedPermissions(OpPerms.DefineRoles, 1);

		permissions.login(user);

		Whitebox.setInternalState(manageJobRoles, "permissions", permissions);

		manageJobRoles.setAccount(operator);
		Whitebox.invokeMethod(manageJobRoles, "checkPermissions");
	}

	@Test
	public void testGetOtherCompetencies_RoleMissing() throws Exception {
		setLoggedInAsContractorAdmin();

		assertNull(manageJobRoles.getOtherCompetencies());
	}

	@Test
	public void testGetOtherCompetencies_CompetencyExists() throws Exception {
		setLoggedInAsContractorAdmin();

		OperatorCompetency competency = new OperatorCompetency();

		JobCompetency jobCompetency = new JobCompetency();
		jobCompetency.setCompetency(competency);

		JobRole role = new JobRole();
		role.getJobCompetencies().add(jobCompetency);

		manageJobRoles.setRole(role);

		assertNotNull(manageJobRoles.getOtherCompetencies());
		assertTrue(manageJobRoles.getOtherCompetencies().isEmpty());
	}

	@Test
	public void testOperatorIDs_NoOperators() throws Exception {
		ContractorAccount contractor = EntityFactory.makeContractor();

		Set<Integer> operatorIDs = Whitebox.invokeMethod(manageJobRoles, "operatorIDs", contractor);

		assertNotNull(operatorIDs);
		assertTrue(operatorIDs.isEmpty());
	}

	@Test
	public void testOperatorIDs_HasOperators() throws Exception {
		ContractorAccount contractor = EntityFactory.makeContractor();
		OperatorAccount operator = EntityFactory.makeOperator();
		EntityFactory.addContractorOperator(contractor, operator);

		Set<Integer> operatorIDs = Whitebox.invokeMethod(manageJobRoles, "operatorIDs", contractor);

		assertNotNull(operatorIDs);
		assertFalse(operatorIDs.isEmpty());
		assertTrue(operatorIDs.contains(operator.getId()));
	}

	@Test
	public void testGetShellOps_NoOperators() throws Exception {
		ContractorAccount contractor = EntityFactory.makeContractor();

		manageJobRoles.setAccount(contractor);

		List<OperatorAccount> hseOperators = Whitebox.invokeMethod(manageJobRoles, "getShellOps");

		assertNotNull(hseOperators);
		assertTrue(hseOperators.isEmpty());
	}

	@Test
	public void testGetShellOps_HasOperators() throws Exception {
		ContractorAccount contractor = EntityFactory.makeContractor();

		OperatorAccount operator = EntityFactory.makeOperator();
		operator.setRequiresCompetencyReview(true);

		EntityFactory.addContractorOperator(contractor, operator);

		manageJobRoles.setAccount(contractor);

		List<OperatorAccount> hseOperators = Whitebox.invokeMethod(manageJobRoles, "getShellOps");

		assertNotNull(hseOperators);
		assertFalse(hseOperators.isEmpty());
		assertTrue(hseOperators.contains(operator));
	}

	private ContractorAccount setLoggedInAsContractorAdmin() {
		ContractorAccount contractor = EntityFactory.makeContractor();

		when(entityManager.find(Account.class, contractor.getId())).thenReturn(contractor);
		when(permissions.getAccountId()).thenReturn(contractor.getId());
		when(permissions.isContractor()).thenReturn(true);
		when(permissions.hasPermission(OpPerms.ContractorAdmin)).thenReturn(true);

		return contractor;
	}
}
