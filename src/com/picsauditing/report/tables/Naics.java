package com.picsauditing.report.tables;

import com.picsauditing.report.fields.FilterType;

public class Naics extends BaseTable {
	
	public Naics() {
		super("naics", "naics", "n", "");
	}

	public Naics(String prefix, String alias, String foreignKey) {
		super("naics", prefix, alias, alias + ".code = " + foreignKey);
	}

	public Naics(String alias, String foreignKey) {
		super("naics", alias, alias, alias + ".code = " + foreignKey);
	}

	protected void addDefaultFields() {
		// add NAICS Category
		addField(prefix + "Code", alias + ".code", FilterType.String);
		addField(prefix + "TRIR", alias + ".trir", FilterType.Integer);
		addField(prefix + "LWCR", alias + ".lwcr", FilterType.Integer);
	}

	public void addFields() {
	}

	public void addJoins() {
	}
}