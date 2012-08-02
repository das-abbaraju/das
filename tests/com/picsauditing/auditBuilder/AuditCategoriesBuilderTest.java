package com.picsauditing.auditBuilder;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Calendar;

import org.junit.Test;
import org.powermock.reflect.Whitebox;

import com.picsauditing.EntityFactory;
import com.picsauditing.jpa.entities.AuditCatData;
import com.picsauditing.jpa.entities.AuditData;
import com.picsauditing.jpa.entities.AuditQuestion;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorAudit;


public class AuditCategoriesBuilderTest {

	@Test
	public void testFindMostRecentAudit() throws Exception {
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
	
		ContractorAudit result = Whitebox.invokeMethod(categoryBuilder, "findMostRecentAudit", 2);
		
		assertEquals(3, result.getId());
		
	}
	
	@Test
	public void testFindAnswer() throws Exception {
		ContractorAccount testContractor = EntityFactory.makeContractor();
		ContractorAudit audit = EntityFactory.makeContractorAudit(1, testContractor);
		audit.setData(new ArrayList<AuditData>());
		
		AuditQuestion questionOne = makeUpQuestion(1, audit, "pink");
		AuditQuestion questionTwo = makeUpQuestion(2, audit, "purple");
		AuditQuestion questionThree = makeUpQuestion(3, audit, "magenta");
		
		AuditCatData auditCatData = EntityFactory.makeAuditCatData();
		
		auditCatData.getCategory().setQuestions(new ArrayList<AuditQuestion>());
		auditCatData.getCategory().getQuestions().add(questionOne);
		auditCatData.getCategory().getQuestions().add(questionTwo);
		auditCatData.getCategory().getQuestions().add(questionThree);
		
		AuditCategoriesBuilder categoryBuilder = new AuditCategoriesBuilder(null, testContractor);
	
		assertEquals("pink", wrapPrivateMethodCall(categoryBuilder, audit, 1).getAnswer());
		assertEquals("purple", wrapPrivateMethodCall(categoryBuilder, audit, 2).getAnswer());
		assertEquals("magenta", wrapPrivateMethodCall(categoryBuilder, audit, 3).getAnswer());	
	}

	private AuditQuestion makeUpQuestion(int id,ContractorAudit audit, String answer) {
		AuditQuestion question = EntityFactory.makeAuditQuestion();
		question.setId(id);
		audit.getData().add(EntityFactory.makeAuditData(answer, question));
		return question;
	}
	
	private AuditData wrapPrivateMethodCall(AuditCategoriesBuilder categoryBuilder, ContractorAudit audit, int currentQuestionId) throws Exception { 
		return Whitebox.invokeMethod(categoryBuilder, "findAnswer", audit,currentQuestionId);
	}
}
