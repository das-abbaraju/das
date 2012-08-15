package com.picsauditing.actions.employees;

import static com.picsauditing.EntityFactory.addCao;
import static com.picsauditing.EntityFactory.addContractorOperator;
import static com.picsauditing.EntityFactory.makeContractor;
import static com.picsauditing.EntityFactory.makeContractorAudit;
import static com.picsauditing.EntityFactory.makeOperator;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

import java.util.Calendar;
import java.util.Locale;

import javax.persistence.EntityManager;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import com.picsauditing.PicsTestUtil;
import com.picsauditing.PICS.I18nCache;
import com.picsauditing.access.OpPerms;
import com.picsauditing.access.OpType;
import com.picsauditing.access.Permissions;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.jpa.entities.AuditTypeClass;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.ContractorOperator;
import com.picsauditing.jpa.entities.ContractorTag;
import com.picsauditing.jpa.entities.Employee;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.jpa.entities.OperatorTag;
import com.picsauditing.search.Database;

public class EmployeeDashboardTest {
	private ContractorAccount contractorAccount;
	private EmployeeDashboard employeeDashboard;

	@Mock
	private EntityManager entityManager;
	@Mock
	private I18nCache i18nCache;
	@Mock
	private Permissions permissions;
	@Mock
	private Database databaseForTesting;

	@AfterClass
	public static void classTearDown() {
		Whitebox.setInternalState(I18nCache.class, "databaseForTesting", (Database) null);
	}

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		Whitebox.setInternalState(I18nCache.class, "databaseForTesting", databaseForTesting);

		employeeDashboard = new EmployeeDashboard();
		setupContractor();

		PicsTestUtil picsTestUtil = new PicsTestUtil();
		picsTestUtil.autowireEMInjectedDAOs(employeeDashboard, entityManager);

