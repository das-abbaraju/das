package com.picsauditing.actions.employees;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.reflect.Whitebox;

import com.opensymphony.xwork2.ActionSupport;
import com.picsauditing.PicsActionTest;
import com.picsauditing.PicsTestUtil;
import com.picsauditing.access.NoRightsException;
import com.picsauditing.access.OpPerms;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.actions.employees.ManageEmployees.EmployeeMissingTasks;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.AssessmentResult;
import com.picsauditing.jpa.entities.AssessmentTest;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.jpa.entities.BaseTable;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.ContractorOperator;
import com.picsauditing.jpa.entities.Employee;
import com.picsauditing.jpa.entities.EmployeeQualification;
import com.picsauditing.jpa.entities.EmployeeRole;
import com.picsauditing.jpa.entities.EmployeeSite;
import com.picsauditing.jpa.entities.Facility;
import com.picsauditing.jpa.entities.JobContractor;
import com.picsauditing.jpa.entities.JobRole;
import com.picsauditing.jpa.entities.JobSite;
import com.picsauditing.jpa.entities.JobSiteTask;
import com.picsauditing.jpa.entities.JobTask;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.jpa.entities.OperatorTag;
import com.picsauditing.jpa.entities.UserStatus;

public class ManageEmployeesTest extends PicsActionTest {
	private ManageEmployees manageEmployees;

	@Mock
	private ContractorAccount contractor;
	@Mock
	private Employee employee;
	@Mock
	private EntityManager entityManager;
	@Mock
	private Query query;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		manageEmployees = new ManageEmployees();
		PicsTestUtil util = new PicsTestUtil();
		util.autowireEMInjectedDAOs(manageEmployees, entityManager);

		super.setUp(manageEmployees);

		setUpEmployeeAndAccount();

		doAnswer(new Answer<Object>() {
			@Override
			public Object answer(InvocationOnMock invocation) throws Throwable {
				return invocation.getArguments()[0];
			}
		}).when(entityManager).persist(any(Employee.class));

