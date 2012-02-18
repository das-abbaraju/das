package com.picsauditing.PICS;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.access.Permissions;
import com.picsauditing.auditBuilder.AuditBuilder;
import com.picsauditing.auditBuilder.AuditPercentCalculator;
import com.picsauditing.auditBuilder.AuditTypeRuleCache;
import com.picsauditing.auditBuilder.AuditTypesBuilder;
import com.picsauditing.auditBuilder.AuditTypesBuilder.AuditTypeDetail;
import com.picsauditing.dao.AuditDecisionTableDAO;
import com.picsauditing.dao.AuditTypeDAO;
import com.picsauditing.dao.BasicDAO;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.dao.InvoiceDAO;
import com.picsauditing.dao.InvoiceFeeDAO;
import com.picsauditing.dao.InvoiceItemDAO;
import com.picsauditing.dao.UserAssignmentDAO;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.AccountLevel;
import com.picsauditing.jpa.entities.AccountStatus;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.jpa.entities.AuditTypeClass;
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
import com.picsauditing.util.Strings;

public class BillingCalculatorSingle {
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
	private AuditDecisionTableDAO auditDAO;
	@Autowired
	private InvoiceFeeDAO invoiceFeeDAO;
	@Autowired
	private InvoiceDAO invoiceDAO;
	@Autowired
	protected BasicDAO dao;
	@Autowired
	private AuditTypeDAO auditTypeDAO;
	@Autowired
	private AuditBuilder auditBuilder;
	@Autowired
	private AuditPercentCalculator auditPercentCalculator;

