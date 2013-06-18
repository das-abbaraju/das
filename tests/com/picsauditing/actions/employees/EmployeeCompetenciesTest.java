package com.picsauditing.actions.employees;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.persistence.EntityManager;

import com.google.common.collect.Table;
import com.google.common.collect.TreeBasedTable;
import org.apache.commons.beanutils.BasicDynaBean;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import com.picsauditing.EntityFactory;
import com.picsauditing.PicsTestUtil;
import com.picsauditing.PICS.I18nCache;
import com.picsauditing.access.Permissions;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.jpa.entities.BaseTable;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.Employee;
import com.picsauditing.jpa.entities.EmployeeCompetency;
import com.picsauditing.jpa.entities.OperatorCompetency;
import com.picsauditing.report.RecordNotFoundException;
import com.picsauditing.search.Database;

public class EmployeeCompetenciesTest {
	private EmployeeCompetencies employeeCompetencies;

	@Mock
	private BasicDynaBean basicDynaBean;
	@Mock
	private Database database;
	@Mock
	private EntityManager entityManager;
	@Mock
	private Permissions permissions;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		Whitebox.setInternalState(I18nCache.class, "databaseForTesting", database);

		employeeCompetencies = new EmployeeCompetencies();
		PicsTestUtil picsTestUtil = new PicsTestUtil();
		picsTestUtil.autowireEMInjectedDAOs(employeeCompetencies, entityManager);

