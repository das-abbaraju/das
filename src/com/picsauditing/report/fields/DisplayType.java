package com.picsauditing.report.fields;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

// Maybe rename this to ColumnType
public enum DisplayType {

	// Strings are left-align, Numbers are right-align

	/*String, Integer, Float, Number, Boolean, Date, DateTime,*/ Flag, CheckMark, LeftAlign, RightAlign;

	private static final Set<SqlFunction> DATE_ONLY_FUNCTIONS = new HashSet<SqlFunction>(Arrays.asList(
			SqlFunction.Date, SqlFunction.Month, SqlFunction.Hour, SqlFunction.Month, SqlFunction.WeekDay,
			SqlFunction.Year, SqlFunction.YearMonth));

	private static final Set<SqlFunction> STRING_ONLY_FUNCTIONS = new HashSet<SqlFunction>(Arrays.asList(
			SqlFunction.Length, SqlFunction.Left, SqlFunction.LowerCase, SqlFunction.UpperCase));

	private static final Set<SqlFunction> NUMERIC_ONLY_FUNCTIONS = new HashSet<SqlFunction>(Arrays.asList(
			SqlFunction.Average, SqlFunction.Sum));

	private static final Set<SqlFunction> VALID_FOR_ALL_TYPES = new HashSet<SqlFunction>(Arrays.asList(
			SqlFunction.Count, SqlFunction.Max, SqlFunction.Min));

	// TODO: move the column type enums here

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
			return this == LeftAlign;
		}

		if (isStringOnlyFunction(function)) {
			return this == LeftAlign;
		}

		if (function == SqlFunction.Round) {
			return this == RightAlign;
		}

		if (isNumericOnlyFunction(function)) {
			return this == RightAlign;
		}

		return false;
	}

	private boolean isNumericOnlyFunction(SqlFunction sqlFunction) {
		return NUMERIC_ONLY_FUNCTIONS.contains(sqlFunction);
	}

	private boolean isStringOnlyFunction(SqlFunction sqlFunction) {
		return STRING_ONLY_FUNCTIONS.contains(sqlFunction);
	}

	private boolean isFunctionValidForAllTypes(SqlFunction sqlFunction) {
		return VALID_FOR_ALL_TYPES.contains(sqlFunction);

		// TODO: Find out if these should or should not be included
		// if (function == QueryMethod.CountDistinct)
		// return true;
		// if (function == QueryMethod.GroupConcat)
		// return true;
	}

	private boolean isDateOnlyFunction(SqlFunction sqlFunction) {
		return DATE_ONLY_FUNCTIONS.contains(sqlFunction);
	}
}
