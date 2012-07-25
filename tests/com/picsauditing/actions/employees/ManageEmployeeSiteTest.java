package com.picsauditing.actions.employees;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Calendar;
import java.util.Date;

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
import com.picsauditing.jpa.entities.EmployeeSite;
import com.picsauditing.jpa.entities.JobSite;
import com.picsauditing.jpa.entities.Note;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.search.Database;

public class ManageEmployeeSiteTest {
	private ManageEmployeeSite manageEmployeeSite;

	@Mock
	private Database databaseForTesting;
	@Mock
	private EntityManager entityManager;
	@Mock
	private Permissions permissions;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		Whitebox.setInternalState(I18nCache.class, "databaseForTesting", databaseForTesting);

		manageEmployeeSite = new ManageEmployeeSite();

		PicsTestUtil picsTestUtil = new PicsTestUtil();
		picsTestUtil.autowireEMInjectedDAOs(manageEmployeeSite, entityManager);

		Whitebox.setInternalState(manageEmployeeSite, "permissions", permissions);
	}

	@AfterClass
	public static void classTearDown() {
		Whitebox.setInternalState(I18nCache.class, "databaseForTesting", (Database) null);
	}

	@Test
	public void testAdd() {
		assertEquals(ActionSupport.SUCCESS, manageEmployeeSite.add());
	}

	@Test
	public void testAdd_Employee() {
		Employee employee = EntityFactory.makeEmployee(null);

		manageEmployeeSite.setEmployee(employee);

		assertEquals(ActionSupport.SUCCESS, manageEmployeeSite.add());
	}

	@Test
	public void testAdd_Operator() {
		OperatorAccount operatorAccount = EntityFactory.makeOperator();

		manageEmployeeSite.setOperator(operatorAccount);

		assertEquals(ActionSupport.SUCCESS, manageEmployeeSite.add());
	}

	@Test
	public void testAdd_EmployeeOperator() {
		OperatorAccount operatorAccount = EntityFactory.makeOperator();
		Employee employee = EntityFactory.makeEmployee(operatorAccount);

		manageEmployeeSite.setEmployee(employee);
		manageEmployeeSite.setOperator(operatorAccount);

		assertEquals(ActionSupport.SUCCESS, manageEmployeeSite.add());

		verify(entityManager, times(2)).persist(any(BaseTable.class));
	}

	@Test
	public void testAdd_EmployeeJobsite() {
		OperatorAccount operatorAccount = EntityFactory.makeOperator();
		Employee employee = EntityFactory.makeEmployee(operatorAccount);

		JobSite jobSite = new JobSite();
		jobSite.setId(1);
		jobSite.setOperator(operatorAccount);

		manageEmployeeSite.setEmployee(employee);
		manageEmployeeSite.setJobSite(jobSite);

		assertEquals(ActionSupport.SUCCESS, manageEmployeeSite.add());

		verify(entityManager, times(2)).persist(any(BaseTable.class));
		assertFalse(manageEmployeeSite.getEmployee().getEmployeeSites().isEmpty());
		assertEquals(jobSite, manageEmployeeSite.getEmployee().getEmployeeSites().get(0).getJobSite());
	}

	@Test
	public void testAddNew() {
		assertEquals("new", manageEmployeeSite.addNew());

		neverMergedOrPersisted();
	}

	@Test
	public void testAddNew_LabelNotEmptyNameEmpty() {
		manageEmployeeSite.setJobSite(new JobSite());
		manageEmployeeSite.getJobSite().setLabel("Label");

		assertEquals("new", manageEmployeeSite.addNew());

		neverMergedOrPersisted();
	}

	@Test
	public void testAddNew_LabelEmptyNameNotEmpty() {
		manageEmployeeSite.setJobSite(new JobSite());
		manageEmployeeSite.getJobSite().setName("Name");

		assertEquals("new", manageEmployeeSite.addNew());

		neverMergedOrPersisted();
	}

	@Test
	public void testAddNew_LabelName() {
		manageEmployeeSite.setJobSite(new JobSite());
		manageEmployeeSite.getJobSite().setId(1);
		manageEmployeeSite.getJobSite().setName("Name");
		manageEmployeeSite.getJobSite().setLabel("Label");

		manageEmployeeSite.setEmployee(EntityFactory.makeEmployee(null));
		manageEmployeeSite.setOperator(EntityFactory.makeOperator());

		assertEquals(ActionSupport.SUCCESS, manageEmployeeSite.addNew());
		assertEquals(manageEmployeeSite.getJobSite(), manageEmployeeSite.getEmployeeSite().getJobSite());

		verify(entityManager).merge(any(BaseTable.class));
		verify(entityManager).persist(any(BaseTable.class));
	}

	@Test
	public void testEdit() {
		assertEquals("edit", manageEmployeeSite.edit());

		neverMergedOrPersisted();
	}

	@Test
	public void testExpire() {
		assertEquals(ActionSupport.SUCCESS, manageEmployeeSite.expire());

		neverMergedOrPersisted();
	}

	@Test
	public void testExpire_EmployeeSiteExpired() {
		JobSite jobSite = new JobSite();
		jobSite.setOperator(EntityFactory.makeOperator());
		jobSite.setLabel("Job Site");

		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.YEAR, -1);

		EmployeeSite employeeSite = new EmployeeSite();
		employeeSite.setEmployee(EntityFactory.makeEmployee(null));
		employeeSite.setId(1);
		employeeSite.setOperator(jobSite.getOperator());
		employeeSite.setEffectiveDate(calendar.getTime());
		employeeSite.setExpirationDate(new Date());

		manageEmployeeSite.setEmployeeSite(employeeSite);

		assertEquals(ActionSupport.SUCCESS, manageEmployeeSite.expire());
		assertFalse(employeeSite.isCurrent());

		verify(entityManager).merge(any(EmployeeSite.class));
		verify(entityManager).persist(any(Note.class));
		verify(entityManager, never()).remove(any(EmployeeSite.class));
	}

	@Test
	public void testExpire_EmployeeSiteNotExpired() {
		JobSite jobSite = new JobSite();
		jobSite.setOperator(EntityFactory.makeOperator());
		jobSite.setLabel("Job Site");

		EmployeeSite employeeSite = new EmployeeSite();
		employeeSite.setEmployee(EntityFactory.makeEmployee(null));
		employeeSite.setId(1);
		employeeSite.setOperator(jobSite.getOperator());
		employeeSite.defaultDates();

		manageEmployeeSite.setEmployeeSite(employeeSite);

		assertEquals(ActionSupport.SUCCESS, manageEmployeeSite.expire());
		assertFalse(employeeSite.isCurrent());

		verify(entityManager).merge(any(EmployeeSite.class));
		verify(entityManager).persist(any(Note.class));
		verify(entityManager, never()).remove(any(EmployeeSite.class));
	}

	@Test
	public void testSave() {
		assertEquals(ActionSupport.SUCCESS, manageEmployeeSite.save());

		neverMergedOrPersisted();
	}

	@Test
	public void testSave_EmployeeSite() {
		EmployeeSite employeeSite = new EmployeeSite();
		employeeSite.setId(1);
		employeeSite.setEmployee(EntityFactory.makeEmployee(EntityFactory.makeContractor()));

		manageEmployeeSite.setEmployeeSite(employeeSite);

		when(entityManager.find(EmployeeSite.class, employeeSite.getId())).thenReturn(employeeSite);

		assertEquals(ActionSupport.SUCCESS, manageEmployeeSite.save());
		assertEquals(employeeSite.getEmployee(), manageEmployeeSite.getEmployee());
		assertEquals(employeeSite.getEmployee().getAccount(), manageEmployeeSite.getAccount());

		verify(entityManager).merge(any(EmployeeSite.class));
		verify(entityManager).persist(any(Note.class));
		verify(entityManager, never()).remove(any(BaseTable.class));
	}

	private void neverMergedOrPersisted() {
		verify(entityManager, never()).merge(any(BaseTable.class));
		verify(entityManager, never()).persist(any(BaseTable.class));
	}
}
