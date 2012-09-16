package com.picsauditing.report;

import java.util.List;

import com.picsauditing.access.ReportValidationException;
import com.picsauditing.util.Strings;

public class FilterExpression {
	static public boolean isValid(String expression) {
		if (expression == null) {
			return true;
		}
		expression = expression.trim();
		if (expression.equals("")) {
			return true;
		}

		// TODO Add regex validation
		// Here's the start of a regular expression for validation
		// (\{[0-9]+\})(\s(AND|OR)\s(\{[0-9]+\}))*

		return true;
	}

	static public String getDefault(int size) {
		String expression = "{1}";

		for (int i = 2; i <= size; i++) {
			expression += " AND {" + i + "}";
		}

		return expression;
	}

	public static String parseWhereClause(String where, List<Filter> filters) throws ReportValidationException {
		if (where == null || Strings.isEmpty(where)) {
			where = FilterExpression.getDefault(filters.size());
		}

		int whereIndex = 1;
		for (Filter filter : filters) {
			String filterExp = filter.getSqlForFilter();
			where = where.replace("{" + whereIndex + "}", "(" + filterExp + ")");
			whereIndex++;
		}

		if (where.contains("{")) {
			// TODO Create a new Exception call ReportFilterExpression extends
			throw new ReportValidationException("DynamicReports.FilterExpressionInvalid "
					+ where);
		}
		return where;
	}
}
