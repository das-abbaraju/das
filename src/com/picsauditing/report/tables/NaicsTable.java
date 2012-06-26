package com.picsauditing.report.tables;

import com.picsauditing.report.fields.FilterType;

public class NaicsTable extends AbstractTable {

	public NaicsTable() {
		super("naics", "naics", "n", "");
	}

	public NaicsTable(String prefix, String alias, String foreignKey) {
		super("naics", prefix, alias, alias + ".code = " + foreignKey);
	}

	public NaicsTable(String alias, String foreignKey) {
		super("naics", alias, alias, alias + ".code = " + foreignKey);
	}

	public void addFields() {
		addField(prefix + "Code", alias + ".code", FilterType.String).setCategory(FieldCategory.NAICS);
		addField(prefix + "TRIR", alias + ".trir", FilterType.Float).setCategory(FieldCategory.NAICS);
		addField(prefix + "LWCR", alias + ".lwcr", FilterType.Float).setCategory(FieldCategory.NAICS);
	}

	public void addJoins() {
	}
}