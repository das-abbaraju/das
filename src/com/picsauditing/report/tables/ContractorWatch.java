package com.picsauditing.report.tables;

import com.picsauditing.report.fields.FilterType;

public class ContractorWatch extends AbstractTable {
	static private String conWatch = "conWatch";

	public ContractorWatch() {
		super("contractor_watch", conWatch, conWatch, conWatch + ".id = a.id");
	}

	public void addFields() {
		addField(prefix + "UserID", alias + ".userID", FilterType.UserID, FieldCategory.AccountInformation);
	}

	public void addJoins() {
	}
}