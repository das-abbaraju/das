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

import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.Industry;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.jpa.entities.YesNo;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/tests.xml")
@TransactionConfiguration(defaultRollback = false)
@Transactional
public class OperatorAccountDAOTest {

	@Autowired
	OperatorAccountDAO operatoraccountDAO;
	OperatorAccount operatoraccount;
	@Autowired
	AccountDAO accountDAO;

	@Test
	public void testSaveAndRemove() {

		operatoraccount = new OperatorAccount();
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
		operatoraccount.setCreatedBy("pics");
		operatoraccount.setDateCreated(new Date(2008, 04, 04));
		operatoraccount.setSeesAllB('n');
		operatoraccount.setActivationEmailsB("pics123@picsauditing.com");
		operatoraccount.setSendActivationEmailB('n');
		operatoraccount.setEmailConfirmedDate(new Date(2008, 12, 42));
		operatoraccount.setActivationEmails("pics@picsauditing.com");
		operatoraccount.setDoSendActivationEmail("Yes");
		operatoraccount.setSeesAllContractors("Yes");
		operatoraccount.setCanAddContractors(YesNo.Yes);
		operatoraccount.setDoContractorsPay("Multiple");
		operatoraccount.setCanSeePQF(YesNo.Yes);
		operatoraccount.setCanSeeDesktop(YesNo.Yes);
		operatoraccount.setCanSeeDA(YesNo.Yes);
		operatoraccount.setCanSeeoffice(YesNo.Yes);
		operatoraccount.setCanSeeField(YesNo.Yes);
		operatoraccount.setCanSeeInsurance(YesNo.Yes);
		operatoraccount.setIsCorporate(YesNo.Yes);
		operatoraccount.setEmrHurdle("1.5");
		operatoraccount.setEmrTime("1");
		operatoraccount.setLwcrTime("12");
		operatoraccount.setLwcrHurdle("10");
		operatoraccount.setTrirHurdle("34.6");
		operatoraccount.setTrirTime("1");
		operatoraccount.setFatalitiesHurdle("1");
		operatoraccount.setFlagEmr(YesNo.Yes);
		operatoraccount.setFlagLwcr(YesNo.Yes);
		operatoraccount.setFlagTrir(YesNo.Yes);
		operatoraccount.setFlagFatalities(YesNo.No);
		operatoraccount.setFlagQ318(YesNo.No);
		operatoraccount.setFlagQ1385(YesNo.Yes);
		operatoraccount.setInsuranceAuditorId(123);
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
		List<OperatorAccount> account = operatoraccountDAO.findWhere("id = 2475");
		assertTrue(account.size() > 0);
	}



}
