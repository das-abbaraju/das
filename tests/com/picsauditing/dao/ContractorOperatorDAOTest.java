package com.picsauditing.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.sql.Date;
import java.util.Calendar;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import com.picsauditing.access.Permissions;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorOperator;
import com.picsauditing.jpa.entities.ContractorOperatorFlag;
import com.picsauditing.jpa.entities.FlagColor;
import com.picsauditing.jpa.entities.Industry;
import com.picsauditing.jpa.entities.LowMedHigh;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.jpa.entities.OshaLog;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/tests.xml")
@TransactionConfiguration(defaultRollback = false)
@Transactional
public class ContractorOperatorDAOTest {
	@Autowired
	ContractorOperatorDAO contractorOperatorDao;

	@Test
	public void testSaveAndRemove() {
		ContractorOperator contractorOperator = contractorOperatorDao.find(588, 16);
		String color = contractorOperator.getForceFlag().toString();
		contractorOperator.setForceFlag(FlagColor.Amber);
		contractorOperatorDao.save(contractorOperator);
		contractorOperator = contractorOperatorDao.find(588, 16);
		assertEquals(FlagColor.Amber, contractorOperator.getForceFlag());
		contractorOperator.setForceFlag(FlagColor.valueOf(color));
		contractorOperatorDao.save(contractorOperator);
	}
}
