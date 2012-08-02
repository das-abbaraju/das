package com.picsauditing.actions.employees;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import javax.persistence.EntityManager;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import com.opensymphony.xwork2.ActionSupport;
import com.picsauditing.EntityFactory;
import com.picsauditing.PicsTestUtil;
import com.picsauditing.PICS.I18nCache;
import com.picsauditing.access.Permissions;
import com.picsauditing.jpa.entities.BaseTable;
import com.picsauditing.jpa.entities.Employee;
import com.picsauditing.jpa.entities.EmployeeRole;
import com.picsauditing.jpa.entities.JobRole;
import com.picsauditing.jpa.entities.Note;
import com.picsauditing.search.Database;

public class ManageEmployeeRoleTest {
	private ManageEmployeeRole manageEmployeeRole;

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

		manageEmployeeRole = new ManageEmployeeRole();

		PicsTestUtil picsTestUtil = new PicsTestUtil();
		picsTestUtil.autowireEMInjectedDAOs(manageEmployeeRole, entityManager);

		Whitebox.setInternalState(manageEmployeeRole, "permissions", permissions);
	}

	@AfterClass
	public static void classTearDown() {
		Whitebox.setInternalState(I18nCache.class, "databaseForTesting", (Database) null);
	}

	@Test
	public void testAdd() {
		assertEquals(ActionSupport.SUCCESS, manageEmployeeRole.add());

		neverMergedOrPersisted();
	}

	@Test
	public void testAdd_Employee() {
		manageEmployeeRole.setEmployee(EntityFactory.makeEmployee(null));

		assertEquals(ActionSupport.SUCCESS, manageEmployeeRole.add());

		neverMergedOrPersisted();
	}

	@Test
	public void testAdd_EmployeeJobRole_New() {
		manageEmployeeRole.setEmployee(EntityFactory.makeEmployee(null));

		JobRole jobRole = new JobRole();
		jobRole.setId(1);

		manageEmployeeRole.setJobRole(jobRole);

		assertEquals(ActionSupport.SUCCESS, manageEmployeeRole.add());

		// One for EmployeeRole, another for Note
		verify(entityManager, times(2)).persist(any(BaseTable.class));
	}

	@Test
	public void testAdd_EmployeeJobRole_Existing() {
		manageEmployeeRole.setEmployee(EntityFactory.makeEmployee(null));

		JobRole jobRole = new JobRole();
		jobRole.setId(1);

		EmployeeRole employeeRole = new EmployeeRole();
		employeeRole.setEmployee(manageEmployeeRole.getEmployee());
		employeeRole.setJobRole(jobRole);

		manageEmployeeRole.getEmployee().getEmployeeRoles().add(employeeRole);
		manageEmployeeRole.setJobRole(jobRole);

		assertEquals(ActionSupport.SUCCESS, manageEmployeeRole.add());
		assertTrue(manageEmployeeRole.hasActionErrors());

		verify(entityManager, never()).persist(any(BaseTable.class));
	}

	@Test
	public void testRemove() {
		assertEquals(ActionSupport.SUCCESS, manageEmployeeRole.remove());
		verify(entityManager, never()).remove(eq(EmployeeRole.class));
	}

	@Test
	public void testRemove_Employee() {
		Employee employee = EntityFactory.makeEmployee(null);

		manageEmployeeRole.setEmployee(employee);

		assertEquals(ActionSupport.SUCCESS, manageEmployeeRole.remove());

		verify(entityManager, never()).remove(eq(EmployeeRole.class));
	}

	@Test
	public void testRemove_Role() {
		JobRole jobRole = new JobRole();
		jobRole.setName("Job Role");

		EmployeeRole employeeRole = new EmployeeRole();
		employeeRole.setEmployee(EntityFactory.makeEmployee(EntityFactory.makeContractor()));
		employeeRole.setJobRole(jobRole);

		manageEmployeeRole.setEmployeeRole(employeeRole);

		assertEquals(ActionSupport.SUCCESS, manageEmployeeRole.remove());

		verify(entityManager).persist(any(Note.class));
		verify(entityManager).remove(any(EmployeeRole.class));
	}

	@Test
	public void testGetEmployeeRole() {
		EmployeeRole employeeRole = new EmployeeRole();
		employeeRole.setEmployee(new Employee());
		employeeRole.setJobRole(new JobRole());

		manageEmployeeRole.setEmployeeRole(employeeRole);

		assertEquals(employeeRole, manageEmployeeRole.getEmployeeRole());
	}

	@Test
	public void testGetJobRole() {
		JobRole jobRole = new JobRole();

		manageEmployeeRole.setJobRole(jobRole);

		assertEquals(jobRole, manageEmployeeRole.getJobRole());
	}

	private void neverMergedOrPersisted() {
		verify(entityManager, never()).merge(any(BaseTable.class));
		verify(entityManager, never()).persist(any(BaseTable.class));
	}
}
