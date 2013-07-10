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
public class SAPSyncEdit extends PicsActionSupport {

	private static final String ACCOUNT = "Account";
	private static final String INVOICE = "Invoice";
	private static final String PAYMENT = "Payment";

	@Autowired
	private ContractorAccountDAO contractorAccountDAO;
	@Autowired
	private InvoiceDAO invoiceDAO;

	private String ids = null;
	private String type = ACCOUNT;
	private boolean needSync = false;
	private boolean clearLastSyncDate = false;

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
			switch (type) {
			case ACCOUNT:
				saveContractor(idList);
				break;
			case INVOICE:
			case PAYMENT:
				saveTransaction(idList);
				break;
			default:
				return ERROR;
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
		contractor.setSapSync(needSync);
		if (clearLastSyncDate) {
			contractor.setSapLastSync(null);
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
		transaction.setSapSync(needSync);
		if (clearLastSyncDate) {
			transaction.setSapLastSync(null);
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

	public boolean isClearLastSyncDate() {
		return clearLastSyncDate;
	}

	public void setClearLastSyncDate(boolean clearLastSyncDate) {
		this.clearLastSyncDate = clearLastSyncDate;
	}
}