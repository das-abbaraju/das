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

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/tests.xml")
@TransactionConfiguration(defaultRollback = true)
@Transactional
public class JobRoleDAOTest extends TestCase {
	@Autowired
	private JobRoleDAO jobRoleDAO;

//	@Test
//	public void testFindDistinctRolesOrderByPercent() throws Exception {
//		List<String> roleOrder = jobRoleDAO.findDistinctRolesOrderByPercent();
//
//		for (String r : roleOrder) {
//			System.out.println(r);
//		}
//
//		assertTrue(roleOrder.size() > 0);
//	}
}
