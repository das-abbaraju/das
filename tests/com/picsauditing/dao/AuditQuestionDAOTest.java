package com.picsauditing.dao;

import java.util.HashSet;
import java.util.List;

import junit.framework.TestCase;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.picsauditing.access.Permissions;
import com.picsauditing.jpa.entities.AuditCategory;
import com.picsauditing.jpa.entities.AuditQuestion;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/tests.xml")
public class AuditQuestionDAOTest extends TestCase {

	@Autowired
	private AuditQuestionDAO questionDAO;

	@Test
	public void testFindByQuestion() {
		List<AuditQuestion> questions = questionDAO.findByQuestion("What",
				new Permissions(), new HashSet<AuditCategory>());
		assertTrue(questions.size() > 0);

		for (AuditQuestion question : questions) {
			System.out.println(question.getName());
		}
	}
}
