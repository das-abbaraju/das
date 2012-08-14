package com.picsauditing.report.tables;

public class NaicsTable extends AbstractTable {

	public NaicsTable(String prefix, String alias, String foreignKey) {
		super("naics", prefix, alias, alias + ".code = " + foreignKey);
	}

	public NaicsTable(String alias, String foreignKey) {
		super("naics", alias, alias, alias + ".code = " + foreignKey);
	}

	public void addFields() {
		addFields(com.picsauditing.jpa.entities.Naics.class);
	}

	public void addJoins() {
	}
}