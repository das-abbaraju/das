package com.picsauditing.PICS;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import com.picsauditing.dao.InvoiceFeeDAO;
import com.picsauditing.jpa.entities.AccountStatus;
import com.picsauditing.jpa.entities.AuditOperator;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.jpa.entities.AuditTypeClass;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorOperator;
import com.picsauditing.jpa.entities.Invoice;
import com.picsauditing.jpa.entities.InvoiceFee;
import com.picsauditing.jpa.entities.InvoiceItem;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.jpa.entities.PaymentAppliedToInvoice;
import com.picsauditing.jpa.entities.User;

public class BillingCalculatorSingle {
	public static final Date CONTRACT_RENEWAL_TIMKEN = DateBean.parseDate("2010-03-31");
	public static final Date CONTRACT_RENEWAL_BASF = DateBean.parseDate("2011-12-31");
	public static final Date CONTRACT_RENEWAL_NEWBELGIUM = DateBean.parseDate("2010-03-31");

	static public void setPayingFacilities(ContractorAccount contractor) {

		List<OperatorAccount> payingOperators = new Vector<OperatorAccount>();
		for (ContractorOperator contractorOperator : contractor.getOperators()) {
			OperatorAccount operator = contractorOperator.getOperatorAccount();
			if (operator.getStatus().isActive() && !"No".equals(operator.getDoContractorsPay()))
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
		InvoiceFee fee = new InvoiceFee();
		if (!contractor.isAcceptsBids()) {

			fee = calculateAnnualFeeForContractor(contractor, fee);
		} else {

			fee.setId(InvoiceFee.BIDONLY);
		}

		return fee;
	}

	static public InvoiceFee calculateAnnualFeeForContractor(ContractorAccount contractor, InvoiceFee fee) {
		setPayingFacilities(contractor);

		if (contractor.getPayingFacilities() == 0) {
			// Contractors with no paying facilities are free
			fee.setId(InvoiceFee.FREE); // $0
			return fee;
		}

		if (isAudited(contractor)) {
			// Audited Contractors have a tiered pricing scheme
			int feeID = calculatePriceTier(contractor.getPayingFacilities());
			// Check to see if the the contractor only has one BASF operator
			if (feeID == InvoiceFee.FACILITIES1) {
				if (CONTRACT_RENEWAL_BASF.after(new Date())) {
					for (ContractorOperator contractorOperator : contractor.getOperators()) {
						if (contractorOperator.getOperatorAccount().getName().startsWith("BASF")) {
							feeID = 105;
						}
					}
				}
			}
			fee.setId(feeID);
		} else
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
				for (AuditOperator audit : operator.getInheritAudits().getAudits()) {
					if (audit.isRequiredFor(contractor)) {
						// This operator requires this audit and can see it

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
		if (billable >= 50)
			return InvoiceFee.FACILITIES50;
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
	 * Create a list of fees that this contractor should be charge for. The following contractor fields are used:
	 * <ul>
	 * <li>membershipDate</li>
	 * <li>newMembershipLevel</li>
	 * <li>membershipLevel</li>
	 * <li>billingStatus</li>
	 * <li>lastUpgradeDate</li>
	 * <li>paymentExpires</li>
	 * </ul>
	 * 
	 * @param contractor
	 * @return
	 */
	static public List<InvoiceItem> createInvoiceItems(ContractorAccount contractor, InvoiceFeeDAO feeDAO) {
		List<InvoiceItem> items = new ArrayList<InvoiceItem>();

		String billingStatus = contractor.getBillingStatus();

		if (billingStatus.equals("Not Calculated"))
			return items;

		if (billingStatus.equals("Current"))
			return items;

		if (billingStatus.equals("Bid Only Account")) {
			Date paymentExpires = DateBean.addMonths(new Date(), 3);
			InvoiceFee fee = getFee(InvoiceFee.BIDONLY, feeDAO);
			items.add(new InvoiceItem(fee, paymentExpires));
			return items;
		}

		if (contractor.getMembershipDate() == null) {
			// This contractor has never paid their activation fee, make
			// them now this applies regardless if this is a new reg or renewal
			int feeID = InvoiceFee.ACTIVATION;
			if (hasReducedActivation(contractor)) {
				feeID = InvoiceFee.ACTIVATION99;
			}
			InvoiceFee fee = getFee(feeID, feeDAO);

			// Activate effective today
			items.add(new InvoiceItem(fee, new Date()));
		}

		// For Reactivation Fee and Reactivating Membership
		else if ("Reactivation".equals(billingStatus) || "Membership Canceled".equals(billingStatus)) {
			InvoiceFee fee = getFee(InvoiceFee.REACTIVATION, feeDAO);
			// Reactivate effective today
			items.add(new InvoiceItem(fee, new Date()));
		}

		if ("Activation".equals(billingStatus) || "Reactivation".equals(billingStatus)
				|| "Membership Canceled".equals(billingStatus)) {
			Date paymentExpires = DateBean.addMonths(new Date(), 12);
			items.add(new InvoiceItem(contractor.getNewMembershipLevel(), paymentExpires));
		}

		if (billingStatus.startsWith("Renew")) {
			// We could eventually customize the 12 months to support
			// monthly/quarterly billing cycles
			Date paymentExpires = DateBean.addMonths(contractor.getPaymentExpires(), 12);
			items.add(new InvoiceItem(contractor.getNewMembershipLevel(), paymentExpires));
		}
		// For Upgrades
		// Calculate a prorated amount depending on when the upgrade happens
		// and when the actual membership expires
		if ("Upgrade".equals(billingStatus)) {
			if (contractor.getNewMembershipLevel() != null && contractor.getMembershipLevel() != null) {
				BigDecimal upgradeAmount = BigDecimal.ZERO;
				String description = "";

				if (contractor.getMembershipLevel().isFree()) {
					// Starting from scratch/Free Membership Level
					upgradeAmount = contractor.getNewMembershipLevel().getAmount();
					description = "Membership Level is: $" + contractor.getNewMembershipLevel().getAmount();

				} else if (DateBean.getDateDifference(contractor.getPaymentExpires()) < 0) {
					// Their membership has already expired so we need to do a
					// full renewal amount
					upgradeAmount = contractor.getNewMembershipLevel().getAmount();
					description = "Membership Level is: $" + contractor.getNewMembershipLevel().getAmount();

				} else {
					// Actual prorated Upgrade
					Date upgradeDate = (contractor.getLastUpgradeDate() == null) ? new Date() : contractor
							.getLastUpgradeDate();
					double daysUntilExpiration = DateBean
							.getDateDifference(upgradeDate, contractor.getPaymentExpires());
					BigDecimal upgradeAmountDifference = contractor.getNewMembershipLevel().getAmount().subtract(
							contractor.getMembershipLevel().getAmount());

					upgradeAmount = new BigDecimal(daysUntilExpiration).multiply(upgradeAmountDifference).divide(
							new BigDecimal(365), 0, RoundingMode.HALF_UP);

					description = "Upgrading from $" + contractor.getMembershipLevel().getAmount() + ". Prorated $"
							+ upgradeAmount;
				}

				InvoiceItem invoiceItem = new InvoiceItem();
				invoiceItem.setInvoiceFee(contractor.getNewMembershipLevel());
				invoiceItem.setAmount(upgradeAmount);
				invoiceItem.setDescription(description);
				// invoiceItem.setPaymentExpires(contractor.getPaymentExpires());
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

	static private InvoiceFee getFee(int feeID, InvoiceFeeDAO feeDao) {
		InvoiceFee fee = null;
		if (feeID > 0) {
			if (feeDao == null) {
				System.out.println("WARNING: dao was not passed in, so fee " + feeID + " will not be linked");
				fee = new InvoiceFee(feeID);
				// fee.setAmount(new BigDecimal(99));
			} else
				fee = feeDao.find(feeID);
		}
		return fee;
	}

	static public boolean hasReducedActivation(ContractorAccount contractor) {
		final Date now = new Date();
		final OperatorAccount requestedBy = contractor.getRequestedBy();

		if (CONTRACT_RENEWAL_NEWBELGIUM.after(now) && requestedBy.getId() == 5147)
			return true;
		if (CONTRACT_RENEWAL_TIMKEN.after(now) && requestedBy.getName().startsWith("Timken"))
			return true;
		if (CONTRACT_RENEWAL_BASF.after(now) && requestedBy.getName().startsWith("BASF"))
			return true;
		return false;
	}

	static public boolean activateContractor(ContractorAccount contractor, Invoice invoice) {
		if (contractor.getStatus().isPendingDeactivated() && invoice.getStatus().isPaid()) {
			for (InvoiceItem item : invoice.getItems()) {
				if (item.getInvoiceFee().getFeeClass().equals("Activation")
						|| item.getInvoiceFee().getId() == InvoiceFee.BIDONLY) {
					contractor.setStatus(AccountStatus.Active);
					contractor.setAuditColumns(new User(User.SYSTEM));
					return true;
				}
			}
		}
		return false;
	}
}
