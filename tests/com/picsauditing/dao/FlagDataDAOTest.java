package com.picsauditing.dao;

import java.util.List;

import junit.framework.TestCase;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.picsauditing.jpa.entities.FlagData;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/tests.xml")
public class FlagDataDAOTest extends TestCase {

	@Autowired
	private FlagDataDAO dataDAO;

	@Test
	public void testFindByContractorAndOperator() {
		List<FlagData> findByContractorAndOperator = dataDAO.findByContractorAndOperator(3, 4744);

		for (FlagData flagData : findByContractorAndOperator) {
			assertEquals("Contractor was incorrect", 3, flagData.getContractor().getId());
			assertEquals("Operator was incorrect", 4744, flagData.getOperator().getId());
		}
	}

	@Test
	public void testFindByOperator() {
		final List<FlagData> findByOperator = dataDAO.findByOperator(4744);

		for (FlagData flagData : findByOperator) {
			assertEquals("Operator was incorrect", 4744, flagData.getOperator().getId());
		}
	}
}
