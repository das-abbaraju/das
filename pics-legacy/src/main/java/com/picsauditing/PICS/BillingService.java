package com.picsauditing.PICS;

import com.picsauditing.PICS.data.DataObservable;
import com.picsauditing.access.Permissions;
import com.picsauditing.actions.contractors.RegistrationServiceEvaluation;
import com.picsauditing.auditBuilder.AuditBuilder;
import com.picsauditing.auditBuilder.AuditPercentCalculator;
import com.picsauditing.auditBuilder.AuditTypeRuleCache;
import com.picsauditing.dao.*;
import com.picsauditing.i18n.service.TranslationService;
import com.picsauditing.jpa.entities.*;
import com.picsauditing.jpa.entities.Currency;
import com.picsauditing.model.billing.AccountingSystemSynchronization;
import com.picsauditing.model.billing.InvoiceModel;
import com.picsauditing.salecommission.InvoiceObserver;
import com.picsauditing.salecommission.PaymentObserver;
import com.picsauditing.service.billing.InvoiceDiscountsService;
import com.picsauditing.service.i18n.TranslationServiceFactory;
import com.picsauditing.util.PicsDateFormat;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;


import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

public class BillingService {
    @Autowired
    protected BasicDAO dao;
    @Autowired
    private InvoiceDAO invoiceDAO;
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
	private InvoiceModel invoiceModel;
    @Autowired
	private AuditDataDAO auditDataDAO;
    @Autowired
    private FeeService feeService;
    @Autowired
    private TaxService taxService;
    @Autowired
    private InvoiceDiscountsService invoiceDiscountsService;

	private final TranslationService translationService = TranslationServiceFactory.getTranslationService();

    public void initService() {
        salesCommissionDataObservable.addObserver(invoiceObserver);
        salesCommissionDataObservable.addObserver(paymentObserver);
    }

    public void syncBalance(ContractorAccount contractor) {
        BigDecimal balance = calculateCurrentBalance(contractor);

        contractor.setBalance(balance.setScale(2, BigDecimal.ROUND_UP));

        feeService.getRuleCache();
        feeService.syncMembershipFees(contractor);
    }

    private BigDecimal calculateCurrentBalance(ContractorAccount contractor) {
        BigDecimal currentBalance = BigDecimal.ZERO;
        for (Invoice invoice : contractor.getInvoices()) {
            if (!invoice.getStatus().isVoid())
                currentBalance = currentBalance.add(invoice.getTotalAmount());
        }

        for (Refund refund : contractor.getRefunds()) {
            if (!refund.getStatus().isVoid())
                currentBalance = currentBalance.add(refund.getTotalAmount());
        }

        for (Payment payment : contractor.getPayments()) {
            if (!payment.getStatus().isVoid())
                currentBalance = currentBalance.subtract(payment.getTotalAmount());
        }

        for (InvoiceCreditMemo creditMemo : contractor.getCreditMemos()) {
            if (!creditMemo.getStatus().isVoid())
                currentBalance = currentBalance.subtract(creditMemo.getTotalAmount());
        }


        return currentBalance;
    }

    public void doFinalFinancialCalculationsBeforeSaving(Invoice invoice) throws Exception {
        taxService.applyTax(invoice);
        addRevRecInfoIfAppropriateToItems(invoice);
    }

    public Invoice verifyAndSaveInvoice(Invoice invoice) throws Exception {
        taxService.verifyTaxIsAtMostOneLineItem(invoice);
        validateRevRec(invoice);
        invoice = invoiceDAO.save(invoice);
        return invoice;
    }

    private void validateRevRec(Invoice invoice) throws Exception {
        for (InvoiceItem item : invoice.getItems()) {
            if (FeeService.isRevRecDeferred(item.getInvoiceFee()) && item.getRevenueFinishDate() == null && item.getRevenueStartDate() == null) {
                throw new Exception(invoice.getInvoiceType() + " Invoice #" + invoice.getId() + " was created " + invoice.getCreationDate() + " and was not stamped for revenue recognition.");
            }
        }
    }

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
        toUpdate.setCommissionableAmount(updateWith.getCommissionableAmount());
		AccountingSystemSynchronization.setToSynchronize(toUpdate);

        doFinalFinancialCalculationsBeforeSaving(toUpdate);
        verifyAndSaveInvoice(toUpdate);

