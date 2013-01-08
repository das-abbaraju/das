package com.picsauditing.actions.contractors;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.picsauditing.PICS.InvoiceService;
import org.springframework.beans.factory.annotation.Autowired;

import com.opensymphony.xwork2.Preparable;
import com.picsauditing.PICS.BillingCalculatorSingle;
import com.picsauditing.dao.InvoiceDAO;
import com.picsauditing.dao.InvoiceFeeDAO;
import com.picsauditing.dao.InvoiceItemDAO;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.FeeClass;
import com.picsauditing.jpa.entities.Invoice;
import com.picsauditing.jpa.entities.InvoiceFee;
import com.picsauditing.jpa.entities.InvoiceItem;
import com.picsauditing.jpa.entities.LowMedHigh;
import com.picsauditing.jpa.entities.NoteCategory;
import com.picsauditing.jpa.entities.TransactionStatus;

/**
 * Class used to edit a Invoice and Invoice Item record with virtually no restrictions
 * 
 * @author Keerthi
 * 
 */
@SuppressWarnings("serial")
public class ConInvoiceMaintain extends ContractorActionSupport implements Preparable {
	@Autowired
	private InvoiceService invoiceService;
	@Autowired
	private InvoiceDAO invoiceDAO;
	@Autowired
	private InvoiceFeeDAO invoiceFeeDAO;
	@Autowired
	private InvoiceItemDAO invoiceItemDAO;
	@Autowired
	private BillingCalculatorSingle billingService;

	public int invoiceId;
	public Invoice invoice;
	public List<InvoiceFee> feeList = null;

	private Map<Integer, Integer> feeMap = new HashMap<Integer, Integer>();

	private int itemID = 0;

	public void prepare() throws Exception {
		id = getParameter("id");
		findContractor();
		invoiceId = getParameter("invoiceId");

		if (invoiceId > 0) {
			invoice = invoiceDAO.find(invoiceId);
		}
	}

	public String execute() throws Exception {
		if ("Save".equals(button)) {
			invoice.setAuditColumns(permissions);

			String message = "Successfully saved data";

			for (InvoiceItem item : invoice.getItems()) {
				if (invoice.getStatus().isVoid() && item.getInvoiceFee().getFeeClass().equals(FeeClass.ImportFee)) {
					boolean removed = billingService.removeImportPQF(contractor);
					if (removed)
						message += " and removed <strong>ImportPQF Audit</strong>";
				}

				int feeID = feeMap.get(item.getId());
				if (item.getInvoiceFee().getId() != feeID)
					item.setInvoiceFee(invoiceFeeDAO.find(feeID));
			}

			invoice.updateAmount();
			invoice.setQbSync(true);
			invoiceService.saveInvoice(invoice);
			addActionMessage(message);
		}

		if ("Remove".equals(button)) {
			if (itemID > 0) {
				for (Iterator<InvoiceItem> items = invoice.getItems().iterator(); items.hasNext();) {
					InvoiceItem item = items.next();
					if (itemID == item.getId()) {
						String message = "Removed line item <strong>" + item.getInvoiceFee().getFee()
								+ "</strong> for " + contractor.getCountry().getCurrency().getSymbol() + item.getAmount();

						if (item.getInvoiceFee().getFeeClass().equals(FeeClass.ImportFee)) {
							boolean removed = billingService.removeImportPQF(contractor);
							if (removed)
								message += " and removed <strong>ImportPQF Audit</strong>";
						}

						items.remove();
						invoiceItemDAO.remove(item);
						addActionMessage(message);
					}
				}

				invoice.updateAmount();
				invoice.setQbSync(true);
				invoiceService.saveInvoice(invoice);
			}
		}

		if ("Delete".equals(button)) {
			// If we are deleting an Invoice, we should perform all the void actions that need to be done.
			billingService.performInvoiceStatusChangeActions(invoice, TransactionStatus.Void);
			addNote(contractor, "Removed Invoice #" + invoiceId + " for " + invoice.getTotalAmount(),
					NoteCategory.Billing, LowMedHigh.Low, false, Account.PicsID, this.getUser());
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
