package com.picsauditing.dao;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.picsauditing.jpa.entities.Employee;

public class EmployeeDAOTest {
	
	@Test
	public void testEmployeePicsNumber() {
		Employee employee = new Employee();
		employee.setId(943148);
		String value = EmployeeDAO.generatePicsNumber(employee);
		assertEquals(11,value.length());
		assertEquals("-",value.substring(5,6));
	}

}
