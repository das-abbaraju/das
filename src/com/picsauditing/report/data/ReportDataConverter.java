package com.picsauditing.report.data;

import java.sql.Timestamp;
import java.text.DateFormatSymbols;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

import org.apache.commons.beanutils.BasicDynaBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.picsauditing.PICS.I18nCache;
import com.picsauditing.jpa.entities.Column;
import com.picsauditing.report.fields.DisplayType;
import com.picsauditing.report.fields.SqlFunction;
import com.picsauditing.util.PicsDateFormat;

public class ReportDataConverter {
	
	private Locale locale;
	private ReportResults reportResults;

	private static I18nCache i18nCache = I18nCache.getInstance();
	private static final Logger logger = LoggerFactory.getLogger(ReportDataConverter.class);

	public ReportDataConverter(Collection<Column> columns, List<BasicDynaBean> results) {
		reportResults = new ReportResults(columns, results);
	}

	public void convertForExtJS() {
		for (ReportCell cell : reportResults.getCells()) {
			Object value = convertValueForJson(cell);
			cell.setValue(value);
		}
	}

	public void convertForPrinting() {
		for (ReportCell cell : reportResults.getCells()) {
			Object value = convertValueForPrinting(cell);
			cell.setValue(value);
		}
	}

	private Object convertValueForJson(ReportCell cell) {
		Object value = cell.getValue();
		if (value == null) {
			return null;
		}

		Object result = convertValueBasedOnCellColumn(cell);
		if (result == null) {
			result = convertValueBasedOnType(value);
			if (result == null) {
				result = value.toString();
			}
		}

		return result;
	}

	private Object convertValueForPrinting(ReportCell cell) {
		Object value = cell.getValue();
		if (value == null)
			return null;

		Object result = convertValueBasedOnCellColumn(cell);
		if (result == null) {
			result = value;
		}

		return result;
	}

	private Object convertValueBasedOnCellColumn(ReportCell cell) {
		Column column = cell.getColumn();
		Object value = cell.getValue();
		Object result = null;

		if (column != null) {
			logger.info("Attempting to convert {}, value: {}", cell.getColumn().getName(), cell.getValue() );

			if (column.getSqlFunction() != null && column.getSqlFunction() == SqlFunction.Month) {
				result = convertValueAsMonth(value);
			}

			if (column.getName().contains("StatusSubstatus")) {
				result = convertValueAsTranslatedStatus((String) value);
			}

			if (column.getField() == null) {
				result = column.getName() + ": Field not available";
			}

			if (column.getField().isTranslated()) {
				String key = column.getField().getI18nKey(value.toString());
				result = getText(key, locale);
			}

			DisplayType displayType = column.getField().getType().getDisplayType();
			if (displayType == DisplayType.Number) {
				result = value;
			}

			if (displayType == DisplayType.Boolean) {
				result = value;
			}
		}

		return result;
	}

	private String convertValueAsMonth(Object value) {
		int month = Integer.parseInt(value.toString());
		return new DateFormatSymbols(locale).getMonths()[month - 1];
	}

	private String convertValueAsTranslatedStatus(String value) {
		String[] valueString = value.split(":");

		String statusI18nKey = "AuditStatus." + valueString[0];
		String statusTranslation = getText(statusI18nKey, locale);
		String valueTranslated = statusTranslation;

		if (valueString.length > 1) {
			String subStatusI18nKey = "AuditSubStatus." + valueString[1];
			String subStatusTranslation = getText(subStatusI18nKey, locale);
			valueTranslated += ": " + subStatusTranslation;
		}

		return valueTranslated;
	}

	private String convertValueBasedOnType(Object value) {
		String result = null;

		if (value instanceof java.sql.Date) {
			java.sql.Date valueAsDate = (java.sql.Date) value;
			result = PicsDateFormat.formatDateIsoOrBlank(valueAsDate);
		}

		if (value instanceof java.sql.Timestamp) {
			Timestamp valueAsTimestamp = (Timestamp) value;
			result = PicsDateFormat.formatDateOrBlank(valueAsTimestamp, PicsDateFormat.DateAndTime);
		}
		return result;
	}

	private static String getText(String key, Locale locale) {
		return i18nCache.getText(key, locale);
	}

	public void setLocale(Locale locale) {
		this.locale = locale;
	}

	public ReportResults getReportResults() {
		return reportResults;
	}
}
