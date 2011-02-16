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

import com.picsauditing.jpa.entities.ContractorAccount;
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
		UserAssignment assignment = dao.findByContractor(conDAO.find(6240));
		assertNotNull(assignment);

		ContractorAccount contractor = conDAO.find(994);

		// Test AZ - Ashley Prather
		assignment = dao.findByContractor(contractor);
		assertEquals(22223, assignment.getUser().getId());
		assertEquals(contractor.getAuditor().getId(), assignment.getUser().getId());

		// Test contractor override Airgas-North Central - Joe Villanueva
		contractor = conDAO.find(6086);
		assignment = dao.findByContractor(contractor);
		assertEquals(27274, assignment.getUser().getId());
		assertEquals(contractor.getAuditor().getId(), assignment.getUser().getId());

		// Test fallback case => Dubai - Estevan
		contractor = conDAO.find(13470);
		assignment = dao.findByContractor(contractor);
		assertEquals(940, assignment.getUser().getId());
		assertEquals(contractor.getAuditor().getId(), assignment.getUser().getId());
	}
}
