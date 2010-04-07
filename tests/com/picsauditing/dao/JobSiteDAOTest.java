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

import com.picsauditing.jpa.entities.Employee;
import com.picsauditing.jpa.entities.JobSite;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/tests.xml")
@TransactionConfiguration(defaultRollback = true)
@Transactional
public class JobSiteDAOTest extends TestCase {
	@Autowired
	private JobSiteDAO siteDAO;

	@Test
	public void testFind() {
		JobSite site = siteDAO.find(1);

		assertNotNull(site);
	}

	@Test
	public void testFindByOperator() {
		List<JobSite> sites = siteDAO.findByOperator(16);

		assertTrue(sites.size() > 0);
	}
	
	@Test
	public void testFindByOperatorWhere() {
		List<JobSite> sites = siteDAO.findByOperatorWhere(16, "id = 3");

		assertTrue(sites.size() > 0);
	}
}
