package com.picsauditing.actions.contractors;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.picsauditing.PICS.BillingService;
import com.picsauditing.PICS.FeeService;
import com.picsauditing.jpa.entities.*;
import com.picsauditing.model.account.AccountStatusChanges;
import com.picsauditing.model.billing.BillingNoteModel;

import org.apache.commons.lang.time.DateUtils;
import org.apache.struts2.ServletActionContext;
import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.PICS.Grepper;
import com.picsauditing.PICS.data.DataEvent;
import com.picsauditing.PICS.data.DataObservable;
import com.picsauditing.PICS.data.InvoiceDataEvent;
import com.picsauditing.access.OpPerms;
import com.picsauditing.dao.OperatorAccountDAO;
import com.picsauditing.dao.TransactionDAO;

@SuppressWarnings("serial")
public class BillingDetail extends ContractorActionSupport {

	private static final String ACTIVATE_BUTTON = "Activate";
	private static final String CREATE_BUTTON = "Create";

	@Autowired
	private BillingService billingService;
    @Autowired
    private FeeService feeService;
	@Autowired
	private OperatorAccountDAO opAccountDao;
	@Autowired
	private TransactionDAO transactionDAO;
	@Autowired
	private DataObservable saleCommissionDataObservable;
	@Autowired
	private BillingNoteModel billingNoteModel;

	private BigDecimal invoiceTotal;
	private List<InvoiceItem> invoiceItems;
	private OperatorAccount requestedBy = null;
	private List<ContractorOperator> freeOperators;
	private List<ContractorOperator> payingOperators;

	public BillingDetail() {
		this.noteCategory = NoteCategory.Billing;
	}

	public String execute() throws Exception {
		this.findContractor();
        feeService.calculateContractorInvoiceFees(contractor);

		invoiceItems = billingService.createInvoiceItems(contractor, billingNoteModel.findUserForPaymentNote(permissions));
		invoiceTotal = billingService.calculateInvoiceTotal(invoiceItems);

		if (CREATE_BUTTON.equalsIgnoreCase(button)) {
			if (invoiceTotal.compareTo(BigDecimal.ZERO) == 0 && !permissions.hasPermission(OpPerms.Billing)) {
				addActionError("Cannot create an Invoice for zero dollars");
				return SUCCESS;
			}

			Invoice invoice = billingService.createInvoiceWithItems(contractor, invoiceItems, new User(permissions.getUserId()));
			billingService.addRevRecInfoIfAppropriateToItems(invoice);
			invoice = billingService.saveInvoice(invoice);


            if (invoice.getInvoiceType() == InvoiceType.Upgrade) {
                contractor.setLastUpgradeDate(null);
            }

			contractor.getInvoices().add(invoice);
			billingService.syncBalance(contractor);
			accountDAO.save(contractor);

			if (invoiceTotal.compareTo(BigDecimal.ZERO) > 0) {
				this.addNote(contractor, "Created invoice for " + contractor.getCountry().getCurrency().getSymbol()
						+ invoiceTotal, NoteCategory.Billing, LowMedHigh.Med, false, Account.PicsID,
						billingNoteModel.findUserForPaymentNote(permissions));
			}

			notifyDataChange(new InvoiceDataEvent(invoice, InvoiceDataEvent.InvoiceEventType.NEW));

			ServletActionContext.getResponse().sendRedirect("InvoiceDetail.action?invoice.id=" + invoice.getId());
			return BLANK;
		}

		if (ACTIVATE_BUTTON.equals(button)) {
			contractor.setStatus(AccountStatus.Active);
			this.addNote(contractor, "Activated the account", NoteCategory.Billing, LowMedHigh.High, true,
					Account.PicsID, billingNoteModel.findUserForPaymentNote(permissions));
		}

		// Automatically deactivating account based on expired membership
		BillingStatus status = billingService.billingStatus(contractor);
		if (!contractor.getStatus().equals(AccountStatus.Deactivated)
				&& (status.isRenewalOverdue() || status.isReactivation())) {
			if (contractor.getAccountLevel().isBidOnly()) {
				contractor.setReason(AccountStatusChanges.BID_ONLY_ACCOUNT_REASON);
			}
		}

        billingService.syncBalance(contractor);

		accountDAO.save(contractor);

		this.subHeading = getText("BillingDetail.title");
		return SUCCESS;
	}

    public BillingStatus getBillingStatus() {
        return billingService.billingStatus(contractor);
    }

	public OperatorAccount getRequestedBy() {
		if (contractor.getRequestedBy() != null) {
			requestedBy = opAccountDao.find(contractor.getRequestedBy().getId());
		}

		return requestedBy;
	}

	public List<InvoiceItem> getInvoiceItems() {
		return invoiceItems;
	}

	public void setInvoiceItems(List<InvoiceItem> invoiceItems) {
		this.invoiceItems = invoiceItems;
	}

	public BigDecimal getInvoiceTotal() {
		return invoiceTotal;
	}

	public List<Transaction> getTransactions() {
		List<Transaction> transactionList = transactionDAO.findWhere("t.account.id = " + contractor.getId());
		if (transactionList == null) {
			return new ArrayList<Transaction>();
		}
		return transactionList;
	}

	public List<ContractorOperator> getNonCorporatePayingOperators() throws Exception {
		if (contractor == null) {
			findContractor();
		}

		if (payingOperators == null) {
			payingOperators = new Grepper<ContractorOperator>() {

				@Override
				public boolean check(ContractorOperator t) {
					return !t.getOperatorAccount().isCorporate()
							&& !t.getOperatorAccount().getDoContractorsPay().equals("No")
							&& t.getOperatorAccount().getStatus().isActiveOrDemo();
				}
			}.grep(contractor.getOperators());
		}

		return payingOperators;
	}

	public List<ContractorOperator> getNonCorporateFreeOperators() throws Exception {
		if (contractor == null) {
			findContractor();
		}

		if (freeOperators == null) {
			freeOperators = new Grepper<ContractorOperator>() {

				@Override
				public boolean check(ContractorOperator t) {
					return !t.getOperatorAccount().isCorporate()
							&& t.getOperatorAccount().getDoContractorsPay().equals(YesNo.No.toString())
							&& t.getOperatorAccount().getStatus().isActiveOrDemo();
				}
			}.grep(contractor.getOperators());
		}

		return freeOperators;
	}

	private <T> void notifyDataChange(DataEvent<T> dataEvent) {
		saleCommissionDataObservable.setChanged();
		saleCommissionDataObservable.notifyObservers(dataEvent);
	}
}