		Whitebox.setInternalState(employeeCompetencies, "permissions", permissions);
	}

	@AfterClass
	public static void classTearDown() {
		Whitebox.setInternalState(I18nCache.class, "databaseForTesting", (Database) null);
	}

    /* This is basically a test of the Employee comparitor, but I've put it here because it was a defect
       in the EmployeeCompetencies use of TreeBasedTable (PICS-10080)
     */
    @Test
    public void testTable() throws Exception {
        Table<Employee, OperatorCompetency, EmployeeCompetency> employeeCompetencyTable = TreeBasedTable.create();
        Account account = mock(Account.class);
        when(account.getId()).thenReturn(343);

        Employee employee = createEmployee(account, 180);
        OperatorCompetency competency = createOperatorCompetency();
        EmployeeCompetency employeeCompetency = createEmployeeCompetency(employee, competency, 49306);

        employeeCompetencyTable.put(employee, competency, employeeCompetency);

        Employee employee2 = createEmployee(account, 7316);
        OperatorCompetency competency2 = createOperatorCompetency();
        EmployeeCompetency employeeCompetency2 = createEmployeeCompetency(employee, competency, 37085);

        employeeCompetencyTable.put(employee2, competency2, employeeCompetency2);

        assertTrue(employeeCompetencyTable.size() == 2);
    }

    private EmployeeCompetency createEmployeeCompetency(Employee employee, OperatorCompetency competency, int id) {
        EmployeeCompetency employeeCompetency = new EmployeeCompetency();
        employeeCompetency.setSkilled(false);
        employeeCompetency.setId(id);
        employeeCompetency.setEmployee(employee);
        employeeCompetency.setCompetency(competency);
        return employeeCompetency;
    }

    private OperatorCompetency createOperatorCompetency() {
        OperatorCompetency competency = new OperatorCompetency();
        competency.setId(3);
        competency.setCategory("Personal Safety");
        competency.setLabel("Height Work");
        competency.setDescription("Working practices for working at height and the use of equipment and specialist PPE.");
        return competency;
    }

    private Employee createEmployee(Account account, int id) {
        Employee employee = new Employee();
        employee.setAccount(account);
        employee.setId(id);
        employee.setLastName("Biggs");
        employee.setFirstName("Michael");
        return employee;
    }

    @Test
	public void testChangeCompetency_Existing() throws Exception {
		Employee employee = EntityFactory.makeEmployee(loggedInContractor());
		OperatorCompetency competency = new OperatorCompetency();
		competency.setLabel("Label");

		EmployeeCompetency employeeCompetency = new EmployeeCompetency();
		employeeCompetency.setCompetency(competency);
		employeeCompetency.setEmployee(employee);
		employeeCompetency.setId(1);
		employeeCompetency.setSkilled(true);

		employee.getEmployeeCompetencies().add(employeeCompetency);

		employeeCompetencies.setEmployee(employee);
		employeeCompetencies.setCompetency(competency);

		employeeCompetencies.changeCompetency();

		assertTrue(employeeCompetencies.hasActionMessages());
		assertFalse(employeeCompetencies.hasActionErrors());
		assertFalse(employeeCompetencies.getEmployee().getEmployeeCompetencies().isEmpty());
		assertFalse(employeeCompetencies.getEmployee().getEmployeeCompetencies().get(0).isSkilled());

		verify(entityManager).merge(any(BaseTable.class));
		verify(entityManager, never()).persist(any(BaseTable.class));
	}

	@Test
	public void testChangeCompetency_NoMatch() throws Exception {
		Employee employee = EntityFactory.makeEmployee(loggedInContractor());
		OperatorCompetency competency = new OperatorCompetency();
		competency.setLabel("Label");

		EmployeeCompetency employeeCompetency = new EmployeeCompetency();
		employeeCompetency.setCompetency(new OperatorCompetency());
		employeeCompetency.setEmployee(employee);
		employeeCompetency.setId(1);
		employeeCompetency.setSkilled(true);

		employee.getEmployeeCompetencies().add(employeeCompetency);

		employeeCompetencies.setEmployee(employee);
		employeeCompetencies.setCompetency(competency);

		employeeCompetencies.changeCompetency();

		assertTrue(employeeCompetencies.hasActionMessages());
		assertFalse(employeeCompetencies.hasActionErrors());
		assertFalse(employeeCompetencies.getEmployee().getEmployeeCompetencies().isEmpty());
		assertTrue(employeeCompetencies.getEmployee().getEmployeeCompetencies().get(0).isSkilled());

		verify(entityManager).persist(any(BaseTable.class));
		verify(entityManager, never()).merge(any(BaseTable.class));
	}

	@Test
	public void testChangeCompetency_New() throws Exception {
		Employee employee = EntityFactory.makeEmployee(EntityFactory.makeContractor());
		OperatorCompetency competency = new OperatorCompetency();

		employeeCompetencies.setEmployee(employee);
		employeeCompetencies.setCompetency(competency);

		employeeCompetencies.changeCompetency();

		assertTrue(employeeCompetencies.hasActionMessages());
		assertFalse(employeeCompetencies.hasActionErrors());
		assertFalse(employeeCompetencies.getEmployee().getEmployeeCompetencies().isEmpty());
		assertTrue(employeeCompetencies.getEmployee().getEmployeeCompetencies().get(0).isSkilled());

		verify(entityManager).persist(any(BaseTable.class));
		verify(entityManager, never()).merge(any(BaseTable.class));
	}

	@Test
	public void testChangeCompetency_MissingComponent() throws Exception {
		employeeCompetencies.changeCompetency();

		assertTrue(employeeCompetencies.hasActionErrors());
		assertFalse(employeeCompetencies.hasActionMessages());

		verify(entityManager, never()).persist(any(BaseTable.class));
		verify(entityManager, never()).merge(any(BaseTable.class));
	}

	@Test(expected = RecordNotFoundException.class)
	public void testFindAccount() throws Exception {
		Whitebox.invokeMethod(employeeCompetencies, "findAccount");
	}

	@Test
	public void testFindAccount_Audit() throws Exception {
		ContractorAccount contractor = EntityFactory.makeContractor();
		ContractorAudit audit = EntityFactory.makeContractorAudit(AuditType.HSE_COMPETENCY, contractor);

		employeeCompetencies.setAudit(audit);

		Whitebox.invokeMethod(employeeCompetencies, "findAccount");
		assertEquals(contractor, employeeCompetencies.getAccount());
	}

	@Test
	public void testFindAccount_Contractor() throws Exception {
		ContractorAccount contractor = loggedInContractor();

		Whitebox.invokeMethod(employeeCompetencies, "findAccount");
		assertEquals(contractor, employeeCompetencies.getAccount());
	}

	@Test
	public void testBuildEmployeeForMap() throws Exception {
		when(basicDynaBean.get("employeeID")).thenReturn(1);
		when(basicDynaBean.get("firstName")).thenReturn("First");
		when(basicDynaBean.get("lastName")).thenReturn("Last");

		Employee employee = Whitebox.invokeMethod(employeeCompetencies, "buildEmployeeForMap", basicDynaBean);

		assertFalse(employeeCompetencies.getEmployees().isEmpty());
		assertNotNull(employee);
		assertEquals(1, employee.getId());
		assertEquals("First", employee.getFirstName());
		assertEquals("Last", employee.getLastName());
	}

	@Test
	public void testBuildCompetencyForMap() throws Exception {
		when(basicDynaBean.get("competencyID")).thenReturn(1);
		when(basicDynaBean.get("category")).thenReturn("Category");
		when(basicDynaBean.get("description")).thenReturn("Description");
		when(basicDynaBean.get("label")).thenReturn("Label");

		OperatorCompetency competency = Whitebox.invokeMethod(employeeCompetencies, "buildCompetencyForMap",
				basicDynaBean);

		assertFalse(employeeCompetencies.getCompetencies().isEmpty());
		assertNotNull(competency);
		assertEquals(1, competency.getId());
		assertEquals("Category", competency.getCategory());
		assertEquals("Description", competency.getDescription());
		assertEquals("Label", competency.getLabel());
	}

	@Test
	public void testBuildEmployeeCompetencyForMap_NoRecord() throws Exception {
		EmployeeCompetency employeeCompetency = Whitebox.invokeMethod(employeeCompetencies,
				"buildEmployeeCompetencyForMap", basicDynaBean, (Employee) null, (OperatorCompetency) null);

		assertNotNull(employeeCompetency);
		assertEquals(0, employeeCompetency.getId());
	}

	@Test
	public void testBuildEmployeeCompetencyForMap_WithRecord() throws Exception {
		Employee employee = EntityFactory.makeEmployee(loggedInContractor());
		OperatorCompetency competency = new OperatorCompetency();

		when(basicDynaBean.get("ecID")).thenReturn(1);
		when(basicDynaBean.get("skilled")).thenReturn("1");

		EmployeeCompetency employeeCompetency = Whitebox.invokeMethod(employeeCompetencies,
				"buildEmployeeCompetencyForMap", basicDynaBean, employee, competency);

		assertNotNull(employeeCompetency);
		assertEquals(1, employeeCompetency.getId());
		assertTrue(employeeCompetency.isSkilled());
		assertEquals(employee, employeeCompetency.getEmployee());
		assertEquals(competency, employeeCompetency.getCompetency());
	}

	@Test
	public void testFillEmployeeJobRoles_3JobRoles() throws Exception {
		Employee employee = EntityFactory.makeEmployee(loggedInContractor());
		Set<String> roles = new TreeSet<String>();

		roles.add("Role 1");
		roles.add("Role 2");
		roles.add("Role 3");

		Map<Employee, Set<String>> jobRoles = new TreeMap<Employee, Set<String>>();
		jobRoles.put(employee, roles);

		employeeCompetencies.setEmployee(employee);
		Whitebox.invokeMethod(employeeCompetencies, "fillEmployeeJobRoles", jobRoles);

		assertFalse(employeeCompetencies.getEmployeeJobRoles().isEmpty());
		assertEquals("Role 1, Role 2, Role 3", employeeCompetencies.getEmployeeJobRoles().get(employee));
	}

	@Test
	public void testFillEmployeeJobRoles_GreaterThan3JobRoles() throws Exception {
		Employee employee = EntityFactory.makeEmployee(loggedInContractor());
		Set<String> roles = new TreeSet<String>();

		roles.add("Role 1");
		roles.add("Role 2");
		roles.add("Role 3");
		roles.add("Role 4");

		Map<Employee, Set<String>> jobRoles = new TreeMap<Employee, Set<String>>();
		jobRoles.put(employee, roles);

		employeeCompetencies.setEmployee(employee);
		Whitebox.invokeMethod(employeeCompetencies, "fillEmployeeJobRoles", jobRoles);

		assertFalse(employeeCompetencies.getEmployeeJobRoles().isEmpty());
		assertEquals("Role 1, Role 2, Role 3...", employeeCompetencies.getEmployeeJobRoles().get(employee));
	}

	private ContractorAccount loggedInContractor() {
		ContractorAccount contractor = EntityFactory.makeContractor();

		when(entityManager.find(Account.class, contractor.getId())).thenReturn(contractor);
		when(permissions.getAccountId()).thenReturn(contractor.getId());
		when(permissions.isContractor()).thenReturn(true);

		return contractor;
	}
}