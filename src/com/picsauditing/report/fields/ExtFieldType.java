package com.picsauditing.report.fields;

import java.util.ArrayList;
import java.util.List;

/**
 * reference http://docs.sencha.com/ext-js/4-0/#!/api/Ext.data.Field
 */
public enum ExtFieldType {
	Auto, String, Int, Float, Boolean, Date;

	public List<QueryMethod> getFunctions() {
		List<QueryMethod> list = new ArrayList<QueryMethod>();
		for (QueryMethod function : QueryMethod.values()) {
			if (isCanUseFunction(function)) {
				list.add(function);
			}
		}
		return list;
	}

	private boolean isCanUseFunction(QueryMethod function) {
		if (this == Auto) {
			// We might want to treat this as a string but I'm not sure.
			// For now, I'm going to say that no functions are allowed.
			return false;
		}

		if (isFunctionValidForAllTypes(function)) {
			return true;
		}

		if (isDateOnlyFunction(function)) {
			return this == Date;
		}

		if (isStringOnlyFunction(function)) {
			return this == String;
		}

		if (function == QueryMethod.Round) {
			return this == Float;
		}

		if (isNumericOnlyFunction(function)) {
			return (this == Int || this == Float);
		}

		return false;
	}

	private boolean isNumericOnlyFunction(QueryMethod function) {
		if (function == QueryMethod.Average)
			return true;
		if (function == QueryMethod.Sum)
			return true;
		return false;
	}

	private boolean isStringOnlyFunction(QueryMethod function) {
		if (function == QueryMethod.LowerCase)
			return true;
		if (function == QueryMethod.UpperCase)
			return true;
		if (function == QueryMethod.Length)
			return true;
		if (function == QueryMethod.Left)
			return true;
		return false;
	}

	private boolean isFunctionValidForAllTypes(QueryMethod function) {
		if (function == QueryMethod.Count)
			return true;
		if (function == QueryMethod.CountDistinct)
			return true;
		if (function == QueryMethod.GroupConcat)
			return true;
		if (function == QueryMethod.Max)
			return true;
		if (function == QueryMethod.Min)
			return true;
		return false;
	}

	private boolean isDateOnlyFunction(QueryMethod function) {
		if (function == QueryMethod.Year)
			return true;
		if (function == QueryMethod.YearMonth)
			return true;
		if (function == QueryMethod.Month)
			return true;
		if (function == QueryMethod.WeekDay)
			return true;
		if (function == QueryMethod.Date)
			return true;
		if (function == QueryMethod.Hour)
			return true;
		return false;
	}
}