	public void setPayingFacilities(ContractorAccount contractor) {
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
		boolean hasAuditGUARD = false;
		boolean hasInsureGUARD = false;
		boolean hasEmployeeAudits = false;
		boolean hasOq = false;
		boolean hasHseCompetency = false;
		boolean hasCor = false;
		boolean hasImportPQF = false;

		ruleCache.initialize(auditDAO);
		AuditTypesBuilder builder = new AuditTypesBuilder(ruleCache, contractor);

		for (AuditTypeDetail detail : builder.calculate()) {
			AuditType auditType = detail.rule.getAuditType();
			if (auditType == null)
				continue;
			if (auditType.isDesktop() || auditType.getId() == AuditType.OFFICE)
				hasAuditGUARD = true;
			if (auditType.getClassType().equals(AuditTypeClass.Policy))
				hasInsureGUARD = true;
			if (auditType.getId() == AuditType.IMPLEMENTATIONAUDITPLUS || auditType.getClassType().isEmployee()
					|| auditType.getClassType().isEmployee())
				hasEmployeeAudits = true;
			if (auditType.getId() == AuditType.HSE_COMPETENCY)
				hasHseCompetency = true;
			if (auditType.getId() == AuditType.COR)
				hasCor = true;
		}

		for (ContractorAudit ca : contractor.getAudits()) {
			if (ca.getAuditType().getId() == AuditType.IMPORT_PQF && !ca.isExpired()) {
				hasImportPQF = true;
				break;
			}
		}

		for (ContractorOperator co : contractor.getOperators()) {
			if (co.getOperatorAccount().isRequiresOQ())
				hasOq = true;
			if (!hasAuditGUARD && hasCor && co.getOperatorAccount().isDescendantOf(OperatorAccount.SUNCOR))
				hasAuditGUARD = true;
		}

		if (hasAuditGUARD) {
			InvoiceFee newLevel = feeDAO.findByNumberOfOperatorsAndClass(FeeClass.AuditGUARD, payingFacilities);
			BigDecimal newAmount = FeeClass.AuditGUARD.getAdjustedFeeAmountIfNecessary(contractor, newLevel);
			contractor.setNewFee(newLevel, newAmount);
		} else {
			contractor.clearNewFee(FeeClass.AuditGUARD, feeDAO);
		}

		if (hasInsureGUARD && !FeeClass.InsureGUARD.isExcludedFor(contractor)) {
			InvoiceFee newLevel = feeDAO.findByNumberOfOperatorsAndClass(FeeClass.InsureGUARD, payingFacilities);
			BigDecimal newAmount = FeeClass.InsureGUARD.getAdjustedFeeAmountIfNecessary(contractor, newLevel);
			contractor.setNewFee(newLevel, newAmount);
		} else {
			contractor.clearNewFee(FeeClass.InsureGUARD, feeDAO);
		}

		if (hasOq || hasHseCompetency || hasEmployeeAudits) {
			// EmployeeGUARD HSE Contractors have a tiered pricing scheme
			InvoiceFee newLevel = feeDAO.findByNumberOfOperatorsAndClass(FeeClass.EmployeeGUARD, payingFacilities);
			BigDecimal newAmount = newLevel.getAmount();

			if (!hasHseCompetency && (hasEmployeeAudits || hasOq))
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

		if (hasImportPQF) {
			InvoiceFee newLevel = invoiceFeeDAO.find(InvoiceFee.IMPORTFEE);

			if (!contractor.getFees().containsKey(FeeClass.ImportFee)) {
				InvoiceFee currentLevel = invoiceFeeDAO.find(InvoiceFee.IMPORTFEEZEROLEVEL);

				ContractorFee importConFee = new ContractorFee();
				importConFee.setAuditColumns();
				importConFee.setContractor(contractor);
				importConFee.setNewLevel(newLevel);
				importConFee.setNewAmount(newLevel.getAmount());
				importConFee.setCurrentLevel(currentLevel);
				importConFee.setCurrentAmount(currentLevel.getAmount());
				importConFee.setFeeClass(FeeClass.ImportFee);
				invoiceFeeDAO.save(importConFee);

				contractor.getFees().put(FeeClass.ImportFee, importConFee);
			} else {
				contractor.setNewFee(newLevel, newLevel.getAmount());
			}
		} else if (contractor.getFees().containsKey(FeeClass.ImportFee)) {
			contractor.clearNewFee(FeeClass.ImportFee, feeDAO);
		}

	}

	public void addInvoiceToContractor(ContractorAccount contractor, Invoice i) {
		invoiceDAO.save(i);

		contractor.getInvoices().add(i);
		contractor.syncBalance();
		accountDao.save(contractor);

		this.addNote(contractor,
				"Created invoice for " + contractor.getCurrencyCode().getSymbol() + i.getTotalAmount(),
				NoteCategory.Billing, LowMedHigh.Med, false, Account.PicsID, new User(User.SYSTEM));
	}

	/**
	 * This can only be used on invoices which are in Unpaid status to prevent Syncing errors w/ Quickbooks.
	 * 
	 * @param toUpdate
	 * @param updateWith
	 * @param permissions
	 */
	public void updateInvoice(Invoice toUpdate, Invoice updateWith, User user) throws Exception {
		if (!toUpdate.getStatus().equals(TransactionStatus.Unpaid))
			throw new Exception("Cannot update Invoice which is in " + toUpdate.getStatus() + " status.");

		if (toUpdate.getPayments().size() > 0)
			throw new Exception("Cannot update Invoices that already have payments applied.");

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

		invoiceDAO.save(toUpdate);

		addNote(toUpdate.getAccount(), "Updated invoice " + toUpdate.getId() + " from " + oldTotal + oldCurrency
				+ " to " + updateWith.getTotalAmount() + updateWith.getCurrency(), NoteCategory.Billing,
				LowMedHigh.Med, false, Account.PicsID, user);
	}

	public Invoice createInvoice(ContractorAccount contractor) {
		return createInvoice(contractor, contractor.getBillingStatus());
	}

	public Invoice createInvoice(ContractorAccount contractor, String billingStatus) {
		calculateAnnualFees(contractor);

		List<InvoiceItem> invoiceItems = createInvoiceItems(contractor, billingStatus);

		BigDecimal invoiceTotal = BigDecimal.ZERO.setScale(2);
		for (InvoiceItem item : invoiceItems)
			invoiceTotal = invoiceTotal.add(item.getAmount());

		if (invoiceTotal.compareTo(BigDecimal.ZERO) == 0) {
			return null;
		}

		Invoice invoice = new Invoice();
		invoice.setAccount(contractor);
		invoice.setCurrency(contractor.getCurrency());
		invoice.setStatus(TransactionStatus.Unpaid);
		invoice.setItems(invoiceItems);
		invoice.setTotalAmount(invoiceTotal);
		invoice.setAuditColumns(new User(User.SYSTEM));

		if (invoiceTotal.compareTo(BigDecimal.ZERO) > 0)
			invoice.setQbSync(true);

		String notes = "";

		// Calculate the due date for the invoice
		if (billingStatus.equals("Activation")) {
			invoice.setDueDate(new Date());
			InvoiceFee activation = invoiceFeeDAO.findByNumberOfOperatorsAndClass(FeeClass.Activation, 1);
			if (contractor.hasReducedActivation(activation)) {
				OperatorAccount reducedOperator = contractor.getReducedActivationFeeOperator(activation);
				notes += "(" + reducedOperator.getName() + " Promotion) Activation reduced from "
						+ contractor.getCurrencyCode().getSymbol() + activation.getAmount() + " to "
						+ contractor.getCurrencyCode().getSymbol() + reducedOperator.getActivationFee() + ". ";
			}
		} else if (billingStatus.equals("Reactivation")) {
			invoice.setDueDate(new Date());
		} else if (billingStatus.equals("Upgrade")) {
			invoice.setDueDate(DateBean.addDays(new Date(), 7));
		} else if (billingStatus.startsWith("Renew")) {
			invoice.setDueDate(contractor.getPaymentExpires());
		}

		if (!contractor.getFees().get(FeeClass.BidOnly).getCurrentLevel().isFree()
				|| !contractor.getFees().get(FeeClass.ListOnly).getCurrentLevel().isFree()) {
			invoice.setDueDate(new Date());
			contractor.setRenew(true);
		}

		if (invoice.getDueDate() == null)
			// For all other statuses like (Current)
			invoice.setDueDate(DateBean.addDays(new Date(), 30));

		// Make sure the invoice isn't due within 7 days for active accounts
		if (contractor.getStatus().isActive() && DateBean.getDateDifference(invoice.getDueDate()) < 7)
			invoice.setDueDate(DateBean.addDays(new Date(), 7));
		// End of Due date

		notes += "Thank you for doing business with PICS!";
		// AppProperty prop = appPropDao.find("invoice_comment");
		// if (prop != null) {
		// notes = prop.getValue();
		// }
		// Add the list of operators if this invoice has a membership level
		// on it
		boolean hasMembership = false;
		for (InvoiceItem item : invoiceItems) {
			if (item.getInvoiceFee().isMembership())
				hasMembership = true;
		}
		if (hasMembership) {
			notes += getOperatorsString(contractor);
		}
		invoice.setNotes(notes);

		for (InvoiceItem item : invoiceItems) {
			item.setInvoice(invoice);
			item.setAuditColumns(new User(User.SYSTEM));
		}

		invoice.preSave();
		return invoice;
	}

	/**
	 * Create a list of fees that this contractor should be charged for. The following contractor fields are used:
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
		return createInvoiceItems(contractor, contractor.getBillingStatus());
	}

	public List<InvoiceItem> createInvoiceItems(ContractorAccount contractor, String billingStatus) {
		List<InvoiceItem> items = new ArrayList<InvoiceItem>();

		if (billingStatus.equals("Not Calculated") || billingStatus.equals("Current"))
			return items;

		addActivationFeeIfApplies(contractor, billingStatus, items);

		if ("Activation".equals(billingStatus) || "Reactivation".equals(billingStatus)
				|| "Membership Canceled".equals(billingStatus)) {
			addYearlyItems(items, contractor, DateBean.addMonths(new Date(), 12));
		} else if (billingStatus.startsWith("Renew")) {
			addYearlyItems(items, contractor, getRenewalDate(contractor));
		} else if ("Upgrade".equals(billingStatus)) {
			List<ContractorFee> upgrades = getUpgradedFees(contractor);

			if (!upgrades.isEmpty()) {
				addProratedUpgradeItems(contractor, items, upgrades);
			}
		}

		return items;
	}

	/**
	 * Calculate a prorated amount depending on when the upgrade happens and when the membership expires.
	 * 
	 * @param contractor
	 * @param items
	 * @param upgrades
	 */
	private void addProratedUpgradeItems(ContractorAccount contractor, List<InvoiceItem> items,
			List<ContractorFee> upgrades) {
		BigDecimal upgradeAmount = BigDecimal.ZERO.setScale(2);

		// Actual prorated Upgrade
		Date upgradeDate = (contractor.getLastUpgradeDate() == null) ? new Date() : contractor.getLastUpgradeDate();
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
								+ upgrade.getCurrentAmount() + ". Prorated " + contractor.getCurrencyCode().getSymbol()
								+ upgradeAmount;
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

	private List<ContractorFee> getUpgradedFees(ContractorAccount contractor) {
		List<ContractorFee> upgrades = new ArrayList<ContractorFee>();
		for (FeeClass feeClass : contractor.getFees().keySet()) {
			ContractorFee fee = contractor.getFees().get(feeClass);
			// Bid-only should not be an upgrade
			// Just a safety check
			if (fee.isHasChanged() && !fee.getNewLevel().isFree()
					&& (!fee.getNewLevel().isBidonly() || !fee.getNewLevel().isListonly()))
				upgrades.add(fee);
		}
		return upgrades;
	}

	private Date getRenewalDate(ContractorAccount contractor) {
		// If I'm upgrading from ListOnly or BidOnly, set renewal date from today for
		// new full term membership, otherwise use Contractor's Payment Expiration Date
		if (!contractor.getFees().get(FeeClass.BidOnly).getCurrentLevel().isFree()
				|| !contractor.getFees().get(FeeClass.ListOnly).getCurrentLevel().isFree())
			return DateBean.addMonths(new Date(), 12);

		return DateBean.addMonths(contractor.getPaymentExpires(), 12);
	}

	private void addYearlyItems(List<InvoiceItem> items, ContractorAccount contractor, Date paymentExpires) {
		for (FeeClass feeClass : contractor.getFees().keySet())
			if (!contractor.getFees().get(feeClass).getNewLevel().isFree()) {
				InvoiceItem newItem = new InvoiceItem(contractor.getFees().get(feeClass).getNewLevel(), contractor
						.getFees().get(feeClass).getNewAmount(), feeClass.isPaymentExpiresNeeded() ? paymentExpires
						: null);
				items.add(newItem);
			}
	}

	private void addActivationFeeIfApplies(ContractorAccount contractor, String billingStatus, List<InvoiceItem> items) {
		// Activations / Reactivations do not apply to bid only contractors
		if (contractor.getAccountLevel().isFull()) {
			if (contractor.getMembershipDate() == null || billingStatus.equals("Activation")) {
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
				InvoiceFee fee = feeDAO.findByNumberOfOperatorsAndClass(FeeClass.Reactivation, contractor
						.getPayingFacilities());
				// Reactivate effective today
				items.add(new InvoiceItem(fee, fee.getAmount(), new Date()));
			}
		}
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

		calculateAnnualFees(contractor);
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

		calculateAnnualFees(contractor);
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
			if (!importPQF.isExpired())
				return true;
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
