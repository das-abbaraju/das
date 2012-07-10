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

	public void addFields() {
		addField(prefix + "Code", alias + ".code", FilterType.String).setCategory(FieldCategory.NAICS);
		addField(prefix + "TRIR", alias + ".trir", FilterType.Float).setCategory(FieldCategory.NAICS);
		addField(prefix + "LWCR", alias + ".lwcr", FilterType.Float).setCategory(FieldCategory.NAICS);
	}

	public void addJoins() {
	}
}