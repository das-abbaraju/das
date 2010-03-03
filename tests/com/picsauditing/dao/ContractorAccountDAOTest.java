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

import com.picsauditing.jpa.entities.AccountStatus;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorOperator;
import com.picsauditing.jpa.entities.Industry;
import com.picsauditing.jpa.entities.LowMedHigh;
import com.picsauditing.jpa.entities.State;
import com.picsauditing.jpa.entities.User;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/tests.xml")
@TransactionConfiguration(defaultRollback = true)
@Transactional
public class ContractorAccountDAOTest {
	@Autowired
	ContractorAccountDAO contractoraccountDAO;
	@Autowired
	AccountDAO accountDAO;

	// @Test
	// public void testUpdateContractorByOperator() {
	// OperatorAccount operator = new OperatorAccount();
	// operator.setId(1813);
	// try {
	// contractoraccountDAO.updateContractorByOperator(operator);
	// } catch (RuntimeException e) {
	// Assert.fail(e.getMessage());
	// }
	// }

	@Test
	public void testSaveAndRemove() {
		Calendar cal = Calendar.getInstance();
		cal.set(2008, 01, 12);

		ContractorAccount contractoraccount = new ContractorAccount();
		contractoraccount.setName("PICS");
		contractoraccount.setPrimaryContact(new User());
		contractoraccount.getPrimaryContact().setName("pics admin");
		contractoraccount.getPrimaryContact().setPhone("999-999-9999");
		contractoraccount.getPrimaryContact().setEmail("pics@picsauditing.com");
		contractoraccount.setAddress("17701 cowan");
		contractoraccount.setCity("irvine");
		contractoraccount.setState(new State("CA"));
		contractoraccount.setZip("92345");
		contractoraccount.setPhone("999-999-9999");
		contractoraccount.setFax("999-999-9999");
		contractoraccount.setWebUrl("www.picsauditing.com");
		contractoraccount.setIndustry(Industry.Construction);
		//contractoraccount.setActive('y');
		contractoraccount.setStatus(AccountStatus.Active);
		contractoraccount.setCreatedBy(new User(1100));
		contractoraccount.setCreationDate(new Date());
		contractoraccount.setTaxId("test17701");
		contractoraccount.setMainTrade("Consulting");
		contractoraccount.setMembershipDate(cal.getTime());
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
		List<ContractorOperator> operators = contractoraccount.getNonCorporateOperators();
		assertTrue(operators.size() > 0);
		for (ContractorOperator co : operators)
			assertTrue(co.getOperatorAccount().getName().length() > 0);

		ContractorAccount cached = contractoraccountDAO.find(14);
		List<ContractorOperator> cachedOps = cached.getNonCorporateOperators();
		assertTrue(cachedOps.size() > 0);
		for (ContractorOperator co : cachedOps)
			assertTrue(co.getOperatorAccount().getName().length() > 0);

		// System.out.println("contractoraccount.getFlags()");
		// for (OperatorAccount operator :
		// contractoraccount.getFlags().keySet()) {
		// System.out.println(contractoraccount.getFlags().get(operator).getFlagColor());
		// }
		// System.out.println("contractoraccount.getOperators()");
		// for (ContractorOperator operator : contractoraccount.getOperators())
		// {
		// System.out.println(operator.getOperatorAccount().getName() +
		// operator.getFlag().getFlagColor());
		// }
		// System.out.println("contractoraccountDAO.findOperators");
		// for (ContractorOperator operator :
		// contractoraccountDAO.findOperators(contractoraccount, new
		// Permissions(), "")) {
		// System.out.println(operator.getOperatorAccount().getName());
		// }
		assertEquals("ECI (Ecology Control Inc.)", contractoraccount.getName());
	}
}
