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

import com.picsauditing.jpa.entities.AssessmentTest;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/tests.xml")
@TransactionConfiguration(defaultRollback = true)
@Transactional
public class AssessmentTestDAOTest extends TestCase {
	@Autowired
	private AssessmentTestDAO testDAO;

	@Test
	public void testFind() {
		AssessmentTest test = testDAO.find(1);

		assertNotNull(test);
	}

	@Test
	public void testFindByCenter() {
		List<AssessmentTest> tests = testDAO.findByAssessmentCenter(11069);
		
		assertTrue(tests.size() > 0);
	}
}
