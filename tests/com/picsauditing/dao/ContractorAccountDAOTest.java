package com.picsauditing.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import com.picsauditing.access.Permissions;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorOperator;
import com.picsauditing.jpa.entities.ContractorOperatorFlag;
import com.picsauditing.jpa.entities.FlagColor;
import com.picsauditing.jpa.entities.Industry;
import com.picsauditing.jpa.entities.LowMedHigh;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.jpa.entities.User;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/tests.xml")
@TransactionConfiguration(defaultRollback = true)
@Transactional
public class ContractorAccountDAOTest {
	@Autowired
	ContractorAccountDAO contractoraccountDAO;
	@Autowired
	ContractorOperatorFlagDAO flagDAO;
	@Autowired
	AccountDAO accountDAO;

	// @Test
	public void testSaveAndRemove() {
		Calendar cal = Calendar.getInstance();
		cal.set(2008, 01, 12);
		
		ContractorAccount contractoraccount = new ContractorAccount();
		contractoraccount.setName("PICS");
		contractoraccount.setLastLogin(new Date());
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
		contractoraccount.setCreatedBy(new User(1100));
		contractoraccount.setCreationDate(new Date());
		contractoraccount.setTaxId("test17701");
		contractoraccount.setMainTrade("Consulting");
		contractoraccount.setAccountDate(cal.getTime());
		contractoraccount.setMembershipDate(cal.getTime());
		contractoraccount.setPayingFacilities(10);
		contractoraccount.setRiskLevel(LowMedHigh.Med);
		contractoraccount = (ContractorAccount) accountDAO.save(contractoraccount);
		assertEquals("test17701", contractoraccount.getTaxId());
		assertTrue(contractoraccount.getId() > 0);

		List<ContractorAccount> testFindWhere = contractoraccountDAO
				.findWhere("main_Trade LIKE 'Consulting' AND taxId = 'test17701'");
		assertEquals(LowMedHigh.Med, testFindWhere.get(0).getRiskLevel());

		accountDAO.remove(contractoraccount.getId());
		ContractorAccount contractoraccount1 = (ContractorAccount) accountDAO.find(contractoraccount.getId());
		assertNull(contractoraccount1);
	}

	@Test
	public void testFind() {
		ContractorAccount contractoraccount = contractoraccountDAO.find(14);
		System.out.println("contractoraccount.getFlags()");
		for (OperatorAccount operator : contractoraccount.getFlags().keySet()) {
			System.out.println(contractoraccount.getFlags().get(operator).getFlagColor());
		}
		System.out.println("contractoraccount.getOperators()");
		for (ContractorOperator operator : contractoraccount.getOperators()) {
			System.out.println(operator.getOperatorAccount().getName() + operator.getFlag().getFlagColor());
		}
		System.out.println("contractoraccountDAO.findOperators");
		for (ContractorOperator operator : contractoraccountDAO.findOperators(contractoraccount, new Permissions(), "")) {
			System.out.println(operator.getOperatorAccount().getName());
		}
		assertEquals("ECI (Ecology Control Inc.)", contractoraccount.getName());
	}

	// @Test
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
