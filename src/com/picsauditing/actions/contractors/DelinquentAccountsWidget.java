package com.picsauditing.actions.contractors;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.InvoiceDAO;
import com.picsauditing.jpa.entities.Invoice;

@SuppressWarnings("serial")
public class DelinquentAccountsWidget extends PicsActionSupport {
	@Autowired
	InvoiceDAO invoiceDAO;

	public String execute() throws Exception {
		if (!permissions.isLoggedIn())
			return LOGIN_AJAX;
		return SUCCESS;
	}

	public List<Invoice> getDelinquentContractors() {
		List<Invoice> iList = invoiceDAO.findDelinquentContractors(permissions, 10);
		return iList;
	}
}