		addNote(toUpdate.getAccount(), "Updated invoice " + toUpdate.getId() + " from " + oldTotal + oldCurrency
                + " to " + updateWith.getTotalAmount() + updateWith.getCurrency(), NoteCategory.Billing,
                LowMedHigh.Med, false, Account.PicsID);
	}

    public Invoice createInvoice(ContractorAccount contractor, User user) throws Exception {
        return createInvoice(contractor, billingStatus(contractor), user);
    }

    public Invoice createInvoice(ContractorAccount contractor, BillingStatus billingStatus, User user) throws Exception {
		Invoice invoice = null;
		List<InvoiceItem> invoiceItems = createInvoiceItems(contractor, billingStatus, user);
		// disallow zero dollar invoices (preserving existing behavior in a refactor)
		BigDecimal invoiceTotal = calculateInvoiceTotal(invoiceItems);
		if (invoiceTotal.compareTo(BigDecimal.ZERO) > 0) {
			invoice = createInvoiceWithItems(contractor, invoiceItems, new User(User.SYSTEM));
			taxService.applyTax(invoice);
		}
		return invoice;
	}

	public Invoice createInvoiceWithItems(ContractorAccount contractor, List<InvoiceItem> invoiceItems, User auditUser) {
        return createInvoiceWithItems(contractor, billingStatus(contractor), invoiceItems, auditUser);
    }

	public Invoice createInvoiceWithItems(ContractorAccount contractor, BillingStatus billingStatus, List<InvoiceItem> invoiceItems, User auditUser) {
		BigDecimal invoiceTotal = calculateInvoiceTotal(invoiceItems);
        BigDecimal invoiceCommissionable = calculateInvoiceCommissionable(invoiceItems);

		Invoice invoice = generateInvoice(contractor, invoiceItems, auditUser, billingStatus, invoiceTotal, invoiceCommissionable);

		syncInvoiceIfAppropriate(invoiceTotal, invoice);

		calculateAndSetDueDateOn(invoice, contractor);
		setContractorRenewToTrueIfNeeded(contractor);

		addNotesToInvoiceIfHaveMembership(contractor, invoiceItems, invoice);

		for (InvoiceItem item : invoiceItems) {
			item.setInvoice(invoice);
			item.setAuditColumns(auditUser);
		}

		return invoice;
	}

	private void syncInvoiceIfAppropriate(BigDecimal invoiceTotal, Invoice invoice) {
		if (invoiceTotal.compareTo(BigDecimal.ZERO) > 0) {
			AccountingSystemSynchronization.setToSynchronize(invoice);
		}
	}

	private void addNotesToInvoiceIfHaveMembership(ContractorAccount contractor, List<InvoiceItem> invoiceItems, Invoice invoice) {
		boolean hasMembership = false;
		for (InvoiceItem item : invoiceItems) {
			if (item.getInvoiceFee().isMembership()) {
				invoice.setNotes(invoiceModel.getSortedClientSiteList(contractor));
				break;
			}
		}
	}

	private Invoice generateInvoice(ContractorAccount contractor, List<InvoiceItem> invoiceItems, User auditUser, BillingStatus billingStatus, BigDecimal invoiceTotal, BigDecimal invoiceCommissionable) {
		Invoice invoice = new Invoice();
		invoice.setAccount(contractor);
		invoice.setCurrency(contractor.getCountry().getCurrency());
		invoice.setStatus(TransactionStatus.Unpaid);
		invoice.setItems(invoiceItems);
		invoice.setTotalAmount(invoiceTotal);
		invoice.setCommissionableAmount(invoiceCommissionable);
		invoice.setAuditColumns(auditUser);
		invoice.setInvoiceType(convertBillingStatusToInvoiceType(invoice, billingStatus));
        invoice.setPayingFacilities(contractor.getPayingFacilities());

		return invoice;
	}

	private InvoiceType convertBillingStatusToInvoiceType(Invoice invoice, BillingStatus billingStatus) {
        Set<FeeClass> invoiceFeeClasses = getInvoiceFeeClasses(invoice);

        if (invoiceFeeClasses.contains(FeeClass.Activation) || invoiceFeeClasses.contains(FeeClass.Reactivation)) {
            return InvoiceType.Activation;
        }

        switch (billingStatus) {
            case Upgrade:
                return InvoiceType.Upgrade;
            case Renewal:
            case RenewalOverdue:
                return InvoiceType.Renewal;
            case Activation:
            case Reactivation:
                return InvoiceType.Activation;
            default:
                return InvoiceType.OtherFees;
        }
    }

    private static Set<FeeClass> getInvoiceFeeClasses(Invoice invoice) {
        Set<FeeClass> feeClasses = new HashSet<>();
        for (InvoiceItem item : invoice.getItems()) {
            feeClasses.add(item.getInvoiceFee().getFeeClass());
        }
        return feeClasses;
    }

	public BigDecimal calculateInvoiceTotal(List<InvoiceItem> invoiceItems) {
		BigDecimal invoiceTotal = BigDecimal.ZERO.setScale(2, BigDecimal.ROUND_UP);
		for (InvoiceItem item : invoiceItems) {
			invoiceTotal = invoiceTotal.add(item.getAmount());
		}
		return invoiceTotal;
	}

    private BigDecimal calculateInvoiceCommissionable(List<InvoiceItem> invoiceItems) {
        BigDecimal invoiceCommissionable = BigDecimal.ZERO.setScale(2, BigDecimal.ROUND_UP);

        for (InvoiceItem item : invoiceItems) {
            if (item.getInvoiceFee().isCommissionEligible()) {
                invoiceCommissionable = invoiceCommissionable.add(item.getAmount());
            }
        }
        return invoiceCommissionable;
	}

	private void calculateAndSetDueDateOn(Invoice invoice, ContractorAccount contractor) {
		BillingStatus billingStatus = billingStatus(contractor);
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
        Map<FeeClass, ContractorFee> fees = contractor.getFees();
        if (!(fees.get(FeeClass.BidOnly) != null && fees.get(FeeClass.BidOnly).getCurrentLevel().isFree())
		 || !(fees.get(FeeClass.ListOnly) != null && fees.get(FeeClass.ListOnly).getCurrentLevel().isFree())) {
			contractor.setRenew(true);
		}
	}

	public List<InvoiceItem> createInvoiceItems(ContractorAccount contractor, User user) {
        return createInvoiceItems(contractor, billingStatus(contractor), user);
    }

	public List<InvoiceItem> createInvoiceItems(ContractorAccount contractor, BillingStatus billingStatus, User user) {
		List<InvoiceItem> items = new ArrayList<>();

		if (billingStatus.equals("Not Calculated") || billingStatus.equals("Current")) {
			return items;
		}

		addActivationFeeIfApplies(contractor, billingStatus, items);
        addProductItems(contractor, billingStatus, user, items);
        addSSIPDiscountIfApplies(contractor, items);

        // TODO: Right now we're ignoring upgrades, but in the near future we need to pro-rate discounts
        if (billingStatus(contractor) != BillingStatus.Upgrade) {
            items.addAll(invoiceDiscountsService.applyDiscounts(contractor, items));
        }

		return items;
	}

    private void addProductItems(ContractorAccount contractor, BillingStatus billingStatus, User user, List<InvoiceItem> items) {
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
    }

	@SuppressWarnings("deprecation")
	private void addProratedUpgradeItems(ContractorAccount contractor, List<InvoiceItem> items,
			List<ContractorFee> upgrades, User user) {
		BigDecimal upgradeAmount;

		// Actual prorated Upgrade
        double daysUntilExpiration = determineDaysUntilExpiration(contractor);

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
                    String currencyDisplay = contractor.getCountry().getCurrency().getDisplay();
                    Locale locale = Locale.ENGLISH;
                    if (user != null) {
                        locale = user.getLocale();
                    }

                    description = buildUpgradeDescription(upgradeAmount, upgrade, description, currencyDisplay, locale);
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
            invoiceItem.setOriginalAmount(upgrade.getNewAmount());
			invoiceItem.setDescription(description);
			items.add(invoiceItem);
		}
	}

    private double determineDaysUntilExpiration(ContractorAccount contractor) {
        Date upgradeDate = (contractor.getLastUpgradeDate() == null) ? new Date() : contractor.getLastUpgradeDate();
        double daysUntilExpiration = DateBean.daysBetween(upgradeDate, contractor.getPaymentExpires());
        if (daysUntilExpiration > 365) {
            daysUntilExpiration = 365.0;
        }
        return daysUntilExpiration;
    }

    private String buildUpgradeDescription(BigDecimal upgradeAmount, ContractorFee upgrade, String description, String currencyDisplay, Locale locale) {
        if (upgrade.getCurrentAmount().floatValue() > 0.0f) {
            description = translationService.getText("Invoice.UpgradingFrom",
                    locale, upgrade.getCurrentAmount(),
                    currencyDisplay, upgradeAmount, currencyDisplay);
        } else if (upgrade.getCurrentAmount().floatValue() == 0.0f) {
            description = translationService.getText("Invoice.UpgradingTo", locale,
                    upgrade.getFeeClass(), upgradeAmount, currencyDisplay);
        }
        return description;
    }

    private List<ContractorFee> getUpgradedFees(ContractorAccount contractor) {
		List<ContractorFee> upgrades = new ArrayList<ContractorFee>();
		for (FeeClass feeClass : contractor.getFees().keySet()) {
			ContractorFee fee = contractor.getFees().get(feeClass);

            InvoiceFee newLevel = fee.getNewLevel();
            if (fee.isUpgrade() && !newLevel.isFree() && (!newLevel.isBidonly() || !newLevel.isListonly())) {
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
        Map<FeeClass, ContractorFee> fees = contractor.getFees();

        for (FeeClass feeClass : fees.keySet()) {
            ContractorFee contractorFee = fees.get(feeClass);
            InvoiceFee newLevel = contractorFee.getNewLevel();
            BigDecimal newAmount = contractorFee.getNewAmount();

            if (!newLevel.isFree() && (feeClass.isMembership() || (!billingStatus.isRenewal() && !billingStatus.isRenewalOverdue()))) {
				InvoiceItem newItem = new InvoiceItem(newLevel, newAmount, newAmount, feeClass.isPaymentExpiresNeeded() ? paymentExpires : null);
				items.add(newItem);
			}
		}
	}

	private void addActivationFeeIfApplies(ContractorAccount contractor, BillingStatus billingStatus, List<InvoiceItem> items) {
		if (contractor.getAccountLevel().isFull()) {
			if (contractor.getMembershipDate() == null || billingStatus.isActivation()) {
				InvoiceItem activation = createLineItem(contractor, FeeClass.Activation, 1);
				items.add(activation);

			} else if (billingStatus.isReactivation() || billingStatus.isCancelled()) {
				items.add(createLineItem(contractor, FeeClass.Reactivation, 1));
			}
		}
	}

    private void addSSIPDiscountIfApplies(ContractorAccount contractor, List<InvoiceItem> items) {
        if (containsActivation(items) && containsAuditGuard(items) &&  contractorDeservesSSIPDiscount(contractor)) {
            InvoiceItem activation = findIn(FeeClass.Activation, items);
            InvoiceItem lineItem = createLineItem(contractor, FeeClass.SSIPDiscountFee, 1);
            lineItem.setAmount(activation.getAmount().multiply(BigDecimal.valueOf(-1)));
            items.add(lineItem);
        }
    }

    private boolean containsActivation(List<InvoiceItem> items) {
        return findIn(FeeClass.Activation, items) != null;
    }

    private boolean containsAuditGuard(List<InvoiceItem> items) {
        return findIn(FeeClass.AuditGUARD, items) != null;
    }

    private InvoiceItem findIn(FeeClass fee, List<InvoiceItem> items) {
        for (InvoiceItem item : items)
            if (item.getInvoiceFee().getFeeClass() == fee) return item;
        return null;
    }

	private boolean contractorDeservesSSIPDiscount(ContractorAccount contractor) {
		List<AuditData> ssipRegistrations = auditDataDAO.findContractorAuditAnswers(contractor.getId(), AuditType.PQF,
				RegistrationServiceEvaluation.QUESTION_ID_REGISTERED_WITH_SSIP);
		List<AuditData> ssipExpirations = auditDataDAO.findContractorAuditAnswers(contractor.getId(), AuditType.SSIP,
				RegistrationServiceEvaluation.QUESTION_ID_SSIP_EXPIRATION_DATE);
		if (CollectionUtils.isEmpty(ssipExpirations) || CollectionUtils.isEmpty(ssipRegistrations)) {
			return false;
		}

		return shouldAddSsipDiscount(contractor) && meetsDiscountCriteria(ssipRegistrations.get(0), ssipExpirations.get(0));
	}

    private boolean shouldAddSsipDiscount(ContractorAccount contractor) {
        for (AuditTypeRule rule : ruleCache.getRules(contractor)) {
            if (rule.isInclude() && rule.getAuditType().getId() == AuditType.SSIP) {
                return true;
            }
        }
        return false;
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
		BigDecimal adjustedFeeAmount = FeeService.getAdjustedFeeAmountIfNecessary(contractor, invoiceFee);

		// Activate effective today
		return new InvoiceItem(invoiceFee, adjustedFeeAmount, adjustedFeeAmount, new Date());
	}

	public boolean activateContractor(ContractorAccount contractor, Invoice invoice) {
		if (contractor.getStatus().isPendingRequestedDeclinedOrDeactivated() && invoice.getStatus().isPaid()) {
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

		feeService.calculateContractorInvoiceFees(contractor);
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

        feeService.calculateContractorInvoiceFees(contractor);
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

		addNote(contractor, "Import PQF option selected.", NoteCategory.Audits, LowMedHigh.Med, true, Account.EVERYONE);
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
			boolean canContractorView, int viewableBy) {
		Note note = new Note();
		note.setAuditColumns();
		note.setAccount(account);
		note.setSummary(newNote);
		note.setPriority(priority);
		note.setNoteCategory(noteCategory);
		note.setViewableById(viewableBy);
		note.setCanContractorView(canContractorView);
		note.setStatus(NoteStatus.Closed);
		dao.save(note);
		return note;
	}

	public void addRevRecInfoIfAppropriateToItems(Invoice invoice) throws Exception {
		ContractorAccount contractor = (ContractorAccount) invoice.getAccount();
		Date invoiceItemStartDate = calculateInvoiceItemRevRecStartDateFor(invoice);
		Date invoiceItemEndDate = calculateInvoiceItemRevRecFinishDateFor(invoice, contractor);
		for (InvoiceItem invoiceItem : invoice.getItems()) {
			if (!FeeService.isRevRecDeferred(invoiceItem.getInvoiceFee())) {
				continue;
			}
			invoiceItem.setRevenueStartDate(invoiceItemStartDate);
			invoiceItem.setRevenueFinishDate(invoiceItemEndDate);
		}
	}

	public Date calculateInvoiceItemRevRecFinishDateFor(Invoice invoice, ContractorAccount contractor) throws Exception {
		switch (invoice.getInvoiceType()) {
			case Activation:
				return calculateActivationRevRecFinishDate(invoice, contractor);
			case Renewal:
				return calculateRenewalRevRecFinishDate(invoice, contractor);
			default:
				return contractor.getPaymentExpires();
		}
	}

	private Date calculateRenewalRevRecFinishDate(Invoice invoice, ContractorAccount contractor) {
		Date oneYearLater;
		oneYearLater = null;
		if (contractorPaymentExpiresIsNullOrLessThanAYearFromNow(contractor)) {
			oneYearLater = DateBean.addYears(contractor.getPaymentExpires(),1);
		} else {
			oneYearLater = contractor.getPaymentExpires();
		}
		return oneYearLater;
	}

	private Date calculateActivationRevRecFinishDate(Invoice invoice, ContractorAccount contractor) {
		Date oneYearLater = null;
		if (contractorPaymentExpiresIsNullOrLessThanAYearFromNow(contractor)) {
			oneYearLater = DateBean.addYears(invoice.getCreationDate(), 1);
		} else {
			oneYearLater = contractor.getPaymentExpires();
		}
		return oneYearLater;
	}

	private boolean contractorPaymentExpiresIsNullOrLessThanAYearFromNow(ContractorAccount contractor) {
		return contractor.getPaymentExpires() == null || contractor.getPaymentExpires().before(DateBean.subtractDays(DateBean.addYears(new Date(),1),1));
	}

	private String generateExceptionStringForInabilityToCalculateRevRec(ContractorAccount contractor, Invoice invoice, Invoice previousInvoice) {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("Cannot calculate revenue recognition for contractor ");
		stringBuilder.append(contractor.getId());
		stringBuilder.append(" for invoice ");
		stringBuilder.append(invoice.getId());
		stringBuilder.append(" of type ");
		stringBuilder.append(invoice.getInvoiceType());
		if (previousInvoice == null) {
			stringBuilder.append(" because cannot find a non-voided previous activation or renewal invoice");
		} else {
			stringBuilder.append(" because previous non-voided activation or renewal invoice ");
			stringBuilder.append(+previousInvoice.getId());
			stringBuilder.append(" doesn't have an invoice item with an end date");
		}
		return stringBuilder.toString();
	}

	public Date getInvoiceItemEndDateFrom(Invoice invoice) {
		if (invoice == null) {
			return null;
		}
		for (InvoiceItem invoiceItem : invoice.getItems()) {
			if (invoiceItem.getRevenueFinishDate() != null) {
				return invoiceItem.getRevenueFinishDate();
			}
		}
		return null;
	}

	private Date calculateInvoiceItemRevRecStartDateFor(Invoice invoice) {
		switch (invoice.getInvoiceType()) {
			case Activation:
				if (invoice.getDueDate() == null) {
					return invoice.getCreationDate();
				} else {
					return invoice.getDueDate();
				}
			case Renewal:
				if (invoice.getDueDate() == null) {
					return DateBean.addMonths(invoice.getCreationDate(), 1);
				} else {
					return invoice.getDueDate();
				}
			default:
				return invoice.getCreationDate();
		}
	}

    public static boolean hasCreditMemosForFullAmount(Invoice invoice) {
        BigDecimal creditMemoAmount = BigDecimal.ZERO;
        for (CreditMemoAppliedToInvoice creditMemoAppliedToInvoice : invoice.getCreditMemos()) {
            creditMemoAmount = creditMemoAmount.add(creditMemoAppliedToInvoice.getAmount());
            if (creditMemoAmount.doubleValue() >= invoice.getTotalAmount().doubleValue()) {
                return true;
            }
        }

        return false;
    }

    /**
     * The following are states of Billing Status: Membership Canceled
     * Contractor is not active and membership is not set to renew:<br />
     * <br>
     * <b>Current</b> means the contractor doesn't owe anything right now<br>
     * <b>Activation</b> means the contractor is not active and has never been
     * active<br>
     * <b>Reactivation</b> means the contractor was active, but is no longer
     * active anymore<br>
     * <b>Upgrade</b> The number of facilities a contractor is at has increased.<br>
     * <b>Do not renew</b> means the contractor has asked not to renew their
     * account<br>
     * <b>Membership Canceled</b> means the contractor closed their account and
     * doesn't want to renew<br>
     * <b>Renewal Overdue</b> Contractor is active and the Membership Expiration
     * Date is past.<br>
     * <b>Renewal</b> Contractor is active and the Membership Expiration Date is
     * in the next 30 Days<br>
     * <b>Not Calculated</b> New Membership level is null<br>
     * <b>Past Due</b> Inovice is open and not paid by due date
     *
     * @return A String of the current Billing Status
     */
    public BillingStatus billingStatus(ContractorAccount contractor) {
        // If contractor is Free, Deleted, or Demo, give a pass on billing
        if (!contractor.isMustPayB() || contractor.getPayingFacilities() == 0 ||
                contractor.getStatus().isDemo() || contractor.getStatus().isDeleted()) {
            return BillingStatus.Current;
        }

        int daysUntilRenewal = (contractor.getPaymentExpires() == null) ? 0 : DateBean.getDateDifference(contractor.getPaymentExpires());

        if (contractor.pendingRequestedOrActive() && contractor.getAccountLevel().isFull() && contractor.newMember()) {
            return BillingStatus.Activation;
        }

        if (contractor.getStatus().isDeactivated() || daysUntilRenewal < -90) {
            // this contractor is not active or their membership expired more
            // than 90 days ago
            if (!contractor.isRenew()) {
                return BillingStatus.Cancelled;
            } else {
                return BillingStatus.Reactivation;
            }
        }

        // if any non-bid or list membership level differs, amount is an upgrade
        boolean upgrade = false;
        boolean currentListOrBidOnly = false;
        Map<FeeClass, ContractorFee> fees = contractor.getFees();
        for (FeeClass feeClass : fees.keySet()) {
            ContractorFee contractorFee = fees.get(feeClass);
            if (!upgrade && contractorFee.isUpgrade() && !feeClass.equals(FeeClass.BidOnly)
                    && !feeClass.equals(FeeClass.ListOnly)) {
                upgrade = true;
            }

            InvoiceFee currentLevel = contractorFee.getCurrentLevel();
            if ((currentLevel.isBidonly() || currentLevel.isListonly()) && !currentLevel.isFree()) {
                currentListOrBidOnly = true;
            }
        }

        if (upgrade) {
            if (currentListOrBidOnly) {
                return BillingStatus.Renewal;
            } else {
                return BillingStatus.Upgrade;
            }
        }

        if (daysUntilRenewal < 0) {
            return BillingStatus.RenewalOverdue;
        }

        if (daysUntilRenewal < 45) {
            return BillingStatus.Renewal;
        }

        if (contractor.hasPastDueInvoice()) {
            return BillingStatus.PastDue;
        }

        return BillingStatus.Current;
    }

    public void setTaxService(TaxService taxService) {
        this.taxService = taxService;
    }
}
