package com.picsauditing.PICS;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import com.picsauditing.actions.contractors.RegistrationServiceEvaluation;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.PICS.data.DataObservable;
import com.picsauditing.access.Permissions;
import com.picsauditing.auditBuilder.AuditBuilder;
import com.picsauditing.auditBuilder.AuditPercentCalculator;
import com.picsauditing.auditBuilder.AuditTypeRuleCache;
import com.picsauditing.auditBuilder.AuditTypesBuilder;
import com.picsauditing.auditBuilder.AuditTypesBuilder.AuditTypeDetail;
import com.picsauditing.dao.AuditDataDAO;
import com.picsauditing.dao.AuditTypeDAO;
import com.picsauditing.dao.BasicDAO;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.dao.InvoiceFeeDAO;
import com.picsauditing.dao.InvoiceItemDAO;
import com.picsauditing.dao.UserAssignmentDAO;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.AccountLevel;
import com.picsauditing.jpa.entities.AccountStatus;
import com.picsauditing.jpa.entities.AuditData;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.jpa.entities.AuditTypeClass;
import com.picsauditing.jpa.entities.BillingStatus;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.ContractorFee;
import com.picsauditing.jpa.entities.ContractorOperator;
import com.picsauditing.jpa.entities.Currency;
import com.picsauditing.jpa.entities.Employee;
import com.picsauditing.jpa.entities.FeeClass;
import com.picsauditing.jpa.entities.Invoice;
import com.picsauditing.jpa.entities.InvoiceFee;
import com.picsauditing.jpa.entities.InvoiceItem;
import com.picsauditing.jpa.entities.LowMedHigh;
import com.picsauditing.jpa.entities.Note;
import com.picsauditing.jpa.entities.NoteCategory;
import com.picsauditing.jpa.entities.NoteStatus;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.jpa.entities.TransactionStatus;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.jpa.entities.YesNo;
import com.picsauditing.model.billing.InvoiceModel;
import com.picsauditing.salecommission.InvoiceObserver;
import com.picsauditing.salecommission.PaymentObserver;
import com.picsauditing.util.PicsDateFormat;

public class BillingCalculatorSingle {

	public static final int DAYS_FOR_PAST_DUE_STATUS = 30;
	public static final int DAYS_BEFORE_CONSIDERED_RENEWAL = 45;
	public static final int DAYS_FOR_CONSIDERED_FOR_DEACTIVATION = 90;

	@Autowired
	private InvoiceItemDAO invoiceItemDAO;
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
	private InvoiceFeeDAO invoiceFeeDAO;
	@Autowired
	private InvoiceService invoiceService;
	@Autowired
	protected BasicDAO dao;
	@Autowired
	private AuditTypeDAO auditTypeDAO;
	@Autowired
	private AuditBuilder auditBuilder;
	@Autowired
	private AuditPercentCalculator auditPercentCalculator;
	@Autowired
	private DataObservable salesCommissionDataObservable;
	@Autowired
	private InvoiceObserver invoiceObserver;
	@Autowired
	private PaymentObserver paymentObserver;
	@Autowired
	private TaxService taxService;
	@Autowired
	private InvoiceModel invoiceModel;
	@Autowired
	private AuditDataDAO auditDataDAO;

	private final I18nCache i18nCache = I18nCache.getInstance();

	public void initService() {
		salesCommissionDataObservable.addObserver(invoiceObserver);
		salesCommissionDataObservable.addObserver(paymentObserver);
	}

