package com.picsauditing.util.excel;

import com.picsauditing.jpa.entities.Column;
import com.picsauditing.report.fields.*;

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

	public static ExcelCellType convert(Column column) {
        FieldType type = column.getField().getType();
        SqlFunction sqlFunction = column.getSqlFunction();
        if (!column.hasNoSqlFunction()) {
            switch (sqlFunction) {
                case Average:
                case Round:
                case StdDev:
                case Sum:
                    return Double;
                case Count:
                case CountDistinct:
                case DaysFromNow:
                case Hour:
                case Length:
                case Year:
                    return Integer;
                case Date:
                    return Date;
                case GroupConcat:
                case LowerCase:
                case UpperCase:
                case WeekDay:
                case Month:
                case YearMonth:
                    return String;
            }
        }

        switch (type) {
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

//		switch (column.getDisplayType()) {
//            case Date:
//            case DateTime:
//                return Date;
//            case AccountID:
//            case Integer:
//            case UserID:
//                return Integer;
//            case Float:
//            case Number:
//                return Double;
//            default:
//                return String;
//		}
	}
}
