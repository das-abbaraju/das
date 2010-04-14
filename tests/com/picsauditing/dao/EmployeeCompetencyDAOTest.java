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

import com.picsauditing.jpa.entities.EmployeeCompetency;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/tests.xml")
@TransactionConfiguration(defaultRollback = true)
@Transactional
public class EmployeeCompetencyDAOTest extends TestCase {
	@Autowired
	private EmployeeCompetencyDAO ecDAO;

	@Test
	public void testFind() {
		EmployeeCompetency result = ecDAO.find(1);

		assertNotNull(result);
	}
	
	@Test
	public void testFindAll() {
		List<EmployeeCompetency> all = ecDAO.findAll();
		
		assertTrue(all.size() > 0);
	}
}