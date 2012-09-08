package com.picsauditing.util.excel;

import com.picsauditing.report.fields.Field;

public enum ExcelCellType {
	String, Integer("0"), Double("#,##0.00"), Money("($#,##0_);($#,##0)"), Date("yyyy-mm-dd"), Enum, Translated;

	private String format = "@";

	private ExcelCellType() {
	}

	private ExcelCellType(String format) {
		this.format = format;
	}

	public String getFormat() {
		return format;
	}

	public boolean isNumeric() {
		if (this == Integer)
			return true;
		if (this == Double)
			return true;
		if (this == Money)
			return true;
		return false;
	}

	public static ExcelCellType convert(Field field) {
		if (field.getType() == null)
			return String;

		switch (field.getType()) {
		case Date:
			return Date;
		case Int:
			return Integer;
		case Float:
			return Double;
		default:
			return String;
		}
	}
}
