package com.picsauditing.report.data;

import java.sql.Timestamp;
import java.text.DateFormatSymbols;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.beanutils.BasicDynaBean;
import org.json.simple.JSONArray;

import com.picsauditing.PICS.I18nCache;
import com.picsauditing.report.Column;
import com.picsauditing.report.fields.ExtFieldType;
import com.picsauditing.report.fields.QueryMethod;

public class ReportDataConverter {
	private final static I18nCache i18nCache = I18nCache.getInstance();
	private Map<String, Column> columnMap;
	private Locale locale;

	public ReportDataConverter(List<Column> columns, Locale locale) {
		convertListToMap(columns);
		this.locale = locale;
	}

	private void convertListToMap(List<Column> columns) {
		columnMap = new HashMap<String, Column>();
		for (Column column : columns) {
			columnMap.put(column.getFieldName().toUpperCase(), column);
		}
	}

	public JSONArray convertToJson(List<BasicDynaBean> results) {
		ReportResults reportResults = new ReportResults(columnMap, results);
		
		for (ReportRow row : reportResults.getRows()) {
			for (ReportCell cell : row.getCells()) {
				Object value = convertValueForJson(cell);
				cell.setValue(value);
			}
		}
		return reportResults.toJson();
	}

	private Object convertValueForJson(ReportCell cell) {
		Column column = cell.getColumn();
		Object value = cell.getValue();
		
		if (column != null) {
			if (column.getMethod() != null && column.getMethod() == QueryMethod.Month) {
				int month = Integer.parseInt(value.toString());
				return new DateFormatSymbols(locale).getMonths()[month-1];
			}
			if (column.getFieldName().contains("StatusSubstatus")) {
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
			if (column.getField().isTranslated()) {
				String key = column.getField().getI18nKey(value.toString());
				return getText(key, locale);
			}
			if (column.getField().getType() == ExtFieldType.Int) {
				return value;
			}
			if (column.getField().getType() == ExtFieldType.Float) {
				return value;
			}
			if (column.getField().getType() == ExtFieldType.Boolean) {
				return value;
			}
		}

		if (value instanceof java.sql.Date) {
			java.sql.Date valueAsDate = (java.sql.Date) value;
			return valueAsDate.getTime();
		}
		if (value instanceof java.sql.Timestamp) {
			Timestamp valueAsTimestamp = (Timestamp) value;
			return valueAsTimestamp.getTime();
		}

		return value.toString();
	}

	private static String getText(String key, Locale locale) {
		return i18nCache.getText(key, locale);
	}

	public JSONArray convertForPrinting(List<BasicDynaBean> queryResults) {
		// TODO Auto-generated method stub
		return null;
	}

}
