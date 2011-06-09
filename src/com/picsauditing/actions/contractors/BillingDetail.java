package com.picsauditing.actions.contractors;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.struts2.ServletActionContext;

import com.picsauditing.PICS.BillingCalculatorSingle;
import com.picsauditing.PICS.DateBean;
import com.picsauditing.access.OpPerms;
import com.picsauditing.dao.AccountDAO;
import com.picsauditing.dao.AppPropertyDAO;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.dao.InvoiceDAO;
import com.picsauditing.dao.InvoiceFeeDAO;
import com.picsauditing.dao.NoteDAO;
import com.picsauditing.dao.TransactionDAO;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.AccountStatus;
import com.picsauditing.jpa.entities.FeeClass;
import com.picsauditing.jpa.entities.Invoice;
import com.picsauditing.jpa.entities.InvoiceFee;
import com.picsauditing.jpa.entities.InvoiceItem;
import com.picsauditing.jpa.entities.LowMedHigh;
import com.picsauditing.jpa.entities.Note;
import com.picsauditing.jpa.entities.NoteCategory;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.jpa.entities.Transaction;
import com.picsauditing.jpa.entities.TransactionStatus;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.util.SpringUtils;

@SuppressWarnings("serial")
public class BillingDetail extends ContractorActionSupport {
	private InvoiceFee activationFee = null;
	private InvoiceDAO invoiceDAO = new InvoiceDAO();
	private TransactionDAO transactionDAO = null;
	private InvoiceFeeDAO invoiceFeeDAO;
	private NoteDAO noteDAO;
	private BigDecimal invoiceTotal;

	private List<InvoiceItem> invoiceItems;

	private OperatorAccount requestedBy = null;

	AppPropertyDAO appPropDao;

	public BillingDetail(ContractorAccountDAO accountDao, ContractorAuditDAO auditDao, InvoiceDAO invoiceDAO,
			InvoiceFeeDAO invoiceFeeDAO, AppPropertyDAO appPropDao, TransactionDAO transactionDAO, NoteDAO noteDAO) {
		this.invoiceDAO = invoiceDAO;
		this.invoiceFeeDAO = invoiceFeeDAO;
		this.appPropDao = appPropDao;
		this.transactionDAO = transactionDAO;
		this.noteDAO = noteDAO;
		this.noteCategory = NoteCategory.Billing;
	}

	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;

		this.findContractor();
		BillingCalculatorSingle.calculateAnnualFees(contractor);

		invoiceItems = BillingCalculatorSingle.createInvoiceItems(contractor, invoiceFeeDAO);

		invoiceTotal = BigDecimal.ZERO.setScale(2);
		for (InvoiceItem item : invoiceItems)
			invoiceTotal = invoiceTotal.add(item.getAmount());

		if ("Create".equalsIgnoreCase(button)) {
			if (invoiceTotal.compareTo(BigDecimal.ZERO) == 0 && !permissions.hasPermission(OpPerms.Billing)) {
				addActionError("Cannot create an Invoice for zero dollars");
				return SUCCESS;
			}

			Invoice invoice = new Invoice();
			invoice.setAccount(contractor);
			invoice.setCurrency(contractor.getCurrency());
			invoice.setStatus(TransactionStatus.Unpaid);
			invoice.setItems(invoiceItems);
			invoice.setTotalAmount(invoiceTotal);
			invoice.setAuditColumns(permissions);

			if (invoiceTotal.compareTo(BigDecimal.ZERO) > 0)
				invoice.setQbSync(true);

			String notes = "";

			// Calculate the due date for the invoice
			if (contractor.getBillingStatus().equals("Activation")) {
				invoice.setDueDate(new Date());
				InvoiceFee activation = invoiceFeeDAO.findByNumberOfOperatorsAndClass(FeeClass.Activation, 1);
				if (contractor.hasReducedActivation(activation)) {
					OperatorAccount reducedOperator = contractor.getReducedActivationFeeOperator(activation);
					notes += "(" + reducedOperator.getName() + " Promotion) Activation reduced from "
							+ contractor.getCurrencyCode().getIcon() + activation.getAmount() + " to "
							+ contractor.getCurrencyCode().getIcon() + reducedOperator.getActivationFee() + ". ";
				}
			} else if (contractor.getBillingStatus().equals("Reactivation")) {
				invoice.setDueDate(new Date());
			} else if (contractor.getBillingStatus().equals("Upgrade")) {
				invoice.setDueDate(DateBean.addDays(new Date(), 7));
			} else if (contractor.getBillingStatus().startsWith("Renew")) {
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
				notes += BillingCalculatorSingle.getOperatorsString(contractor);
			}
			invoice.setNotes(notes);

			contractor.getInvoices().add(invoice);

			for (InvoiceItem item : invoiceItems) {
				item.setInvoice(invoice);
				item.setAuditColumns(permissions);
			}
			invoice = invoiceDAO.save(invoice);

			contractor.getInvoices().add(invoice);
			contractor.syncBalance();
			accountDao.save(contractor);

			if (invoiceTotal.compareTo(BigDecimal.ZERO) > 0) {
				this.addNote(contractor,
						"Created invoice for " + contractor.getCurrencyCode().getIcon() + invoiceTotal,
						NoteCategory.Billing, LowMedHigh.Med, false, Account.PicsID, this.getUser());
			}
			ServletActionContext.getResponse().sendRedirect("InvoiceDetail.action?invoice.id=" + invoice.getId());
			return BLANK;
		}

		if ("Activate".equals(button)) {
			contractor.setStatus(AccountStatus.Active);
			this.addNote(contractor, "Activated the account", NoteCategory.Billing, LowMedHigh.High, true,
					Account.PicsID, this.getUser());
		}

		// Automatically deactivating account based on expired membership
		String status = contractor.getBillingStatus();
		if (!contractor.getStatus().equals(AccountStatus.Deactivated)
				&& ("Renewal Overdue".equals(status) || "Reactivation".equals(status))) {
			contractor.setStatus(AccountStatus.Deactivated);
			if ("Renewal Overdue".equals(status))
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

		accountDao.save(contractor);

		this.subHeading = "Billing Detail";
		return SUCCESS;
	}

	public OperatorAccount getRequestedBy() {
		AccountDAO dao = (AccountDAO) SpringUtils.getBean("AccountDAO");

		if (contractor.getRequestedBy() != null)
			requestedBy = (OperatorAccount) dao.find(contractor.getRequestedBy().getId());

		return requestedBy;
	}

	public InvoiceFee getActivationFee() {
		return activationFee;
	}

	public void setActivationFee(InvoiceFee activationFee) {
		this.activationFee = activationFee;
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
}
