package com.picsauditing.report.data;

import java.sql.Timestamp;
import java.text.DateFormatSymbols;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

import org.apache.commons.beanutils.BasicDynaBean;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import com.picsauditing.PICS.I18nCache;
import com.picsauditing.jpa.entities.Column;
import com.picsauditing.jpa.entities.Filter;
import com.picsauditing.jpa.entities.Report;
import com.picsauditing.jpa.entities.Sort;
import com.picsauditing.report.access.ReportUtil;
import com.picsauditing.report.fields.DisplayType;
import com.picsauditing.report.fields.SqlFunction;

public class ReportDataConverter {
	
	private final static I18nCache i18nCache = I18nCache.getInstance();
	private Locale locale;
	private ReportResults reportResults;
	
	private static final Logger logger = LoggerFactory.getLogger(ReportDataConverter.class);

	public ReportDataConverter(Collection<Column> columns, List<BasicDynaBean> results) {
		reportResults = new ReportResults(columns, results);
	}

	@SuppressWarnings("unchecked")
	public JSONObject convertReportForFrontEnd(Report report) {
		JSONObject jsonReport = new JSONObject();

		// TODO: Create constants for these values 
		jsonReport.put("id", report.getId());
		jsonReport.put("type", report.getModelType());
		jsonReport.put("name", report.getName());		
		jsonReport.put("description", report.getId());
		jsonReport.put("columns", convertColumnsForFrontEnd(report.getColumns()));
		jsonReport.put("filters", convertFiltersForFrontEnd(report.getFilters()));
		jsonReport.put("sorts", convertSortsForFrontEnd(report.getSorts()));
		jsonReport.put("filter_expression", report.getFilterExpression());
		jsonReport.put("num_times_favorited", report.getNumTimesFavorited());
		jsonReport.put("is_editable", report.isEditable());
		jsonReport.put("is_favorite", report.isFavorite());
		jsonReport.put("creation_date", report.getCreationDate());
		jsonReport.put("created_by", report.getCreatedBy().getName());
		jsonReport.put("update_date", report.getUpdateDate());
		jsonReport.put("updated_by", report.getUpdatedBy().getName());
		
		jsonReport.put(ReportUtil.REPORT, jsonReport);
		
		return jsonReport;
	}
	
	@SuppressWarnings("unchecked")
	public JSONArray convertColumnsForFrontEnd(List<Column> columns) {
		JSONArray columnArray = new JSONArray();
		if (CollectionUtils.isEmpty(columns)) {
			return columnArray;
		}
		
		for (Column column : columns) {
			JSONObject columnObject = new JSONObject();
			columnObject.put("id", column.getFieldNameWithoutMethod());
			columnObject.put("type", column.getField().getType());
			columnObject.put("category", column.getField().getCategory());
			columnObject.put("name", column.getName());
			columnObject.put("description", column.getField());
			columnObject.put("url", column.getField().getUrl());
			columnObject.put("sql_function", column.getSqlFunction());
			columnObject.put("width", column.getWidth());
			columnObject.put("is_sortable", column.getField().isSortable());
			
			columnArray.add(columnObject);
		}
		
		return columnArray;
	}
	
	@SuppressWarnings("unchecked")
	public JSONArray convertFiltersForFrontEnd(List<Filter> filters) {
		JSONArray filterArray = new JSONArray();
		if (CollectionUtils.isEmpty(filters)) {
			return filterArray;
		}
		
		for (Filter filter : filters) {
			JSONObject filterObject = new JSONObject();
			filterObject.put("id", filter.getName());
			filterObject.put("type", filter.getField().getType());
			filterObject.put("category", filter.getField().getCategory());
			filterObject.put("name", filter.getName());
			filterObject.put("description", filter.getField().getHelp());
			filterObject.put("operator", filter.getOperator());
			filterObject.put("value", filter.getValues());
			filterObject.put("column_compare_id", filter.getColumnCompare());
			
			filterArray.add(filterObject);
		}
		
		return filterArray;
	}	
	
	@SuppressWarnings("unchecked")
	public JSONArray convertSortsForFrontEnd(List<Sort> sorts) {
		JSONArray sortArray = new JSONArray();
		if (CollectionUtils.isEmpty(sorts)) {
			return sortArray;
		}
		
		for (Sort sort : sorts) {
			JSONObject sortObject = new JSONObject();
			sortObject.put("id", sort.getName());
			sortObject.put("direction", getSortDirection(sort));
			
			sortArray.add(sortObject);
		}
		
		return sortArray;
	}
	
	private String getSortDirection(Sort sort) {
		return sort.isAscending() ? Sort.ASCENDING : Sort.DESCENDING;
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
			if (displayType == DisplayType.Integer) {
				return value;
			}
			
			if (displayType == DisplayType.Float) {
				return value;
			}
			
			if (displayType == DisplayType.Boolean) {
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
			if (column.getField().getType().getDisplayType() == DisplayType.Boolean) {
				return value;
			}
		}

		return value;
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
