package com.picsauditing.actions;

import java.util.List;

import org.bouncycastle.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.access.NoRightsException;
import com.picsauditing.access.OpPerms;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.InvoiceDAO;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.Transaction;

@SuppressWarnings("serial")
public class QBSyncEdit extends PicsActionSupport {

	private static final String ACCOUNT = "Account";
	private static final String INVOICE = "Invoice";

	@Autowired
	private ContractorAccountDAO contractorAccountDAO;
	@Autowired
	private InvoiceDAO invoiceDAO;

	private String ids = null;
	private String type = ACCOUNT;
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
			if (type.equals(ACCOUNT)) {
				saveContractor(idList);
			}

			else if (type.equals(INVOICE)) {
				saveTransaction(idList);
			}
		}

		return SUCCESS;
	}

	private void saveContractor(String[] idList) {
		List<ContractorAccount> contractors = contractorAccountDAO.findWhere(ContractorAccount.class, "t.id IN (" + ids
				+ ")");

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
		if (clearListID) {
			contractor.setQbListID(null);
			contractor.setQbListCAID(null);
			contractor.setQbListUKID(null);
			contractor.setQbListEUID(null);
		}
		contractorAccountDAO.save(contractor);
	}

	private void saveTransaction(String[] idList) {
		List<Transaction> transactions = invoiceDAO.findWhere(Transaction.class, "t.id IN (" + ids + ")");

		if (transactions.isEmpty())
			addActionError(getText("InvoiceDetail.error.CantFindInvoice"));
		else if (transactions.size() != idList.length)
			addActionError("We could not find all of the invoice you were looking for");

		if (!hasActionErrors()) {
			for (Transaction transaction : transactions) {
				editTranaction(transaction);
				addActionMessage(getText("InvoiceDetail.message.SavedInvoice") + " #" + transaction.getId());
			}
		}
	}

	private void editTranaction(Transaction transaction) {
		transaction.setQbSync(needSync);
		if (clearListID) {
			transaction.setQbListID(null);
		}
		invoiceDAO.save(transaction);
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
