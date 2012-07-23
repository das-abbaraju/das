package com.picsauditing.actions.employees;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.persistence.EntityManager;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import com.picsauditing.EntityFactory;
import com.picsauditing.PicsTestUtil;
import com.picsauditing.PICS.I18nCache;
import com.picsauditing.access.NoRightsException;
import com.picsauditing.access.OpPerms;
import com.picsauditing.access.Permissions;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.actions.TranslationActionSupport;
import com.picsauditing.actions.employees.ManageEmployees.EmployeeMissingTasks;
import com.picsauditing.dao.EmployeeDAO;
import com.picsauditing.dao.JobSiteTaskDAO;
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
import com.picsauditing.jpa.entities.Note;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.jpa.entities.UserStatus;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ ManageEmployees.class, ActionContext.class, I18nCache.class, TranslationActionSupport.class })
@PowerMockIgnore({ "javax.xml.parsers.*", "ch.qos.logback.*", "org.slf4j.*", "org.apache.xerces.*" })
public class ManageEmployeesTest {
	private ManageEmployees manageEmployees;

	@Mock
	private ActionContext actionContext;
	@Mock
	private EmployeeDAO employeeDAO;
	@Mock
	private EntityManager entityManager;
	@Mock
	private I18nCache i18nCache;
	@Mock
	private JobSiteTaskDAO jobSiteTaskDAO;
	@Mock
	private Permissions permissions;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		PowerMockito.mockStatic(I18nCache.class);

		manageEmployees = new ManageEmployees();

		PicsTestUtil picsTestUtil = new PicsTestUtil();
		picsTestUtil.autowireEMInjectedDAOs(manageEmployees, entityManager);

