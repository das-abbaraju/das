package com.picsauditing.actions.report;

import com.picsauditing.search.SelectFilter;

public class ReportAccountQuick extends ReportAccount {
	protected String accountName;
	public ReportAccountQuick() {
		this.forwardSingleResults = true;
	}
	public String getAccountName() {
		return accountName;
	}
	public void setAccountName(String accountName) {
		this.accountName = accountName;
		if(accountName == null || accountName.length() == 0)
			accountName = "";
		try {
		 int id = Integer.parseInt(accountName);
		 report.addFilter(new SelectFilter("id", "a.id LIKE '%?%'",
			accountName, "", ""));
			 
		} catch(NumberFormatException nfe) {
			
		report.addFilter(new SelectFilter("accountName", "a.name LIKE '%?%'",
				accountName, "", ""));
		}
	}
}