	public void setPayingFacilities(ContractorAccount contractor) {
		List<OperatorAccount> payingOperators = new Vector<OperatorAccount>();
		for (ContractorOperator contractorOperator : contractor.getNonCorporateOperators()) {
			OperatorAccount operator = contractorOperator.getOperatorAccount();
			if (operator.getStatus().isActive() && !"No".equals(operator.getDoContractorsPay())) {
				payingOperators.add(operator);
			}
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

	public void calculateContractorInvoiceFees(ContractorAccount contractor) {
		if (contractor.getStatus().isRequested()) {
			return;
		}

		setPayingFacilities(contractor);
		int payingFacilities = contractor.getPayingFacilities();

		if (payingFacilities == 0) {
			// Contractors with no paying facilities are free
			for (FeeClass feeClass : contractor.getFees().keySet()) {
				if (feeClass.isMembership()) {
					contractor.clearNewFee(feeClass, feeDAO);
				}
			}

			return;
		}

		// Checking Audits
		boolean hasAuditGUARD = false;
		boolean hasInsureGUARD = false;
		boolean hasEmployeeAudits = false;
		boolean hasOq = false;
		boolean hasHseCompetency = false;
		boolean hasCorOrIec = false;
		boolean hasImportPQF = false;
        boolean hasSSIP = false;

		AuditTypesBuilder builder = new AuditTypesBuilder(ruleCache, contractor);

		Set<OperatorAccount> operatorsRequiringInsureGUARD = new HashSet<OperatorAccount>();
		for (AuditTypeDetail detail : builder.calculate()) {
			AuditType auditType = detail.rule.getAuditType();
			if (auditType == null) {
				continue;
			}
			if (auditType.isDesktop() || auditType.getId() == AuditType.OFFICE) {
				hasAuditGUARD = true;
			}
			if (auditType.getClassType().equals(AuditTypeClass.Policy)) {
				operatorsRequiringInsureGUARD.addAll(detail.operators);
				if (!hasInsureGUARD) {
					hasInsureGUARD = qualifiesForInsureGuard(operatorsRequiringInsureGUARD);
				}
			}
			if (auditType.getId() == AuditType.IMPLEMENTATIONAUDITPLUS || auditType.getClassType().isEmployee()) {
				hasEmployeeAudits = true;
			}
			if (auditType.getId() == AuditType.HSE_COMPETENCY) {
				hasHseCompetency = true;
			}
			if (auditType.getId() == AuditType.COR || auditType.getId() == AuditType.IEC_AUDIT) {
				hasCorOrIec = true;
			}
            if (auditType.getId() == AuditType.SSIP) {
                hasSSIP = true;
            }
		}

		for (ContractorAudit ca : contractor.getAudits()) {
			if (ca.getAuditType().getId() == AuditType.IMPORT_PQF && !ca.isExpired()) {
				hasImportPQF = true;
				break;
			}
		}

		for (ContractorOperator co : contractor.getOperators()) {
			if (co.getOperatorAccount().isRequiresOQ()) {
				hasOq = true;
			}
			if (!hasAuditGUARD && hasCorOrIec && co.getOperatorAccount().isDescendantOf(OperatorAccount.SUNCOR)) {
				hasAuditGUARD = true;
			}

            if (!hasAuditGUARD && hasSSIP && co.getOperatorAccount().isOrIsDescendantOf(OperatorAccount.M_and_S)) {
                hasAuditGUARD = true;
            }
		}

		if (hasAuditGUARD) {
			InvoiceFee newLevel = feeDAO.findByNumberOfOperatorsAndClass(FeeClass.AuditGUARD, payingFacilities);
			BigDecimal newAmount = FeeClass.AuditGUARD.getAdjustedFeeAmountIfNecessary(contractor, newLevel);
			contractor.setNewFee(newLevel, newAmount);
		} else {
			contractor.clearNewFee(FeeClass.AuditGUARD, feeDAO);
		}

		if (hasInsureGUARD) {
			InvoiceFee newLevel = feeDAO.findByNumberOfOperatorsAndClass(FeeClass.InsureGUARD, payingFacilities);
			BigDecimal newAmount = FeeClass.InsureGUARD.getAdjustedFeeAmountIfNecessary(contractor, newLevel);

			if (!FeeClass.InsureGUARD.isExcludedFor(contractor, newLevel, operatorsRequiringInsureGUARD)) {
				contractor.setNewFee(newLevel, newAmount);
			} else {
				contractor.clearNewFee(FeeClass.InsureGUARD, feeDAO);
			}
		} else {
			contractor.clearNewFee(FeeClass.InsureGUARD, feeDAO);
		}

		if ((hasOq || hasHseCompetency || hasEmployeeAudits)) {
			// EmployeeGUARD HSE Contractors have a tiered pricing scheme
			InvoiceFee newLevel = feeDAO.findByNumberOfOperatorsAndClass(FeeClass.EmployeeGUARD, payingFacilities);
			BigDecimal newAmount = contractor.getCountry().getAmount(newLevel);

			if (!hasHseCompetency && (hasEmployeeAudits || hasOq)) {
				newAmount = BigDecimal.ZERO.setScale(2, BigDecimal.ROUND_UP);
			}

			contractor.setNewFee(newLevel, newAmount);
		} else {
			contractor.clearNewFee(FeeClass.EmployeeGUARD, feeDAO);
		}

		// Selecting either bid-only/list-only fee or DocuGUARD fee
		if (contractor.getAccountLevel().equals(AccountLevel.ListOnly)) {
			// Set list-only
			InvoiceFee newLevel = feeDAO.findByNumberOfOperatorsAndClass(FeeClass.ListOnly, 1);
			contractor.setNewFee(newLevel, contractor.getCountry().getAmount(newLevel));

			// Turn off DocuGUARD fee
			contractor.clearNewFee(FeeClass.DocuGUARD, feeDAO);
			// Turn off BidOnly fee
			contractor.clearNewFee(FeeClass.BidOnly, feeDAO);
		} else if (contractor.getAccountLevel().isBidOnly()) {
			// Set bid-only
			InvoiceFee newLevel = feeDAO.findByNumberOfOperatorsAndClass(FeeClass.BidOnly, 1);
			contractor.setNewFee(newLevel, contractor.getCountry().getAmount(newLevel));

			// Turn off DocuGUARD fee
			contractor.clearNewFee(FeeClass.DocuGUARD, feeDAO);
			// Turn off ListOnly fee
			contractor.clearNewFee(FeeClass.ListOnly, feeDAO);
		} else {
			// Turn on DocuGUARD fee
			InvoiceFee newLevel = feeDAO.findByNumberOfOperatorsAndClass(FeeClass.DocuGUARD, payingFacilities);
			contractor.setNewFee(newLevel, contractor.getCountry().getAmount(newLevel));

			// Turn off bid-only
			contractor.clearNewFee(FeeClass.BidOnly, feeDAO);
			// Turn off ListOnly fee
			contractor.clearNewFee(FeeClass.ListOnly, feeDAO);
		}

		if (hasImportPQF) {
			InvoiceFee newLevel = invoiceFeeDAO.find(InvoiceFee.IMPORTFEE);

			if (!contractor.getFees().containsKey(FeeClass.ImportFee)) {
				InvoiceFee currentLevel = invoiceFeeDAO.find(InvoiceFee.IMPORTFEEZEROLEVEL);

				ContractorFee importConFee = new ContractorFee();
				importConFee.setAuditColumns();
				importConFee.setContractor(contractor);
				importConFee.setNewLevel(newLevel);
				importConFee.setNewAmount(contractor.getCountry().getAmount(newLevel));
				importConFee.setCurrentLevel(currentLevel);
				importConFee.setCurrentAmount(contractor.getCountry().getAmount(currentLevel));
				importConFee.setFeeClass(FeeClass.ImportFee);
				invoiceFeeDAO.save(importConFee);

				contractor.getFees().put(FeeClass.ImportFee, importConFee);
			} else {
				contractor.setNewFee(newLevel, contractor.getCountry().getAmount(newLevel));
			}
		} else if (contractor.getFees().containsKey(FeeClass.ImportFee)) {
			contractor.clearNewFee(FeeClass.ImportFee, feeDAO);
		}

	}

	protected boolean qualifiesForInsureGuard(Set<OperatorAccount> operatorsRequiringInsureGUARD) {
		return (!(IGisExemptedFor(operatorsRequiringInsureGUARD)));
	}

	private boolean IGisExemptedFor(Set<OperatorAccount> operators) {
		for (OperatorAccount oa : operators) {
			int id = oa.getId();
			if (id == OperatorAccount.AI || id == OperatorAccount.CINTAS_CANADA) {
				continue;
			}
			if (oa.isDescendantOf(OperatorAccount.AI)) {
				continue;
			}
			return false;
		}
		return true;
	}

	/**
	 * This can only be used on invoices which are in Unpaid status to prevent
	 * Syncing errors w/ Quickbooks.
	 *
	 * @param toUpdate
	 * @param updateWith
	 * @param permissions
	 */
	public void updateInvoice(Invoice toUpdate, Invoice updateWith, User user) throws Exception {
		if (!toUpdate.getStatus().equals(TransactionStatus.Unpaid)) {
			throw new Exception("Cannot update Invoice which is in " + toUpdate.getStatus() + " status.");
		}

		if (toUpdate.getPayments().size() > 0) {
			throw new Exception("Cannot update Invoices that already have payments applied.");
		}

		BigDecimal oldTotal = toUpdate.getTotalAmount();
		Currency oldCurrency = toUpdate.getCurrency();

		Iterator<InvoiceItem> iterator = toUpdate.getItems().iterator();
		while (iterator.hasNext()) {
			InvoiceItem item = iterator.next();
			iterator.remove();
			invoiceItemDAO.remove(item);
		}

		toUpdate.setAmountApplied(updateWith.getAmountApplied());
		toUpdate.setAuditColumns(user);
		toUpdate.setCurrency(updateWith.getCurrency());
		toUpdate.setDueDate(updateWith.getDueDate());

		toUpdate.getItems().addAll(updateWith.getItems());
		updateWith.getItems().clear();
		for (InvoiceItem item : toUpdate.getItems()) {
			item.setInvoice(toUpdate);
		}

		toUpdate.setNotes(updateWith.getNotes());
		toUpdate.setPaidDate(updateWith.getPaidDate());
		toUpdate.setPoNumber(updateWith.getPoNumber());
		toUpdate.setTotalAmount(updateWith.getTotalAmount());
		toUpdate.setQbSync(true);

		invoiceService.saveInvoice(toUpdate);

		addNote(toUpdate.getAccount(), "Updated invoice " + toUpdate.getId() + " from " + oldTotal + oldCurrency
				+ " to " + updateWith.getTotalAmount() + updateWith.getCurrency(), NoteCategory.Billing,
				LowMedHigh.Med, false, Account.PicsID, user);
	}

	public Invoice createInvoice(ContractorAccount contractor, User user) throws Exception {
		return createInvoice(contractor, contractor.getBillingStatus(), user);
	}

	public Invoice createInvoice(ContractorAccount contractor, BillingStatus billingStatus, User user) throws Exception {
		Invoice invoice = null;
		List<InvoiceItem> invoiceItems = createInvoiceItems(contractor, billingStatus, user);
		// disallow zero dollar invoices (preserving existing behavior in a
		// refactor)
		BigDecimal invoiceTotal = calculateInvoiceTotal(invoiceItems);
		if (invoiceTotal.compareTo(BigDecimal.ZERO) > 0) {
			invoice = createInvoiceWithItems(contractor, invoiceItems, new User(User.SYSTEM));
			taxService.applyTax(invoice);
		}
		return invoice;
	}

	public Invoice createInvoiceWithItems(ContractorAccount contractor, List<InvoiceItem> invoiceItems, User auditUser) {
		BigDecimal invoiceTotal = calculateInvoiceTotal(invoiceItems);

		Invoice invoice = new Invoice();
		invoice.setAccount(contractor);
		invoice.setCurrency(contractor.getCountry().getCurrency());
		invoice.setStatus(TransactionStatus.Unpaid);
		invoice.setItems(invoiceItems);
		invoice.setTotalAmount(invoiceTotal);
		invoice.setAuditColumns(auditUser);

		if (invoiceTotal.compareTo(BigDecimal.ZERO) > 0) {
			invoice.setQbSync(true);
		}

		calculateAndSetDueDateOn(invoice, contractor);
		setContractorRenewToTrueIfNeeded(contractor);

		// Add the list of operators if this invoice has a membership level
		// on it
		boolean hasMembership = false;
		for (InvoiceItem item : invoiceItems) {
			if (item.getInvoiceFee().isMembership()) {
				hasMembership = true;
			}
		}

		if (hasMembership) {
			invoice.setNotes(invoiceModel.getSortedClientSiteList(contractor));
		}

		for (InvoiceItem item : invoiceItems) {
			item.setInvoice(invoice);
			item.setAuditColumns(auditUser);
		}

		return invoice;
	}

	public BigDecimal calculateInvoiceTotal(List<InvoiceItem> invoiceItems) {
		BigDecimal invoiceTotal = BigDecimal.ZERO.setScale(2, BigDecimal.ROUND_UP);
		for (InvoiceItem item : invoiceItems) {
			invoiceTotal = invoiceTotal.add(item.getAmount());
		}
		return invoiceTotal;
	}

	private static void calculateAndSetDueDateOn(Invoice invoice, ContractorAccount contractor) {
		BillingStatus billingStatus = contractor.getBillingStatus();
		invoice.setDueDate(calculateInvoiceDueDate(contractor, billingStatus, new Date(), invoice.getDueDate()));
	}

	public static Date calculateInvoiceDueDate(ContractorAccount contractor, BillingStatus billingStatus, Date today,
			Date dueDate) {
		if (BillingStatus.Activation == billingStatus) {
			dueDate = today;
		} else if (BillingStatus.Reactivation == billingStatus) {
			dueDate = today;
		} else if (BillingStatus.Upgrade == billingStatus) {
			dueDate = DateBean.addDays(today, 7);
		} else if (BillingStatus.Renewal == billingStatus || BillingStatus.RenewalOverdue == billingStatus) {
			dueDate = contractor.getPaymentExpires();
		}

		if (dueDate == null) {
			dueDate = DateBean.addDays(today, 30);
		}

		// Make sure the invoice isn't due within 7 days for active accounts
		if (contractor.getStatus().isActive() && DateBean.daysBetween(today, dueDate) < 7) {
			dueDate = DateBean.addDays(today, 7);
		}
		return dueDate;
	}

	private void setContractorRenewToTrueIfNeeded(ContractorAccount contractor) {
		if (!contractor.getFees().get(FeeClass.BidOnly).getCurrentLevel().isFree()
				|| !contractor.getFees().get(FeeClass.ListOnly).getCurrentLevel().isFree()) {
			contractor.setRenew(true);
		}
	}

	/**
	 * Create a list of fees that this contractor should be charged for. The
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

	public List<InvoiceItem> createInvoiceItems(ContractorAccount contractor, User user) {
		return createInvoiceItems(contractor, contractor.getBillingStatus(), user);
	}

	public List<InvoiceItem> createInvoiceItems(ContractorAccount contractor, BillingStatus billingStatus, User user) {
		List<InvoiceItem> items = new ArrayList<InvoiceItem>();

		if (billingStatus.equals("Not Calculated") || billingStatus.equals("Current")) {
			return items;
		}

		addActivationFeeIfApplies(contractor, billingStatus, items);

		if (billingStatus.isActivation() || billingStatus.isReactivation() || billingStatus.isCancelled()) {
			addYearlyItems(items, contractor, DateBean.addMonths(new Date(), 12), billingStatus);
		} else if (billingStatus.isRenewal() || billingStatus.isRenewalOverdue()) {
			addYearlyItems(items, contractor, getRenewalDate(contractor), billingStatus);
		} else if (billingStatus.isUpgrade()) {
			List<ContractorFee> upgrades = getUpgradedFees(contractor);

			if (!upgrades.isEmpty()) {
				addProratedUpgradeItems(contractor, items, upgrades, user);
			}
		}

		return items;
	}

	/**
	 * Calculate a prorated amount depending on when the upgrade happens and
	 * when the membership expires.
	 *
	 * @param contractor
	 * @param items
	 * @param upgrades
	 */
	private void addProratedUpgradeItems(ContractorAccount contractor, List<InvoiceItem> items,
			List<ContractorFee> upgrades, User user) {
		BigDecimal upgradeAmount = BigDecimal.ZERO.setScale(2, BigDecimal.ROUND_UP);

		// Actual prorated Upgrade
		Date upgradeDate = (contractor.getLastUpgradeDate() == null) ? new Date() : contractor.getLastUpgradeDate();
		double daysUntilExpiration = DateBean.daysBetween(upgradeDate, contractor.getPaymentExpires());
		if (daysUntilExpiration > 365) {
			daysUntilExpiration = 365.0;
		}

		BigDecimal upgradeTotal = BigDecimal.ZERO.setScale(2, BigDecimal.ROUND_UP);
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
						description = i18nCache.getText("Invoice.UpgradingFrom", user != null ? user.getLocale()
								: Locale.ENGLISH, upgrade.getCurrentAmount(), contractor.getCountry().getCurrency()
								.getDisplay(), upgradeAmount, contractor.getCountry().getCurrency().getDisplay());
					} else if (upgrade.getCurrentAmount().floatValue() == 0.0f) {
						description = i18nCache.getText("Invoice.UpgradingTo", user != null ? user.getLocale()
								: Locale.ENGLISH, upgrade.getFeeClass(), upgradeAmount, contractor.getCountry()
								.getCurrency().getDisplay());
					}
				} else {
					upgradeAmount = BigDecimal.ZERO.setScale(2, BigDecimal.ROUND_UP);
				}

				// If not membership fee, don't pro-rate amount
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

	private List<ContractorFee> getUpgradedFees(ContractorAccount contractor) {
		List<ContractorFee> upgrades = new ArrayList<ContractorFee>();
		for (FeeClass feeClass : contractor.getFees().keySet()) {
			ContractorFee fee = contractor.getFees().get(feeClass);
			// Bid-only should not be an upgrade
			// Just a safety check
			if (fee.isHasChanged() && !fee.getNewLevel().isFree()
					&& (!fee.getNewLevel().isBidonly() || !fee.getNewLevel().isListonly())) {
				upgrades.add(fee);
			}
		}
		return upgrades;
	}

	protected Date getRenewalDate(ContractorAccount contractor) {
		return DateBean.addMonths(contractor.getPaymentExpires(), 12);
	}

	private void addYearlyItems(List<InvoiceItem> items, ContractorAccount contractor, Date paymentExpires,
			BillingStatus billingStatus) {
		for (FeeClass feeClass : contractor.getFees().keySet()) {
			if (!contractor.getFees().get(feeClass).getNewLevel().isFree()
					&& (feeClass.isMembership() || (!billingStatus.isRenewal() && !billingStatus.isRenewalOverdue()))) {
				InvoiceItem newItem = new InvoiceItem(contractor.getFees().get(feeClass).getNewLevel(), contractor
						.getFees().get(feeClass).getNewAmount(), feeClass.isPaymentExpiresNeeded() ? paymentExpires
						: null);
				items.add(newItem);
			}
		}
	}

	private void addActivationFeeIfApplies(ContractorAccount contractor, BillingStatus billingStatus,
			List<InvoiceItem> items) {
		// Activations / Reactivations do not apply to bid only contractors
		if (contractor.getAccountLevel().isFull()) {
			if (contractor.getMembershipDate() == null || billingStatus.isActivation()) {
				// This contractor has never paid their activation fee, make
				// them now this applies regardless if this is a new reg or
				// renewal
				InvoiceItem activation = createLineItem(contractor, FeeClass.Activation, 1);
				items.add(activation);

				if (contractorDeservesSSIPDiscount(contractor)) {
					InvoiceItem lineItem = createLineItem(contractor, FeeClass.SSIPDiscountFee, 1);
					lineItem.setAmount(activation.getAmount().multiply(BigDecimal.valueOf(-1)));
					items.add(lineItem);
				}

				// For Reactivation Fee and Reactivating Membership
			} else if (billingStatus.isReactivation() || billingStatus.isCancelled()) {
				items.add(createLineItem(contractor, FeeClass.Reactivation, contractor.getPayingFacilities()));
			}
		}
	}

	private boolean contractorDeservesSSIPDiscount(ContractorAccount contractor) {
		List<AuditData> ssipRegistrations = auditDataDAO.findContractorAuditAnswers(contractor.getId(), AuditType.PQF,
				RegistrationServiceEvaluation.QUESTION_ID_REGISTERED_WITH_SSIP);
		List<AuditData> ssipExpirations = auditDataDAO.findContractorAuditAnswers(contractor.getId(), AuditType.SSIP,
				RegistrationServiceEvaluation.QUESTION_ID_SSIP_EXPIRATION_DATE);
		if (CollectionUtils.isEmpty(ssipExpirations) || CollectionUtils.isEmpty(ssipRegistrations)) {
			return false;
		}

		return meetsDiscountCriteria(ssipRegistrations.get(0), ssipExpirations.get(0));
	}

	private boolean meetsDiscountCriteria(AuditData ssipRegistration, AuditData ssipExpirationDate) {
		return ssipIsPresent(ssipRegistration) && isWithinDateRange(ssipExpirationDate);
	}

	private boolean ssipIsPresent(AuditData data) {
		return (data != null && data.isAnswered() && YesNo.Yes.toString().equals(data.getAnswer()));
	}

	private boolean isWithinDateRange(AuditData data) {
		if (data != null && !data.isAnswered()) {
			return false;
		}

		Date ssipExDate = DateBean.parseDate(data.getAnswer(), PicsDateFormat.Iso);
		return ssipExDate != null && DateBean.isBeyond(ssipExDate, 30, DateBean.Interval.Days);
	}

	private InvoiceItem createLineItem(ContractorAccount contractor, FeeClass feeClass, int numberOfSites) {
		InvoiceFee invoiceFee = feeDAO.findByNumberOfOperatorsAndClass(feeClass, numberOfSites);
		BigDecimal adjustedFeeAmount = feeClass.getAdjustedFeeAmountIfNecessary(contractor, invoiceFee);

		// Activate effective today
		return new InvoiceItem(invoiceFee, adjustedFeeAmount, new Date());
	}

	public boolean activateContractor(ContractorAccount contractor, Invoice invoice) {
		if (contractor.getStatus().isPendingOrDeactivated() && invoice.getStatus().isPaid()) {
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
		boolean removedImportPQF = false;
		for (ContractorAudit audit : contractor.getAudits()) {
			if (audit.getAuditType().getId() == AuditType.IMPORT_PQF && !audit.isExpired()) {
				audit.setExpiresDate(new Date());
				conAuditDao.save(audit);
				removedImportPQF = true;
			}
		}

		this.calculateContractorInvoiceFees(contractor);
		accountDao.save(contractor);
		return removedImportPQF;
	}

	public void addImportPQF(ContractorAccount contractor, Permissions permissions) {
		List<ContractorAudit> importPQFs = conAuditDao.findWhere(100, "contractorAccount.id = " + contractor.getId()
				+ " AND auditType.id = " + AuditType.IMPORT_PQF, "");

		if (importPQFs.isEmpty()) {
			createNewImportPQF(contractor, permissions);
		} else if (!hasActiveImportPQF(importPQFs)) {
			activateExpiredImportPQF(importPQFs);
		}

		this.calculateContractorInvoiceFees(contractor);
		contractor.setCompetitorMembership(true);
		accountDao.save(contractor);
	}

	private void createNewImportPQF(ContractorAccount contractor, Permissions permissions) {
		ContractorAudit importAudit = new ContractorAudit();
		importAudit.setAuditType(auditTypeDAO.find(AuditType.IMPORT_PQF));
		importAudit.setManuallyAdded(true);
		importAudit.setAuditColumns(permissions);
		importAudit.setContractorAccount(contractor);
		contractor.getAudits().add(importAudit);
		auditTypeDAO.save(importAudit);

		auditBuilder.buildAudits(contractor);
		auditPercentCalculator.percentCalculateComplete(importAudit);

		addNote(contractor, "Import PQF option selected.", NoteCategory.Audits, LowMedHigh.Med, true, Account.EVERYONE,
				new User(permissions.getUserId()));
	}

	private void activateExpiredImportPQF(List<ContractorAudit> importPQFs) {
		for (ContractorAudit importPQF : importPQFs) {
			if (importPQF.isExpired()) {
				importPQF.setExpiresDate(null);
			}

			return;
		}
	}

	private boolean hasActiveImportPQF(List<ContractorAudit> importPQFs) {
		for (ContractorAudit importPQF : importPQFs) {
			if (!importPQF.isExpired()) {
				return true;
			}
		}

		return false;
	}

	protected Note addNote(Account account, String newNote, NoteCategory noteCategory, LowMedHigh priority,
			boolean canContractorView, int viewableBy, User user) {
		return addNote(account, newNote, noteCategory, LowMedHigh.Low, true, viewableBy, user, null);
	}

	protected Note addNote(Account account, String newNote, NoteCategory category, LowMedHigh priority,
			boolean canContractorView, int viewableBy, User user, Employee employee) {
		Note note = new Note();
		note.setAuditColumns();
		note.setAccount(account);
		note.setSummary(newNote);
		note.setPriority(priority);
		note.setNoteCategory(category);
		note.setViewableById(viewableBy);
		note.setCanContractorView(canContractorView);
		note.setStatus(NoteStatus.Closed);
		note.setEmployee(employee);
		dao.save(note);
		return note;
	}
}
