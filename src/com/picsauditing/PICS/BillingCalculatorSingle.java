package com.picsauditing.PICS;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import com.picsauditing.PICS.AuditBuilder.AuditTypeDetail;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.InvoiceFeeDAO;
import com.picsauditing.jpa.entities.AccountStatus;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.jpa.entities.AuditTypeClass;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorFee;
import com.picsauditing.jpa.entities.ContractorOperator;
import com.picsauditing.jpa.entities.FeeClass;
import com.picsauditing.jpa.entities.Invoice;
import com.picsauditing.jpa.entities.InvoiceFee;
import com.picsauditing.jpa.entities.InvoiceItem;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.jpa.entities.User;

public class BillingCalculatorSingle {

	public static final Date CONTRACT_RENEWAL_BASF = DateBean.parseDate("2012-01-01");

	static public void setPayingFacilities(ContractorAccount contractor) {

		List<OperatorAccount> payingOperators = new Vector<OperatorAccount>();
		for (ContractorOperator contractorOperator : contractor.getNonCorporateOperators()) {
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

	static public void calculateAnnualFees(ContractorAccount contractor) {
		setPayingFacilities(contractor);

		InvoiceFeeDAO invoiceDAO = (InvoiceFeeDAO) com.picsauditing.util.SpringUtils.getBean("InvoiceFeeDAO");

		int payingFacilities = contractor.getPayingFacilities();

		if (payingFacilities == 0) {
			// Contractors with no paying facilities are free
			for (FeeClass feeClass : contractor.getFees().keySet()) {
				InvoiceFee newLevel = invoiceDAO.findByNumberOfOperatorsAndClass(feeClass, payingFacilities);
				contractor.getFees().get(feeClass).setNewLevel(newLevel);
			}

			return;
		}

		// Checking Audits
		boolean auditGUARD = false;
		boolean insureGUARD = false;
		boolean employeeGUARD = false;

		Map<AuditType, AuditTypeDetail> map = AuditBuilder.calculateRequiredAuditTypes(contractor);
		for (AuditType auditType : map.keySet()) {
			if (auditType == null)
				continue;
			if (auditType.isDesktop() || auditType.getId() == AuditType.OFFICE)
				auditGUARD = true;
			if (auditType.getClassType().equals(AuditTypeClass.Policy))
				insureGUARD = true;
			if (auditType.getId() == AuditType.IMPLEMENTATIONAUDITPLUS || auditType.getClassType().isIm())
				employeeGUARD = true;
		}

		if (auditGUARD) {
			// Audited Contractors have a tiered pricing scheme
			InvoiceFee newLevel = invoiceDAO.findByNumberOfOperatorsAndClass(FeeClass.AuditGUARD, payingFacilities);
			contractor.getFees().get(FeeClass.AuditGUARD).setNewLevel(newLevel);
		} else {
			InvoiceFee newLevel = invoiceDAO.findByNumberOfOperatorsAndClass(FeeClass.AuditGUARD, 0);
			contractor.getFees().get(FeeClass.AuditGUARD).setNewLevel(newLevel);
		}

		if (insureGUARD) {
			// InsureGUARD Contractors have free pricing currently
			InvoiceFee newLevel = invoiceDAO.findByNumberOfOperatorsAndClass(FeeClass.InsureGUARD, payingFacilities);
			contractor.getFees().get(FeeClass.InsureGUARD).setNewLevel(newLevel);
		} else {
			InvoiceFee newLevel = invoiceDAO.findByNumberOfOperatorsAndClass(FeeClass.InsureGUARD, 0);
			contractor.getFees().get(FeeClass.InsureGUARD).setNewLevel(newLevel);
		}

		// EmployeeGUARD
		if (!employeeGUARD) {
			for (ContractorOperator co : contractor.getOperators()) {
				if (co.getOperatorAccount().isRequiresOQ())
					employeeGUARD = true;
				if (co.getOperatorAccount().isRequiresCompetencyReview())
					employeeGUARD = true;
			}
		}

		if (employeeGUARD) {
			// EmployeeGUARD HSE Contractors have a tiered pricing scheme
			InvoiceFee newLevel = invoiceDAO.findByNumberOfOperatorsAndClass(FeeClass.EmployeeGUARD, payingFacilities);
			contractor.getFees().get(FeeClass.EmployeeGUARD).setNewLevel(newLevel);
		} else {
			InvoiceFee newLevel = invoiceDAO.findByNumberOfOperatorsAndClass(FeeClass.EmployeeGUARD, 0);
			contractor.getFees().get(FeeClass.EmployeeGUARD).setNewLevel(newLevel);
		}

		// Selecting either list-only fee or DocuGUARD fee
		if (contractor.isAcceptsBids()) {
			// Set list-only
			InvoiceFee newLevel = invoiceDAO.findByNumberOfOperatorsAndClass(FeeClass.ListOnly, 1);
			contractor.getFees().get(FeeClass.ListOnly).setNewLevel(newLevel);

			// Turn off DocuGUARD fee
			newLevel = invoiceDAO.findByNumberOfOperatorsAndClass(FeeClass.DocuGUARD, 0);
			contractor.getFees().get(FeeClass.DocuGUARD).setNewLevel(newLevel);
		} else {
			// Turn on DocuGUARD fee
			InvoiceFee newLevel = invoiceDAO.findByNumberOfOperatorsAndClass(FeeClass.DocuGUARD, payingFacilities);
			contractor.getFees().get(FeeClass.DocuGUARD).setNewLevel(newLevel);

			// Turn off list-only
			newLevel = invoiceDAO.findByNumberOfOperatorsAndClass(FeeClass.ListOnly, 0);
			contractor.getFees().get(FeeClass.ListOnly).setNewLevel(newLevel);
		}

	}

	/**
	 * Create a list of fees that this contractor should be charge for. The
	 * following contractor fields are used:
	 * <ul>
	 * <li>membershipDate</li>
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
		if (billingStatus.equals("Not Calculated") || billingStatus.equals("Current"))
			return items;

		int payingFacilities = contractor.getPayingFacilities();

		// Activations / Reactivations do not apply to list only contractors
		if (!contractor.isAcceptsBids()) {
			if (contractor.getMembershipDate() == null) {
				// This contractor has never paid their activation fee, make
				// them now this applies regardless if this is a new reg or
				// renewal
				InvoiceFee fee = feeDAO.findByNumberOfOperatorsAndClass(FeeClass.Activation, 1);
				if (contractor.hasReducedActivation(fee)) {
					OperatorAccount reducedOperator = contractor.getReducedActivationFeeOperator(fee);
					fee = feeDAO.findByNumberOfOperatorsAndClass(FeeClass.Activation, 0);
					fee.setAmount(new BigDecimal(reducedOperator.getActivationFee()));
				}

				// Activate effective today
				items.add(new InvoiceItem(fee, new Date(), contractor));
				// For Reactivation Fee and Reactivating Membership
			} else if ("Reactivation".equals(billingStatus) || "Membership Canceled".equals(billingStatus)) {
				InvoiceFee fee = feeDAO.findByNumberOfOperatorsAndClass(FeeClass.Reactivation, payingFacilities);
				// Reactivate effective today
				items.add(new InvoiceItem(fee, new Date(), contractor));
			}
		}

		if ("Activation".equals(billingStatus) || "Reactivation".equals(billingStatus)
				|| "Membership Canceled".equals(billingStatus)) {
			Date paymentExpires = DateBean.addMonths(new Date(), 12);
			for (FeeClass feeClass : contractor.getFees().keySet())
				if (!contractor.getFees().get(feeClass).getNewLevel().isFree()) {
					InvoiceItem newItem = new InvoiceItem(contractor.getFees().get(feeClass).getNewLevel(), feeClass
							.isPaymentExpiresNeeded() ? paymentExpires : null, contractor);
					newItem.setAmount(contractor.getFees().get(feeClass).getNewLevel().getAmount(contractor));
					items.add(newItem);
				}
		}

		if (billingStatus.startsWith("Renew")) {
			// We could eventually customize the 12 months to support
			// monthly/quarterly billing cycles
			Date paymentExpires = DateBean.addMonths(contractor.getPaymentExpires(), 12);
			for (FeeClass feeClass : contractor.getFees().keySet()) {
				if (!contractor.getFees().get(feeClass).getNewLevel().isFree()) {
					InvoiceItem newItem = new InvoiceItem(contractor.getFees().get(feeClass).getNewLevel(), feeClass
							.isPaymentExpiresNeeded() ? paymentExpires : null, contractor);
					newItem.setAmount(contractor.getFees().get(feeClass).getNewLevel().getAmount(contractor));
					items.add(newItem);
				}
			}
		}
		// For Upgrades
		// Calculate a prorated amount depending on when the upgrade happens
		// and when the actual membership expires
		if ("Upgrade".equals(billingStatus)) {
			List<ContractorFee> upgrades = new ArrayList<ContractorFee>();
			for (FeeClass feeClass : contractor.getFees().keySet()) {
				ContractorFee fee = contractor.getFees().get(feeClass);
				// Bid-only should not be an upgrade
				// Just a safety check
				if (fee.isHasChanged() && !fee.getNewLevel().isBidonly())
					upgrades.add(fee);
			}

			if (!upgrades.isEmpty()) {
				BigDecimal upgradeAmount = BigDecimal.ZERO;
				String description = "";

				// Actual prorated Upgrade
				Date upgradeDate = (contractor.getLastUpgradeDate() == null) ? new Date() : contractor
						.getLastUpgradeDate();
				double daysUntilExpiration = DateBean.getDateDifference(upgradeDate, contractor.getPaymentExpires());
				if (daysUntilExpiration > 365)
					daysUntilExpiration = 365.0;

				BigDecimal upgradeTotal = BigDecimal.ZERO;
				for (ContractorFee upgrade : upgrades) {
					BigDecimal upgradeAmountDifference = upgrade.getNewLevel().getAmount(contractor);
					if (!contractor.isAcceptsBids()) {
						upgradeAmountDifference = upgradeAmountDifference.subtract(upgrade.getCurrentLevel().getAmount(
								contractor));
					}

					upgradeAmount = new BigDecimal(daysUntilExpiration).multiply(upgradeAmountDifference).divide(
							new BigDecimal(365), 0, RoundingMode.HALF_UP);

					upgradeTotal = upgradeTotal.add(upgradeAmount);

					if (upgradeAmount.floatValue() > 0)
						description = "Upgrading from $" + upgrade.getCurrentLevel().getAmount(contractor)
								+ ". Prorated $" + upgradeAmount;
					else
						description = "";

					InvoiceItem invoiceItem = new InvoiceItem();
					invoiceItem.setInvoiceFee(upgrade.getNewLevel());
					invoiceItem.setAmount(upgradeAmount);
					invoiceItem.setDescription(description);
					if (upgrade.getFeeClass().isPaymentExpiresNeeded())
						invoiceItem.setPaymentExpires(contractor.getPaymentExpires());

					items.add(invoiceItem);
				}
			}
		}

		// Need to change Canadian contractors a GST for all invoices
		if (contractor.getCurrencyCode().isCanada()) {
			BigDecimal total = BigDecimal.ZERO;
			for (InvoiceItem ii : items)
				total = total.add(ii.getAmount());

			InvoiceFee gst = feeDAO.findByNumberOfOperatorsAndClass(FeeClass.GST, contractor.getPayingFacilities());
			InvoiceItem invoiceItem = new InvoiceItem();
			invoiceItem.setInvoiceFee(gst);
			invoiceItem.setAmount(gst.getGSTSurchage(total));
			invoiceItem.setDescription("5% Goods & Services Tax");
			items.add(invoiceItem);
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

	static public boolean activateContractor(ContractorAccount contractor, Invoice invoice,
			ContractorAccountDAO accountDao) {
		if (contractor.getStatus().isPendingDeactivated() && invoice.getStatus().isPaid()) {
			for (InvoiceItem item : invoice.getItems()) {
				if (item.getInvoiceFee().isActivation() || item.getInvoiceFee().isBidonly()) {
					contractor.setStatus(AccountStatus.Active);
					contractor.setAuditColumns(new User(User.SYSTEM));
					accountDao.save(contractor);
					return true;
				}
			}
		}
		return false;
	}

	@SuppressWarnings("deprecation")
	static public BigDecimal getDiscountedMembershipAmount(InvoiceFee fee, ContractorAccount contractor) {
		if (fee.isMembership()) {
			if (fee.isFree())
				return BigDecimal.ZERO;

			// AuditGUARD Discounts
			if (fee.getFeeClass().equals(FeeClass.AuditGUARD)) {

				// BASF discount for 1 operator
				if (contractor.getPayingFacilities() == 1) {
					Date now = new Date();
					if (CONTRACT_RENEWAL_BASF.after(now)) {
						for (ContractorOperator contractorOperator : contractor.getNonCorporateOperators()) {
							if (contractorOperator.getOperatorAccount().getName().startsWith("BASF")) {
								return new BigDecimal(299);
							}
						}
					}
				}
			}
			// EmployeeGUARD Discounts
			else if (fee.getFeeClass().equals(FeeClass.EmployeeGUARD)) {
				boolean employeeAudits = false;
				boolean oq = false;
				boolean hseCompetency = false;

				Map<AuditType, AuditTypeDetail> map = AuditBuilder.calculateRequiredAuditTypes(contractor);
				for (AuditType auditType : map.keySet()) {
					if (auditType.getId() == AuditType.IMPLEMENTATIONAUDITPLUS || auditType.getClassType().isIm()) {
						employeeAudits = true;
						break;
					}
				}

				for (ContractorOperator co : contractor.getOperators()) {
					if (co.getOperatorAccount().isRequiresOQ())
						oq = true;
					if (co.getOperatorAccount().isRequiresCompetencyReview())
						hseCompetency = true;
				}

				if (!hseCompetency && (employeeAudits || oq))
					return BigDecimal.ZERO;
			}
		}

		// No discounts apply
		return fee.getAmount();
	}
}
