package com.picsauditing.PICS;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.Calendar;
import java.util.List;
import java.util.Vector;

import com.picsauditing.jpa.entities.AuditOperator;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.jpa.entities.AuditTypeClass;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorOperator;
import com.picsauditing.jpa.entities.Invoice;
import com.picsauditing.jpa.entities.InvoiceFee;
import com.picsauditing.jpa.entities.InvoiceItem;
import com.picsauditing.jpa.entities.OperatorAccount;

public class BillingCalculatorSingle {

	static public void setPayingFacilities(ContractorAccount contractor) {

		List<OperatorAccount> payingOperators = new Vector<OperatorAccount>();
		for (ContractorOperator contractorOperator : contractor.getOperators()) {
			OperatorAccount operator = contractorOperator.getOperatorAccount();
			if (operator.getDoContractorsPay() != null && !"No".equals(operator.getDoContractorsPay()))
				payingOperators.add(operator);
		}

		if (payingOperators.size() == 1) {
			// Only one operator, let's see if it's a multiple
			if (payingOperators.get(0).getDoContractorsPay().equals("Multiple")) {
				contractor.setPayingFacilities(0);
				return;
			}
		}

		contractor.setPayingFacilities(payingOperators.size());
	}

	static public InvoiceFee calculateAnnualFee(ContractorAccount contractor) {
		setPayingFacilities(contractor);

		InvoiceFee fee = new InvoiceFee();

		if (contractor.getPayingFacilities() == 0) {
			// Contractors with no paying facilities are free
			fee.setId(InvoiceFee.FREE); // $0
			return fee;
		}

		if (isAudited(contractor))
			// Audited Contractors have a tiered pricing scheme
			fee.setId(calculatePriceTier(contractor.getPayingFacilities()));
		else
			fee.setId(InvoiceFee.PQFONLY); // $99

		return fee;

	}

	static private boolean isAudited(ContractorAccount contractor) {
		// We have at least one paying operator, let's see if they need to be
		// audited

		for (ContractorOperator contractorOperator : contractor.getOperators()) {
			OperatorAccount operator = contractorOperator.getOperatorAccount();
			if (operator.getDoContractorsPay() != null && !operator.getDoContractorsPay().equals("No")) {
				// See if this operator requires this contractor to be audited
				for (AuditOperator audit : operator.getAudits()) {
					if (audit.isRequiredFor(contractor)) {
						// This operator requires this audit and can see it
						if (audit.getAuditType().getId() == AuditType.DA && "Yes".equals(contractor.getOqEmployees()))
							return true;

						if (audit.getAuditType().getId() == AuditType.DESKTOP
								|| audit.getAuditType().getId() == AuditType.OFFICE
								|| audit.getAuditType().getClassType() == AuditTypeClass.IM)
							return true;
					}
				}
			}
		}
		return false;
	}

	/**
	 * 
	 * @param billable
	 *            the number of billable facilities
	 * @return the InvoiceFee.id for the annual membership level
	 */
	static private Integer calculatePriceTier(int billable) {
		if (billable >= 20)
			return InvoiceFee.FACILITIES20;
		if (billable >= 13)
			return InvoiceFee.FACILITIES13;
		if (billable >= 9)
			return InvoiceFee.FACILITIES9;
		if (billable >= 5)
			return InvoiceFee.FACILITIES5;
		if (billable >= 2)
			return InvoiceFee.FACILITIES2;
		return InvoiceFee.FACILITIES1;
	}

