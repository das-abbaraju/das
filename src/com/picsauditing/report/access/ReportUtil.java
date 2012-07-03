package com.picsauditing.report.access;

import java.util.List;

import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.picsauditing.access.ReportValidationException;
import com.picsauditing.jpa.entities.Report;
import com.picsauditing.report.Column;

public class ReportUtil {

	public static final String COLUMNS = "columns";
	public static final String FILTERS = "filters";
	public static final String SORTS = "sorts";
	public static final String FILTER_EXPRESSION = "filterExpression";

	public static Column getColumnFromFieldName(String fieldName, List<Column> columns) {
		if (fieldName == null)
			return null;

		for (Column column : columns) {
			if (column.getFieldName().equals(fieldName))
				return column;
		}

		return null;
	}

	public static void validate(Report report) throws ReportValidationException {
		// TODO Add i18n to this
		if (report == null)
			throw new ReportValidationException("Please provide a saved or ad hoc report to run");

		// TODO Add i18n to this
		if (report.getModelType() == null)
			throw new ReportValidationException("The report is missing its base", report);

		try {
			new JSONParser().parse(report.getParameters());
		} catch (ParseException e) {
			throw new ReportValidationException(e, report);
		}
	}
}
