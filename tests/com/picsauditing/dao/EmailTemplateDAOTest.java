package com.picsauditing.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.Date;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.picsauditing.jpa.entities.EmailTemplate;
import com.picsauditing.jpa.entities.OperatorAccount;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/tests.xml")
public class EmailTemplateDAOTest {

	@Autowired
	private EmailTemplateDAO emailTemplateDAO;

	@Test
	public void testSaveAndRemove() {
		EmailTemplate emailTemplate = new EmailTemplate();
		emailTemplate.setOperatorAccount(new OperatorAccount());
		emailTemplate.getOperatorAccount().setId(228);
		emailTemplate.setCreationDate(new Date());
		emailTemplate.setSubject("Test Email");
		emailTemplate.setTemplateName("Test");
		emailTemplateDAO.save(emailTemplate);
		assertEquals(true, emailTemplate.getId() > 0);
		emailTemplateDAO.remove(emailTemplate.getId());
		assertNull(emailTemplateDAO.find(emailTemplate.getId()));
	}
}
