package com.picsauditing.dao;

import static org.junit.Assert.*;

import java.util.List;

import junit.framework.TestCase;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import com.picsauditing.jpa.entities.OperatorCompetency;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/tests.xml")
@TransactionConfiguration(defaultRollback = true)
@Transactional
public class OperatorCompetencyDAOTest extends TestCase {
	@Autowired
	private OperatorCompetencyDAO competencyDAO;

	@Test
	public void testFind() throws Exception {
		OperatorCompetency competency = competencyDAO.find(2);

		assertTrue(competency.getAllCategories().size() > 0);
	}
}
