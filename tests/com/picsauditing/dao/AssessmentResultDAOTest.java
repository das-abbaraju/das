package com.picsauditing.dao;

import junit.framework.TestCase;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import com.picsauditing.jpa.entities.AssessmentResult;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/tests.xml")
@TransactionConfiguration(defaultRollback = true)
@Transactional
public class AssessmentResultDAOTest extends TestCase {
	@Autowired
	private AssessmentResultDAO resultDAO;

	@Test
	public void testFind() {
		AssessmentResult result = resultDAO.find(1);

		assertNotNull(result);
	}
}