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

import com.picsauditing.jpa.entities.JobSiteTask;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/tests.xml")
@TransactionConfiguration(defaultRollback = true)
@Transactional
public class JobSiteTaskDAOTest extends TestCase {
	@Autowired
	private JobSiteTaskDAO siteTaskDAO;

	@Test
	public void testFind() {
		JobSiteTask siteTask = siteTaskDAO.find(1);

		assertNotNull(siteTask);
	}

	@Test
	public void testFindByOperator() {
		List<JobSiteTask> siteTasks = siteTaskDAO.findByJob(1);

		assertTrue(siteTasks.size() > 0);
	}
}
