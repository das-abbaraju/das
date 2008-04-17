package com.picsauditing.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.hibernate.validator.AssertTrue;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.picsauditing.jpa.entities.AuditStatus;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.jpa.entities.OshaLog;
import com.picsauditing.jpa.entities.User;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/tests.xml")
public class ContractorAuditDAOTest {

	@Autowired
	ContractorAuditDAO contractorauditDAO;

	@Test
	public void testSaveAndRemove() {
		ContractorAudit contractoraudit = new ContractorAudit();
		contractoraudit.setAuditType(new AuditType());
		contractoraudit.getAuditType().setAuditTypeID(8);
		contractoraudit.setContractorAccount(new ContractorAccount());
		contractoraudit.getContractorAccount().setId(1003);
		contractoraudit.setCreatedDate(Calendar.getInstance().getTime());
		contractoraudit.setAuditStatus(AuditStatus.Active);
		contractoraudit.setExpiresDate(new Date());
		contractoraudit.setAuditor(new User());
		contractoraudit.getAuditor().setId(941); // tallred
		contractoraudit.setAssignedDate(new Date());
		contractoraudit.setScheduledDate(new Date());
		contractoraudit.setCompletedDate(new Date());
		contractoraudit.setClosedDate(new Date());
		contractoraudit.setRequestingOpAccount(new OperatorAccount());
		contractoraudit.getRequestingOpAccount().setId(784);
		contractoraudit.setAuditLocation("irvine");
		contractoraudit.setPercentComplete(50);
		contractoraudit.setPercentVerified(50);
		contractorauditDAO.save(contractoraudit);
		assertEquals(true, contractoraudit.getId() > 0);
		contractorauditDAO.remove(contractoraudit.getId());
		ContractorAudit contractoraudit1 = contractorauditDAO.find(contractoraudit.getId());
		assertNull(contractoraudit1);
	}

	@Test
	public void testFindByContractor() {
		List<ContractorAudit> contractoraudit = contractorauditDAO.findByContractor(707);
		assertEquals(3260, contractoraudit.get(0).getId());
	}

	@Test
	public void testFindContractorActiveAudit() {
		ContractorAudit contractoraudit = contractorauditDAO.findActiveByContractor(3, 1);
		assertEquals(3259, contractoraudit.getId());
	}

	@Test
	public void testFind() {
		ContractorAudit contractoraudit = contractorauditDAO.find(3259);
		assertEquals("95", contractoraudit.getPercentComplete());
	}

	@Test
	public void testUpdate() {
		ContractorAudit contractoraudit = contractorauditDAO.find(4657);
		OshaLog osha = new OshaLog();
		osha.setId(1130);
		osha.setFatalities1(120);
		for(OshaLog osha2 : contractoraudit.getContractorAccount().getOshas())
			if (osha2.getId() == osha.getId())
				osha2 = osha;
		contractorauditDAO.save(contractoraudit);
	}
}
