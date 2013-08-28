package com.picsauditing.actions.employees;

import com.picsauditing.PicsTestUtil;
import com.picsauditing.PicsTranslationTest;
import com.picsauditing.access.OpPerms;
import com.picsauditing.access.OpType;
import com.picsauditing.access.Permissions;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.jpa.entities.*;
import com.picsauditing.util.test.TranslatorFactorySetup;
import org.junit.*;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import javax.persistence.EntityManager;
import java.util.*;

import static com.picsauditing.EntityFactory.*;
import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class EmployeeDashboardTest extends PicsTranslationTest {

	private ContractorAccount contractorAccount;
	private EmployeeDashboard employeeDashboard;

	@Mock
	private EntityManager entityManager;
	@Mock
	private Permissions permissions;

	@BeforeClass
	public static void classSetUp() throws Exception {
		PicsTranslationTest.setupTranslationServiceForTest();
		TranslatorFactorySetup.setupTranslatorFactoryForTest();
	}

	@AfterClass
	public static void classTearDown() throws Exception {
		PicsTranslationTest.tearDownTranslationService();
		TranslatorFactorySetup.resetTranslatorFactoryAfterTest();
	}

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		super.resetTranslationService();

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
	public void testExecute_FailsGracefullyIfContractorIsNull() throws Exception {
		employeeDashboard.startup();
		employeeDashboard.setContractor(null);

		String result = employeeDashboard.execute();

		assertEquals(PicsActionSupport.ERROR, result);
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
	public void testIsCanEditEmployees_OperatorCorporate() {
		OperatorAccount operator = mock(OperatorAccount.class);
		when(entityManager.find(eq(OperatorAccount.class), anyInt())).thenReturn(operator);

		when(permissions.isOperatorCorporate()).thenReturn(true);
		when(operator.hasCompetencyRequiringDocumentation()).thenReturn(false);
		assertFalse(employeeDashboard.isCanEditEmployees());

		when(operator.hasCompetencyRequiringDocumentation()).thenReturn(true);
		contractorAccount.setOperators(Collections.<ContractorOperator>emptyList());
		assertFalse(employeeDashboard.isCanEditEmployees());

		when(permissions.isRequiresCompetencyReview()).thenReturn(true);
		contractorAccount.setOperators(Collections.<ContractorOperator>emptyList());
		assertFalse(employeeDashboard.isCanEditEmployees());

		List<ContractorOperator> contractorOperatorList = new ArrayList<>();
		ContractorOperator contractorOperator = mock(ContractorOperator.class);
		when(contractorOperator.getOperatorAccount()).thenReturn(operator);
		contractorOperatorList.add(contractorOperator);

		when(operator.hasCompetencyRequiringDocumentation()).thenReturn(true);
		contractorAccount.setOperators(contractorOperatorList);
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

	@Ignore
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
		operatorTag.setCategory(OperatorTagCategory.CompetencyReview);

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
		Whitebox.setInternalState(employeeDashboard, "operators", contractorAccount.getOperators());
		Whitebox.setInternalState(employeeDashboard, "permissions", permissions);
	}

	@SuppressWarnings("deprecation")
	private void setExpectedActions() {
		when(permissions.isContractor()).thenReturn(true);
		when(permissions.getAccountId()).thenReturn(contractorAccount.getId());
		when(permissions.hasPermission(OpPerms.ContractorSafety)).thenReturn(true);

		when(entityManager.find(ContractorAccount.class, contractorAccount.getId())).thenReturn(contractorAccount);

		when(translationService.hasKey(anyString(), any(Locale.class))).thenReturn(true);
		when(translationService.getText(anyString(), any(Locale.class), anyObject())).thenReturn("Hello World");
	}
}
