package com.picsauditing.jpa.entities;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Date;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.picsauditing.EntityFactory;
import com.picsauditing.PICS.DateBean;

public class ContractorAccountTest {
	private ContractorAccount contractor;
	
	@Before
	public void setup() {
		contractor = EntityFactory.makeContractor();
	}
	
	@Ignore
	@Test
	public void testCreateInvoiceItems() {
		// InvoiceFee feeFree = new InvoiceFee(InvoiceFee.FREE);
		// InvoiceFee feePQFOnly = new InvoiceFee(InvoiceFee.PQFONLY);
		// feePQFOnly.setAmount(new BigDecimal(99));
		//		
		// ContractorAccount contractor = EntityFactory.makeContractor();
		// contractor.setMembershipLevel(feeFree);
		// // Registered yesterday
		// contractor.setCreationDate(DateBean.addDays(new Date(), -1));
		// contractor.setPaymentExpires(contractor.getCreationDate());
		//		
		// assertEquals("Not Calculated", contractor.getBillingStatus());
		//		
		// contractor.setNewMembershipLevel(feeFree);
		// assertEquals("Current", contractor.getBillingStatus());
		//
		// contractor.setNewMembershipLevel(feePQFOnly);
		// assertEquals("Upgrade", contractor.getBillingStatus());
		//
		// contractor.setMembershipLevel(feePQFOnly);
		// assertEquals("Renewal Overdue", contractor.getBillingStatus());
		//
		// // Expires next month
		// contractor.setPaymentExpires(DateBean.addMonths(new Date(), 1));
		// assertEquals("Renewal", contractor.getBillingStatus());
		//
		// contractor.setRenew(false);
		// assertEquals("Do not renew", contractor.getBillingStatus());
		//		
		// contractor.setStatus(AccountStatus.Deactivated);
		// assertEquals("Membership Canceled", contractor.getBillingStatus());
		//
		// contractor.setPaymentExpires(contractor.getCreationDate());
		// contractor.setRenew(true);
		// assertEquals("Activation", contractor.getBillingStatus());
		//		
		// contractor.setMembershipDate(new Date());
		// assertEquals("Reactivation", contractor.getBillingStatus());
		//		
		// contractor.setMustPay("No");
		// assertEquals("Current", contractor.getBillingStatus());
	}

	@Test
	public void testCreditCard_expiresToday() {
		contractor.setCcExpiration(new Date());
		contractor.setCcOnFile(true);
		assertTrue(contractor.isCcValid());
	}

	@Test
	public void testCreditCard_expiredTwoMonthsAgo() {
		contractor.setCcOnFile(true);
		contractor.setCcExpiration(DateBean.addMonths(new Date(), -2));
		assertFalse(contractor.isCcValid());
	}

	@Test
	public void testIsOnlyAssociatedWith_WhenNoOperatorAssociationExistsReturnsFalse() {
		assertTrue(contractor.getOperators().size() == 0);
		assertFalse(contractor.isOnlyAssociatedWith(OperatorAccount.SUNCOR));
	}

	@Test
	public void testIsOnlyAssociatedWith_WhenOneExpectedAssociationExistsReturnsTrue() {
		EntityFactory.addContractorOperator(contractor, EntityFactory.makeSuncorOperator());
		
		assertTrue(contractor.getOperators().size() == 1);
		assertTrue(contractor.isOnlyAssociatedWith(OperatorAccount.SUNCOR));
	}

	@Test
	public void testIsOnlyAssociatedWith_WhenOneUnexpectedAssociationExistsReturnsFalse() {
		EntityFactory.addContractorOperator(contractor, new OperatorAccount());

		assertTrue(contractor.getOperators().size() == 1);
		assertFalse(contractor.isOnlyAssociatedWith(OperatorAccount.SUNCOR));
	}

	@Test
	public void testIsOnlyAssociatedWith_WhenOnlyExclusiveAssociationsExistReturnsTrue() {
		EntityFactory.addContractorOperator(contractor, EntityFactory.makeSuncorOperator());
		EntityFactory.addContractorOperator(contractor, EntityFactory.makeSuncorOperator());
		EntityFactory.addContractorOperator(contractor, EntityFactory.makeSuncorOperator());

		assertTrue(contractor.getOperators().size() > 1);
		assertTrue(contractor.isOnlyAssociatedWith(OperatorAccount.SUNCOR));
	}
	
	@Test
	public void testIsOnlyAssociatedWith_WhenOneNonexclusiveAssociationOfManyExistsReturnsFalse() {
		EntityFactory.addContractorOperator(contractor, EntityFactory.makeSuncorOperator());
		EntityFactory.addContractorOperator(contractor, EntityFactory.makeOperator());
		EntityFactory.addContractorOperator(contractor, EntityFactory.makeSuncorOperator());

		assertTrue(contractor.getOperators().size() > 1);
		assertFalse(contractor.isOnlyAssociatedWith(OperatorAccount.SUNCOR));
	}
}
