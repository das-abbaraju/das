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

import com.picsauditing.jpa.entities.EmployeeRole;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/tests.xml")
@TransactionConfiguration(defaultRollback = true)
@Transactional
public class EmployeeRoleDAOTest extends TestCase {
	@Autowired
	private EmployeeRoleDAO erDAO;

	@Test
	public void testFind() {
		EmployeeRole result = erDAO.find(1);

		assertNotNull(result);
	}

	@Test
	public void testFindAll() {
		List<EmployeeRole> all = erDAO.findAll();

		assertTrue(all.size() > 0);
	}
}