	/**
	 * Create a list of fees that this contractor should be charge for
	 * @param contractor
	 * @return
	 */
	static public List<InvoiceItem> createInvoiceItems(ContractorAccount contractor) {
		List<InvoiceItem> items = new ArrayList<InvoiceItem>();
		// TODO add in the activation fee if the date is ever missing
		if (contractor.getMembershipDate() == null) {
			InvoiceFee fee = new InvoiceFee(InvoiceFee.ACTIVATION);
			items.add(new InvoiceItem(fee));

		}

		// For Activation Fee and New Membership
		if ("Activation".equals(contractor.getBillingStatus())) {
			if (contractor.getNewMembershipLevel().getId() != InvoiceFee.FREE
					&& contractor.getMembershipLevel().getId() == InvoiceFee.FREE) {
				items.add(new InvoiceItem(contractor.getNewMembershipLevel()));
			}
		}

		// For Reactivation Fee and Reactivating Membership
		if ("Reactivation".equals(contractor.getBillingStatus())) {
			InvoiceFee fee = new InvoiceFee(InvoiceFee.REACTIVATION);

			if (contractor.getNewMembershipLevel() != null)
				items.add(new InvoiceItem(contractor.getNewMembershipLevel()));

			items.add(new InvoiceItem(fee));
		}

		// For Renewals
		if (contractor.getBillingStatus().startsWith("Renew")) {
			if (contractor.getMembershipLevel() != null)
				items.add(new InvoiceItem(contractor.getMembershipLevel()));
		}

		// For Upgrades
		// Calculate a prorated amount depending on when the upgrade happens
		// and when the actual membership expires
		if ("Upgrade".equals(contractor.getBillingStatus())) {
			if (contractor.getNewMembershipLevel() != null && contractor.getMembershipLevel() != null) {
				BigDecimal upgradeAmount = BigDecimal.ZERO;
				String description = "";

				if (contractor.getMembershipLevel().isFree()) {
					// Starting from scratch/Free Membership Level
					upgradeAmount = contractor.getNewMembershipLevel().getAmount();
					description = "Membership Level is: $" + contractor.getNewMembershipLevel().getAmount();

				} else if (DateBean.getDateDifference(contractor.getPaymentExpires()) < 0) {
					// Their membership has already expired so we need to do a full renewal amount
					upgradeAmount = contractor.getNewMembershipLevel().getAmount();
					description = "Membership Level is: $" + contractor.getNewMembershipLevel().getAmount();

				} else {
					// Actual prorated Upgrade
					Date upgradeDate = (contractor.getLastUpgradeDate() == null) ? new Date() : contractor
							.getLastUpgradeDate();
					double daysUntilExpiration = DateBean
							.getDateDifference(upgradeDate, contractor.getPaymentExpires());
					BigDecimal upgradeAmountDifference = contractor.getNewMembershipLevel().getAmount().subtract(contractor.getMembershipLevel().getAmount());

					BigDecimal proratedCalc = upgradeAmountDifference.divide(new BigDecimal(365));
					upgradeAmount = new BigDecimal(daysUntilExpiration).multiply(upgradeAmountDifference);

					description = "Upgrading from $" + contractor.getMembershipLevel().getAmount() + ". Prorated $"
							+ upgradeAmount;
				}

				InvoiceItem invoiceItem = new InvoiceItem();
				invoiceItem.setInvoiceFee(contractor.getNewMembershipLevel());
				invoiceItem.setAmount(upgradeAmount.round(new MathContext(0, RoundingMode.HALF_UP)));
				invoiceItem.setDescription(description);
				items.add(invoiceItem);
			}
		}

		return items;
	}

	/**
	 * Return TRUE if any one of the items is for a Fee Class = "Membership"
	 * 
	 * @param items
	 * @return
	 */
	static public boolean isContainsMembership(List<InvoiceItem> items) {

		for (InvoiceItem item : items)
			if (item.getInvoiceFee().getFeeClass().equals("Membership"))
				return true;
		return false;
	}

	/**
	 * Return TRUE if any one of the items is for a Fee Class = "Membership" and the amount isn't prorated
	 * 
	 * @param items
	 * @return
	 */
	static public boolean isContainsFullMembership(List<InvoiceItem> items) {
		for (InvoiceItem item : items)
			if (item.getInvoiceFee().getFeeClass().equals("Membership"))
				return (item.getAmount() == item.getInvoiceFee().getAmount());
		return false;
	}
	
}
