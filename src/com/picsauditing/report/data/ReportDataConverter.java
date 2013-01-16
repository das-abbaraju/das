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
		if (value == null)
			return null;
		
		Column column = cell.getColumn();

		if (column != null) {
			// TODO Change this method to use column.getField().getColumnType() instead
			
			if (column.getSqlFunction() != null && column.getSqlFunction() == SqlFunction.Month) {
				int month = Integer.parseInt(value.toString());
				return new DateFormatSymbols(locale).getMonths()[month - 1];
			}
			
			if (column.getName().contains("StatusSubstatus")) {
				String[] valueString = ((String) value).split(":");

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

			if (column.getField() == null) {
				// This really shouldn't happen but just in case, this message
				// is better than an NPE
				return column.getName() + ": Field not available";
			}
			
			if (column.getField().isTranslated()) {
				String key = column.getField().getI18nKey(value.toString());
				return getText(key, locale);
			}
			
			DisplayType displayType = column.getField().getType().getDisplayType();
			if (displayType == DisplayType.RightAlign) {
				return value;
			}
			
			if (displayType == DisplayType.RightAlign) {
				return value;
			}
			
			if (displayType == DisplayType.CheckMark) {
				return value;
			}
		}

		if (value instanceof java.sql.Date) {
			java.sql.Date valueAsDate = (java.sql.Date) value;
			return PicsDateFormat.formatDateIsoOrBlank(valueAsDate);
		}
		
		if (value instanceof java.sql.Timestamp) {
			Timestamp valueAsTimestamp = (Timestamp) value;
			return PicsDateFormat.formatDateOrBlank(valueAsTimestamp, PicsDateFormat.DateAndTime);
		}

		return value.toString();
	}

	private Object convertValueForPrinting(ReportCell cell) {
		Object value = cell.getValue();
		if (value == null)
			return null;
		
		Column column = cell.getColumn();

		if (column != null) {
			logger.info("Converting {} value: {}", cell.getColumn().getName(), value );
			
			if (column.getSqlFunction() != null && column.getSqlFunction() == SqlFunction.Month) {
				int month = Integer.parseInt(value.toString());
				return new DateFormatSymbols(locale).getMonths()[month - 1];
			}
			if (column.getName().contains("StatusSubstatus")) {
				String[] valueString = ((String) value).split(":");

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

			if (column.getField() == null) {
				// This really shouldn't happen but just in case, this message
				// is better than an NPE
				return column.getName() + ": Field not available";
			}
			if (column.getField().isTranslated()) {
				String key = column.getField().getI18nKey(value.toString());
				return getText(key, locale);
			}
			if (column.getField().getType().getDisplayType() == DisplayType.CheckMark) {
				return value;
			}
		}

		return value;
	}

	private static String getText(String key, Locale locale) {
		return I18nCache.getInstance().getText(key, locale);
	}

	public void setLocale(Locale locale) {
		this.locale = locale;
	}

	public ReportResults getReportResults() {
		return reportResults;
	}
}
