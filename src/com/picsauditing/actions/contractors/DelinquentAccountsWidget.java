package com.picsauditing.actions.contractors;

import java.util.List;

import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.InvoiceDAO;
import com.picsauditing.jpa.entities.Invoice;

@SuppressWarnings("serial")
public class DelinquentAccountsWidget extends PicsActionSupport {
	InvoiceDAO invoiceDAO;

	public DelinquentAccountsWidget(InvoiceDAO invoiceDAO) {
		this.invoiceDAO = invoiceDAO;
	}

	public String execute() throws Exception {
		loadPermissions();
		if (!permissions.isLoggedIn())
			return LOGIN_AJAX;
		return SUCCESS;
	}

	public List<Invoice> getDelinquentContractors() {
		List<Invoice> iList = invoiceDAO.findDelinquentContractors(permissions, 10);
		return iList;
	}
}
