package com.picsauditing.actions.contractors;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.apache.struts2.ServletActionContext;

import com.picsauditing.PICS.BillingCalculatorSingle;
import com.picsauditing.PICS.DateBean;
import com.picsauditing.dao.AccountDAO;
import com.picsauditing.dao.AppPropertyDAO;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.dao.InvoiceDAO;
import com.picsauditing.dao.InvoiceFeeDAO;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.AppProperty;
import com.picsauditing.jpa.entities.Invoice;
import com.picsauditing.jpa.entities.InvoiceFee;
import com.picsauditing.jpa.entities.InvoiceItem;
import com.picsauditing.jpa.entities.LowMedHigh;
import com.picsauditing.jpa.entities.NoteCategory;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.util.SpringUtils;

@SuppressWarnings("serial")
public class BillingDetail extends ContractorActionSupport {
	private InvoiceFee activationFee = null;
	private InvoiceDAO invoiceDAO = new InvoiceDAO();
	private InvoiceFeeDAO invoiceFeeDAO;
	private BigDecimal invoiceTotal;

	private List<InvoiceItem> invoiceItems;

	private OperatorAccount requestedBy = null;

	AppPropertyDAO appPropDao;

	public BillingDetail(ContractorAccountDAO accountDao, ContractorAuditDAO auditDao, InvoiceDAO invoiceDAO,
			InvoiceFeeDAO invoiceFeeDAO, AppPropertyDAO appPropDao) {
		super(accountDao, auditDao);
		this.invoiceDAO = invoiceDAO;
		this.invoiceFeeDAO = invoiceFeeDAO;
		this.appPropDao = appPropDao;
		this.noteCategory = NoteCategory.Billing;
	}

	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;

		this.findContractor();
		InvoiceFee newFee = BillingCalculatorSingle.calculateAnnualFee(contractor);
		newFee = invoiceFeeDAO.find(newFee.getId());
		contractor.setNewMembershipLevel(newFee);

		invoiceItems = BillingCalculatorSingle.createInvoiceItems(contractor, invoiceFeeDAO);

		invoiceTotal = new BigDecimal(0);
		for (InvoiceItem item : invoiceItems)
			invoiceTotal = invoiceTotal.add(item.getAmount());

		if ("Create".equalsIgnoreCase(button)) {

			Invoice invoice = new Invoice();
			invoice.setAccount(contractor);
			invoice.setPaid(false);
			invoice.setItems(invoiceItems);
			invoice.setTotalAmount(invoiceTotal);
			invoice.setAuditColumns(getUser());
			invoice.setQbSync(true);

			// Calculate the due date for the invoice
			if (contractor.getBillingStatus().equals("Activation")
					|| contractor.getBillingStatus().equals("Reactivation")) {
				invoice.setDueDate(new Date());
			} else if (contractor.getBillingStatus().equals("Upgrade")) {
				invoice.setDueDate(DateBean.addDays(contractor.getLastUpgradeDate(), 30));
			} else if (contractor.getBillingStatus().startsWith("Renew")) {
				invoice.setDueDate(contractor.getPaymentExpires());
			}
			if (invoice.getDueDate() == null)
				// For all other statuses like (Current)
				invoice.setDueDate(DateBean.addDays(new Date(), 30));
			
			// Make sure the invoice isn't due within 7 days for active accounts
			if (contractor.isActiveB() && DateBean.getDateDifference(invoice.getDueDate()) < 7)
				invoice.setDueDate(DateBean.addDays(new Date(), 7));
			// End of Due date

			String notes = "Thank you for your business.";
			AppProperty prop = appPropDao.find("invoice_comment");
			if (prop != null) {
				notes = prop.getValue();
			}
			invoice.setNotes(notes);

			contractor.getInvoices().add(invoice);

			for (InvoiceItem item : invoiceItems) {
				item.setInvoice(invoice);
				item.setAuditColumns(getUser());
			}
			invoice = invoiceDAO.save(invoice);

			contractor.getInvoices().add(invoice);
			contractor.syncBalance();
			accountDao.save(contractor);

			this.addNote(contractor, "Created invoice for $" + invoiceTotal, NoteCategory.Billing, LowMedHigh.Med,
					false, Account.PicsID);

			ServletActionContext.getResponse().sendRedirect("InvoiceDetail.action?invoice.id=" + invoice.getId());
			return BLANK;
		}
		
		if ("Activate".equals(button)) {
			contractor.setActive('Y');
			this.addNote(contractor, "Activated free account", NoteCategory.Billing, LowMedHigh.High, true, Account.PicsID);
		}

		contractor.syncBalance();

		this.subHeading = "Billing Detail";
		return SUCCESS;
	}

	public OperatorAccount getRequestedBy() {
		AccountDAO dao = (AccountDAO) SpringUtils.getBean("AccountDAO");

		if (contractor.getRequestedById() != 0)
			requestedBy = (OperatorAccount) dao.find(contractor.getRequestedById());

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

}