		setPrivateVariables();
		setExpectedActions();
	}

	@Test
	public void testStartup() throws Exception {
		employeeDashboard.startup();

		for (Employee employee : contractorAccount.getEmployees()) {
			assertTrue(employeeDashboard.getActiveEmployees().contains(employee));
		}

		assertNotNull(employeeDashboard.getEmployeeGuardAudits());
		assertFalse(employeeDashboard.getEmployeeGuardAudits().isEmpty());

		for (ContractorAudit contractorAudit : employeeDashboard.getEmployeeGuardAudits()) {
			assertTrue(contractorAccount.getAudits().contains(contractorAudit));
		}
	}

	@Test
	public void testExecute() throws Exception {
		employeeDashboard.startup();
		assertEquals(PicsActionSupport.SUCCESS, employeeDashboard.execute());

		assertNotNull(employeeDashboard.getAuditsByYearAndType());
		assertTrue(employeeDashboard.getAuditsByYearAndType().size() > 0);
		assertFalse(employeeDashboard.getDisplayedAudits().isEmpty());
	}

	@Test
	public void testCompetencyReview_NoYearSet() throws Exception {
		employeeDashboard.startup();
		employeeDashboard.setAuditTypeID(AuditType.INTEGRITYMANAGEMENT);

		assertEquals(PicsActionSupport.SUCCESS, employeeDashboard.employeeGUARDAudits());

		assertNotNull(employeeDashboard.getDisplayedAudits());
		assertFalse(employeeDashboard.getDisplayedAudits().isEmpty());
	}

	@Test
	public void testCompetencyReview_YearSet() throws Exception {
		employeeDashboard.startup();
		employeeDashboard.setYear(2000);
		employeeDashboard.setAuditTypeID(AuditType.INTEGRITYMANAGEMENT);

		assertEquals(PicsActionSupport.SUCCESS, employeeDashboard.employeeGUARDAudits());

		assertNotNull(employeeDashboard.getDisplayedAudits());
		assertTrue(employeeDashboard.getDisplayedAudits().isEmpty());
	}

	@Test
	public void testTrainingVerification_NoYearSet() throws Exception {
		employeeDashboard.startup();
		employeeDashboard.setAuditTypeID(AuditType.IMPLEMENTATIONAUDITPLUS);

		assertEquals(PicsActionSupport.SUCCESS, employeeDashboard.employeeGUARDAudits());

		assertNotNull(employeeDashboard.getDisplayedAudits());
		assertFalse(employeeDashboard.getDisplayedAudits().isEmpty());
	}

	@Test
	public void testTrainingVerification_YearSet() throws Exception {
		employeeDashboard.startup();
		employeeDashboard.setYear(2000);
		employeeDashboard.setAuditTypeID(AuditType.IMPLEMENTATIONAUDITPLUS);

		assertEquals(PicsActionSupport.SUCCESS, employeeDashboard.employeeGUARDAudits());

		assertNotNull(employeeDashboard.getDisplayedAudits());
		assertTrue(employeeDashboard.getDisplayedAudits().isEmpty());
	}

	@Test
	public void testGetDistinctAuditTypes() throws Exception {
		employeeDashboard.startup();
		employeeDashboard.execute();

		assertNotNull(employeeDashboard.getDistinctAuditTypes());
		assertEquals(3, employeeDashboard.getDistinctAuditTypes().size());
	}

	@Test
	public void testIsCanAddAudits() {
		assertFalse(employeeDashboard.isCanAddAudits());

		when(permissions.isAdmin()).thenReturn(true);
		assertTrue(employeeDashboard.isCanAddAudits());

		when(permissions.isAdmin()).thenReturn(false);
		when(permissions.hasPermission(OpPerms.ManageAudits, OpType.Edit)).thenReturn(true);
		assertTrue(employeeDashboard.isCanAddAudits());

		when(permissions.hasPermission(OpPerms.ManageAudits, OpType.Edit)).thenReturn(false);
		when(permissions.hasPermission(OpPerms.ManageAudits)).thenReturn(true);
		assertFalse(employeeDashboard.isCanAddAudits());
	}

	@Test
	public void testIsCanEditEmployees() {
		assertFalse(employeeDashboard.isCanEditEmployees());

		when(permissions.isAdmin()).thenReturn(true);
		assertTrue(employeeDashboard.isCanEditEmployees());

		when(permissions.isAdmin()).thenReturn(false);
		when(permissions.hasPermission(OpPerms.ContractorAdmin)).thenReturn(true);
		assertTrue(employeeDashboard.isCanEditEmployees());

		when(permissions.hasPermission(OpPerms.ContractorAdmin)).thenReturn(false);
		when(permissions.hasPermission(OpPerms.ManageEmployees)).thenReturn(true);
		assertFalse(employeeDashboard.isCanEditEmployees());

		when(permissions.hasPermission(OpPerms.ManageEmployees, OpType.Edit)).thenReturn(true);
		assertTrue(employeeDashboard.isCanEditEmployees());
	}

	@Test
	public void testIsCanEditJobRoles() {
		assertFalse(employeeDashboard.isCanEditJobRoles());

		when(permissions.isAdmin()).thenReturn(true);
		assertTrue(employeeDashboard.isCanEditJobRoles());

		when(permissions.isAdmin()).thenReturn(false);
		when(permissions.hasPermission(OpPerms.DefineRoles, OpType.Edit)).thenReturn(true);
		assertTrue(employeeDashboard.isCanEditJobRoles());

		when(permissions.hasPermission(OpPerms.DefineRoles, OpType.Edit)).thenReturn(false);
		when(permissions.hasPermission(OpPerms.DefineRoles)).thenReturn(true);
		assertFalse(employeeDashboard.isCanEditJobRoles());
	}

	@Test
	public void testIsCanEditCompetencies() {
		assertFalse(employeeDashboard.isCanEditCompetencies());

		when(permissions.hasPermission(OpPerms.DefineCompetencies)).thenReturn(true);
		assertFalse(employeeDashboard.isCanEditCompetencies());

		when(permissions.hasPermission(OpPerms.DefineCompetencies, OpType.Edit)).thenReturn(true);
		assertTrue(employeeDashboard.isCanEditCompetencies());
	}

	@Test
	public void testGetYearsDescending() throws Exception {
		employeeDashboard.startup();
		employeeDashboard.execute();

		assertEquals((Integer) 2012, employeeDashboard.getYearsDescending().get(0));
		assertEquals((Integer) 2010, employeeDashboard.getYearsDescending().get(1));
		assertEquals((Integer) 2009, employeeDashboard.getYearsDescending().get(2));
	}

	private void setupContractor() {
		contractorAccount = makeContractor();
		OperatorAccount operatorAccount = makeOperator();
		addContractorOperator(contractorAccount, operatorAccount);

		addEmployees();
		addEmployeeGUARDTag();
		addEmployeeSpecificContractorAudits();
	}

	private void addEmployees() {
		for (int i = 1; i <= 5; i++) {
			Employee employee = new Employee();
			employee.setId(i);
			employee.setFirstName(i + "First");
			employee.setLastName(i + "Last");
			employee.setTitle(i + "Title");

			contractorAccount.getEmployees().add(employee);
		}
	}

	private void addEmployeeGUARDTag() {
		OperatorTag operatorTag = new OperatorTag();
		operatorTag.setTag("HSE Competency");

		ContractorTag contractorTag = new ContractorTag();
		contractorTag.setContractor(contractorAccount);
		contractorTag.setTag(operatorTag);

		contractorAccount.getOperatorTags().add(contractorTag);
	}

	private void addEmployeeSpecificContractorAudits() {
		contractorAccount.getAudits().add(makeContractorAudit(AuditType.INTEGRITYMANAGEMENT, contractorAccount));
		contractorAccount.getAudits().add(makeContractorAudit(AuditType.IMPLEMENTATIONAUDITPLUS, contractorAccount));
		contractorAccount.getAudits().add(makeContractorAudit(AuditType.PQF, contractorAccount));
		contractorAccount.getAudits().add(makeContractorAudit(AuditType.HSE_COMPETENCY, contractorAccount));

		Calendar calendar = Calendar.getInstance();
		int year = 2010;
		calendar.set(Calendar.YEAR, year--);

		for (ContractorAudit contractorAudit : contractorAccount.getAudits()) {
			contractorAudit.setEffectiveDate(calendar.getTime());
			calendar.set(Calendar.YEAR, year--);

			contractorAudit.getAuditType().setCanContractorView(true);
			contractorAudit.getAuditType().setClassType(AuditTypeClass.Employee);

			for (ContractorOperator contractorOperator : contractorAccount.getOperators()) {
				addCao(contractorAudit, contractorOperator.getOperatorAccount());
			}
		}
	}

	private void setPrivateVariables() {
		Whitebox.setInternalState(employeeDashboard, "contractor", contractorAccount);
		Whitebox.setInternalState(employeeDashboard, "i18nCache", i18nCache);
		Whitebox.setInternalState(employeeDashboard, "operators", contractorAccount.getOperators());
		Whitebox.setInternalState(employeeDashboard, "permissions", permissions);
	}

	private void setExpectedActions() {
		when(permissions.isContractor()).thenReturn(true);
		when(permissions.getAccountId()).thenReturn(contractorAccount.getId());
		when(permissions.hasPermission(OpPerms.ContractorSafety)).thenReturn(true);

		when(entityManager.find(ContractorAccount.class, contractorAccount.getId())).thenReturn(contractorAccount);

		when(i18nCache.hasKey(anyString(), any(Locale.class))).thenReturn(true);
		when(i18nCache.getText(anyString(), any(Locale.class), anyObject())).thenReturn("Hello World");
	}
}
