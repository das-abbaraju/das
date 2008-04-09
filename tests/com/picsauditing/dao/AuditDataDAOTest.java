package com.picsauditing.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.picsauditing.jpa.entities.AuditData;
import com.picsauditing.jpa.entities.AuditQuestion;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.YesNo;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/tests.xml")
public class AuditDataDAOTest {

	@Autowired
	AuditDataDAO auditdataDAO;

	@Test
	public void testSaveAndRemove() {
		AuditData auditdata = new AuditData();
		auditdata.setContractorAccount(new ContractorAccount());
		auditdata.getContractorAccount().setId(744);
		auditdata.setAudit(new ContractorAudit());
		auditdata.getAudit().setId(3259);
		auditdata.setQuestion(new AuditQuestion());
		auditdata.getQuestion().setQuestionID(48);
		auditdata.setNum(Short.parseShort("300"));
		auditdata.setAnswer("junit testing");
		auditdata.setAuditor(new ContractorAccount());
		auditdata.getAuditor().setId(744);
		auditdata.setComment("junit");
		auditdata.setDateVerified(new Date(2008 - 04 - 07));
		auditdata.setVerifiedAnswer("test");
		auditdata.setIsCorrect(YesNo.No);
		auditdata.setWasChanged(YesNo.No);
		auditdata = auditdataDAO.save(auditdata);
		assertEquals("junit testing", auditdata.getAnswer());
		auditdataDAO.remove(auditdata.getDataID());
		AuditData auditdata1 = auditdataDAO.find(auditdata.getDataID());
		assertNull(auditdata1);
	}

	@Test
	public void testFind() {
		AuditData auditdata = auditdataDAO.find(15);
		assertEquals("Yes", auditdata.getAnswer());
		assertEquals(0, auditdata.getNum());
	}

	@Test
	public void testFindAnswers() {
		List<Integer> questionid = new LinkedList<Integer>();
		questionid.add(new Integer(37));
		questionid.add(new Integer(38));
		questionid.add(new Integer(39));
		questionid.add(new Integer(40));
		questionid.add(new Integer(41));
		HashMap<Integer, AuditData> auditdata = (HashMap<Integer, AuditData>) auditdataDAO
				.findAnswers(249, questionid);
		assertEquals("Pacific Industrial Contractor Screening", auditdata.get(
				37).getAnswer());
	}

}
