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

import com.picsauditing.jpa.entities.EmployeeAssessmentAuthorization;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/tests.xml")
@TransactionConfiguration(defaultRollback = true)
@Transactional
public class EmployeeAssessmentAuthorizationDAOTest extends TestCase {
	@Autowired
	private EmployeeAssessmentAuthorizationDAO authDAO;

	@Test
	public void testFind() {
		EmployeeAssessmentAuthorization result = authDAO.find(1);

		assertNotNull(result);
	}
	
	@Test
	public void testFindByEmployee() {
		List<EmployeeAssessmentAuthorization> results = authDAO.findByEmployee(108);

		assertTrue(results.size() > 0);
	}
	
	@Test
	public void testFindAll() {
		List<EmployeeAssessmentAuthorization> all = authDAO.findAll();
		
		assertTrue(all.size() > 0);
	}
}