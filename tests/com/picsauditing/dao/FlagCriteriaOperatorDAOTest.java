package com.picsauditing.dao;

import java.util.List;

import junit.framework.TestCase;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.picsauditing.jpa.entities.FlagCriteriaOperator;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/tests.xml")
public class FlagCriteriaOperatorDAOTest extends TestCase {

	@Autowired
	private FlagCriteriaOperatorDAO fcoDAO;

	@Test
	public void testFindByOperator() {
		final int opID = 4744;

		List<FlagCriteriaOperator> fcos = fcoDAO.findByOperator(opID);

		for (FlagCriteriaOperator fco : fcos) {
			assertEquals("This does not belong to the previous operator", fco.getOperator().getId(), opID);
		}
	}
}
