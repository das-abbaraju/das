package com.picsauditing.dao;

import static org.junit.Assert.assertEquals;

import java.util.Date;
import java.util.Iterator;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import com.picsauditing.PICS.FacilityChanger;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorOperator;
import com.picsauditing.jpa.entities.FlagColor;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.util.SpringUtils;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/tests.xml")
@TransactionConfiguration(defaultRollback = false)
@Transactional
public class ContractorOperatorDAOTest {
	@Autowired
	ContractorOperatorDAO contractorOperatorDao;
	@Autowired
	ContractorAccountDAO contractorDao;

	@Test
	public void testSave() {
		ContractorOperator contractorOperator = contractorOperatorDao.find(588, 16);
		if (contractorOperator.getForceFlag() != null) {
			String color = contractorOperator.getForceFlag().toString();
			contractorOperator.setForceFlag(FlagColor.Amber);
			contractorOperatorDao.save(contractorOperator);
			contractorOperator = contractorOperatorDao.find(588, 16);
			assertEquals(FlagColor.Amber, contractorOperator.getForceFlag());
			contractorOperator.setForceFlag(FlagColor.valueOf(color));
			contractorOperatorDao.save(contractorOperator);
			contractorOperatorDao.remove(contractorOperator);
			
		}
	}

	@Test
	public void testAddAndRemove() {
		ContractorOperator co = new ContractorOperator();
		co.setOperatorAccount(new OperatorAccount());
		co.getOperatorAccount().setId(1813); // Cherry Point
		co.setContractorAccount(new ContractorAccount());
		co.getContractorAccount().setId(3); // Ancon Marine
		co.setDateAdded(new Date());
		co = contractorOperatorDao.save(co);
		
		contractorOperatorDao.remove(co);
		co = null;
	}
	

}
