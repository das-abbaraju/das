package com.picsauditing.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.picsauditing.jpa.entities.AuditQuestion;
import com.picsauditing.jpa.entities.AuditSubCategory;
import com.picsauditing.jpa.entities.YesNo;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/tests.xml")
public class AuditQuestionDAOTest {

	@Autowired
	private AuditQuestionDAO auditquestionDAO;

	@Test
	public void testSaveAndRemove() {
		AuditQuestion auditquestion = new AuditQuestion();
		auditquestion.setSubCategory(new AuditSubCategory());
		auditquestion.getSubCategory().setId(22);
		auditquestion.setNumber(Short.parseShort("100"));
		auditquestion.setQuestion("write junit test class");
		auditquestion.setHasRequirement(YesNo.No);
		auditquestion.setOkAnswer("NA");
		auditquestion.setRequirement("junit test");
		auditquestion.setIsRequired("Depends");
		auditquestion.setDependsOnAnswer("fail or pass");
		auditquestion.setQuestionType("text");
		auditquestion.setIsVisible(YesNo.Yes);
		auditquestion.setLastModified(new Date());
		auditquestion.setTitle("junit test");
		auditquestion.setIsGroupedWithPrevious(YesNo.Yes);
		auditquestion.setLinkUrl1("test1");
		auditquestion.setLinkText1("test1");
		auditquestion.setLinkUrl2("test1");
		auditquestion.setLinkText2("test1");
		auditquestion.setLinkUrl3("test1");
		auditquestion.setLinkText3("test1");
		auditquestion.setLinkUrl5("test1");
		auditquestion.setLinkText5("test1");
		auditquestion.setLinkUrl6("test1");
		auditquestion.setLinkText6("test1");
		auditquestion.setDateCreated(new Date(2008 - 04 - 07));
		auditquestion.setIsRedFlagQuestion(YesNo.No);
		auditquestion = auditquestionDAO.save(auditquestion);
		assertEquals("write junit test class", auditquestion.getQuestion());
		assertTrue(auditquestion.getQuestionID() > 0);
		auditquestionDAO.remove(auditquestion.getQuestionID());
		AuditQuestion auditquestion1 = auditquestionDAO.find(auditquestion
				.getQuestionID());
		assertNull(auditquestion1);
	}

	@Test
	public void testFind() {
		AuditQuestion auditquestion = auditquestionDAO.find(39);
		assertEquals("City:", auditquestion.getQuestion());
		assertEquals("Text", auditquestion.getQuestionType());
	}

	@Test
	public void testFindBySubCategory() {
		List<AuditQuestion> auditquestion = auditquestionDAO
				.findBySubCategory(56);
		assertEquals(970, auditquestion.get(0).getQuestionID());
	}

}
