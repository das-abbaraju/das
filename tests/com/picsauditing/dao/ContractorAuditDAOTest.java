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
import org.springframework.transaction.annotation.Transactional;

import com.picsauditing.jpa.entities.AuditCatData;
import com.picsauditing.jpa.entities.AuditCategory;
import com.picsauditing.jpa.entities.AuditData;
import com.picsauditing.jpa.entities.AuditQuestion;
import com.picsauditing.jpa.entities.AuditStatus;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.util.SpringUtils;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/tests.xml")
@Transactional
public class ContractorAuditDAOTest {

	@Autowired
	ContractorAuditDAO contractorauditDAO;
	@Autowired
	AuditDataDAO dataDAO;
	@Autowired
	AuditCategoryDataDAO catDataDAO;

	@Test
	public void testSaveAndRemove() {
		ContractorAudit contractoraudit = new ContractorAudit();
		contractoraudit.setAuditType(new AuditType());
		contractoraudit.getAuditType().setId(8);
		contractoraudit.setContractorAccount(new ContractorAccount());
		contractoraudit.getContractorAccount().setId(1003);
		contractoraudit.setCreationDate(Calendar.getInstance().getTime());
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
		contractoraudit = contractorauditDAO.save(contractoraudit);
		int newID = contractoraudit.getId();
		assertEquals(true, newID > 0);

		// Add children as well
		AuditData answer = new AuditData();
		answer.setAudit(contractoraudit);
		answer.setQuestion(new AuditQuestion());
		answer.getQuestion().setId(39); // City
		answer.setAnswer("Irvine");
		dataDAO.save(answer);

		AuditCatData category = new AuditCatData();
		category.setAudit(contractoraudit);
		category.setCategory(new AuditCategory());
		category.getCategory().setId(2); // General Information
		catDataDAO.save(category);

		contractorauditDAO.remove(newID);

		ContractorAudit contractoraudit1 = contractorauditDAO.find(contractoraudit.getId());
		assertNull(contractoraudit1);
	}

	// @Test
	public void testFindByContractor() {
		List<ContractorAudit> contractoraudit = contractorauditDAO.findByContractor(707);
		assertEquals(3260, contractoraudit.get(0).getId());
	}

	// @Test
	public void testFindContractorActiveAudit() {
		ContractorAudit contractoraudit = contractorauditDAO.findActiveByContractor(14, 1);
		assertEquals(3259, contractoraudit.getId());
	}

	// @Test
	public void testFind() {
		ContractorAudit contractoraudit = contractorauditDAO.find(3259);
		assertEquals("95", contractoraudit.getPercentComplete());
	}

	// @Test
	public void testUpdate() {
		ContractorAudit contractoraudit = contractorauditDAO.find(4657);
		contractoraudit.setClosedDate(new Date());
		contractorauditDAO.save(contractoraudit);
	}

	// @Test
	public void testDelete() {
		// contractorauditDAO.remove(12370);
	}

	@Test
	public void testFindWhere() {
		String where = "auditStatus = 'Pending' AND scheduledDate IS NOT NULL AND contractorAccount.status IN ('Active','Demo') AND contractorAccount IN (SELECT ca.contractorAccount FROM ContractorAudit ca WHERE ca.auditor.id = 910)";
		List<ContractorAudit> audits = contractorauditDAO.findWhere(10, where, "scheduledDate");
		for (ContractorAudit audit : audits) {
			System.out.println("audit " + audit.getId() + audit.getAuditType().getAuditName());
		}
	}

	// @Test
	public void testCopy() throws Exception {
		ContractorAccountDAO ncon = (ContractorAccountDAO) SpringUtils.getBean("ContractorAccountDAO");
		ContractorAudit oAudit = contractorauditDAO.find(3521);
		//contractorauditDAO.copy(oAudit, ncon.find(1533));
	}
	
	@Test
	public void testFindExpiredAudits() {
		String where = "expiresDate < NOW() AND auditStatus <> 'Expired'";
		List<ContractorAudit> conList = contractorauditDAO.findWhere(50,where, "expiresDate");
		System.out.println(conList.size());
	}

	@Test
	public void testFindExpiredCertificate() {
		List<ContractorAudit> conList = contractorauditDAO.findExpiredCertificates();
		System.out.println("List Size is :" + conList.size() + " Contractor Account " +conList.get(0).getContractorAccount().getId());
	}
}

