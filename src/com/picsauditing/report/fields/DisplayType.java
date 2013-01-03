package com.picsauditing.report.fields;

import java.util.ArrayList;
import java.util.List;

public enum DisplayType {
	String, Integer, Float, Number, Boolean, Date, DateTime, Flag;

	public List<SqlFunction> getFunctions() {
		List<SqlFunction> list = new ArrayList<SqlFunction>();
		for (SqlFunction function : SqlFunction.values()) {
			if (isCanUseFunction(function)) {
				list.add(function);
			}
		}
		return list;
	}

	private boolean isCanUseFunction(SqlFunction function) {
		if (isFunctionValidForAllTypes(function)) {
			return true;
		}

		if (isDateOnlyFunction(function)) {
			return this == Date;
		}

		if (isStringOnlyFunction(function)) {
			return this == String;
		}

		if (function == SqlFunction.Round) {
			return this == Float;
		}

		if (isNumericOnlyFunction(function)) {
			return (this == Integer || this == Float);
		}

		return false;
	}

	private boolean isNumericOnlyFunction(SqlFunction function) {
		if (function == SqlFunction.Average)
			return true;
		if (function == SqlFunction.Sum)
			return true;
		return false;
	}

	private boolean isStringOnlyFunction(SqlFunction function) {
//		if (function == QueryMethod.LowerCase)
//			return true;
//		if (function == QueryMethod.UpperCase)
//			return true;
//		if (function == QueryMethod.Length)
//			return true;
//		if (function == QueryMethod.Left)
//			return true;
		return false;
	}

	private boolean isFunctionValidForAllTypes(SqlFunction function) {
		if (function == SqlFunction.Count)
			return true;
//		if (function == QueryMethod.CountDistinct)
//			return true;
//		if (function == QueryMethod.GroupConcat)
//			return true;
		if (function == SqlFunction.Max)
			return true;
		if (function == SqlFunction.Min)
			return true;
		return false;
	}

	private boolean isDateOnlyFunction(SqlFunction function) {
//		if (function == QueryMethod.Year)
//			return true;
//		if (function == QueryMethod.YearMonth)
//			return true;
//		if (function == QueryMethod.Month)
//			return true;
//		if (function == QueryMethod.WeekDay)
//			return true;
//		if (function == QueryMethod.Date)
//			return true;
//		if (function == QueryMethod.Hour)
//			return true;
		return false;
	}
}
