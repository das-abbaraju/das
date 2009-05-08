package com.picsauditing.actions.contractors;

import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;

import com.opensymphony.xwork2.Preparable;
import com.picsauditing.access.OpPerms;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.dao.InvoiceDAO;
import com.picsauditing.dao.InvoiceFeeDAO;
import com.picsauditing.jpa.entities.Invoice;
import com.picsauditing.jpa.entities.InvoiceFee;
import com.picsauditing.jpa.entities.InvoiceItem;

/**
 * Class used to edit a Invoice and Invoice Item record with virtually no restrictions
 * @author Keerthi
 * 
 */
public class ConInvoiceMaintain extends ContractorActionSupport implements Preparable {
	public int invoiceId;
	public Invoice invoice;
	public List<InvoiceFee> feeList = null;
	public List<InvoiceItem> invList = new ArrayList<InvoiceItem>();
	
	private InvoiceDAO invoiceDAO;
	private InvoiceFeeDAO invoiceFeeDAO;
	
	public ConInvoiceMaintain(ContractorAccountDAO accountDao,
			ContractorAuditDAO auditDao, InvoiceDAO invoiceDAO, InvoiceFeeDAO invoiceFeeDAO) {
		super(accountDao, auditDao);
		this.invoiceDAO = invoiceDAO;
		this.invoiceFeeDAO = invoiceFeeDAO;
	}

	public void prepare() throws Exception {
		if (!forceLogin())
			return;
		id = getParameter("id");
		findContractor();
		invoiceId = getParameter("invoiceId");
		
		if (invoiceId > 0) {
			invoice = invoiceDAO.find(invoiceId);
			invList.addAll(invoice.getItems());
			//invoiceDAO.clear();
		}
	}
	
	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;

		permissions.tryPermission(OpPerms.InvoiceEdit);

		if ("Save".equals(button)) {
			if (invoice.getId() > 0) {
				addActionError("Failed to do something here");
				//invoiceDAO.clear();
				throw new InputMismatchException();
			}
			invoice.setAuditColumns(getUser());
			for(InvoiceItem invoiceItem : invList) {
				invoiceItem.setAuditColumns(getUser());
			}
			invoice.getItems().addAll(invList);
			invoice.setQbSync(true);
			//invoiceDAO.save(invoice);
			addActionMessage("Successfully saved data");
		}
		
		if ("Delete".equals(button)) {
			invoiceDAO.remove(invoiceId);
			return "BillingDetail";
		}

		return SUCCESS;
	}

	public int getInvoiceId() {
		return invoiceId;
	}

	public void setInvoiceId(int invoiceId) {
		this.invoiceId = invoiceId;
	}
	
	public List<InvoiceFee> getFeeList() {
		if (feeList == null)
			feeList = invoiceFeeDAO.findAll();

		return feeList;
	}

	public List<InvoiceItem> getInvList() {
		return invList;
	}

	public void setInvList(List<InvoiceItem> invList) {
		this.invList = invList;
	}
}
