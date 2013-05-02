package com.picsauditing.PICS;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

import java.util.Date;
import java.util.GregorianCalendar;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.picsauditing.jpa.entities.AccountStatus;
import com.picsauditing.jpa.entities.BillingStatus;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.Invoice;

public class BillingCalculatorSingleSimpleTest {

	private static Date getStartOfBilling() {
		return new GregorianCalendar(2001, 3, 14).getTime();
	}

	private static Date getPaymentExpiration() {
		return new GregorianCalendar(2001, 2, 15).getTime();
	}

	@Test
	public void testCalculateInvoiceDueDate_BillingStatusActivation() throws Exception {
		assertDueDate(BillingStatus.Activation, getStartOfBilling());
	}

	private static void assertDueDate(BillingStatus billingStatus, Date paymentExpiration, AccountStatus status) {
		ContractorAccount contractor = new ContractorAccount();
		contractor.setPaymentExpires(paymentExpiration);

		if (status != null) {
			contractor.setStatus(status);
		}

		assertDueDate(contractor, billingStatus, paymentExpiration);
	}

	private static void assertDueDate(BillingStatus billingStatus, Date expected) {
		ContractorAccount contractor = new ContractorAccount();
		assertDueDate(contractor, billingStatus, expected);
	}

	private static void assertDueDate(ContractorAccount contractor, BillingStatus billingStatus, Date expected) {
		Date dueDate = BillingCalculatorSingle.calculateInvoiceDueDate(contractor, billingStatus, getStartOfBilling(),
				getStartOfBilling());

		assertEquals(expected, dueDate);
	}

	@Test
	public void testCalculateInvoiceDueDate_BillingStatusReactivation() throws Exception {
		assertDueDate(BillingStatus.Reactivation, getStartOfBilling());
	}

	@Test
	public void testCalculateInvoiceDueDate_BillingStatusUpgrade() throws Exception {
		assertDueDate(BillingStatus.Upgrade, DateBean.addDays(getStartOfBilling(), 7));
	}

	@Test
	public void testCalculateInvoiceDueDate_BillingStatusRenewal() throws Exception {
		assertDueDate(BillingStatus.Renewal, getPaymentExpiration(), null);
	}

	@Test
	public void testCalculateInvoiceDueDate_BillingStatusRenewalOverdue() throws Exception {
		assertDueDate(BillingStatus.RenewalOverdue, getPaymentExpiration(), null);
	}

	@Test
	public void testCalculateInvoiceDueDate_NullDueDate() throws Exception {
		assertDueDate(BillingStatus.RenewalOverdue, DateBean.addDays(getStartOfBilling(), 30));
	}

	@Test
	public void testCalculateInvoiceDueDate_IncreaseDueDateWhenLessThanSevenDaysFromToday() throws Exception {
		ContractorAccount contractor = new ContractorAccount();
		contractor.setStatus(AccountStatus.Active);
		contractor.setPaymentExpires(getStartOfBilling());

		Date dueDate = BillingCalculatorSingle.calculateInvoiceDueDate(contractor, BillingStatus.Renewal, getStartOfBilling(), DateBean.addDays(getStartOfBilling(), 2));

		assertEquals(DateBean.addDays(getStartOfBilling(), 7), dueDate);
	}

}
