package com.picsauditing.actions.employees;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import com.picsauditing.PicsTranslationTest;
import com.picsauditing.access.Permissions;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.ContractorOperatorDAO;
import com.picsauditing.dao.NoteDAO;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.ContractorOperator;
import com.picsauditing.jpa.entities.Employee;
import com.picsauditing.jpa.entities.EmployeeSite;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.report.RecordNotFoundException;

public class EmployeeDetailTest extends PicsTranslationTest {
	public static final int VISIBLE = 1;
	public static final int NOT_VISIBLE = 123;
	private EmployeeDetail employeeDetail;

	@Mock
	private Account account;
	@Mock
	private ContractorOperatorDAO contractorOperatorDAO;
	@Mock
	private Employee employee;
	@Mock
	private Permissions permissions;
	@Mock
	private NoteDAO noteDAO;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		employeeDetail = new EmployeeDetail();
		employeeDetail.setEmployee(employee);

		Whitebox.setInternalState(employeeDetail, "coDAO", contractorOperatorDAO);
		Whitebox.setInternalState(employeeDetail, "noteDao", noteDAO);
		Whitebox.setInternalState(employeeDetail, "permissions", permissions);

		when(account.getId()).thenReturn(VISIBLE);
		when(employee.getAccount()).thenReturn(account);
		when(employee.getId()).thenReturn(VISIBLE);
	}

	@Test(expected = RecordNotFoundException.class)
	public void testExecute_EmployeeIsNull() throws Exception {
		employeeDetail.setEmployee(null);
		employeeDetail.execute();
	}

	@Test(expected = RecordNotFoundException.class)
	public void testExecute_EmployeeIDIsZero() throws Exception {
		when(employee.getId()).thenReturn(0);
		employeeDetail.execute();
	}

	@Test
	public void testExecute_EmployeeValid() throws Exception {
		assertEquals(PicsActionSupport.SUCCESS, employeeDetail.execute());

		verify(account).getId();
		verify(employee, times(2)).getId();
		verify(noteDAO).findWhere(anyInt(), anyString(), anyInt());
	}

	@Test
	public void testIsCanViewContractor_ContractorUserWithEmployeeUnderContractor() throws Exception {
		when(permissions.getAccountId()).thenReturn(VISIBLE);
		when(permissions.isContractor()).thenReturn(true);
		assertTrue(employeeDetail.isCanViewContractor());
	}

	@Test
	public void testIsCanViewContractor_ContractorUserWithEmployeeNotUnderContractor() throws Exception {
		when(permissions.getAccountId()).thenReturn(NOT_VISIBLE);
		when(permissions.isContractor()).thenReturn(true);
		assertFalse(employeeDetail.isCanViewContractor());
	}

	@Test
	public void testIsCanViewContractor_OperatorCorporateLinkedToContractor() throws Exception {
		when(permissions.getAccountId()).thenReturn(NOT_VISIBLE);
		when(permissions.isOperatorCorporate()).thenReturn(true);
		when(contractorOperatorDAO.find(VISIBLE, NOT_VISIBLE)).thenReturn(mock(ContractorOperator.class));
		assertTrue(employeeDetail.isCanViewContractor());
	}

	@Test
	public void testIsCanViewContractor_OperatorCorporateNotLinkedToContractor() throws Exception {
		when(permissions.getAccountId()).thenReturn(NOT_VISIBLE);
		when(permissions.isOperatorCorporate()).thenReturn(true);
		assertFalse(employeeDetail.isCanViewContractor());
	}

	@Test
	public void testIsCanViewContractor_PicsEmployee() throws Exception {
		when(permissions.isPicsEmployee()).thenReturn(true);
		assertTrue(employeeDetail.isCanViewContractor());
	}

	@Test
	public void testIsCanViewOperator_Contractor() throws Exception {
		when(permissions.isContractor()).thenReturn(true);
		assertFalse(employeeDetail.isCanViewOperator());
	}

	@Test
	public void testIsCanViewOperator_OperatorNotInUmbrella() throws Exception {
		Set<Integer> visibleAccounts = new HashSet<>();
		visibleAccounts.add(NOT_VISIBLE);

		when(permissions.isOperatorCorporate()).thenReturn(true);
		when(permissions.getVisibleAccounts()).thenReturn(visibleAccounts);

		assertFalse(employeeDetail.isCanViewOperator());
	}

	@Test
	public void testIsCanViewOperator_OperatorInUmbrella() throws Exception {
		Set<Integer> visibleAccounts = new HashSet<>();
		visibleAccounts.add(VISIBLE);

		when(permissions.isOperatorCorporate()).thenReturn(true);
		when(permissions.getVisibleAccounts()).thenReturn(visibleAccounts);

		assertTrue(employeeDetail.isCanViewOperator());
	}

	@Test
	public void testIsCanViewOperator_PicsEmployee() throws Exception {
		when(permissions.isPicsEmployee()).thenReturn(true);
		assertTrue(employeeDetail.isCanViewOperator());
	}

	@Test
	public void testGetWorksAt_OtherOperatorCorporateVisible() throws Exception {
		setUpOtherOperatorEmployeeSite(true);
		List<EmployeeSite> worksAt = employeeDetail.getWorksAt();
		assertFalse(worksAt.isEmpty());
		assertEquals(1, worksAt.size());
	}

	@Test
	public void testGetWorksAt_OtherOperatorCorporateNotVisible() throws Exception {
		setUpOtherOperatorEmployeeSite(false);
		List<EmployeeSite> worksAt = employeeDetail.getWorksAt();
		assertTrue(worksAt.isEmpty());
	}

	private void setUpOtherOperatorEmployeeSite(boolean visible) {
		EmployeeSite visibleToOperator = mock(EmployeeSite.class);
		OperatorAccount operatorAccount = mock(OperatorAccount.class);

		List<EmployeeSite> sites = new ArrayList<>();
		sites.add(visibleToOperator);

		Set<Integer> visibleAccounts = new HashSet<>();
		visibleAccounts.add(VISIBLE);

		when(employee.getEmployeeSites()).thenReturn(sites);
		when(operatorAccount.getId()).thenReturn(visible ? VISIBLE : NOT_VISIBLE);
		when(permissions.getVisibleAccounts()).thenReturn(visibleAccounts);
		when(permissions.isOperatorCorporate()).thenReturn(true);
		when(visibleToOperator.getOperator()).thenReturn(operatorAccount);
		when(visibleToOperator.isActive()).thenReturn(true);
	}

	@Test
	public void testGetWorksAt_InactiveSite() throws Exception {
		EmployeeSite inactive = mock(EmployeeSite.class);

		List<EmployeeSite> sites = new ArrayList<>();
		sites.add(inactive);

		when(employee.getEmployeeSites()).thenReturn(sites);
		when(inactive.isActive()).thenReturn(false);

		List<EmployeeSite> worksAt = employeeDetail.getWorksAt();
		assertTrue(worksAt.isEmpty());
	}

	@Test
	public void testGetWorksAt_PicsEmployee() throws Exception {
		EmployeeSite active = mock(EmployeeSite.class);
		EmployeeSite inactive = mock(EmployeeSite.class);

		List<EmployeeSite> sites = new ArrayList<>();
		sites.add(active);
		sites.add(inactive);

		when(active.isActive()).thenReturn(true);
		when(employee.getEmployeeSites()).thenReturn(sites);
		when(permissions.isPicsEmployee()).thenReturn(true);

		List<EmployeeSite> worksAt = employeeDetail.getWorksAt();
		assertFalse(worksAt.isEmpty());
		assertEquals(1, worksAt.size());
		assertTrue(worksAt.contains(active));
	}

	@Test
	public void testGetWorksAt_Contractor() throws Exception {
		EmployeeSite active = mock(EmployeeSite.class);
		EmployeeSite inactive = mock(EmployeeSite.class);

		List<EmployeeSite> sites = new ArrayList<>();
		sites.add(active);
		sites.add(inactive);

		when(active.isActive()).thenReturn(true);
		when(employee.getEmployeeSites()).thenReturn(sites);
		when(permissions.getAccountId()).thenReturn(VISIBLE);
		when(permissions.isContractor()).thenReturn(true);

		List<EmployeeSite> worksAt = employeeDetail.getWorksAt();
		assertFalse(worksAt.isEmpty());
		assertEquals(1, worksAt.size());
		assertTrue(worksAt.contains(active));
	}
}
