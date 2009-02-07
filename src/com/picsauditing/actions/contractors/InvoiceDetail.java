package com.picsauditing.actions.contractors;

import java.util.Date;

import com.picsauditing.access.NoRightsException;
import com.picsauditing.access.OpPerms;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.InvoiceDAO;
import com.picsauditing.dao.NoteDAO;
import com.picsauditing.jpa.entities.Invoice;

@SuppressWarnings("serial")
public class InvoiceDetail extends PicsActionSupport {
	private int id;
	private InvoiceDAO invoiceDAO;
	private NoteDAO noteDAO;
	private Invoice invoice;
	
	public InvoiceDetail(InvoiceDAO invoiceDAO) {
		this.invoiceDAO = invoiceDAO;
	}

	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;
		
		invoice = invoiceDAO.find(id);
		
		if (!permissions.hasPermission(OpPerms.AllContractors)
				&& permissions.getAccountId() != invoice.getAccount().getId()) {
			throw new NoRightsException("You can't view this invoice");
		}
		
		if ("ChargeCard".equals(button)) {
			// TODO process the credit card
			String transactionID = "RETURN IT HERE";
			invoice.setPaid(true);
			invoice.setPaidDate(new Date());
			invoice.setTransactionID(transactionID);
			invoice.setAuditColumns(getUser());
			invoiceDAO.save(invoice);
			// also set the notecategory to Billing
			noteDAO.addPicsAdminNote(invoice.getAccount(), getUser(), "Paid the invoice");
		}
		
		return SUCCESS;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Invoice getInvoice() {
		return invoice;
	}

	public void setInvoice(Invoice invoice) {
		this.invoice = invoice;
	}
	
	
}
