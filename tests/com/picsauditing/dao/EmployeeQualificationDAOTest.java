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

import com.picsauditing.jpa.entities.EmployeeQualification;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/tests.xml")
@TransactionConfiguration(defaultRollback = true)
@Transactional
public class EmployeeQualificationDAOTest extends TestCase {
	@Autowired
	private EmployeeQualificationDAO qualificationDAO;

	@Test
	public void testFind() {
		EmployeeQualification qualification = qualificationDAO.find(1);

		assertNotNull(qualification);
	}

	@Test
	public void testFindByEmployee() {
		List<EmployeeQualification> qualifications = qualificationDAO.findByEmployee(8);

		assertTrue(qualifications.size() > 0);
	}
	
	@Test
	public void testFindByTask() {
		List<EmployeeQualification> qualifications = qualificationDAO.findByTask(1);

		assertTrue(qualifications.size() > 0);
	}
}
