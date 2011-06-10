package com.picsauditing.PICS;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import com.picsauditing.auditBuilder.AuditTypeRuleCache;
import com.picsauditing.auditBuilder.AuditTypesBuilder;
import com.picsauditing.auditBuilder.AuditTypesBuilder.AuditTypeDetail;
import com.picsauditing.dao.AuditDecisionTableDAO;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.InvoiceFeeDAO;
import com.picsauditing.jpa.entities.AccountLevel;
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
import com.picsauditing.util.Strings;

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

		InvoiceFeeDAO feeDAO = (InvoiceFeeDAO) com.picsauditing.util.SpringUtils.getBean("InvoiceFeeDAO");

		int payingFacilities = contractor.getPayingFacilities();

		if (payingFacilities == 0) {
			// Contractors with no paying facilities are free
			for (FeeClass feeClass : contractor.getFees().keySet()) {
				if (feeClass.isMembership())
					contractor.clearNewFee(feeClass, feeDAO);
			}

			return;
		}

		// Checking Audits
		boolean auditGUARD = false;
		boolean insureGUARD = false;
		boolean employeeAudits = false;
		boolean oq = false;
		boolean hseCompetency = false;

		AuditTypeRuleCache ruleCache = (AuditTypeRuleCache) com.picsauditing.util.SpringUtils
				.getBean("AuditTypeRuleCache");
		AuditDecisionTableDAO auditDAO = (AuditDecisionTableDAO) com.picsauditing.util.SpringUtils
				.getBean("AuditDecisionTableDAO");
		ruleCache.initialize(auditDAO);
		AuditTypesBuilder builder = new AuditTypesBuilder(ruleCache, contractor);

		for (AuditTypeDetail detail : builder.calculate()) {
			AuditType auditType = detail.rule.getAuditType();
			if (auditType == null)
				continue;
			if (auditType.isDesktop() || auditType.getId() == AuditType.OFFICE)
				auditGUARD = true;
			if (auditType.getClassType().equals(AuditTypeClass.Policy))
				insureGUARD = true;
			if (auditType.getId() == AuditType.IMPLEMENTATIONAUDITPLUS || auditType.getClassType().isIm())
				employeeAudits = true;
			if (auditType.getId() == AuditType.HSE_COMPETENCY)
				hseCompetency = true;
		}

		for (ContractorOperator co : contractor.getOperators()) {
			if (co.getOperatorAccount().isRequiresOQ())
				oq = true;
		}

		if (auditGUARD) {
			// Audited Contractors have a tiered pricing scheme
			InvoiceFee newLevel = feeDAO.findByNumberOfOperatorsAndClass(FeeClass.AuditGUARD, payingFacilities);
			BigDecimal newAmount = newLevel.getAmount();

			// calculating discount(s)
			if (contractor.getPayingFacilities() == 1) {
				Date now = new Date();
				if (CONTRACT_RENEWAL_BASF.after(now)) {
					for (ContractorOperator contractorOperator : contractor.getNonCorporateOperators()) {
						if (contractorOperator.getOperatorAccount().getName().startsWith("BASF")) {
							newAmount = new BigDecimal(299);
						}
					}
				}
			}

			contractor.setNewFee(newLevel, newAmount);
		} else {
			contractor.clearNewFee(FeeClass.AuditGUARD, feeDAO);
		}

		if (insureGUARD) {
			// InsureGUARD Contractors have free pricing currently
			InvoiceFee newLevel = feeDAO.findByNumberOfOperatorsAndClass(FeeClass.InsureGUARD, payingFacilities);
			contractor.setNewFee(newLevel, newLevel.getAmount());
		} else {
			contractor.clearNewFee(FeeClass.InsureGUARD, feeDAO);
		}

		if (oq || hseCompetency || employeeAudits) {
			// EmployeeGUARD HSE Contractors have a tiered pricing scheme
			InvoiceFee newLevel = feeDAO.findByNumberOfOperatorsAndClass(FeeClass.EmployeeGUARD, payingFacilities);
			BigDecimal newAmount = newLevel.getAmount();

			if (!hseCompetency && (employeeAudits || oq))
				newAmount = BigDecimal.ZERO;

			contractor.setNewFee(newLevel, newAmount);
		} else {
			contractor.clearNewFee(FeeClass.EmployeeGUARD, feeDAO);
		}

		// Selecting either bid-only/list-only fee or DocuGUARD fee
		if (contractor.getAccountLevel().equals(AccountLevel.ListOnly)) {
			// Set list-only
			InvoiceFee newLevel = feeDAO.findByNumberOfOperatorsAndClass(FeeClass.ListOnly, 1);
			contractor.setNewFee(newLevel, newLevel.getAmount());

			// Turn off DocuGUARD fee
			contractor.clearNewFee(FeeClass.DocuGUARD, feeDAO);
			// Turn off BidOnly fee
			contractor.clearNewFee(FeeClass.BidOnly, feeDAO);
		} else if (contractor.getAccountLevel().isBidOnly()) {
			// Set bid-only
			InvoiceFee newLevel = feeDAO.findByNumberOfOperatorsAndClass(FeeClass.BidOnly, 1);
			contractor.setNewFee(newLevel, newLevel.getAmount());

			// Turn off DocuGUARD fee
			contractor.clearNewFee(FeeClass.DocuGUARD, feeDAO);
			// Turn off ListOnly fee
			contractor.clearNewFee(FeeClass.ListOnly, feeDAO);
		} else {
			// Turn on DocuGUARD fee
			InvoiceFee newLevel = feeDAO.findByNumberOfOperatorsAndClass(FeeClass.DocuGUARD, payingFacilities);
			contractor.setNewFee(newLevel, newLevel.getAmount());

			// Turn off bid-only
			contractor.clearNewFee(FeeClass.BidOnly, feeDAO);
			// Turn off ListOnly fee
			contractor.clearNewFee(FeeClass.ListOnly, feeDAO);
		}

	}

	/**
	 * Create a list of fees that this contractor should be charge for. The following contractor fields are used:
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

		// Activations / Reactivations do not apply to bid only contractors
		if (contractor.getAccountLevel().isFull()) {
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
				items.add(new InvoiceItem(fee, fee.getAmount(), new Date()));
				// For Reactivation Fee and Reactivating Membership
			} else if ("Reactivation".equals(billingStatus) || "Membership Canceled".equals(billingStatus)) {
				InvoiceFee fee = feeDAO.findByNumberOfOperatorsAndClass(FeeClass.Reactivation, payingFacilities);
				// Reactivate effective today
				items.add(new InvoiceItem(fee, fee.getAmount(), new Date()));
			}
		}

		if ("Activation".equals(billingStatus) || "Reactivation".equals(billingStatus)
				|| "Membership Canceled".equals(billingStatus)) {
			Date paymentExpires = DateBean.addMonths(new Date(), 12);
			for (FeeClass feeClass : contractor.getFees().keySet())
				if (!contractor.getFees().get(feeClass).getNewLevel().isFree()) {
					InvoiceItem newItem = new InvoiceItem(contractor.getFees().get(feeClass).getNewLevel(), contractor
							.getFees().get(feeClass).getNewAmount(), feeClass.isPaymentExpiresNeeded() ? paymentExpires
							: null);
					items.add(newItem);
				}
		}

		if (billingStatus.startsWith("Renew")) {
			// We could eventually customize the 12 months to support
			// monthly/quarterly billing cycles
			for (FeeClass feeClass : contractor.getFees().keySet()) {
				if (!contractor.getFees().get(feeClass).getNewLevel().isFree()) {
					Date paymentExpires = DateBean.addMonths(contractor.getPaymentExpires(), 12);
					// If I'm upgrading from ListOnly or BidOnly, set renewal date from today for
					// new full term membership
					if (!contractor.getFees().get(FeeClass.BidOnly).getCurrentLevel().isFree()
							|| !contractor.getFees().get(FeeClass.ListOnly).getCurrentLevel().isFree())
						paymentExpires = DateBean.addMonths(new Date(), 12);

					InvoiceItem newItem = new InvoiceItem(contractor.getFees().get(feeClass).getNewLevel(), contractor
							.getFees().get(feeClass).getNewAmount(), feeClass.isPaymentExpiresNeeded() ? paymentExpires
							: null);
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
				if (fee.isHasChanged() && !fee.getNewLevel().isFree()
						&& (!fee.getNewLevel().isBidonly() || !fee.getNewLevel().equals(AccountLevel.ListOnly)))
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
					BigDecimal upgradeAmountDifference = upgrade.getNewAmount();
					if (contractor.getAccountLevel().isFull()) {
						upgradeAmountDifference = upgradeAmountDifference.subtract(upgrade.getCurrentAmount());
					}

					upgradeAmount = new BigDecimal(daysUntilExpiration).multiply(upgradeAmountDifference).divide(
							new BigDecimal(365), 0, RoundingMode.HALF_UP);

					if (upgradeAmount.floatValue() > 0) {
						upgradeTotal = upgradeTotal.add(upgradeAmount);
						description = "Upgrading from " + contractor.getCurrencyCode().getIcon()
								+ upgrade.getCurrentAmount() + ". Prorated " + contractor.getCurrencyCode().getIcon()
								+ upgradeAmount;
					} else
						upgradeAmount = BigDecimal.ZERO;

					InvoiceItem invoiceItem = new InvoiceItem();
					invoiceItem.setInvoiceFee(upgrade.getNewLevel());
					invoiceItem.setAmount(upgradeAmount);
					invoiceItem.setDescription(description);
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
				if (item.getInvoiceFee().isActivation() || item.getInvoiceFee().isBidonly()
						|| item.getInvoiceFee().isListonly()) {
					contractor.setStatus(AccountStatus.Active);
					contractor.setAuditColumns(new User(User.SYSTEM));
					accountDao.save(contractor);
					return true;
				}
			}
		}
		return false;
	}

	public static String getOperatorsString(ContractorAccount contractor) {
		List<String> operatorsString = new ArrayList<String>();

		for (ContractorOperator co : contractor.getNonCorporateOperators()) {
			String doContractorsPay = co.getOperatorAccount().getDoContractorsPay();

			if (doContractorsPay.equals("Yes") || !doContractorsPay.equals("Multiple"))
				operatorsString.add(co.getOperatorAccount().getName());
		}

		Collections.sort(operatorsString);

		return " You are listed on the following operator list(s): " + Strings.implode(operatorsString, ", ");
	}
}
