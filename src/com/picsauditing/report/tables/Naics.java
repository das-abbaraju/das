package com.picsauditing.report.tables;

import com.picsauditing.report.fieldtypes.FilterType;

public class Naics extends BaseTable {
	public Naics(String alias, String foreignKey) {
		super("naics", alias, alias + ".code = " + foreignKey);
	}

	protected void addDefaultFields() {
		addField(alias + "Code", alias + ".code", FilterType.Number);
		addField(alias + "TRIR", alias + ".trir", FilterType.Number);
		addField(alias + "LWCR", alias + ".lwcr", FilterType.Number);
	}

	public void addFields() {
	}

	public void addJoins() {
	}

}
