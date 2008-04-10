package com.picsauditing.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

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
		contractoraudit.setExpiresDate(new Date(2009 - 12 - 12));
		contractoraudit.setAuditor(new User());
		contractoraudit.getAuditor().setId(941); // tallred
		contractoraudit.setAssignedDate(new Date(2008 - 04 - 12));
		contractoraudit.setScheduledDate(new Date(2008 - 04 - 21));
		contractoraudit.setCompletedDate(new Date());
		contractoraudit.setClosedDate(new Date());
		contractoraudit.setRequestingOpAccount(new OperatorAccount());
		contractoraudit.getRequestingOpAccount().setId(784);
		contractoraudit.setAuditLocation("irvine");
		contractoraudit.setPercentComplete("50");
		contractoraudit.setPercentVerified("50");
		contractorauditDAO.save(contractoraudit);
		assertEquals("irvine", contractoraudit.getAuditLocation());
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
		ContractorAudit contractoraudit = contractorauditDAO.findContractorActiveAudit(3, 1);
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
		for(OshaLog osha : contractoraudit.getContractorAccount().getOshas())
			osha.setFatalities1(12345);
		contractorauditDAO.save(contractoraudit);
	}
}
