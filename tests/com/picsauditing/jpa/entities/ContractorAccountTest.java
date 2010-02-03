package com.picsauditing.jpa.entities;

import java.math.BigDecimal;
import java.util.Date;

import junit.framework.TestCase;

import org.junit.Test;

import com.picsauditing.EntityFactory;
import com.picsauditing.PICS.DateBean;

public class ContractorAccountTest extends TestCase {
	@Test
	public void testCreateInvoiceItems() {
		InvoiceFee feeFree = new InvoiceFee(InvoiceFee.FREE);
		InvoiceFee feePQFOnly = new InvoiceFee(InvoiceFee.PQFONLY);
		feePQFOnly.setAmount(new BigDecimal(99));
		
		ContractorAccount contractor = EntityFactory.makeContractor();
		contractor.setMembershipLevel(feeFree);
		// Registered yesterday
		contractor.setCreationDate(DateBean.addDays(new Date(), -1));
		contractor.setPaymentExpires(contractor.getCreationDate());
		
		assertEquals("Not Calculated", contractor.getBillingStatus());
		
		contractor.setNewMembershipLevel(feeFree);
		assertEquals("Current", contractor.getBillingStatus());

		contractor.setNewMembershipLevel(feePQFOnly);
		assertEquals("Upgrade", contractor.getBillingStatus());

		contractor.setMembershipLevel(feePQFOnly);
		assertEquals("Renewal Overdue", contractor.getBillingStatus());

		// Expires next month
		contractor.setPaymentExpires(DateBean.addMonths(new Date(), 1));
		assertEquals("Renewal", contractor.getBillingStatus());

		contractor.setRenew(false);
		assertEquals("Do not renew", contractor.getBillingStatus());
		
		//contractor.setActive('N');
		contractor.setStatus(AccountStatus.Deactivated);
		assertEquals("Membership Canceled", contractor.getBillingStatus());

		contractor.setPaymentExpires(contractor.getCreationDate());
		contractor.setRenew(true);
		assertEquals("Activation", contractor.getBillingStatus());
		
		contractor.setMembershipDate(new Date());
		assertEquals("Reactivation", contractor.getBillingStatus());
		
		contractor.setMustPay("No");
		assertEquals("Current", contractor.getBillingStatus());
	}

	public void testCreditCard() {
		ContractorAccount contractor = EntityFactory.makeContractor();
		contractor.setCcExpiration(new Date());
		contractor.setCcOnFile(true);
		assertTrue(contractor.isCcValid());
		contractor.setCcExpiration(DateBean.addMonths(new Date(), -2) );
		assertFalse(contractor.isCcValid());
	}
}
