package com.picsauditing.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.sql.Date;
import java.util.Calendar;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import com.picsauditing.jpa.entities.Industry;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.jpa.entities.YesNo;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/tests.xml")
@TransactionConfiguration(defaultRollback = false)
@Transactional
public class OperatorAccountDAOTest {
	
	@Autowired
	OperatorAccountDAO operatoraccountDAO;
	@Autowired
	AccountDAO accountDAO;

	@Test
	public void testSaveAndRemove() {

		OperatorAccount operatoraccount = new OperatorAccount();
		operatoraccount.setName("PICS");
		operatoraccount.setUsername("29");
		operatoraccount.setPassword("testpics");
		operatoraccount.setPasswordChange(new Date(2008, 12, 31));
		operatoraccount.setLastLogin(Calendar.getInstance().getTime());
		operatoraccount.setContact("pics admin");
		operatoraccount.setAddress("17701 cowan");
		operatoraccount.setCity("irvine");
		operatoraccount.setState("ca");
		operatoraccount.setZip("92345");
		operatoraccount.setPhone("999-999-9999");
		operatoraccount.setPhone2("999-999-9999");
		operatoraccount.setFax("999-999-9999");
		operatoraccount.setEmail("pics@picsauditing.com");
		operatoraccount.setWebUrl("www.picsauditing.com");
		operatoraccount.setIndustry(Industry.Petrochemical);
		operatoraccount.setActive('y');
		operatoraccount.setCreatedBy(new User(1100));
		operatoraccount.setCreationDate(new java.util.Date());
		operatoraccount.setSeesAllB('n');
		operatoraccount.setActivationEmailsB("pics123@picsauditing.com");
		operatoraccount.setSendActivationEmailB('n');
		operatoraccount.setEmailConfirmedDate(new Date(2008, 12, 42));
		operatoraccount.setActivationEmails("pics@picsauditing.com");
		operatoraccount.setDoSendActivationEmail("Yes");
		operatoraccount.setDoContractorsPay("Multiple");
		operatoraccount.setCanSeeInsurance(YesNo.Yes);
		operatoraccount.setIsCorporate(YesNo.Yes);
		operatoraccount.setInsuranceAuditor(new User());
		operatoraccount.getInsuranceAuditor().setId(941); //tallred
		operatoraccount.setIsUserManualUploaded(YesNo.Yes);
		operatoraccount.setApprovesRelationships(YesNo.No);
		assertEquals("pics@picsauditing.com", operatoraccount
				.getActivationEmails());
		operatoraccount = operatoraccountDAO.save(operatoraccount);
		assertTrue(operatoraccount.getId() > 0);
		int newID = operatoraccount.getId();
		operatoraccountDAO.remove(newID);
		OperatorAccount operatoraccount1 = operatoraccountDAO
				.find(operatoraccount.getId());
		assertNull(operatoraccount1);

	}

	@Test
	public void testFindWhere() {
		List<OperatorAccount> account = operatoraccountDAO.findWhere(true, "active = 'Y'");
		assertTrue(account.size() > 0);
	}

	//@Test
//	public void testContractorCount() {
//		long start = System.currentTimeMillis();
//		int count = operatoraccountDAO.getContractorCount(2475);
//		assertTrue(count > 300 && count < 500);
//		count = operatoraccountDAO.getContractorCount(2475);
//		long end = System.currentTimeMillis();
//		System.out.println("SQL took" + (end - start) + " msecs" );
//	}

}
