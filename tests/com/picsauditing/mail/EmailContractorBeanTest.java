package com.picsauditing.mail;

import junit.framework.TestCase;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.jpa.entities.ContractorAccount;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/tests.xml")
public class EmailContractorBeanTest extends TestCase {
/*
	@Autowired
	private EmailContractorBean mailer;
	@Autowired
	private ContractorAccountDAO contractorAccountDAO;

	@Test
	public void testEmail() throws Exception {
		ContractorAccount contractor = contractorAccountDAO.find(14);

		mailer.setTestMode(true);
		mailer.sendMessage(EmailTemplates.welcome, contractor);
	}
*/
	@Test
	public void testEmail() throws Exception {
	
	}
		
}
