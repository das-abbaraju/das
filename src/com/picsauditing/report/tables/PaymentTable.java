package com.picsauditing.report.tables;

import com.picsauditing.report.fields.FilterType;

public class PaymentTable extends AbstractTable {

	public PaymentTable() {
		super("invoice", "payment", "p", "i.tableType = 'P'");
	}

	public PaymentTable(String prefix, String alias, String foreignKey) {
		super("invoice", prefix, alias, alias + ".id = " + foreignKey);
	}

	public PaymentTable(String alias, String foreignKey) {
		super("invoice", alias, alias, alias + ".id = " + foreignKey);
		
	}
	
	public void addFields() {
		addField(prefix + "CreationDate", alias + ".creationDate", FilterType.Date);

		addFields(com.picsauditing.jpa.entities.Payment.class);
	}

	public void addJoins() {
		addLeftJoin(new AccountTable(prefix + "Account", alias + ".accountID"));
	}
}
