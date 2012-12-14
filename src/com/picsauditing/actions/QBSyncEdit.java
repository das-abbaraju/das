package com.picsauditing.actions;

import java.util.List;

import org.bouncycastle.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.access.NoRightsException;
import com.picsauditing.access.OpPerms;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.InvoiceDAO;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.Invoice;

@SuppressWarnings("serial")
public class QBSyncEdit extends PicsActionSupport {
	@Autowired
	private ContractorAccountDAO contractorAccountDAO;
	@Autowired
	private InvoiceDAO invoiceDAO;

	private String ids = null;
	private String type = "Account";
	private boolean needSync = false;
	private boolean clearListID = false;

	public String execute() throws NoRightsException {
		permissions.tryPermission(OpPerms.Billing);

		if (button != null) {
			if (button.equals("save")) {
				save();
			}
		}
		
		return SUCCESS;
	}
		
	public String save() throws NoRightsException {
		String[] idList = Strings.split(ids, ',');

		if (type != null && ids != null) {
			if (type.equals("Account")) {
				saveContractor(idList);
			}

			else if (type.equals("Invoice")) {
				saveInvoice(idList);
			}
		}
		

		return SUCCESS;
	}

	private void saveContractor(String[] idList) {
		List<ContractorAccount> contractors = contractorAccountDAO.findWhere(ContractorAccount.class, "t.id IN (" + ids + ")");
		
		if (contractors.isEmpty())
			addActionError("We could not find any account you were looking for");
		else if (contractors.size() != idList.length)
			addActionError("We could not find all of the accounts you were looking for");
			
		if (!hasActionErrors()) {
			for (ContractorAccount contractor : contractors) {
				editContractor(contractor);
				addActionMessage(getTextParameterized("ContractorEdit.message.SaveContractor", contractor.getName()));
			}
		}
	}

	private void editContractor(ContractorAccount contractor) {
		contractor.setQbSync(needSync);
		if (clearListID)
		{
			contractor.setQbListID(null);
		}
		contractorAccountDAO.save(contractor);
	}

	private void saveInvoice(String[] idList) {
		List<Invoice> invoices = invoiceDAO.findWhere(Invoice.class, "t.id IN (" + ids + ")");

		if (invoices.isEmpty())
			addActionError(getText("InvoiceDetail.error.CantFindInvoice"));
		else if (invoices.size() != idList.length)
			addActionError("We could not find all of the invoices you were looking for");

		if (!hasActionErrors()) {
			for (Invoice invoice : invoices) {
				editInvoice(invoice);
				addActionMessage(getText("InvoiceDetail.message.SavedInvoice") + " #" + invoice.getId());
			}
		}
	}

	private void editInvoice(Invoice invoice) {
		invoice.setQbSync(needSync);
		if (clearListID)
		{
			invoice.setQbListID(null);
		}
		invoiceDAO.save(invoice);
	}

	public String getIds() {
		return ids;
	}

	public void setIds(String ids) {
		this.ids = ids;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public boolean isNeedSync() {
		return needSync;
	}

	public void setNeedSync(boolean needSync) {
		this.needSync = needSync;
	}

	public boolean isClearListID() {
		return clearListID;
	}

	public void setClearListID(boolean clearListID) {
		this.clearListID = clearListID;
	}
}
