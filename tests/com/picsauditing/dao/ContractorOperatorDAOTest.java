package com.picsauditing.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Date;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorOperator;
import com.picsauditing.jpa.entities.FlagColor;
import com.picsauditing.jpa.entities.OperatorAccount;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/tests.xml")
@TransactionConfiguration(defaultRollback = false)
@Transactional
public class ContractorOperatorDAOTest {
	@Autowired
	ContractorOperatorDAO contractorOperatorDao;

	@Test
	public void testSave() {
		ContractorOperator contractorOperator = contractorOperatorDao.find(588, 16);
		if (contractorOperator.getForceFlag() != null) {
			String color = contractorOperator.getForceFlag().toString();
			contractorOperator.setForceFlag(FlagColor.Amber);
			contractorOperatorDao.save(contractorOperator);
			contractorOperator = contractorOperatorDao.find(588, 16);
			assertEquals("contractorOperator's flag should be amber", FlagColor.Amber, contractorOperator.getForceFlag());
			contractorOperator.setForceFlag(FlagColor.valueOf(color));
			
			contractorOperatorDao.save(contractorOperator);
			assertNotNull("after a save the contractorOperator should exist", contractorOperatorDao.find(contractorOperator.getId()));
			
			contractorOperatorDao.remove(contractorOperator);
			assertNull(contractorOperatorDao.find(contractorOperator.getId()));			
		}
	}

	@Test
	public void testAddAndRemove() {
		ContractorOperator co = new ContractorOperator();
		co.setOperatorAccount(new OperatorAccount());
		co.getOperatorAccount().setId(1813); // Cherry Point
		co.setContractorAccount(new ContractorAccount());
		co.getContractorAccount().setId(3); // Ancon Marine
		co.setCreationDate(new Date());
		co.setWorkStatus("P");
		
		contractorOperatorDao.save(co);
		assertTrue("the contractor should exist after a save", co.getId() > 0);
		
		contractorOperatorDao.remove(co.getId());
		assertNull("the contractor should not exist after a remove", contractorOperatorDao.find(co.getId()));
	}
	

}