		when(entityManager.createQuery(anyString())).thenReturn(query);
	}

	@Test(expected = NoRightsException.class)
	public void testPrepare_ContractorWithoutAdminPermission() throws Exception {
		when(permissions.isContractor()).thenReturn(true);
		when(permissions.hasPermission(OpPerms.ContractorAdmin)).thenReturn(false);

		manageEmployees.prepare();
	}

	@Test
	public void testPrepare_OperatorCorporateWithoutEmployee() throws Exception {
		when(permissions.isOperatorCorporate()).thenReturn(true);
		when(permissions.getAccountId()).thenReturn(123);

		manageEmployees.prepare();

		assertEquals(permissions.getAccountId(), manageEmployees.getId());
	}

	@Test
	public void testPrepare_OperatorCorporateWithEmployeeUnderNonVisibleAccount() throws Exception {
		when(permissions.isOperatorCorporate()).thenReturn(true);
		when(permissions.getAccountId()).thenReturn(123);

		manageEmployees.setEmployee(employee);
		manageEmployees.prepare();

		assertEquals(permissions.getAccountId(), manageEmployees.getId());
	}

	@Test
	public void testPrepare_OperatorCorporateWithEmployeeUnderVisibleAccount() throws Exception {
		Set<Integer> visibleAccounts = new HashSet<Integer>();
		visibleAccounts.add(12345);

		when(contractor.getId()).thenReturn(12345);
		when(permissions.isOperatorCorporate()).thenReturn(true);
		when(permissions.getAccountId()).thenReturn(123);
		when(permissions.getVisibleAccounts()).thenReturn(visibleAccounts);

		manageEmployees.setEmployee(employee);
		manageEmployees.prepare();

		assertEquals(contractor.getId(), manageEmployees.getId());
	}

	@Test
	public void testFindAccount_Employee() {
		manageEmployees.setEmployee(employee);
		manageEmployees.findAccount();

		assertEquals(contractor, manageEmployees.getAccount());
	}

	@Test
	public void testFindAccount_ID() {
		parameters.put("id", new String[] { "123" });

		when(contractor.getId()).thenReturn(123);
		when(entityManager.find(Account.class, 123)).thenReturn(contractor);

		manageEmployees.findAccount();

		assertEquals(contractor, manageEmployees.getAccount());
	}

	@Test
	public void testFindAccount_Permissions() {
		when(contractor.getId()).thenReturn(123);
		when(entityManager.find(Account.class, 123)).thenReturn(contractor);
		when(permissions.getAccountId()).thenReturn(123);
		when(permissions.getAccountType()).thenReturn("Test AccountType");

		assertEquals(contractor, manageEmployees.getAccount());
	}

	@Test
	public void testExecute() throws Exception {
		manageEmployees.setAccount(contractor);

		when(query.getResultList()).thenReturn(null);

		assertEquals(ActionSupport.SUCCESS, manageEmployees.execute());
	}

	@Test
	public void testExecute_Audit() throws Exception {
		ContractorAudit audit = mock(ContractorAudit.class);
		AuditType auditType = mock(AuditType.class);

		when(audit.getAuditType()).thenReturn(auditType);
		when(audit.getContractorAccount()).thenReturn(contractor);
		when(auditType.getId()).thenReturn(AuditType.HSE_COMPETENCY);

		manageEmployees.setAudit(audit);

		assertEquals(ActionSupport.SUCCESS, manageEmployees.execute());
		assertEquals(audit.getContractorAccount(), manageEmployees.getAccount());
	}

	@Test
	public void testExecute_Employee() throws Exception {
		manageEmployees.setEmployee(employee);
		manageEmployees.findAccount();

		assertEquals(ActionSupport.SUCCESS, manageEmployees.execute());
		assertEquals(employee.getAccount(), manageEmployees.getAccount());
	}

	@Test
	public void testAdd() {
		manageEmployees.setAccount(contractor);

		assertEquals(ActionSupport.SUCCESS, manageEmployees.add());
		assertEquals(0, manageEmployees.getEmployee().getId());
	}

	@Test
	public void testSave() throws Exception {
		saveCommonBehaviors(employee);
	}

	@Test
	public void testSave_New() throws Exception {
		saveCommonBehaviors(employee);
	}

	@Test
	public void testSave_SetAccount() throws Exception {
		manageEmployees.setAccount(contractor);
		saveCommonBehaviors(employee);
		assertEquals(contractor, manageEmployees.getEmployee().getAccount());
	}

	@Test
	public void testSave_SSNCheck9Characters() throws Exception {
		manageEmployees.setSsn("999999999");
		saveCommonBehaviors(employee);
		verify(employee).setSsn("999999999");
	}

	@Test
	public void testSave_SSNCheckInvalidFormat() throws Exception {
		manageEmployees.setSsn("Hello1 World9");
		saveCommonBehaviors(employee);
		assertTrue(manageEmployees.hasActionErrors());
		verify(employee, never()).setSsn(anyString());

	}

	@Test
	public void testSave_EmailInvalidFormat() throws Exception {
		when(employee.getEmail()).thenReturn("Hello World");
		saveCommonBehaviors(employee);
		verify(employee).setEmail("info@picsauditing.com");
		verify(employee, never()).setEmail("Hello World");
	}

	@Test
	public void testSave_EmailValidFormat() throws Exception {
		when(employee.getEmail()).thenReturn("lani@test.com");
		saveCommonBehaviors(employee);
		assertEquals("lani@test.com", manageEmployees.getEmployee().getEmail());
	}

	@Test
	public void testInactivate() {
		manageEmployees.setAccount(contractor);

		assertEquals(ActionSupport.SUCCESS, manageEmployees.inactivate());
		assertFalse(manageEmployees.hasActionMessages());

		verify(entityManager, never()).merge(any(Employee.class));
		verify(entityManager, never()).persist(any(Employee.class));
	}

	@Test
	public void testInactivate_Employee() {

		when(query.getResultList()).thenReturn(null);

		manageEmployees.setEmployee(employee);
		manageEmployees.findAccount();

		assertEquals(ActionSupport.SUCCESS, manageEmployees.inactivate());
		assertTrue(manageEmployees.hasActionMessages());

		verify(employee).setStatus(UserStatus.Inactive);
		verify(entityManager).persist(any(Employee.class));
	}

	@Test
	public void testActivate() throws Exception {
		manageEmployees.setAccount(contractor);
		assertEquals(PicsActionSupport.REDIRECT, manageEmployees.activate());
		verify(entityManager, never()).merge(any(BaseTable.class));
		verify(entityManager, never()).persist(any(BaseTable.class));
	}

	@Test
	public void testActivate_Employee() throws Exception {
		manageEmployees.setEmployee(employee);
		manageEmployees.findAccount();

		assertEquals(PicsActionSupport.REDIRECT, manageEmployees.activate());

		verify(entityManager).persist(any(Employee.class));
	}

	@Test
	public void testDelete() {
		manageEmployees.setAccount(contractor);
		assertEquals(ActionSupport.SUCCESS, manageEmployees.delete());
		verify(query).getResultList();
	}

	@Test
	public void testDelete_Employee() {
		manageEmployees.setEmployee(employee);
		manageEmployees.findAccount();

		assertEquals(ActionSupport.SUCCESS, manageEmployees.delete());
		assertNull(manageEmployees.getEmployee());
		assertTrue(manageEmployees.hasActionMessages());

		verify(query, times(2)).getResultList();
		verify(entityManager).persist(any(Employee.class));
	}

	@Test
	public void testGetActiveEmployees() throws Exception {
		when(query.getResultList()).thenReturn(null);
		manageEmployees.setAccount(contractor);
		manageEmployees.findAccount();
		assertNull(manageEmployees.getActiveEmployees());

		List<Employee> employees = new ArrayList<Employee>();
		employees.add(employee);

		when(query.getResultList()).thenReturn(employees);
		manageEmployees.findAccount();
		assertEquals(employees, manageEmployees.getActiveEmployees());
	}

	@Test
	public void testSetActiveEmployees() {
		manageEmployees.setActiveEmployees(null);
		assertNull(manageEmployees.getActiveEmployees());

		List<Employee> employees = new ArrayList<Employee>();

		manageEmployees.setActiveEmployees(employees);
		assertNotNull(manageEmployees.getActiveEmployees());
		assertTrue(manageEmployees.getActiveEmployees().isEmpty());

		employees.add(employee);
		assertFalse(manageEmployees.getActiveEmployees().isEmpty());
	}

	@Test
	public void testLoad() {
		assertEquals("edit", manageEmployees.load());
	}

	@Test
	public void testGetFileName() {
		assertEquals("emp_0", manageEmployees.getFileName(0));
		assertEquals("emp_1", manageEmployees.getFileName(1));
	}

	@Test
	public void testGetToday() {
		Calendar now = Calendar.getInstance();
		Calendar today = Calendar.getInstance();

		today.setTime(manageEmployees.getToday());

		assertEquals(now.get(Calendar.YEAR), today.get(Calendar.YEAR));
		assertEquals(now.get(Calendar.DAY_OF_YEAR), today.get(Calendar.DAY_OF_YEAR));
		assertEquals(now.get(Calendar.HOUR_OF_DAY), today.get(Calendar.HOUR_OF_DAY));
	}

	@Test
	public void testGetExpirationDate() {
		Calendar now = Calendar.getInstance();
		now.add(Calendar.YEAR, 3);

		Calendar expirationDate = Calendar.getInstance();

		expirationDate.setTime(manageEmployees.getExpirationDate());

		assertEquals(now.get(Calendar.YEAR), expirationDate.get(Calendar.YEAR));
	}

	@Test
	public void testGetUnusedJobRoles() {
		JobRole jobRole = mock(JobRole.class);

		List<JobRole> jobRoles = new ArrayList<JobRole>();
		jobRoles.add(jobRole);

		when(contractor.getJobRoles()).thenReturn(jobRoles);
		when(jobRole.getAccount()).thenReturn(contractor);
		when(jobRole.getName()).thenReturn("Job Role");
		when(jobRole.isActive()).thenReturn(true);

		manageEmployees.setEmployee(employee);

		assertFalse(manageEmployees.getUnusedJobRoles().isEmpty());
	}

	@Test
	public void testGetUnusedJobRoles_JobRoleInactive() {
		JobRole jobRole = mock(JobRole.class);

		List<JobRole> jobRoles = new ArrayList<JobRole>();
		jobRoles.add(jobRole);

		when(contractor.getJobRoles()).thenReturn(jobRoles);
		when(jobRole.getAccount()).thenReturn(contractor);
		when(jobRole.getName()).thenReturn("Job Role");

		manageEmployees.setEmployee(employee);

		assertTrue(manageEmployees.getUnusedJobRoles().isEmpty());
	}

	@Test
	public void testGetUnusedJobRoles_EmployeeRoleExists() {
		EmployeeRole employeeRole = mock(EmployeeRole.class);
		JobRole jobRole = mock(JobRole.class);

		List<EmployeeRole> employeeRoles = new ArrayList<EmployeeRole>();
		employeeRoles.add(employeeRole);

		List<JobRole> jobRoles = new ArrayList<JobRole>();
		jobRoles.add(jobRole);

		when(contractor.getJobRoles()).thenReturn(jobRoles);
		when(employee.getEmployeeRoles()).thenReturn(employeeRoles);
		when(employeeRole.getEmployee()).thenReturn(employee);
		when(employeeRole.getJobRole()).thenReturn(jobRole);
		when(jobRole.getAccount()).thenReturn(contractor);
		when(jobRole.getName()).thenReturn("Job Role");
		when(jobRole.isActive()).thenReturn(false);

		manageEmployees.setEmployee(employee);

		assertTrue(manageEmployees.getUnusedJobRoles().isEmpty());
	}

	@Test
	public void testIsShowJobRolesSection() {
		manageEmployees.setEmployee(employee);

		assertFalse(manageEmployees.isShowJobRolesSection());
	}

	@Test
	public void testIsShowJobRolesSection_UnusedJobRole() {
		testGetUnusedJobRoles();

		assertTrue(manageEmployees.isShowJobRolesSection());
	}

	@Test
	public void testIsShowJobRolesSection_EmployeeRole() {
		EmployeeRole employeeRole = mock(EmployeeRole.class);

		List<EmployeeRole> employeeRoles = new ArrayList<EmployeeRole>();
		employeeRoles.add(employeeRole);

		when(employee.getEmployeeRoles()).thenReturn(employeeRoles);
		when(employeeRole.getEmployee()).thenReturn(employee);

		manageEmployees.setEmployee(employee);

		assertTrue(manageEmployees.isShowJobRolesSection());
	}

	@Test
	public void testGetOqOperators() {
		when(contractor.getType()).thenReturn("Account");

		manageEmployees.setEmployee(employee);

		assertNotNull(manageEmployees.getOqOperators());
		assertTrue(manageEmployees.getOqOperators().isEmpty());
	}

	@Test
	public void testGetOqOperators_ContractorHasJobContractor() {
		JobContractor jobContractor = mock(JobContractor.class);
		JobSite jobSite = mock(JobSite.class);
		OperatorAccount operator = setUpOperatorAndContractorOperator(false, false);

		List<JobContractor> jobContractors = new ArrayList<JobContractor>();
		jobContractors.add(jobContractor);

		when(contractor.getJobSites()).thenReturn(jobContractors);
		when(jobContractor.getContractor()).thenReturn(contractor);
		when(jobContractor.getJob()).thenReturn(jobSite);
		when(jobSite.getOperator()).thenReturn(operator);

		manageEmployees.setEmployee(employee);

		assertNotNull(manageEmployees.getOqOperators());
		assertFalse(manageEmployees.getOqOperators().isEmpty());
	}

	@Test
	public void testGetOqOperators_ContractorHasOperatorWithHSEAndOQ() {
		setUpOperatorAndContractorOperator(true, true);

		manageEmployees.setEmployee(employee);

		assertNotNull(manageEmployees.getHseOperators());
		assertNotNull(manageEmployees.getOqOperators());
		assertFalse(manageEmployees.getHseOperators().isEmpty());
		assertFalse(manageEmployees.getOqOperators().isEmpty());
	}

	@Test
	public void testGetOqOperators_Operator() {
		OperatorAccount operator = setUpOperatorAndContractorOperator(false, false);

		when(employee.getAccount()).thenReturn(operator);
		when(operator.isOperator()).thenReturn(true);
		when(operator.isOperatorCorporate()).thenReturn(true);

		manageEmployees.setEmployee(employee);

		assertNotNull(manageEmployees.getOqOperators());
		assertTrue(manageEmployees.getOqOperators().isEmpty());
	}

	@Test
	public void testGetOqOperators_OperatorHasJobSite() {
		OperatorAccount operator = setUpOperatorAndContractorOperator(false, true);

		when(employee.getAccount()).thenReturn(operator);

		manageEmployees.setEmployee(employee);

		assertNotNull(manageEmployees.getOqOperators());
		assertFalse(manageEmployees.getOqOperators().isEmpty());
	}

	@Test
	public void testGetOqOperators_Corporate() {
		OperatorAccount operator = setUpOperatorAndContractorOperator(false, false);

		when(employee.getAccount()).thenReturn(operator);
		when(operator.isCorporate()).thenReturn(true);
		when(operator.isOperatorCorporate()).thenReturn(true);

		manageEmployees.setEmployee(employee);

		assertNotNull(manageEmployees.getOqOperators());
		assertTrue(manageEmployees.getOqOperators().isEmpty());
	}

	@Test
	public void testGetOqOperators_CorporateWithFacilities() {
		Facility facility = mock(Facility.class);
		OperatorAccount corporate = setUpOperatorAndContractorOperator(false, false);
		OperatorAccount operator = setUpOperatorAndContractorOperator(false, true);

		List<Facility> facilities = new ArrayList<Facility>();
		facilities.add(facility);

		when(corporate.getCorporateFacilities()).thenReturn(facilities);
		when(corporate.isCorporate()).thenReturn(true);
		when(employee.getAccount()).thenReturn(corporate);
		when(facility.getCorporate()).thenReturn(corporate);
		when(facility.getOperator()).thenReturn(operator);
		when(operator.isCorporate()).thenReturn(true);

		manageEmployees.setEmployee(employee);

		assertNotNull(manageEmployees.getOqOperators());
		assertFalse(manageEmployees.getOqOperators().isEmpty());
		assertEquals(operator, manageEmployees.getOqOperators().get(0).getOperator());
	}

	@Test
	public void testGetAllOqOperators() {
		manageEmployees.setAccount(contractor);

		assertNotNull(manageEmployees.getAllOqOperators());
		assertTrue(manageEmployees.getAllOqOperators().isEmpty());
	}

	@Test
	public void testGetAllOqOperators_OperatorRequiresOQ() {
		setUpOperatorAndContractorOperator(false, true);

		manageEmployees.setAccount(contractor);

		assertNotNull(manageEmployees.getAllOqOperators());
		assertFalse(manageEmployees.getAllOqOperators().isEmpty());
	}

	@Test
	public void testGetHseOperators() {
		when(contractor.isContractor()).thenReturn(false);

		manageEmployees.setEmployee(employee);

		assertNotNull(manageEmployees.getHseOperators());
		assertTrue(manageEmployees.getHseOperators().isEmpty());
	}

	@Test
	public void testGetHseOperators_ContractorHasNonCorporateOperatorNotRequiringHSE() {
		setUpOperatorAndContractorOperator(false, false);

		manageEmployees.setEmployee(employee);

		assertNotNull(manageEmployees.getHseOperators());
		assertTrue(manageEmployees.getHseOperators().isEmpty());
	}

	@Test
	public void testGetHseOperators_ContractorHasNonCorporateOperatorRequiringHSE() {
		setUpOperatorAndContractorOperator(true, false);

		manageEmployees.setEmployee(employee);

		assertNotNull(manageEmployees.getHseOperators());
		assertFalse(manageEmployees.getHseOperators().isEmpty());
	}

	@Test
	public void testGetHseOperators_Operator() {
		OperatorAccount operator = setUpOperatorAndContractorOperator(true, false);

		when(employee.getAccount()).thenReturn(operator);

		manageEmployees.setEmployee(employee);

		assertNotNull(manageEmployees.getHseOperators());
		assertFalse(manageEmployees.getHseOperators().isEmpty());
	}

	@Test
	public void testGetHseOperators_Corporate() {
		OperatorAccount operator = setUpOperatorAndContractorOperator(true, false);

		when(employee.getAccount()).thenReturn(operator);
		when(operator.isCorporate()).thenReturn(true);

		manageEmployees.setEmployee(employee);

		assertNotNull(manageEmployees.getHseOperators());
		assertFalse(manageEmployees.getHseOperators().isEmpty());
	}

	@Test
	public void testGetHseOperators_CorporateWithFacilites() {
		Facility facility = mock(Facility.class);
		OperatorAccount corporate = setUpOperatorAndContractorOperator(true, false);
		OperatorAccount operator = setUpOperatorAndContractorOperator(true, false);

		List<Facility> facilities = new ArrayList<Facility>();
		facilities.add(facility);

		when(corporate.getCorporateFacilities()).thenReturn(facilities);
		when(corporate.isCorporate()).thenReturn(true);
		when(employee.getAccount()).thenReturn(corporate);
		when(facility.getCorporate()).thenReturn(corporate);
		when(facility.getOperator()).thenReturn(operator);
		when(operator.isOperator()).thenReturn(true);

		manageEmployees.setEmployee(employee);

		assertNotNull(manageEmployees.getHseOperators());
		assertFalse(manageEmployees.getHseOperators().isEmpty());
		assertEquals(2, manageEmployees.getHseOperators().size());
	}

	@Test
	public void testGetEmpPhoto() {
		when(employee.getAccount()).thenReturn(null);

		manageEmployees.setEmployee(employee);

		assertEquals("emp_" + employee.getId() + null, manageEmployees.getEmpPhoto());
	}

	@Test
	public void testGetEmpPhoto_PhotoNotNull() {
		when(employee.getAccount()).thenReturn(null);
		when(employee.getPhoto()).thenReturn("Photo");

		manageEmployees.setEmployee(employee);

		assertEquals("emp_" + employee.getId() + "Photo", manageEmployees.getEmpPhoto());
	}

	@Test
	public void testGetMissingTasks() {
		when(query.getResultList()).thenReturn(new ArrayList<JobSiteTask>());

		manageEmployees.setEmployee(employee);

		EmployeeMissingTasks employeeMissingTasks = manageEmployees.getMissingTasks(1);

		assertNotNull(employeeMissingTasks);
		assertTrue(employeeMissingTasks.getMissingTasks().isEmpty());
		assertTrue(employeeMissingTasks.getQualifiedTasks().isEmpty());
	}

	@Test
	public void testGetMissingTasks_JobSiteTaskMissing() {
		getMissingTasksCommonBehaviors();

		manageEmployees.setEmployee(employee);

		EmployeeMissingTasks employeeMissingTasks = manageEmployees.getMissingTasks(1);

		assertNotNull(employeeMissingTasks);
		assertFalse(employeeMissingTasks.getMissingTasks().isEmpty());
		assertTrue(employeeMissingTasks.getQualifiedTasks().isEmpty());
	}

	@Test
	public void testGetMissingTasks_EmployeeHasQualifiedJobSiteTask() {
		EmployeeQualification qualification = mock(EmployeeQualification.class);
		JobSiteTask jobSiteTask = getMissingTasksCommonBehaviors();

		Set<EmployeeQualification> qualifications = new HashSet<EmployeeQualification>();
		qualifications.add(qualification);

		when(employee.getEmployeeQualifications()).thenReturn(qualifications);
		when(qualification.getTask()).thenReturn(jobSiteTask.getTask());
		when(qualification.isCurrent()).thenReturn(true);
		when(qualification.isQualified()).thenReturn(true);

		manageEmployees.setEmployee(employee);

		EmployeeMissingTasks missingTasks = manageEmployees.getMissingTasks(1);

		assertNotNull(missingTasks);
		assertTrue(missingTasks.getMissingTasks().isEmpty());
		assertFalse(missingTasks.getQualifiedTasks().isEmpty());
	}

	@Test
	public void testGetMissingTasks_EmployeeHasNonQualifiedJobSiteTask() {
		EmployeeQualification qualification = mock(EmployeeQualification.class);
		JobSiteTask jobSiteTask = getMissingTasksCommonBehaviors();

		Set<EmployeeQualification> qualifications = new HashSet<EmployeeQualification>();
		qualifications.add(qualification);

		when(employee.getEmployeeQualifications()).thenReturn(qualifications);
		when(qualification.getTask()).thenReturn(jobSiteTask.getTask());
		when(qualification.isCurrent()).thenReturn(true);

		manageEmployees.setEmployee(employee);

		EmployeeMissingTasks missingTasks = manageEmployees.getMissingTasks(1);

		assertNotNull(missingTasks);
		assertFalse(missingTasks.getMissingTasks().isEmpty());
		assertTrue(missingTasks.getQualifiedTasks().isEmpty());
	}

	@Test
	public void testGetAllJobTasks() {
		assertNull(manageEmployees.getAllJobTasks());

		verify(query, never()).getResultList();
	}

	@Test
	public void testGetAllJobTasks_Employee() {
		manageEmployees.setEmployee(employee);

		when(query.getResultList()).thenReturn(new ArrayList<JobSiteTask>());

		assertNotNull(manageEmployees.getAllJobTasks());

		verify(query).getResultList();
	}

	@Test
	public void testGetAllJobTasks_EmployeeTasks() {
		manageEmployees.setEmployee(employee);

		JobSiteTask jobSiteTask = mock(JobSiteTask.class);
		JobTask jobTask = mock(JobTask.class);

		when(jobSiteTask.getTask()).thenReturn(jobTask);

		ArrayList<JobSiteTask> jobSiteTasks = new ArrayList<JobSiteTask>();
		jobSiteTasks.add(jobSiteTask);

		when(query.getResultList()).thenReturn(jobSiteTasks);

		assertNotNull(manageEmployees.getAllJobTasks());

		verify(query).getResultList();
	}

	@Test
	public void testGetNccerResults() {
		assertNull(manageEmployees.getNccerResults());
	}

	@Test
	public void testGetNccerResults_Employee() {
		manageEmployees.setEmployee(employee);

		assertNotNull(manageEmployees.getNccerResults());
		assertTrue(manageEmployees.getNccerResults().isEmpty());
	}

	@Test
	public void testGetNccerResults_EmployeeHasValidAssessmentResults() {
		Account center = mock(Account.class);
		AssessmentResult result = mock(AssessmentResult.class);
		AssessmentTest test = mock(AssessmentTest.class);

		List<AssessmentResult> results = new ArrayList<AssessmentResult>();
		results.add(result);

		when(center.getId()).thenReturn(Account.ASSESSMENT_NCCER);
		when(employee.getAssessmentResults()).thenReturn(results);
		when(result.getAssessmentTest()).thenReturn(test);
		when(result.isCurrent()).thenReturn(true);
		when(test.getAssessmentCenter()).thenReturn(center);
		when(test.getQualificationMethod()).thenReturn("Qualification Method");

		manageEmployees.setEmployee(employee);

		assertNotNull(manageEmployees.getNccerResults());
		assertFalse(manageEmployees.getNccerResults().isEmpty());
	}

	@Test
	public void testAddInitialSites_Clients() throws Exception {
		manageEmployees.setEmployee(employee);
		manageEmployees.setInitialClients(new int[] { 1 });
		Whitebox.invokeMethod(manageEmployees, "addInitialSites");
		verify(entityManager).persist(any(EmployeeSite.class));
	}

	@Test
	public void testAddInitialSites_JobSite() throws Exception {
		JobSite jobSite = mock(JobSite.class);

		List<JobSite> jobSites = new ArrayList<JobSite>();
		jobSites.add(jobSite);

		when(query.getResultList()).thenReturn(jobSites);

		manageEmployees.setEmployee(employee);
		manageEmployees.setInitialJobSites(new int[] { 1 });

		Whitebox.invokeMethod(manageEmployees, "addInitialSites");
		verify(entityManager).persist(any(EmployeeSite.class));
	}

	@Test
	public void testIsHseOperator() throws Exception {
		EmployeeSite employeeSite = mock(EmployeeSite.class);
		OperatorAccount operator = setUpOperatorAndContractorOperator(false, false);

		assertFalse(manageEmployees.isHseOperator(operator));
		// Missing tag
		when(operator.isRequiresCompetencyReview()).thenReturn(true);
		assertFalse(manageEmployees.isHseOperator(operator));

		operator = setUpOperatorAndContractorOperator(true, false);
		assertTrue(manageEmployees.isHseOperator(operator));

		assertFalse(manageEmployees.isHseOperator(employeeSite));
		// Missing HSE Operator
		when(employeeSite.isCurrent()).thenReturn(true);
		assertFalse(manageEmployees.isHseOperator(employeeSite));

		when(employeeSite.getOperator()).thenReturn(operator);
		assertTrue(manageEmployees.isHseOperator(employeeSite));

	}

	@Test
	public void testOperatorHasHSECompetencyTag() throws Exception {
		OperatorAccount operator = setUpOperatorAndContractorOperator(false, true);

		Boolean hasHseTag = (Boolean) Whitebox.invokeMethod(manageEmployees, "operatorHasHSECompetencyTag",
				(OperatorAccount) null);
		assertFalse(hasHseTag);

		hasHseTag = (Boolean) Whitebox.invokeMethod(manageEmployees, "operatorHasHSECompetencyTag", operator);
		assertFalse(hasHseTag);

		operator = setUpOperatorAndContractorOperator(true, false);
		hasHseTag = (Boolean) Whitebox.invokeMethod(manageEmployees, "operatorHasHSECompetencyTag", operator);
		assertTrue(hasHseTag);
	}

	private void setUpEmployeeAndAccount() {
		when(contractor.isContractor()).thenReturn(true);
		when(contractor.getType()).thenReturn("Contractor");
		when(employee.getAccount()).thenReturn(contractor);
		when(employee.getDisplayName()).thenReturn("Unit Tester");
		when(employee.getFirstName()).thenReturn("Unit");
		when(employee.getId()).thenReturn(0);
		when(employee.getLastName()).thenReturn("Tester");
		when(employee.getStatus()).thenReturn(UserStatus.Active);
		when(employee.getTitle()).thenReturn("Title");
	}

	private OperatorAccount setUpOperatorAndContractorOperator(boolean enableHse, boolean enableOQ) {
		ContractorOperator contractorOperator = mock(ContractorOperator.class);
		OperatorAccount operator = mock(OperatorAccount.class);

		List<ContractorOperator> nonCorporate = new ArrayList<ContractorOperator>();
		nonCorporate.add(contractorOperator);

		when(contractor.getNonCorporateOperators()).thenReturn(nonCorporate);
		when(contractor.getOperators()).thenReturn(nonCorporate);
		when(contractorOperator.getOperatorAccount()).thenReturn(operator);
		when(operator.isOperatorCorporate()).thenReturn(true);

		if (enableHse) {
			OperatorTag tag = mock(OperatorTag.class);

			List<OperatorTag> tags = new ArrayList<OperatorTag>();
			tags.add(tag);

			when(contractor.isRequiresCompetencyReview()).thenReturn(true);
			when(operator.getTags()).thenReturn(tags);
			when(operator.isRequiresCompetencyReview()).thenReturn(true);
			when(tag.getTag()).thenReturn(OperatorTag.HSE_COMPETENCY);
		}

		if (enableOQ) {
			JobSite jobSite = mock(JobSite.class);

			List<JobSite> jobSites = new ArrayList<JobSite>();
			jobSites.add(jobSite);

			when(contractor.isRequiresOQ()).thenReturn(true);
			when(jobSite.getOperator()).thenReturn(operator);
			when(operator.getJobSites()).thenReturn(jobSites);
			when(operator.isRequiresOQ()).thenReturn(true);
		}

		return operator;
	}

	private void saveCommonBehaviors(Employee employee) throws Exception {
		manageEmployees.setEmployee(employee);
		manageEmployees.findAccount();

		assertEquals(PicsActionSupport.REDIRECT, manageEmployees.save());

		verify(entityManager, atLeastOnce()).persist(any(BaseTable.class));
	}

	private JobSiteTask getMissingTasksCommonBehaviors() {
		JobSiteTask jobSiteTask = new JobSiteTask();
		jobSiteTask.setTask(new JobTask());

		List<JobSiteTask> jobSiteTasks = new ArrayList<JobSiteTask>();
		jobSiteTasks.add(jobSiteTask);

		when(query.getResultList()).thenReturn(jobSiteTasks);

		return jobSiteTask;
	}
}
