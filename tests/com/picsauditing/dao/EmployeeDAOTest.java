package com.picsauditing.dao;

import java.util.Date;
import java.util.List;

import junit.framework.TestCase;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import com.picsauditing.PICS.DateBean;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.Employee;
import com.picsauditing.jpa.entities.EmployeeClassification;
import com.picsauditing.jpa.entities.User;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/tests.xml")
@TransactionConfiguration(defaultRollback = true)
@Transactional
public class EmployeeDAOTest extends TestCase {
	@Autowired
	private EmployeeDAO employeeDAO;

	@Test
	public void testFind() throws Exception {
		Employee employee = employeeDAO.find(8);

		assertNotNull(employee);
	}

	@Test
	public void testFindWhere() throws Exception {
		List<Employee> employees = employeeDAO.findWhere("", 10);

		assertTrue(employees.size() == 10);
	}

	@Test
	public void testSave() throws Exception {
		Employee e = new Employee();
		e.setFirstName("Kyle");
		e.setLastName("P");
		e.setHireDate(new Date());
		e.setBirthDate(DateBean.parseDate("2010-03-10"));
		e.setSsn("999999999");
		e.setAuditColumns(new User(2357));
		e.setAccount(new Account());
		e.getAccount().setId(1100);
		e.setClassification(EmployeeClassification.FullTime);

		employeeDAO.save(e);

		List<Employee> employees = employeeDAO.findWhere("firstName = 'Kyle' and birthDate = '2010-03-10'");

		for (Employee employee : employees) {
			assertTrue(employee.getAccount().getId() == 1100);
			assertTrue(employee.isActive());
		}
	}
}