		Whitebox.setInternalState(manageEmployees, "employeeDAO", employeeDAO);
		Whitebox.setInternalState(manageEmployees, "permissions", permissions);
	}

	@Test(expected = NoRightsException.class)
	public void testPrepare_ContractorWithoutAdminPermission() throws Exception {
		PowerMockito.mockStatic(ActionContext.class);
		PowerMockito.mockStatic(TranslationActionSupport.class);

		when(I18nCache.getInstance()).thenReturn(i18nCache);
		when(TranslationActionSupport.getLocaleStatic()).thenReturn(Locale.ENGLISH);
		when(i18nCache.getText(anyString(), any(Locale.class), any())).thenReturn("Text");

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
		Employee employee = EntityFactory.makeEmployee(null);

		manageEmployees.setEmployee(employee);

		when(permissions.isOperatorCorporate()).thenReturn(true);
		when(permissions.getAccountId()).thenReturn(123);

		manageEmployees.prepare();

		assertEquals(permissions.getAccountId(), manageEmployees.getId());
	}

	@Test
	public void testPrepare_OperatorCorporateWithEmployeeUnderVisibleAccount() throws Exception {
		Set<Integer> visibleAccounts = new HashSet<Integer>();
		visibleAccounts.add(12345);

		Account account = new Account();
		account.setId(12345);

		Employee employee = EntityFactory.makeEmployee(account);

		manageEmployees.setEmployee(employee);

		when(permissions.isOperatorCorporate()).thenReturn(true);
		when(permissions.getAccountId()).thenReturn(123);
		when(permissions.getVisibleAccounts()).thenReturn(visibleAccounts);

		manageEmployees.prepare();

		assertEquals(account.getId(), manageEmployees.getId());
	}

	@Test
	public void testFindAccount_Audit() {
		ContractorAudit audit = EntityFactory.makeContractorAudit(AuditType.HSE_COMPETENCY,
				EntityFactory.makeContractor());

		manageEmployees.setAudit(audit);
		manageEmployees.findAccount();

		assertEquals(audit.getContractorAccount(), manageEmployees.getAccount());
	}

	@Test
	public void testFindAccount_Employee() {
		ContractorAccount contractor = EntityFactory.makeContractor();
		Employee employee = EntityFactory.makeEmployee(contractor);

		manageEmployees.setEmployee(employee);
		manageEmployees.findAccount();

		assertEquals(contractor, manageEmployees.getAccount());
	}

	@Test
	public void testFindAccount_ID() {
		PowerMockito.mockStatic(ActionContext.class);

		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("id", new String[] { "123" });

		Account account = new Account();
		account.setId(123);

		when(ActionContext.getContext()).thenReturn(actionContext);
		when(actionContext.getParameters()).thenReturn(parameters);
		when(entityManager.find(Account.class, 123)).thenReturn(account);

		manageEmployees.findAccount();

		assertEquals(account, manageEmployees.getAccount());
	}

	@Test
	public void testFindAccount_Permissions() {
		Account account = new Account();
		account.setId(123);

		when(permissions.getAccountId()).thenReturn(123);
		when(entityManager.find(Account.class, 123)).thenReturn(account);

		assertEquals(account, manageEmployees.getAccount());
	}

	@Test
	public void testExecute() throws Exception {
		ContractorAccount contractorAccount = EntityFactory.makeContractor();
		manageEmployees.setAccount(contractorAccount);

		when(employeeDAO.findWhere(anyString())).thenReturn(null);

		assertEquals(ActionSupport.SUCCESS, manageEmployees.execute());
	}

	@Test
	public void testExecute_Audit() throws Exception {
		ContractorAudit contractorAudit = EntityFactory.makeContractorAudit(AuditType.HSE_COMPETENCY,
				EntityFactory.makeContractor());
		manageEmployees.setAudit(contractorAudit);

		assertEquals(ActionSupport.SUCCESS, manageEmployees.execute());
		assertEquals(contractorAudit.getContractorAccount(), manageEmployees.getAccount());
	}

	@Test
	public void testExecute_Employee() throws Exception {
		Employee employee = EntityFactory.makeEmployee(EntityFactory.makeContractor());
		manageEmployees.setEmployee(employee);

		manageEmployees.findAccount();

		assertEquals(ActionSupport.SUCCESS, manageEmployees.execute());
		assertEquals(employee.getAccount(), manageEmployees.getAccount());
	}

	@Test
	public void testAdd() {
		ContractorAccount contractorAccount = EntityFactory.makeContractor();
		manageEmployees.setAccount(contractorAccount);

		assertEquals(ActionSupport.SUCCESS, manageEmployees.add());
		assertEquals(0, manageEmployees.getEmployee().getId());
	}

	@Test
	public void testSave() throws Exception {
		Employee employee = EntityFactory.makeEmployee(EntityFactory.makeContractor());

		saveCommonBehaviors(employee);
	}

	@Test
	public void testSave_New() throws Exception {
		Employee employee = EntityFactory.makeEmployee(EntityFactory.makeContractor());
		employee.setId(0);

		saveCommonBehaviors(employee);
		verify(entityManager).persist(any(Note.class));
	}

	@Test
	public void testSave_SetAccount() throws Exception {
		ContractorAccount contractorAccount = EntityFactory.makeContractor();
		manageEmployees.setAccount(contractorAccount);

		Employee employee = EntityFactory.makeEmployee(null);

		saveCommonBehaviors(employee);
		assertEquals(contractorAccount, manageEmployees.getEmployee().getAccount());
	}

	@Test
	public void testSave_SSNCheck9Characters() throws Exception {
		Employee employee = EntityFactory.makeEmployee(EntityFactory.makeContractor());
		manageEmployees.setSsn("999999999");

		saveCommonBehaviors(employee);
		assertEquals("999999999", manageEmployees.getEmployee().getSsn());
	}

	@Test
	public void testSave_SSNCheckInvalidFormat() throws Exception {
		Employee employee = EntityFactory.makeEmployee(EntityFactory.makeContractor());
		manageEmployees.setSsn("Hello1 World9");

		saveCommonBehaviors(employee);
		assertTrue(manageEmployees.hasActionErrors());
		assertNull(manageEmployees.getEmployee().getSsn());

	}

	@Test
	public void testSave_EmailInvalidFormat() throws Exception {
		Employee employee = EntityFactory.makeEmployee(EntityFactory.makeContractor());
		employee.setEmail("Hello World");

		saveCommonBehaviors(employee);
		assertEquals("info@picsauditing.com", manageEmployees.getEmployee().getEmail());
	}

	@Test
	public void testSave_EmailValidFormat() throws Exception {
		Employee employee = EntityFactory.makeEmployee(EntityFactory.makeContractor());
		employee.setEmail("lani@test.com");

		saveCommonBehaviors(employee);
		assertEquals("lani@test.com", manageEmployees.getEmployee().getEmail());
	}

	@Test
	public void testInactivate() {
		ContractorAccount contractorAccount = EntityFactory.makeContractor();

		manageEmployees.setAccount(contractorAccount);

		assertEquals(ActionSupport.SUCCESS, manageEmployees.inactivate());
		assertFalse(manageEmployees.hasActionMessages());

		verify(employeeDAO, never()).save(any(Employee.class));
	}

	@Test
	public void testInactivate_Employee() {
		Employee employee = EntityFactory.makeEmployee(EntityFactory.makeContractor());

		when(employeeDAO.findWhere(anyString())).thenReturn(null);
		when(employeeDAO.save(any(Employee.class))).thenReturn(employee);

		manageEmployees.setEmployee(employee);
		manageEmployees.findAccount();

		assertEquals(ActionSupport.SUCCESS, manageEmployees.inactivate());
		assertTrue(manageEmployees.hasActionMessages());
		assertEquals(UserStatus.Inactive, manageEmployees.getEmployee().getStatus());

		verify(employeeDAO).save(any(Employee.class));
	}

	@Test
	public void testActivate() throws Exception {
		ContractorAccount contractorAccount = EntityFactory.makeContractor();
		manageEmployees.setAccount(contractorAccount);

		assertEquals(PicsActionSupport.REDIRECT, manageEmployees.activate());

		verify(employeeDAO, never()).save(any(Employee.class));
	}

	@Test
	public void testActivate_Employee() throws Exception {
		Employee employee = EntityFactory.makeEmployee(EntityFactory.makeContractor());
		manageEmployees.setEmployee(employee);
		manageEmployees.findAccount();

		when(employeeDAO.save(any(Employee.class))).thenReturn(employee);

		assertEquals(PicsActionSupport.REDIRECT, manageEmployees.activate());

		verify(employeeDAO).save(any(Employee.class));
	}

	@Test
	public void testDelete() {
		ContractorAccount contractorAccount = EntityFactory.makeContractor();
		manageEmployees.setAccount(contractorAccount);

		assertEquals(ActionSupport.SUCCESS, manageEmployees.delete());

		verify(employeeDAO).findWhere(anyString());
	}

	@Test
	public void testDelete_Employee() {
		Employee employee = EntityFactory.makeEmployee(EntityFactory.makeContractor());
		manageEmployees.setEmployee(employee);
		manageEmployees.findAccount();

		when(employeeDAO.save(any(Employee.class))).thenReturn(employee);

		assertEquals(ActionSupport.SUCCESS, manageEmployees.delete());
		assertNull(manageEmployees.getEmployee());
		assertTrue(manageEmployees.hasActionMessages());

		verify(employeeDAO).findWhere(anyString());
		verify(employeeDAO).save(any(Employee.class));
	}

	@Test
	public void testGetActiveEmployees() throws Exception {
		manageEmployees.setAccount(EntityFactory.makeContractor());
		when(employeeDAO.findWhere(anyString())).thenReturn(null);
		manageEmployees.execute();
		assertNull(manageEmployees.getActiveEmployees());

		List<Employee> employees = new ArrayList<Employee>();
		employees.add(EntityFactory.makeEmployee(null));

		when(employeeDAO.findWhere(anyString())).thenReturn(employees);
		manageEmployees.execute();
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

		employees.add(EntityFactory.makeEmployee(null));
		assertFalse(manageEmployees.getActiveEmployees().isEmpty());
	}

	@Test
	public void testAddRoleAjax() {
		assertEquals("roles", manageEmployees.addRoleAjax());

		neverMergedOrPersisted();
	}

	@Test
	public void testAddRoleAjax_Employee() {
		manageEmployees.setEmployee(EntityFactory.makeEmployee(null));

		assertEquals("roles", manageEmployees.addRoleAjax());

		neverMergedOrPersisted();
	}

	@Test
	public void testAddRoleAjax_EmployeeJobRole_New() {
		manageEmployees.setEmployee(EntityFactory.makeEmployee(null));

		JobRole jobRole = new JobRole();
		jobRole.setId(1);
		when(entityManager.find(eq(JobRole.class), anyInt())).thenReturn(jobRole);

		assertEquals("roles", manageEmployees.addRoleAjax());

		// One for EmployeeRole, another for Note
		verify(entityManager, times(2)).persist(any(BaseTable.class));
	}

	@Test
	public void testAddRoleAjax_EmployeeJobRole_Existing() {
		manageEmployees.setEmployee(EntityFactory.makeEmployee(null));

		JobRole jobRole = new JobRole();
		jobRole.setId(1);

		EmployeeRole employeeRole = new EmployeeRole();
		employeeRole.setEmployee(manageEmployees.getEmployee());
		employeeRole.setJobRole(jobRole);

		manageEmployees.getEmployee().getEmployeeRoles().add(employeeRole);

		when(entityManager.find(eq(JobRole.class), anyInt())).thenReturn(jobRole);

		assertEquals("roles", manageEmployees.addRoleAjax());
		assertTrue(manageEmployees.hasActionErrors());

		verify(entityManager, never()).persist(any(BaseTable.class));
	}

	@Test
	public void testRemoveRoleAjax() {
		assertEquals("roles", manageEmployees.removeRoleAjax());
		verify(entityManager, never()).remove(eq(EmployeeRole.class));
	}

	@Test
	public void testRemoveRoleAjax_EmployeeOnly() {
		Employee employee = EntityFactory.makeEmployee(null);

		manageEmployees.setEmployee(employee);

		assertEquals("roles", manageEmployees.removeRoleAjax());

		verify(entityManager, never()).remove(eq(EmployeeRole.class));
	}

	@Test
	public void testRemoveRoleAjax_ChildOnly() {
		manageEmployees.setChildID(1);

		assertEquals("roles", manageEmployees.removeRoleAjax());

		verify(entityManager, never()).remove(eq(EmployeeRole.class));
	}

	@Test
	public void testRemoveRoleAjax_EmployeeChild() {
		Employee employee = EntityFactory.makeEmployee(null);
		manageEmployees.setEmployee(employee);
		manageEmployees.setChildID(1);

		assertEquals("roles", manageEmployees.removeRoleAjax());

		verify(entityManager, never()).remove(eq(EmployeeRole.class));
	}

	@Test
	public void testRemoveRoleAjax_EmployeeChildRole() {
		Employee employee = EntityFactory.makeEmployee(null);
		EmployeeRole employeeRole = new EmployeeRole();
		employeeRole.setEmployee(employee);
		employeeRole.setJobRole(new JobRole());

		manageEmployees.setEmployee(employee);
		manageEmployees.setChildID(1);

		when(entityManager.find(eq(EmployeeRole.class), anyInt())).thenReturn(employeeRole);

		assertEquals("roles", manageEmployees.removeRoleAjax());

		verify(entityManager).remove(any(EmployeeRole.class));
		verify(entityManager).persist(any(Note.class));
	}

	@Test
	public void testAddSiteAjax() {
		assertEquals("sites", manageEmployees.addSiteAjax());
	}

	@Test
	public void testAddSiteAjax_Employee() {
		Employee employee = EntityFactory.makeEmployee(null);

		manageEmployees.setEmployee(employee);

		assertEquals("sites", manageEmployees.addSiteAjax());
	}

	@Test
	public void testAddSiteAjax_Operator() {
		OperatorAccount operatorAccount = EntityFactory.makeOperator();

		manageEmployees.setOp(operatorAccount);

		assertEquals("sites", manageEmployees.addSiteAjax());
	}

	@Test
	public void testAddSiteAjax_EmployeeOperatorIDGreaterThanZero() {
		OperatorAccount operatorAccount = EntityFactory.makeOperator();
		Employee employee = EntityFactory.makeEmployee(operatorAccount);

		manageEmployees.setEmployee(employee);
		manageEmployees.setOp(operatorAccount);

		assertEquals("sites", manageEmployees.addSiteAjax());

		verify(entityManager, times(2)).persist(any(BaseTable.class));
		assertFalse(manageEmployees.getEmployee().getEmployeeSites().isEmpty());
	}

	@Test
	public void testAddSiteAjax_EmployeeOperatorIDZero() {
		OperatorAccount operatorAccount = EntityFactory.makeOperator();
		operatorAccount.setId(0);
		Employee employee = EntityFactory.makeEmployee(operatorAccount);

		manageEmployees.setEmployee(employee);
		manageEmployees.setOp(operatorAccount);

		assertEquals("sites", manageEmployees.addSiteAjax());

		verify(entityManager, never()).persist(any(BaseTable.class));
	}

	@Test
	public void testAddSiteAjax_EmployeeOperatorIDLessThanZero() {
		OperatorAccount operatorAccount = EntityFactory.makeOperator();
		operatorAccount.setId(0 - operatorAccount.getId());
		Employee employee = EntityFactory.makeEmployee(operatorAccount);

		JobSite jobSite = new JobSite();
		jobSite.setOperator(operatorAccount);

		when(entityManager.find(eq(JobSite.class), anyInt())).thenReturn(jobSite);

		manageEmployees.setEmployee(employee);
		manageEmployees.setOp(operatorAccount);

		assertEquals("sites", manageEmployees.addSiteAjax());

		verify(entityManager, times(2)).persist(any(BaseTable.class));
		assertFalse(manageEmployees.getEmployee().getEmployeeSites().isEmpty());
		assertEquals(jobSite, manageEmployees.getEmployee().getEmployeeSites().get(0).getJobSite());
	}

	@Test
	public void testRemoveSiteAjax() {
		assertEquals("sites", manageEmployees.removeSiteAjax());

		neverMergedOrPersisted();
	}

	@Test
	public void testRemoveSiteAjax_ChildID() {
		manageEmployees.setChildID(1);

		assertEquals("sites", manageEmployees.removeSiteAjax());

		neverMergedOrPersisted();
	}

	@Test
	public void testRemoveSiteAjax_Employee() {
		manageEmployees.setEmployee(EntityFactory.makeEmployee(null));

		assertEquals("sites", manageEmployees.removeSiteAjax());
	}

	@Test
	public void testRemoveSiteAjax_ChildIDEmployee_EmployeeSiteNull() {
		manageEmployees.setChildID(1);
		manageEmployees.setEmployee(EntityFactory.makeEmployee(null));

		assertEquals("sites", manageEmployees.removeSiteAjax());

		verify(entityManager).find(EmployeeSite.class, 1);
	}

	@Test
	public void testRemoveSiteAjax_ChildIDEmployee_EmployeeSiteExpired() {
		manageEmployees.setChildID(1);
		manageEmployees.setEmployee(EntityFactory.makeEmployee(null));

		JobSite jobSite = new JobSite();
		jobSite.setOperator(EntityFactory.makeOperator());
		jobSite.setLabel("Job Site");

		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.YEAR, -1);

		EmployeeSite employeeSite = new EmployeeSite();
		employeeSite.setId(1);
		employeeSite.setOperator(jobSite.getOperator());
		employeeSite.setEffectiveDate(calendar.getTime());
		employeeSite.setExpirationDate(new Date());

		manageEmployees.getEmployee().getEmployeeSites().add(employeeSite);

		when(entityManager.find(EmployeeSite.class, manageEmployees.getChildID())).thenReturn(employeeSite);

		assertEquals("sites", manageEmployees.removeSiteAjax());
		assertFalse(employeeSite.isCurrent());

		verify(entityManager).find(EmployeeSite.class, 1);
		verify(entityManager).merge(any(EmployeeSite.class));
		verify(entityManager).persist(any(Note.class));
		verify(entityManager, never()).remove(any(EmployeeSite.class));
	}

	@Test
	public void testRemoveSiteAjax_ChildIDEmployee_EmployeeSiteNotExpired() {
		manageEmployees.setChildID(1);
		manageEmployees.setEmployee(EntityFactory.makeEmployee(null));

		JobSite jobSite = new JobSite();
		jobSite.setOperator(EntityFactory.makeOperator());
		jobSite.setLabel("Job Site");

		EmployeeSite employeeSite = new EmployeeSite();
		employeeSite.defaultDates();
		employeeSite.setOperator(jobSite.getOperator());

		manageEmployees.getEmployee().getEmployeeSites().add(employeeSite);

		when(entityManager.find(EmployeeSite.class, manageEmployees.getChildID())).thenReturn(employeeSite);

		assertEquals("sites", manageEmployees.removeSiteAjax());
		assertTrue(employeeSite.isCurrent());

		verify(entityManager).find(EmployeeSite.class, 1);
		verify(entityManager).persist(any(BaseTable.class));
		verify(entityManager).remove(any(EmployeeSite.class));
	}

	@Test
	public void testNewSiteAjax() {
		assertEquals("sites", manageEmployees.newSiteAjax());

		neverMergedOrPersisted();
	}

	@Test
	public void testNewSiteAjax_LabelNotEmptyNameEmpty() {
		manageEmployees.setJobSite(new JobSite());
		manageEmployees.getJobSite().setLabel("Label");

		assertEquals("sites", manageEmployees.newSiteAjax());

		neverMergedOrPersisted();
	}

	@Test
	public void testNewSiteAjax_LabelEmptyNameNotEmpty() {
		manageEmployees.setJobSite(new JobSite());
		manageEmployees.getJobSite().setName("Name");

		assertEquals("sites", manageEmployees.newSiteAjax());

		neverMergedOrPersisted();
	}

	@Test
	public void testNewSiteAjax_LabelName() {
		manageEmployees.setJobSite(new JobSite());
		manageEmployees.getJobSite().setId(1);
		manageEmployees.getJobSite().setName("Name");
		manageEmployees.getJobSite().setLabel("Label");

		assertEquals("sites", manageEmployees.newSiteAjax());
		assertEquals(manageEmployees.getJobSite(), manageEmployees.getEsSite().getJobSite());

		verify(entityManager).merge(any(EmployeeSite.class));
		verify(entityManager).persist(any(JobSite.class));
	}

	@Test
	public void testEditSiteAjax() {
		assertEquals("sites", manageEmployees.editSiteAjax());

		neverMergedOrPersisted();
	}

	@Test
	public void testEditSiteAjax_Employee() {
		manageEmployees.setEmployee(EntityFactory.makeEmployee(null));

		assertEquals("sites", manageEmployees.editSiteAjax());

		neverMergedOrPersisted();
	}

	@Test
	public void testEditSiteAjax_ChildID() {
		manageEmployees.setChildID(1);

		assertEquals("sites", manageEmployees.editSiteAjax());

		neverMergedOrPersisted();
	}

	@Test
	public void testEditSiteAjax_EmployeeChildID() {
		manageEmployees.setEmployee(EntityFactory.makeEmployee(EntityFactory.makeContractor()));
		manageEmployees.setChildID(1);

		EmployeeSite employeeSite = new EmployeeSite();
		employeeSite.setId(1);
		employeeSite.setEmployee(manageEmployees.getEmployee());

		when(entityManager.find(EmployeeSite.class, 1)).thenReturn(employeeSite);

		assertEquals("sites", manageEmployees.editSiteAjax());

		verify(entityManager).find(EmployeeSite.class, 1);
		verify(entityManager).merge(any(Note.class));
		verify(entityManager).persist(any(EmployeeSite.class));
	}

	@Test
	public void testGetSiteAjax() {
		assertEquals("getSite", manageEmployees.getSiteAjax());
	}

	@Test
	public void testGetSiteAjax_ChildID() {
		manageEmployees.setChildID(1);

		when(entityManager.find(EmployeeSite.class, 1)).thenReturn(null);

		assertEquals("getSite", manageEmployees.getSiteAjax());
		assertNull(manageEmployees.getEsSite());

		verify(entityManager).find(EmployeeSite.class, 1);
	}

	@Test
	public void testGetSiteAjax_ChildIDEmployeeSiteNotNull() {
		manageEmployees.setChildID(1);

		EmployeeSite employeeSite = new EmployeeSite();

		when(entityManager.find(EmployeeSite.class, 1)).thenReturn(employeeSite);

		assertEquals("getSite", manageEmployees.getSiteAjax());
		assertNotNull(manageEmployees.getEsSite());
		assertEquals(employeeSite, manageEmployees.getEsSite());

		verify(entityManager).find(EmployeeSite.class, 1);
	}

	@Test
	public void testLoadAjax() {
		assertEquals("employees", manageEmployees.loadAjax());
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
		ContractorAccount contractorAccount = EntityFactory.makeContractor();

		JobRole jobRole = new JobRole();
		jobRole.setAccount(contractorAccount);
		jobRole.setName("Job Role");

		contractorAccount.getJobRoles().add(jobRole);

		manageEmployees.setEmployee(EntityFactory.makeEmployee(contractorAccount));

		assertFalse(manageEmployees.getUnusedJobRoles().isEmpty());
	}

	@Test
	public void testGetUnusedJobRoles_JobRoleInactive() {
		ContractorAccount contractorAccount = EntityFactory.makeContractor();

		JobRole jobRole = new JobRole();
		jobRole.setAccount(contractorAccount);
		jobRole.setActive(false);
		jobRole.setName("Job Role");

		contractorAccount.getJobRoles().add(jobRole);

		manageEmployees.setEmployee(EntityFactory.makeEmployee(contractorAccount));

		assertTrue(manageEmployees.getUnusedJobRoles().isEmpty());
	}

	@Test
	public void testGetUnusedJobRoles_EmployeeRoleExists() {
		ContractorAccount contractorAccount = EntityFactory.makeContractor();
		Employee employee = EntityFactory.makeEmployee(contractorAccount);

		JobRole jobRole = new JobRole();
		jobRole.setAccount(contractorAccount);
		jobRole.setActive(false);
		jobRole.setName("Job Role");

		EmployeeRole employeeRole = new EmployeeRole();
		employeeRole.setEmployee(employee);
		employeeRole.setJobRole(jobRole);

		contractorAccount.getJobRoles().add(jobRole);
		employee.getEmployeeRoles().add(employeeRole);

		manageEmployees.setEmployee(employee);

		assertTrue(manageEmployees.getUnusedJobRoles().isEmpty());
	}

	@Test
	public void testIsShowJobRolesSection() {
		manageEmployees.setEmployee(EntityFactory.makeEmployee(EntityFactory.makeContractor()));

		assertFalse(manageEmployees.isShowJobRolesSection());
	}

	@Test
	public void testIsShowJobRolesSection_UnusedJobRole() {
		testGetUnusedJobRoles();

		assertTrue(manageEmployees.isShowJobRolesSection());
	}

	@Test
	public void testIsShowJobRolesSection_EmployeeRole() {
		EmployeeRole employeeRole = new EmployeeRole();

		Employee employee = EntityFactory.makeEmployee(EntityFactory.makeContractor());
		employee.getEmployeeRoles().add(employeeRole);

		manageEmployees.setEmployee(employee);

		assertTrue(manageEmployees.isShowJobRolesSection());
	}

	@Test
	public void testGetOqOperators() {
		Account account = new Account();
		account.setType("Account");

		manageEmployees.setEmployee(EntityFactory.makeEmployee(account));

		assertNotNull(manageEmployees.getOqOperators());
		assertTrue(manageEmployees.getOqOperators().isEmpty());
	}

	@Test
	public void testGetOqOperators_ContractorHasJobContractor() {
		ContractorAccount contractorAccount = EntityFactory.makeContractor();
		OperatorAccount operatorAccount = EntityFactory.makeOperator();

		JobSite jobSite = new JobSite();
		jobSite.setOperator(operatorAccount);

		JobContractor jobContractor = new JobContractor();
		jobContractor.setContractor(contractorAccount);
		jobContractor.setJob(jobSite);

		contractorAccount.getJobSites().add(jobContractor);

		manageEmployees.setEmployee(EntityFactory.makeEmployee(contractorAccount));

		assertNotNull(manageEmployees.getOqOperators());
		assertFalse(manageEmployees.getOqOperators().isEmpty());
	}

	@Test
	public void testGetOqOperators_ContractorHasOperatorWithHSEAndOQ() {
		ContractorAccount contractorAccount = EntityFactory.makeContractor();
		contractorAccount.setRequiresCompetencyReview(true);

		OperatorAccount operatorAccount = EntityFactory.makeOperator();
		operatorAccount.setRequiresCompetencyReview(true);

		JobSite jobSite = new JobSite();
		jobSite.setOperator(operatorAccount);

		operatorAccount.getJobSites().add(jobSite);

		EntityFactory.addContractorOperator(contractorAccount, operatorAccount);

		manageEmployees.setEmployee(EntityFactory.makeEmployee(contractorAccount));

		assertNotNull(manageEmployees.getOqOperators());
		assertFalse(manageEmployees.getOqOperators().isEmpty());
	}

	@Test
	public void testGetOqOperators_Operator() {
		OperatorAccount operatorAccount = EntityFactory.makeOperator();

		manageEmployees.setEmployee(EntityFactory.makeEmployee(operatorAccount));

		assertNotNull(manageEmployees.getOqOperators());
		assertTrue(manageEmployees.getOqOperators().isEmpty());
	}

	@Test
	public void testGetOqOperators_OperatorHasJobSite() {
		OperatorAccount operatorAccount = EntityFactory.makeOperator();

		JobSite jobSite = new JobSite();
		jobSite.setOperator(operatorAccount);

		operatorAccount.getJobSites().add(jobSite);

		manageEmployees.setEmployee(EntityFactory.makeEmployee(operatorAccount));

		assertNotNull(manageEmployees.getOqOperators());
		assertFalse(manageEmployees.getOqOperators().isEmpty());
	}

	@Test
	public void testGetOqOperators_Corporate() {
		OperatorAccount operatorAccount = EntityFactory.makeOperator();
		operatorAccount.setType("Corporate");

		manageEmployees.setEmployee(EntityFactory.makeEmployee(operatorAccount));

		assertNotNull(manageEmployees.getOqOperators());
		assertTrue(manageEmployees.getOqOperators().isEmpty());
	}

	@Test
	public void testGetOqOperators_CorporateWithFacilities() {
		OperatorAccount corporateAccount = EntityFactory.makeOperator();
		corporateAccount.setType("Corporate");

		OperatorAccount operatorAccount = EntityFactory.makeOperator();
		operatorAccount.setType("Corporate");

		JobSite jobSite = new JobSite();
		jobSite.setOperator(operatorAccount);

		operatorAccount.getJobSites().add(jobSite);

		Facility facility = new Facility();
		facility.setCorporate(corporateAccount);
		facility.setOperator(operatorAccount);

		corporateAccount.getCorporateFacilities().add(facility);

		manageEmployees.setEmployee(EntityFactory.makeEmployee(corporateAccount));

		assertNotNull(manageEmployees.getOqOperators());
		assertFalse(manageEmployees.getOqOperators().isEmpty());
		assertEquals(operatorAccount, manageEmployees.getOqOperators().get(0).getOperator());
	}

	@Test
	public void testGetAllOqOperators() {
		manageEmployees.setAccount(EntityFactory.makeContractor());

		assertNotNull(manageEmployees.getAllOqOperators());
		assertTrue(manageEmployees.getAllOqOperators().isEmpty());
	}

	@Test
	public void testGetAllOqOperators_OperatorRequiresOQ() {
		ContractorAccount contractorAccount = EntityFactory.makeContractor();

		OperatorAccount operatorAccount = EntityFactory.makeOperator();
		operatorAccount.setRequiresOQ(true);

		EntityFactory.addContractorOperator(contractorAccount, operatorAccount);

		manageEmployees.setAccount(contractorAccount);

		assertNotNull(manageEmployees.getAllOqOperators());
		assertFalse(manageEmployees.getAllOqOperators().isEmpty());
	}

	@Test
	public void testGetHseOperators() {
		Account account = new Account();
		account.setType("Account");

		manageEmployees.setEmployee(EntityFactory.makeEmployee(account));

		assertNotNull(manageEmployees.getHseOperators());
		assertTrue(manageEmployees.getHseOperators().isEmpty());
	}

	@Test
	public void testGetHseOperators_ContractorHasNonCorporateOperatorNotRequiringHSE() {
		ContractorAccount contractorAccount = EntityFactory.makeContractor();
		OperatorAccount operatorAccount = EntityFactory.makeOperator();
		EntityFactory.addContractorOperator(contractorAccount, operatorAccount);

		manageEmployees.setEmployee(EntityFactory.makeEmployee(contractorAccount));

		assertNotNull(manageEmployees.getHseOperators());
		assertTrue(manageEmployees.getHseOperators().isEmpty());
	}

	@Test
	public void testGetHseOperators_ContractorHasNonCorporateOperatorRequiringHSE() {
		ContractorAccount contractorAccount = EntityFactory.makeContractor();
		contractorAccount.setRequiresCompetencyReview(true);

		OperatorAccount operatorAccount = EntityFactory.makeOperator();
		operatorAccount.setRequiresCompetencyReview(true);

		ContractorOperator contractorOperator = new ContractorOperator();
		contractorOperator.setContractorAccount(contractorAccount);
		contractorOperator.setOperatorAccount(operatorAccount);

		contractorAccount.getOperators().add(contractorOperator);

		manageEmployees.setEmployee(EntityFactory.makeEmployee(contractorAccount));

		assertNotNull(manageEmployees.getHseOperators());
		assertFalse(manageEmployees.getHseOperators().isEmpty());
	}

	@Test
	public void testGetHseOperators_Operator() {
		manageEmployees.setEmployee(EntityFactory.makeEmployee(EntityFactory.makeOperator()));

		assertNotNull(manageEmployees.getHseOperators());
		assertFalse(manageEmployees.getHseOperators().isEmpty());
	}

	@Test
	public void testGetHseOperators_Corporate() {
		OperatorAccount operatorAccount = EntityFactory.makeOperator();
		operatorAccount.setType("Corporate");

		manageEmployees.setEmployee(EntityFactory.makeEmployee(operatorAccount));

		assertNotNull(manageEmployees.getHseOperators());
		assertFalse(manageEmployees.getHseOperators().isEmpty());
	}

	@Test
	public void testGetHseOperators_CorporateWithFacilites() {
		OperatorAccount corporateAccount = EntityFactory.makeOperator();
		corporateAccount.setType("Corporate");

		OperatorAccount operatorAccount = EntityFactory.makeOperator();
		operatorAccount.setType("Corporate");

		Facility facility = new Facility();
		facility.setCorporate(corporateAccount);
		facility.setOperator(operatorAccount);

		corporateAccount.getCorporateFacilities().add(facility);

		manageEmployees.setEmployee(EntityFactory.makeEmployee(corporateAccount));

		assertNotNull(manageEmployees.getHseOperators());
		assertFalse(manageEmployees.getHseOperators().isEmpty());
		assertEquals(2, manageEmployees.getHseOperators().size());
	}

	@Test
	public void testGetEmpPhoto() {
		Employee employee = EntityFactory.makeEmployee(null);

		manageEmployees.setEmployee(employee);

		assertEquals("emp_" + employee.getId() + null, manageEmployees.getEmpPhoto());
	}

	@Test
	public void testGetEmpPhoto_PhotoNotNull() {
		Employee employee = EntityFactory.makeEmployee(null);
		employee.setPhoto("Photo");

		manageEmployees.setEmployee(employee);

		assertEquals("emp_" + employee.getId() + "Photo", manageEmployees.getEmpPhoto());
	}

	@Test
	public void testGetMissingTasks() {
		Whitebox.setInternalState(manageEmployees, "siteTaskDAO", jobSiteTaskDAO);

		when(jobSiteTaskDAO.findByJob(anyInt())).thenReturn(new ArrayList<JobSiteTask>());

		manageEmployees.setEmployee(EntityFactory.makeEmployee(null));

		EmployeeMissingTasks employeeMissingTasks = manageEmployees.getMissingTasks(1);

		assertNotNull(employeeMissingTasks);
		assertTrue(employeeMissingTasks.getMissingTasks().isEmpty());
		assertTrue(employeeMissingTasks.getQualifiedTasks().isEmpty());
	}

	@Test
	public void testGetMissingTasks_JobSiteTaskMissing() {
		getMissingTasksCommonBehaviors();

		manageEmployees.setEmployee(EntityFactory.makeEmployee(null));

		EmployeeMissingTasks employeeMissingTasks = manageEmployees.getMissingTasks(1);

		assertNotNull(employeeMissingTasks);
		assertFalse(employeeMissingTasks.getMissingTasks().isEmpty());
		assertTrue(employeeMissingTasks.getQualifiedTasks().isEmpty());
	}

	@Test
	public void testGetMissingTasks_EmployeeHasQualifiedJobSiteTask() {
		JobSiteTask jobSiteTask = getMissingTasksCommonBehaviors();

		EmployeeQualification employeeQualification = new EmployeeQualification();
		employeeQualification.setQualified(true);
		employeeQualification.setTask(jobSiteTask.getTask());
		employeeQualification.defaultDates();

		Employee employee = EntityFactory.makeEmployee(null);
		employee.getEmployeeQualifications().add(employeeQualification);

		manageEmployees.setEmployee(employee);

		EmployeeMissingTasks employeeMissingTasks = manageEmployees.getMissingTasks(1);

		assertNotNull(employeeMissingTasks);
		assertTrue(employeeMissingTasks.getMissingTasks().isEmpty());
		assertFalse(employeeMissingTasks.getQualifiedTasks().isEmpty());
	}

	@Test
	public void testGetMissingTasks_EmployeeHasNonQualifiedJobSiteTask() {
		JobSiteTask jobSiteTask = getMissingTasksCommonBehaviors();

		EmployeeQualification employeeQualification = new EmployeeQualification();
		employeeQualification.setTask(jobSiteTask.getTask());
		employeeQualification.defaultDates();

		Employee employee = EntityFactory.makeEmployee(null);
		employee.getEmployeeQualifications().add(employeeQualification);

		manageEmployees.setEmployee(employee);

		EmployeeMissingTasks employeeMissingTasks = manageEmployees.getMissingTasks(1);

		assertNotNull(employeeMissingTasks);
		assertFalse(employeeMissingTasks.getMissingTasks().isEmpty());
		assertTrue(employeeMissingTasks.getQualifiedTasks().isEmpty());
	}

	@Test
	public void testGetAllJobTasks() {
		assertNull(manageEmployees.getAllJobTasks());

		verify(jobSiteTaskDAO, never()).findByEmployeeAccount(anyInt());
	}

	@Test
	public void testGetAllJobTasks_Employee() {
		Whitebox.setInternalState(manageEmployees, "siteTaskDAO", jobSiteTaskDAO);

		manageEmployees.setEmployee(EntityFactory.makeEmployee(EntityFactory.makeContractor()));

		when(jobSiteTaskDAO.findByEmployeeAccount(anyInt())).thenReturn(new ArrayList<JobSiteTask>());

		assertNotNull(manageEmployees.getAllJobTasks());

		verify(jobSiteTaskDAO).findByEmployeeAccount(anyInt());
	}

	@Test
	public void testGetNccerResults() {
		assertNull(manageEmployees.getNccerResults());
	}

	@Test
	public void testGetNccerResults_Employee() {
		manageEmployees.setEmployee(EntityFactory.makeEmployee(null));

		assertNotNull(manageEmployees.getNccerResults());
		assertTrue(manageEmployees.getNccerResults().isEmpty());
	}

	@Test
	public void testGetNccerResults_EmployeeHasValidAssessmentResults() {
		AssessmentTest assessmentTest = new AssessmentTest();
		assessmentTest.setAssessmentCenter(new Account());
		assessmentTest.getAssessmentCenter().setId(Account.ASSESSMENT_NCCER);
		assessmentTest.setQualificationMethod("Qualification Method");

		AssessmentResult assessmentResult = new AssessmentResult();
		assessmentResult.defaultDates();
		assessmentResult.setAssessmentTest(assessmentTest);

		manageEmployees.setEmployee(EntityFactory.makeEmployee(null));
		manageEmployees.getEmployee().getAssessmentResults().add(assessmentResult);

		assertNotNull(manageEmployees.getNccerResults());
		assertFalse(manageEmployees.getNccerResults().isEmpty());
	}

	private void saveCommonBehaviors(Employee employee) throws Exception {
		when(employeeDAO.save(any(Employee.class))).thenReturn(employee);

		if (employee.getEmail() == null) {
			employee.setEmail("");
		}

		manageEmployees.setEmployee(employee);
		manageEmployees.findAccount();

		assertEquals(PicsActionSupport.REDIRECT, manageEmployees.save());

		verify(employeeDAO).save(any(Employee.class));
	}

	private void neverMergedOrPersisted() {
		verify(entityManager, never()).merge(any(BaseTable.class));
		verify(entityManager, never()).persist(any(BaseTable.class));
	}

	private JobSiteTask getMissingTasksCommonBehaviors() {
		Whitebox.setInternalState(manageEmployees, "siteTaskDAO", jobSiteTaskDAO);

		JobSiteTask jobSiteTask = new JobSiteTask();
		jobSiteTask.setTask(new JobTask());

		List<JobSiteTask> jobSiteTasks = new ArrayList<JobSiteTask>();
		jobSiteTasks.add(jobSiteTask);

		when(jobSiteTaskDAO.findByJob(anyInt())).thenReturn(jobSiteTasks);

		return jobSiteTask;
	}
}
