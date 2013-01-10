package com.picsauditing.actions.contractors;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.picsauditing.PICS.InvoiceService;
import org.apache.struts2.ServletActionContext;
import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.PICS.BillingCalculatorSingle;
import com.picsauditing.PICS.DateBean;
import com.picsauditing.PICS.Grepper;
import com.picsauditing.PICS.data.DataEvent;
import com.picsauditing.PICS.data.DataObservable;
import com.picsauditing.PICS.data.InvoiceDataEvent;
import com.picsauditing.access.Anonymous;
import com.picsauditing.access.OpPerms;
import com.picsauditing.access.Permissions;
import com.picsauditing.access.UserAccess;
import com.picsauditing.dao.NoteDAO;
import com.picsauditing.dao.OperatorAccountDAO;
import com.picsauditing.dao.TransactionDAO;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.AccountStatus;
import com.picsauditing.jpa.entities.BillingStatus;
import com.picsauditing.jpa.entities.ContractorOperator;
import com.picsauditing.jpa.entities.FeeClass;
import com.picsauditing.jpa.entities.Invoice;
import com.picsauditing.jpa.entities.InvoiceItem;
import com.picsauditing.jpa.entities.LowMedHigh;
import com.picsauditing.jpa.entities.Note;
import com.picsauditing.jpa.entities.NoteCategory;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.jpa.entities.Transaction;
import com.picsauditing.jpa.entities.TransactionStatus;
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
	private NoteDAO noteDAO;
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
		if (!forceLogin())
			return LOGIN;

		this.findContractor();
		billingService.calculateContractorInvoiceFees(contractor);

		// TODO: are we using this outside of invoice creation? 
		invoiceItems = billingService.createInvoiceItems(contractor, getUser());
		// TODO: are we using this outside of invoice creation? 
		invoiceTotal = BigDecimal.ZERO.setScale(2);
		for (InvoiceItem item : invoiceItems) {
			invoiceTotal = invoiceTotal.add(item.getAmount());
		}

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
			contractor.setStatus(AccountStatus.Deactivated);
			contractor.setRenew(false);
			if (contractor.getAccountLevel().isBidOnly())
				contractor.setReason("Bid Only Account");
			Note note = new Note(contractor, new User(User.SYSTEM),
					"Automatically inactivating account based on expired membership");
			note.setNoteCategory(NoteCategory.Billing);
			note.setCanContractorView(true);
			note.setViewableById(Account.PicsID);
			noteDAO.save(note);
		}

		contractor.syncBalance();

		accountDAO.save(contractor);

		this.subHeading = getText("BillingDetail.title");
		return SUCCESS;
	}

	public OperatorAccount getRequestedBy() {
		if (contractor.getRequestedBy() != null)
			requestedBy = opAccountDao.find(contractor.getRequestedBy().getId());

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
		if (transactionList == null)
			return new ArrayList<Transaction>();
		return transactionList;
	}

	public List<ContractorOperator> getNonCorporatePayingOperators() throws Exception {
		if (contractor == null)
			findContractor();

		if (payingOperators == null) {
			payingOperators = new Grepper<ContractorOperator>() {

				@Override
				public boolean check(ContractorOperator t) {
					return !t.getOperatorAccount().isCorporate()
							&& !t.getOperatorAccount().getDoContractorsPay().equals("No")
							&& t.getOperatorAccount().getStatus().isActiveDemo();
				}
			}.grep(contractor.getOperators());
		}

		return payingOperators;
	}

	public List<ContractorOperator> getNonCorporateFreeOperators() throws Exception {
		if (contractor == null)
			findContractor();

		if (freeOperators == null) {
			freeOperators = new Grepper<ContractorOperator>() {

				@Override
				public boolean check(ContractorOperator t) {
					return !t.getOperatorAccount().isCorporate()
							&& t.getOperatorAccount().getDoContractorsPay().equals("No")
							&& t.getOperatorAccount().getStatus().isActiveDemo();
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
