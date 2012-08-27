package com.picsauditing.actions.report;


import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.actions.autocomplete.ReportFilterAutocompleter;
import com.picsauditing.jpa.entities.Report;
import com.picsauditing.model.ReportModel;
import com.picsauditing.report.access.ReportUtil;
import com.picsauditing.report.fields.Field;
import com.picsauditing.util.Strings;

@SuppressWarnings({ "unchecked", "serial" })
public class ReportDynamicAutocomplete extends PicsActionSupport {

	@Autowired
	private ReportFilterAutocompleter reportFilterAutocompleter;

	private Report report;

	private String fieldName = "";
	private String searchQuery = "";

	private static final Logger logger = LoggerFactory.getLogger(ReportDynamicAutocomplete.class);

	public String execute() {
		try {
			if (Strings.isEmpty(fieldName))
				throw new Exception("Please pass a fieldName when calling list");

			ReportModel.validate(report);

			Map<String, Field> availableFields = ReportModel.buildAvailableFields(report.getTable(), permissions);
			Field field = availableFields.get(fieldName.toUpperCase());

			if (field == null)
				throw new Exception("Available field undefined");

			if (field.getFilterType().isEnum()) {
				json = field.renderEnumFieldAsJson(permissions.getLocale());
			} else if (field.getFilterType().isAutocomplete()) {
				json = reportFilterAutocompleter.getFilterAutocompleteResultsJSON(field.getAutocompleteType(),
						searchQuery, permissions);
			} else if (field.getFilterType().isLowMedHigh()) {
				json = field.renderLowMedHighFieldAsJson(permissions.getLocale());
			} else {
				throw new Exception(field.getFilterType() + " not supported by list function.");
			}

			json.put("success", true);
		} catch (Exception e) {
			logger.error("Unexpected exception in ReportDynamic.report()", e);
			writeJsonErrorMessage(e);
		}

		return JSON;
	}

	public String translateValue() {
		try {
			if (Strings.isEmpty(fieldName))
				throw new Exception("Please pass a fieldName when calling list");

			ReportModel.validate(report);

			Map<String, Field> availableFields = ReportModel.buildAvailableFields(report.getTable(), permissions);
			Field field = availableFields.get(fieldName.toUpperCase());

			if (field == null)
				throw new Exception("Available field undefined");

			JSONArray jsonArray = new JSONArray();
			String[] values = searchQuery.split(",");
			String[] translationValueArray = new String[values.length];

			for (int i = 0; i < values.length; i++) {
				String translationKey = field.getI18nKey(values[i]);
				translationValueArray[i] = ReportUtil.getText(translationKey, permissions.getLocale());
			}
			String translatedString = ReportUtil.getText(field.getI18nKey(searchQuery), permissions.getLocale());

			JSONObject translationMap = new JSONObject();
			translationMap.put("key", searchQuery);

			if (translatedString == null) {
				translatedString = searchQuery;
			}
			translatedString = StringUtils.join(translationValueArray, ",");

			translationMap.put("value", translatedString);

			jsonArray.add(translationMap);

			json.put("result", jsonArray);

			json.put("success", true);
		} catch (Exception e) {
			logger.error("Unexpected exception in ReportDynamic.report()", e);
			writeJsonErrorMessage(e);
		}

		return JSON;
	}

	private void writeJsonErrorMessage(Exception e) {
		json.put("success", false);
		json.put("error", e.getCause() + " " + e.getMessage());
	}

	public Report getReport() {
		return report;
	}

	public void setReport(Report report) {
		this.report = report;
	}

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public String getSearchQuery() {
		return searchQuery;
	}

	public void setSearchQuery(String searchQuery) {
		this.searchQuery = searchQuery;
	}
}
