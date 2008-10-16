package com.picsauditing.actions.report;

import com.picsauditing.search.SelectFilter;
import com.picsauditing.search.SelectFilterInteger;

public class ReportAccountQuick extends ReportAccount {
	protected String accountName;

	public ReportAccountQuick() {
		this.forwardSingleResults = true;
	}

	public String getAccountName() {
		return accountName;
	}

	public void setAccountName(String accountName) {
		accountName = accountName.trim();
		if (accountName == null || accountName.length() == 0)
			accountName = "";
		this.accountName = accountName;
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
