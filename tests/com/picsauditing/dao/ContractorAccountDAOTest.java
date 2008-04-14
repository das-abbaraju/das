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
import org.springframework.transaction.annotation.Transactional;

import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.LowMedHigh;
import com.picsauditing.jpa.entities.OshaLog;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/tests.xml")
@Transactional
public class ContractorAccountDAOTest {
	@Autowired
	ContractorAccountDAO contractoraccountDAO;
	ContractorAccount contractoraccount;
	@Autowired
	AccountDAO accountDAO;

	@Test
	public void testSaveAndRemove() {
		contractoraccount = new ContractorAccount();
		contractoraccount.setName("PICS");
		contractoraccount.setUsername("testpics112");
		contractoraccount.setPassword("testpics");
		contractoraccount.setPasswordChange(new Date(12 - 12 - 2008));
		contractoraccount.setLastLogin(Calendar.getInstance().getTime());
		contractoraccount.setContact("pics admin");
		contractoraccount.setAddress("17701 cowan");
		contractoraccount.setCity("irvine");
		contractoraccount.setState("ca");
		contractoraccount.setZip("92345");
		contractoraccount.setPhone("999-999-9999");
		contractoraccount.setPhone2("999-999-9999");
		contractoraccount.setFax("999-999-9999");
		contractoraccount.setEmail("pics@picsauditing.com");
		contractoraccount.setWebUrl("www.picsauditing.com");
		contractoraccount.setIndustry("contracting");
		contractoraccount.setActive('y');
		contractoraccount.setCreatedBy("pics");
		contractoraccount.setDateCreated(new Date(2008 - 04 - 04));
		contractoraccount.setSeesAllB('n');
		contractoraccount.setActivationEmailsB("pics@picsauditing.com");
		contractoraccount.setSendActivationEmailB('n');
		contractoraccount.setEmailConfirmedDate(new Date(2008 - 12 - 42));
		contractoraccount.setTaxId("test17701");
		contractoraccount.setMainTrade("Consulting");
		contractoraccount.setTrades("Contractors Prescreening");
		contractoraccount.setSubTrades("junit testing");
		contractoraccount.setAccountDate(new Date(2008 - 04 - 19));
		contractoraccount.setPaid("yes");
		contractoraccount.setLastPayment(new Date(2008 - 04 - 26));
		contractoraccount.setBillingCycle(3);
		contractoraccount.setIsExempt("no");
		contractoraccount.setMembershipDate(new Date(2004 - 12 - 12));
		contractoraccount.setPayingFacilities(10);
		contractoraccount.setRiskLevel(LowMedHigh.Med);
		contractoraccount = (ContractorAccount) accountDAO
				.save(contractoraccount);
		assertEquals("test17701", contractoraccount.getTaxId());
		assertTrue(contractoraccount.getId() > 0);
		contractoraccountDAO.remove(contractoraccount.getId());
		ContractorAccount contractoraccount1 = contractoraccountDAO
				.find(contractoraccount.getId());
		assertNull(contractoraccount1);
	}

	@Test
	public void testFind() {
		ContractorAccount contractoraccount = contractoraccountDAO.find(3);
		for (OshaLog osha : contractoraccount.getOshas()) {
			assertEquals(0, osha.getFatalities1());
		}
		assertEquals("123456789", contractoraccount.getTaxId());
	}

	@Test
	public void testFindWhere() {
		List<ContractorAccount> contractoraccount = contractoraccountDAO
				.findWhere("mainTrade LIKE 'Engineering'");
		assertEquals("Inactive", contractoraccount.get(0).getStatus());
	}
}
