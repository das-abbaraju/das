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
	public void testFindAll() {
		List<AssessmentTest> tests = testDAO.findAll();
		
		assertTrue(tests.size() > 0);
	}
	
	@Test
	public void testFindRandom() {
		AssessmentTest test = testDAO.findRandom();
		
		assertNotNull(test);
	}
	
	// Uncomment when there is actual data
//	@Test
//	public void testFindExpired() {
//		List<AssessmentTest> tests = testDAO.findExpired(null);
//		
//		assertTrue(tests.size() > 0);
//	}
//	
//	@Test
//	public void testFindInEffect() {
//		List<AssessmentTest> tests = testDAO.findInEffect(null);
//		
//		assertTrue(tests.size() > 0);
//	}
	
	@Test
	public void testFindWhere() {
		List<AssessmentTest> tests = testDAO.findWhere("id > 0");
		
		assertTrue(tests.size() > 0);
	}
}
