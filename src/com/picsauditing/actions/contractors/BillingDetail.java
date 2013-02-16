package com.picsauditing.actions.contractors;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.picsauditing.model.account.AccountStatusChanges;
import org.apache.struts2.ServletActionContext;
import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.PICS.BillingCalculatorSingle;
import com.picsauditing.PICS.Grepper;
import com.picsauditing.PICS.InvoiceService;
import com.picsauditing.PICS.data.DataEvent;
import com.picsauditing.PICS.data.DataObservable;
import com.picsauditing.PICS.data.InvoiceDataEvent;
import com.picsauditing.access.OpPerms;
import com.picsauditing.dao.OperatorAccountDAO;
import com.picsauditing.dao.TransactionDAO;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.AccountStatus;
import com.picsauditing.jpa.entities.BillingStatus;
import com.picsauditing.jpa.entities.ContractorOperator;
import com.picsauditing.jpa.entities.Invoice;
import com.picsauditing.jpa.entities.InvoiceItem;
import com.picsauditing.jpa.entities.LowMedHigh;
import com.picsauditing.jpa.entities.NoteCategory;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.jpa.entities.Transaction;
import com.picsauditing.jpa.entities.User;

@SuppressWarnings("serial")
public class BillingDetail extends ContractorActionSupport {
	@Autowired
	private BillingCalculatorSingle billingService;
	@Autowired
	private OperatorAccountDAO opAccountDao;
	@Autowired
	private InvoiceService invoiceService;
	@Autowired
	private TransactionDAO transactionDAO;
	@Autowired
	private DataObservable saleCommissionDataObservable;

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
		billingService.calculateContractorInvoiceFees(contractor);

		invoiceItems = billingService.createInvoiceItems(contractor, getUser());
		invoiceTotal = billingService.calculateInvoiceTotal(invoiceItems);

		if ("Create".equalsIgnoreCase(button)) {
			if (invoiceTotal.compareTo(BigDecimal.ZERO) == 0 && !permissions.hasPermission(OpPerms.Billing)) {
				addActionError("Cannot create an Invoice for zero dollars");
				return SUCCESS;
			}

			Invoice invoice = billingService.createInvoiceWithItems(contractor, invoiceItems,
					new User(permissions.getUserId()));

			invoice = invoiceService.saveInvoice(invoice);

			contractor.getInvoices().add(invoice);
			contractor.syncBalance();
			accountDAO.save(contractor);

			if (invoiceTotal.compareTo(BigDecimal.ZERO) > 0) {
				this.addNote(contractor, "Created invoice for " + contractor.getCountry().getCurrency().getSymbol()
						+ invoiceTotal, NoteCategory.Billing, LowMedHigh.Med, false, Account.PicsID, this.getUser());
			}

			notifyDataChange(new InvoiceDataEvent(invoice, InvoiceDataEvent.InvoiceEventType.NEW));

			ServletActionContext.getResponse().sendRedirect("InvoiceDetail.action?invoice.id=" + invoice.getId());
			return BLANK;
		}

		if ("Activate".equals(button)) {
			contractor.setStatus(AccountStatus.Active);
			this.addNote(contractor, "Activated the account", NoteCategory.Billing, LowMedHigh.High, true,
					Account.PicsID, this.getUser());
		}

		// Automatically deactivating account based on expired membership
		BillingStatus status = contractor.getBillingStatus();
		if (!contractor.getStatus().equals(AccountStatus.Deactivated)
				&& (status.isRenewalOverdue() || status.isReactivation())) {
			if (contractor.getAccountLevel().isBidOnly()) {
				contractor.setReason(AccountStatusChanges.BID_ONLY_ACCOUNT_REASON);
			}
		}

		contractor.syncBalance();

		accountDAO.save(contractor);

		this.subHeading = getText("BillingDetail.title");
		return SUCCESS;
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
							&& t.getOperatorAccount().getDoContractorsPay().equals("No")
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
