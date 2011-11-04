package com.picsauditing.PICS;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.auditBuilder.AuditTypeRuleCache;
import com.picsauditing.auditBuilder.AuditTypesBuilder;
import com.picsauditing.auditBuilder.AuditTypesBuilder.AuditTypeDetail;
import com.picsauditing.dao.AuditDecisionTableDAO;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.dao.InvoiceFeeDAO;
import com.picsauditing.dao.UserAssignmentDAO;
import com.picsauditing.jpa.entities.AccountLevel;
import com.picsauditing.jpa.entities.AccountStatus;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.jpa.entities.AuditTypeClass;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.ContractorFee;
import com.picsauditing.jpa.entities.ContractorOperator;
import com.picsauditing.jpa.entities.FeeClass;
import com.picsauditing.jpa.entities.Invoice;
import com.picsauditing.jpa.entities.InvoiceFee;
import com.picsauditing.jpa.entities.InvoiceItem;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.jpa.entities.TransactionStatus;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.util.Strings;

public class BillingCalculatorSingle {
	@Autowired
	private InvoiceFeeDAO feeDAO;
	@Autowired
	private ContractorAccountDAO accountDao;
	@Autowired
	private ContractorAuditDAO conAuditDao;
	@Autowired
	private UserAssignmentDAO uaDAO;
	@Autowired
	private AuditTypeRuleCache ruleCache;
	@Autowired
	private AuditDecisionTableDAO auditDAO;

	public static final Date CONTRACT_RENEWAL_BASF = DateBean.parseDate("2012-01-01");
	public static final Date SUNCOR_DISCOUNT_EXPIRATION = DateBean.parseDate("2011-12-01");

