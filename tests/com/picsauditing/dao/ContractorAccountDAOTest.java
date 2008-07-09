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

import com.picsauditing.access.Permissions;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorOperator;
import com.picsauditing.jpa.entities.ContractorOperatorFlag;
import com.picsauditing.jpa.entities.FlagColor;
import com.picsauditing.jpa.entities.Industry;
import com.picsauditing.jpa.entities.LowMedHigh;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.jpa.entities.OshaLog;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/tests.xml")
@TransactionConfiguration(defaultRollback = false)
@Transactional
public class ContractorAccountDAOTest {
	@Autowired
	ContractorAccountDAO contractoraccountDAO;
	@Autowired
	ContractorOperatorFlagDAO flagDAO;
	@Autowired
	AccountDAO accountDAO;

	@Test
	public void testSaveAndRemove() {
		ContractorAccount contractoraccount = new ContractorAccount();
		contractoraccount.setName("PICS");
		contractoraccount.setUsername("testpics120");
		contractoraccount.setPassword("testpics");
		contractoraccount.setPasswordChange(new Date(2008, 01, 12));
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
		contractoraccount.setIndustry(Industry.Construction);
		contractoraccount.setActive('y');
		contractoraccount.setCreatedBy("pics");
		contractoraccount.setDateCreated(new Date(2008, 04, 04));
		contractoraccount.setSeesAllB('n');
		contractoraccount.setActivationEmailsB("pics@picsauditing.com");
		contractoraccount.setSendActivationEmailB('n');
		contractoraccount.setEmailConfirmedDate(new Date(2008, 12, 42));
		contractoraccount.setTaxId("test17701");
		contractoraccount.setMainTrade("Consulting");
		contractoraccount.setTrades("Contractors Prescreening");
		contractoraccount.setSubTrades("junit testing");
		contractoraccount.setAccountDate(new Date(2008, 04, 19));
		contractoraccount.setPaid("yes");
		contractoraccount.setLastPayment(new Date(2008, 04, 26));
		contractoraccount.setBillingCycle(3);
		contractoraccount.setIsExempt("no");
		contractoraccount.setMembershipDate(new Date(2004, 12, 12));
		contractoraccount.setPayingFacilities(10);
		contractoraccount.setRiskLevel(LowMedHigh.Med);
		contractoraccount = (ContractorAccount) accountDAO.save(contractoraccount);
		assertEquals("test17701", contractoraccount.getTaxId());
		assertTrue(contractoraccount.getId() > 0);

		List<ContractorAccount> testFindWhere = contractoraccountDAO
				.findWhere("main_Trade LIKE 'Consulting' AND taxId = 'test17701'");
		assertEquals("junit testing", testFindWhere.get(0).getSubTrades());

		contractoraccountDAO.remove(contractoraccount.getId());
		ContractorAccount contractoraccount1 = contractoraccountDAO.find(contractoraccount.getId());
		assertNull(contractoraccount1);
	}

	@Test
	public void testFind() {
		ContractorAccount contractoraccount = contractoraccountDAO.find(14);
		for (OshaLog osha : contractoraccount.getOshas()) {
			assertEquals(0, osha.getYear1().getFatalities());
		}

		System.out.println("contractoraccount.getFlags()");
		for (OperatorAccount operator : contractoraccount.getFlags().keySet()) {
			System.out.println(contractoraccount.getFlags().get(operator).getFlagColor());
		}
		System.out.println("contractoraccount.getOperators()");
		for (ContractorOperator operator : contractoraccount.getOperators()) {
			System.out.println(operator.getOperatorAccount().getName() + operator.getFlag().getFlagColor());
		}
		System.out.println("contractoraccountDAO.findOperators");
		for (ContractorOperator operator : contractoraccountDAO.findOperators(contractoraccount, new Permissions())) {
			System.out.println(operator.getOperatorAccount().getName());
		}
		assertEquals("ECI (Ecology Control Inc.)", contractoraccount.getName());
	}

	// @Test
	public void testFindWhere() {
		List<ContractorAccount> contractoraccount = contractoraccountDAO.findWhere("mainTrade LIKE 'Engineering'");
		assertEquals("Inactive", contractoraccount.get(0).getStatus());
	}

	//@Test
	public void addContractorOperatorFlag() {
		ContractorAccount contractoraccount = contractoraccountDAO.find(14);
		OperatorAccount operator = (OperatorAccount) accountDAO.find(1251);

		ContractorOperatorFlag coFlag = new ContractorOperatorFlag();
		coFlag.setFlagColor(FlagColor.Red);
		coFlag.setContractorAccount(contractoraccount);
		coFlag.setOperatorAccount(operator);
		coFlag.setLastUpdate(new Date(0));
		flagDAO.save(coFlag);
		flagDAO.remove(coFlag.getId());
	}
}
