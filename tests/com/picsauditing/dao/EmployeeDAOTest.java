package com.picsauditing.dao;

import java.util.List;

import junit.framework.TestCase;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import com.picsauditing.jpa.entities.Employee;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/tests.xml")
@TransactionConfiguration(defaultRollback = true)
@Transactional
public class EmployeeDAOTest extends TestCase {
	@Autowired
	private EmployeeDAO employeeDAO;

	@Test
	public void testFind() {
		Employee employee = employeeDAO.find(1);

		assertNotNull(employee);
	}

	@Test
	public void testFindAll() {
		List<Employee> employees = employeeDAO.findAll();

		assertTrue(employees.size() > 0);
	}
}
