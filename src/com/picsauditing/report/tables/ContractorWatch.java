package com.picsauditing.report.tables;

public class ContractorWatch extends AbstractTable {
	public ContractorWatch() {
		super("contractor_watch");
		// addFields(Invoice.class);
		// addField(prefix + "UserID", alias + ".userID", FilterType.UserID,
		// FieldCategory.AccountInformation);
	}

	protected void addJoins() {
	}
}