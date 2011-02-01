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

import com.picsauditing.jpa.entities.UserAssignment;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/tests.xml")
@TransactionConfiguration(defaultRollback = true)
@Transactional
public class UserAssignmentDAOTest extends TestCase {
	@Autowired
	private UserAssignmentDAO dao;
	@Autowired
	private ContractorAccountDAO conDAO;

	@Test
	public final void testFind() throws Exception {
		List<UserAssignment> uams = dao.findAll();
		assertTrue(uams.size() > 0);
	}

	@Test
	public void testFindByContractor() throws Exception {
		List<UserAssignment> uams = dao.findByContractor(conDAO.find(6240));
		assertTrue(uams.size() > 0);
	}
}
