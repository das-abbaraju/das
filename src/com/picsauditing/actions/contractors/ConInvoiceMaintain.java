package com.picsauditing.actions.contractors;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.opensymphony.xwork2.Preparable;
import com.picsauditing.access.OpPerms;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.dao.InvoiceDAO;
import com.picsauditing.dao.InvoiceFeeDAO;
import com.picsauditing.dao.InvoiceItemDAO;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.Invoice;
import com.picsauditing.jpa.entities.InvoiceFee;
import com.picsauditing.jpa.entities.InvoiceItem;
import com.picsauditing.jpa.entities.LowMedHigh;
import com.picsauditing.jpa.entities.NoteCategory;
import com.picsauditing.jpa.entities.User;

/**
 * Class used to edit a Invoice and Invoice Item record with virtually no
 * restrictions
 * 
 * @author Keerthi
 * 
 */
@SuppressWarnings("serial")
public class ConInvoiceMaintain extends ContractorActionSupport implements Preparable {
	public int invoiceId;
	public Invoice invoice;
	public List<InvoiceFee> feeList = null;

	private Map<Integer, Integer> feeMap = new HashMap<Integer, Integer>();

	private int itemID = 0;

	private InvoiceDAO invoiceDAO;
	private InvoiceFeeDAO invoiceFeeDAO;
	private InvoiceItemDAO invoiceItemDAO;

	public ConInvoiceMaintain(ContractorAccountDAO accountDao, ContractorAuditDAO auditDao, InvoiceDAO invoiceDAO,
			InvoiceFeeDAO invoiceFeeDAO, InvoiceItemDAO invoiceItemDAO) {
		super(accountDao, auditDao);
		this.invoiceDAO = invoiceDAO;
		this.invoiceFeeDAO = invoiceFeeDAO;
		this.invoiceItemDAO = invoiceItemDAO;
	}

	public void prepare() throws Exception {
		if (!forceLogin())
			return;
		id = getParameter("id");
		findContractor();
		invoiceId = getParameter("invoiceId");

		if (invoiceId > 0) {
			invoice = invoiceDAO.find(invoiceId);
		}
	}

	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;

		permissions.tryPermission(OpPerms.InvoiceEdit);

		if ("Save".equals(button)) {
			invoice.setAuditColumns(new User(User.SYSTEM));

			for (InvoiceItem item : invoice.getItems()) {
				int feeID = feeMap.get(item.getId());
				if (item.getInvoiceFee().getId() != feeID)
					item.setInvoiceFee(invoiceFeeDAO.find(feeID));
			}

			invoice.updateAmount();
			invoice.setQbSync(true);
			invoiceDAO.save(invoice);
			addActionMessage("Successfully saved data");
		}

		if ("Remove".equals(button)) {
			if (itemID > 0) {
				for (Iterator<InvoiceItem> items = invoice.getItems().iterator(); items.hasNext();) {
					InvoiceItem item = items.next();
					if (itemID == item.getId()) {
						items.remove();
						invoiceItemDAO.remove(item);
						addActionMessage("Removed line item <strong>" + item.getInvoiceFee().getFee()
								+ "</strong> for $" + item.getAmount());
					}
				}

				invoice.updateAmount();
				invoice.setQbSync(true);
				invoiceDAO.save(invoice);
			}
		}

		if ("Delete".equals(button)) {
			addNote(contractor, "Removed Invoice #"+ invoiceId +" for "+ invoice.getTotalAmount(), NoteCategory.Billing, LowMedHigh.Low, false, Account.PicsID, this.getUser());
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

	public Map<Integer, Integer> getFeeMap() {
		return feeMap;
	}

	public void setFeeMap(Map<Integer, Integer> feeMap) {
		this.feeMap = feeMap;
	}

	public int getItemID() {
		return itemID;
	}

	public void setItemID(int itemID) {
		this.itemID = itemID;
	}
}
