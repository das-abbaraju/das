package com.picsauditing.auditBuilder;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Calendar;

import org.junit.Test;

import com.picsauditing.EntityFactory;
import com.picsauditing.jpa.entities.AuditCatData;
import com.picsauditing.jpa.entities.AuditData;
import com.picsauditing.jpa.entities.AuditQuestion;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorAudit;


public class AuditCategoriesBuilderTest {

	@Test
	public void testFindMostRecentAudit() {
		ContractorAccount testContractor = EntityFactory.makeContractor();
		testContractor.setAudits(new ArrayList<ContractorAudit>());
		
		Calendar oldAuditCreationDate = Calendar.getInstance();
		oldAuditCreationDate.set(Calendar.YEAR, 2001);
		
		Calendar newAuditCreationDate = Calendar.getInstance();
		newAuditCreationDate.set(Calendar.YEAR, 2002);
		
		ContractorAudit auditTypeOne = EntityFactory.makeContractorAudit(1, testContractor);
		auditTypeOne.setId(1);
		ContractorAudit auditTypeTwoOld = EntityFactory.makeContractorAudit(2, testContractor);
		auditTypeTwoOld.setCreationDate(oldAuditCreationDate.getTime());
		auditTypeTwoOld.setId(2);
		ContractorAudit auditTypeTwoNew = EntityFactory.makeContractorAudit(2, testContractor);
		auditTypeTwoNew.setCreationDate(newAuditCreationDate.getTime());
		auditTypeTwoNew.setId(3);
		
		testContractor.getAudits().add(auditTypeOne);
		testContractor.getAudits().add(auditTypeTwoOld);
		testContractor.getAudits().add(auditTypeTwoNew);
		
		
		AuditCategoriesBuilder categoryBuilder = new AuditCategoriesBuilder(null, testContractor);
	
		assertEquals(3, categoryBuilder.findMostRecentAudit(2).getId());
		
	}
	
	@Test
	public void testFindAnswer() {
		ContractorAccount testContractor = EntityFactory.makeContractor();
		ContractorAudit auditTypeOne = EntityFactory.makeContractorAudit(1, testContractor);
		auditTypeOne.setData(new ArrayList<AuditData>());
		
		AuditQuestion questionOne = EntityFactory.makeAuditQuestion();
		questionOne.setId(1);
		auditTypeOne.getData().add(EntityFactory.makeAuditData("pink", questionOne));
		
		AuditQuestion questionTwo = EntityFactory.makeAuditQuestion();
		questionTwo.setId(2);
		auditTypeOne.getData().add(EntityFactory.makeAuditData("purple", questionTwo));
		
		AuditQuestion questionThree = EntityFactory.makeAuditQuestion();
		questionThree.setId(3);
		auditTypeOne.getData().add(EntityFactory.makeAuditData("magenta", questionThree));
		
		AuditCatData auditCatData = EntityFactory.makeAuditCatData();
		
		auditCatData.getCategory().setQuestions(new ArrayList<AuditQuestion>());
		auditCatData.getCategory().getQuestions().add(questionOne);
		auditCatData.getCategory().getQuestions().add(questionTwo);
		auditCatData.getCategory().getQuestions().add(questionThree);
		
		AuditCategoriesBuilder categoryBuilder = new AuditCategoriesBuilder(null, testContractor);
	
		assertEquals("pink", categoryBuilder.findAnswer(auditTypeOne, 1).getAnswer());
		assertEquals("purple", categoryBuilder.findAnswer(auditTypeOne, 2).getAnswer());
		assertEquals("magenta", categoryBuilder.findAnswer(auditTypeOne, 3).getAnswer());
		
	}
}
