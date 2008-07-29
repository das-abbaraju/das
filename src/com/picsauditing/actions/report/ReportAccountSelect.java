package com.picsauditing.actions.report;

import com.picsauditing.search.SelectFilter;
import com.picsauditing.search.SelectFilterInteger;

public class ReportAccountSelect extends ReportAccount {
	
	public String getAccountName() {
		return accountName;
	}

	public void setAccountName(String accountName) {
		this.accountName = accountName;
		if (accountName == null || accountName.length() == 0)
			accountName = "";
		try {
			int id = Integer.parseInt(accountName);
			report.addFilter(new SelectFilterInteger("id", "a.id = ?",
					id));
		} catch (NumberFormatException nfe) {
			report.addFilter(new SelectFilter("accountName",
					"a.name LIKE '%?%'", accountName.trim(), "", ""));
		}
	}
}