	public void setPayingFacilities(ContractorAccount contractor) {
		List<OperatorAccount> gcOperators = contractor.getGeneralContractorOperators();
		List<OperatorAccount> nonGCOperators = contractor.getOperatorAccounts();

		for (OperatorAccount o : gcOperators) {
			for (ContractorAccount c : o.getGeneralContractors()) {
				// Cleaning out all Operators related to the General Contractor
				nonGCOperators.removeAll(c.getOperatorAccounts());
			}
		}

		Set<OperatorAccount> gcAndNonGCOperators = new HashSet<OperatorAccount>();
		gcAndNonGCOperators.addAll(gcOperators);
		gcAndNonGCOperators.addAll(nonGCOperators);

		List<OperatorAccount> payingOperators = new Vector<OperatorAccount>();
		for (OperatorAccount o : gcAndNonGCOperators) {
			if (o.getStatus().isActive() && !"No".equals(o.getDoContractorsPay()))
				payingOperators.add(o);
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

	public void calculateAnnualFees(ContractorAccount contractor) {
		setPayingFacilities(contractor);

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
		boolean cor = false;
		boolean importPQF = false;

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
			if (auditType.getId() == AuditType.COR)
				cor = true;
		}

		for (ContractorAudit ca : contractor.getAudits()) {
			if (ca.getAuditType().getId() == AuditType.IMPORT_PQF && !ca.isExpired()) {
				importPQF = true;
				break;
			}
		}

		for (ContractorOperator co : contractor.getOperators()) {
			if (co.getOperatorAccount().isRequiresOQ())
				oq = true;
			if (!auditGUARD && cor && co.getOperatorAccount().isDescendantOf(OperatorAccount.SUNCOR))
				auditGUARD = true;
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
							newAmount = new BigDecimal(299).setScale(2);
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
				newAmount = BigDecimal.ZERO.setScale(2);

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

		if (contractor.getFees().containsKey(FeeClass.ImportFee)) {
			if (importPQF) {
				InvoiceFee newLevel = feeDAO.findByNumberOfOperatorsAndClass(FeeClass.ImportFee, payingFacilities);
				contractor.setNewFee(newLevel, newLevel.getAmount());
			} else {
				contractor.clearNewFee(FeeClass.ImportFee, feeDAO);
			}
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

	public List<InvoiceItem> createInvoiceItems(ContractorAccount contractor) {
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
					fee.setAmount(new BigDecimal(reducedOperator.getActivationFee()).setScale(2));
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
						&& (!fee.getNewLevel().isBidonly() || !fee.getNewLevel().isListonly()))
					upgrades.add(fee);
			}

			if (!upgrades.isEmpty()) {
				BigDecimal upgradeAmount = BigDecimal.ZERO.setScale(2);

				// Actual prorated Upgrade
				Date upgradeDate = (contractor.getLastUpgradeDate() == null) ? new Date() : contractor
						.getLastUpgradeDate();
				double daysUntilExpiration = DateBean.getDateDifference(upgradeDate, contractor.getPaymentExpires());
				if (daysUntilExpiration > 365)
					daysUntilExpiration = 365.0;

				BigDecimal upgradeTotal = BigDecimal.ZERO.setScale(2);
				for (ContractorFee upgrade : upgrades) {
					String description = "";
					BigDecimal upgradeAmountDifference = upgrade.getNewAmount();
					if (contractor.getAccountLevel().isFull()) {
						upgradeAmountDifference = upgradeAmountDifference.subtract(upgrade.getCurrentAmount());
					}

					// If membership fee prorate amount
					if (upgrade.getFeeClass().isMembership()) {
						upgradeAmount = new BigDecimal(daysUntilExpiration).multiply(upgradeAmountDifference).divide(
								new BigDecimal(365), 0, RoundingMode.HALF_UP);

						if (upgradeAmount.floatValue() > 0.0f) {
							upgradeTotal = upgradeTotal.add(upgradeAmount);
							if (upgrade.getCurrentAmount().floatValue() > 0.0f) {
								description = "Upgrading from " + contractor.getCurrencyCode().getSymbol()
										+ upgrade.getCurrentAmount() + ". Prorated "
										+ contractor.getCurrencyCode().getSymbol() + upgradeAmount;
							} else if (upgrade.getCurrentAmount().floatValue() == 0.0f) {
								description = "Upgrading to " + upgrade.getFeeClass() + ". Prorated "
										+ contractor.getCurrencyCode().getSymbol() + upgradeAmount;
							}
						} else
							upgradeAmount = BigDecimal.ZERO.setScale(2);

						// If not membership fee, don't prorate amount
					} else {
						upgradeAmount = upgrade.getNewAmount();
					}

					InvoiceItem invoiceItem = new InvoiceItem();
					invoiceItem.setInvoiceFee(upgrade.getNewLevel());
					invoiceItem.setAmount(upgradeAmount);
					invoiceItem.setDescription(description);
					items.add(invoiceItem);
				}
			}
		}

		List<InvoiceItem> discounts = getDiscountItems(contractor);
		items.addAll(discounts);

		return items;
	}

	public boolean activateContractor(ContractorAccount contractor, Invoice invoice) {
		if (contractor.getStatus().isPendingDeactivated() && invoice.getStatus().isPaid()) {
			for (InvoiceItem item : invoice.getItems()) {
				if (item.getInvoiceFee().isActivation() || item.getInvoiceFee().isReactivation()
						|| item.getInvoiceFee().isBidonly() || item.getInvoiceFee().isListonly()) {
					contractor.setStatus(AccountStatus.Active);
					contractor.setAuditColumns(new User(User.SYSTEM));
					accountDao.save(contractor);
					return true;
				}
			}
		}
		return false;
	}

	public String getOperatorsString(ContractorAccount contractor) {
		List<String> operatorsString = new ArrayList<String>();

		for (ContractorOperator co : contractor.getNonCorporateOperators()) {
			String doContractorsPay = co.getOperatorAccount().getDoContractorsPay();

			if (doContractorsPay.equals("Yes") || !doContractorsPay.equals("Multiple"))
				operatorsString.add(co.getOperatorAccount().getName());
		}

		Collections.sort(operatorsString);

		return " You are listed on the following operator list(s): " + Strings.implode(operatorsString, ", ");
	}

	public List<InvoiceItem> getDiscountItems(ContractorAccount contractor) {
		List<InvoiceItem> discounts = new ArrayList<InvoiceItem>();

		// Suncor First Year Registration
		if (!contractor.getFees().get(FeeClass.AuditGUARD).getNewLevel().isFree()
				&& contractor.getRequestedBy() != null
				&& contractor.getRequestedBy().isDescendantOf(OperatorAccount.SUNCOR)
				&& Boolean.TRUE.equals(contractor.getHasCanadianCompetitor())
				&& new Date().before(SUNCOR_DISCOUNT_EXPIRATION)) {
			// Safety check to make sure discount hasn't already been applied

			InvoiceFee suncorDiscount = feeDAO.findByNumberOfOperatorsAndClass(FeeClass.SuncorDiscount, contractor
					.getPayingFacilities());
			if (!isHasDiscountBeenApplied(contractor, suncorDiscount)) {
				InvoiceItem invoiceItem = new InvoiceItem();
				invoiceItem.setInvoiceFee(suncorDiscount);

				// Registration date by June 30: Discount $200
				// Registration date by July 31: Discount $180
				// Registration date by Aug 31: Discount $160
				// Registration date by Sept 30: Discount $140
				// Registration date by Oct 31: Discount $120
				// Registration date by Nov 30: Discount $100

				// Calculating discount based off the day of invoice creation
				BigDecimal discountAmount = BigDecimal.ZERO.setScale(2);
				Date today = new Date();

				if (today.before(DateBean.parseDate("2011-07-01")))
					discountAmount = discountAmount.add(new BigDecimal(-200.00));
				else if (today.after(DateBean.parseDate("2011-07-01"))
						&& today.before(DateBean.parseDate("2011-08-01")))
					discountAmount = discountAmount.add(new BigDecimal(-180.00));
				else if (today.after(DateBean.parseDate("2011-08-01"))
						&& today.before(DateBean.parseDate("2011-09-01")))
					discountAmount = discountAmount.add(new BigDecimal(-160.00));
				else if (today.after(DateBean.parseDate("2011-09-01"))
						&& today.before(DateBean.parseDate("2011-10-01")))
					discountAmount = discountAmount.add(new BigDecimal(-140.00));
				else if (today.after(DateBean.parseDate("2011-10-01"))
						&& today.before(DateBean.parseDate("2011-11-01")))
					discountAmount = discountAmount.add(new BigDecimal(-120.00));
				else if (today.after(DateBean.parseDate("2011-11-01"))
						&& today.before(DateBean.parseDate("2011-12-01")))
					discountAmount = discountAmount.add(new BigDecimal(-100.00));

				invoiceItem.setAmount(discountAmount);
				discounts.add(invoiceItem);
			}
		}

		return discounts;
	}

	private boolean isHasDiscountBeenApplied(ContractorAccount contractor, InvoiceFee discount) {
		for (Invoice i : contractor.getInvoices()) {
			if (!i.getStatus().isVoid()) {
				for (InvoiceItem ii : i.getItems()) {
					if (ii.getInvoiceFee().equals(discount))
						return true;
				}
			}
		}

		return false;
	}

	public void performInvoiceStatusChangeActions(Invoice invoice, TransactionStatus newStatus) {
		ContractorAccount contractor = (ContractorAccount) invoice.getAccount();
		if (newStatus.isVoid()) {
			removeImportPQF(contractor);
		} else if (newStatus.isPaid()) {
			// assign Auditor to ImportPQF
			for (InvoiceItem item : invoice.getItems()) {
				if (item.getInvoiceFee().getId() == InvoiceFee.IMPORTFEE && invoice.getStatus().isPaid()) {
					for (ContractorAudit ca : contractor.getAudits()) {
						if (ca.getAuditType().getId() == AuditType.IMPORT_PQF && ca.getAuditor() == null) {
							ca.setAuditor(uaDAO.findByContractor(contractor).getUser());
							ca.setAssignedDate(new Date());
						}
					}
				}
			}
		}
	}

	public boolean removeImportPQF(ContractorAccount contractor) {
		for (ContractorAudit audit : contractor.getAudits()) {
			if (audit.getAuditType().getId() == AuditType.IMPORT_PQF && !audit.isExpired()) {
				audit.setExpiresDate(new Date());
				conAuditDao.save(audit);
				return true;
			}
		}

		return false;
	}
}
