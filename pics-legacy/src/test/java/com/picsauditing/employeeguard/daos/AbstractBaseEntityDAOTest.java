package com.picsauditing.employeeguard.daos;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import javax.persistence.EntityManager;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import com.picsauditing.employeeguard.entities.Employee;
import com.picsauditing.employeeguard.entities.builders.EmployeeBuilder;

public class AbstractBaseEntityDAOTest {

	private BaseEntityDAOImpl baseEntityDAOImpl;

	@Mock
	private EntityManager entityManager;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		baseEntityDAOImpl = new BaseEntityDAOImpl();
		Whitebox.setInternalState(baseEntityDAOImpl, "em", entityManager);
	}

	@Test
	public void testFind() {
		when(entityManager.find(Employee.class, 1)).thenReturn(new EmployeeBuilder().firstName("Bob").build());

		Employee employee = baseEntityDAOImpl.find(1);

		verify(entityManager).find(any(Class.class), anyObject());
		assertEquals("Bob", employee.getFirstName());
	}

	@Test
	public void testPersist() {
		Employee employee = baseEntityDAOImpl.save(new EmployeeBuilder().firstName("Bob").build());

		verify(entityManager).persist(any(Employee.class));
		verify(entityManager).flush();
		assertEquals("Bob", employee.getFirstName());
	}

	@Test
	public void testMerge() {
		Employee employee = new EmployeeBuilder().firstName("Bob").id(10).build();
		when(entityManager.merge(employee)).thenReturn(employee);

		employee = baseEntityDAOImpl.save(employee);

		verify(entityManager).merge(employee);
		verify(entityManager).flush();
		assertEquals("Bob", employee.getFirstName());
	}

	@Test
	public void testDelete() {
		Employee employee = new EmployeeBuilder().firstName("Bob").id(10).build();

		baseEntityDAOImpl.delete(employee);

		verify(entityManager).remove(employee);
		verify(entityManager).flush();
	}

	@Test
	public void testDetach() {
		Employee employee = new EmployeeBuilder().firstName("Bob").id(10).build();

		baseEntityDAOImpl.detach(employee);

		verify(entityManager).detach(employee);
	}

	@Test
	public void testRefresh() {
		Employee employee = new EmployeeBuilder().firstName("Bob").id(10).build();

		baseEntityDAOImpl.refresh(employee);

		verify(entityManager).refresh(employee);
	}

	private class BaseEntityDAOImpl extends AbstractBaseEntityDAO<Employee> {
		public BaseEntityDAOImpl() {
			this.type = Employee.class;
		}
	}

}
