package com.picsauditing.report.data;

import java.sql.*;
import java.text.DateFormatSymbols;
import java.util.*;
import java.util.Date;

import com.picsauditing.report.fields.FieldType;
import com.picsauditing.util.TimeZoneUtil;
import org.apache.commons.beanutils.BasicDynaBean;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.picsauditing.PICS.I18nCache;
import com.picsauditing.jpa.entities.Column;
import com.picsauditing.report.fields.DisplayType;
import com.picsauditing.report.fields.Field;
import com.picsauditing.report.fields.SqlFunction;
import com.picsauditing.util.PicsDateFormat;

public class ReportDataConverter {

	private Locale locale;
	private final ReportResults reportResults;

	private static I18nCache i18nCache = I18nCache.getInstance();
	private static final Logger logger = LoggerFactory.getLogger(ReportDataConverter.class);

	public ReportDataConverter(Collection<Column> columns, List<BasicDynaBean> results) {
		reportResults = new ReportResults(columns, results);
	}

	public void convertForExtJS(TimeZone timezone) {
		for (ReportCell cell : reportResults.getCells()) {
			Object value = convertValueForJson(cell, timezone);
			cell.setValue(value);
		}
	}

	public void convertForPrinting() {
		for (ReportCell cell : reportResults.getCells()) {
			Object value = convertValueForPrinting(cell);
			cell.setValue(value);
		}
	}

    public JSONArray convertForChart() {
        JSONArray dataJson = new JSONArray();
        for (ReportRow row : reportResults.getRows()) {
            JSONObject rowColumnJson = new JSONObject();
            JSONArray rowJson = new JSONArray();
            for (ReportCell cell : row.getCells()) {
                JSONObject cellJson = new JSONObject();
                cellJson.put("v",cell.getValue().toString());
                cellJson.put("f",null);

                rowJson.add(cellJson);
            }
            rowColumnJson.put("c",rowJson);
            dataJson.add(rowColumnJson);
        }

        return dataJson;
    }

    private Object convertValueForJson(ReportCell cell, TimeZone timezone) {
		Object value = cell.getValue();
		if (value == null) {
			return null;
		}

        FieldType type = cell.getColumn().getField().getType();


		Object result = convertValueBasedOnCellColumn(cell, false);
		if (result == null) {
			result = convertValueBasedOnType(value, type, timezone);
			if (result == null) {
				result = value.toString();
			}
		}

		return result;
	}

	private Object convertValueForPrinting(ReportCell cell) {
		Object value = cell.getValue();
		if (value == null) {
			return null;
		}

		Object result = convertValueBasedOnCellColumn(cell, true);
		if (result == null) {
			result = value;
		}

		return result;
	}

	private Object convertValueBasedOnCellColumn(ReportCell cell, boolean forPrint) {
		Column column = cell.getColumn();
		Object value = cell.getValue();
		Object result = null;

		if (column == null) {
			return result;
		}

		logger.info("Attempting to convert {}, value: {}", column.getName(), value);

		SqlFunction sqlFunction = column.getSqlFunction();
		if (sqlFunction != null && sqlFunction == SqlFunction.Month) {
			result = convertValueAsMonth(value);
		}

		if (column.getName().contains("StatusSubstatus")) {
			result = convertValueAsTranslatedStatus((String) value);
		}

		Field field = column.getField();
		if (field == null) {
			result = column.getName() + ": Field not available";
			return result;
		}

		if (field.isTranslated() && column.hasNoSqlFunction()) {
			String key = field.getI18nKey(value.toString());
			result = getText(key, locale);
		}

		DisplayType displayType = field.getType().getDisplayType();
		if (displayType == DisplayType.Number) {
			result = value;
		}

		if (displayType == DisplayType.Boolean) {
            if (forPrint) {
            	long numericValue = safeConversionToLong(value);
                if (numericValue == 1) {
					result = "Y";
				} else {
					result = "N";
				}
            } else {
				result = value;
			}
        }

		return result;
	}

	private long safeConversionToLong(Object value) {
		if (value instanceof Integer) {
			return ((Integer) value).intValue();
		} else if (value instanceof Long) {
			return ((Long) value).longValue();
		}

		return 0;
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

	private String convertValueBasedOnType(Object value, FieldType type, TimeZone timezone) {
		String result = null;

        Date date = null;

        if (value instanceof Timestamp) {
            date = new Date(((Timestamp)value).getTime());
        } else if (value instanceof java.sql.Date) {
            date = new Date(((java.sql.Date)value).getTime());
        } else {
            return null;
        }

		if (type == FieldType.Date) {
			result = PicsDateFormat.formatDateIsoOrBlank(date);
		}

		if (type == FieldType.DateTime) {
			result = TimeZoneUtil.getFormattedTimeStringWithNewTimeZone(timezone, PicsDateFormat.DateAndTimeNoTimezone, date);
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
