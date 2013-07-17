package com.picsauditing.util.excel;

import com.picsauditing.report.fields.Field;

public enum ExcelCellType {
	String, Integer("0"), Double("#,##0.00"), Money("($#,##0_);($#,##0)"), Date("m/d/yyyy"), Enum, Translated;

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
		switch (field.getType()) {
            case Date:
            case DateTime:
                return Date;
            case AccountID:
            case Integer:
            case UserID:
                return Integer;
            case Float:
            case Number:
                return Double;
            default:
                return String;
		}
	}
}
