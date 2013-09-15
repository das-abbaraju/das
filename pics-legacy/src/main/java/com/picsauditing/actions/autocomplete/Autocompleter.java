package com.picsauditing.actions.autocomplete;

import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.ReportDAO;
import com.picsauditing.jpa.entities.Report;
import com.picsauditing.model.general.AutocompleteSharing;
import com.picsauditing.report.RecordNotFoundException;
import com.picsauditing.report.ReportUtil;
import com.picsauditing.report.fields.Field;
import com.picsauditing.report.fields.FieldType;
import com.picsauditing.report.models.ReportModelFactory;
import com.picsauditing.util.Strings;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@SuppressWarnings({ "unchecked", "serial" })
public class Autocompleter extends PicsActionSupport {

	@Autowired
	private AutocompleteSharing autocompleteSharing;
	@Autowired
	private ReportDAO reportDAO;

	private FieldType fieldType;

	// TODO: we should not have to assign these to empty strings
	private String searchQuery = Strings.EMPTY_STRING;
	private String searchKey;
	private String fieldId;
	private int reportId;

	private static final Logger logger = LoggerFactory.getLogger(Autocompleter.class);

	/**
	 * For the general Autocomplete cases that are simple key-value pairs in the
	 * JSON.
	 */
	@Override
	public String execute() {
		try {
			if (fieldType == null) {
				fieldType = findFieldType(fieldId);
			}

			json.put("success", true);
			switch (fieldType.getFilterType()) {
			case Autocomplete:
                AbstractAutocompleteService<?> autocompleteService = fieldType.getAutocompleteService();

                if (Strings.isNotEmpty(searchKey)) {
					if (searchKey.contains(", ")) {
						List<String> searchKeys = Arrays.asList(searchKey.split(","));
						JSONArray fullArray = new JSONArray();
						for (String key : searchKeys) {
							JSONObject result = autocompleteService.searchByKey(key.trim(), permissions);
							JSONArray internal = (JSONArray) result.get("result");
							fullArray.addAll(internal);
						}
						json.put("result",fullArray);
					} else {
						json = autocompleteService.searchByKey(searchKey, permissions);
					}

				} else {
					json = autocompleteService.getJson(searchQuery, permissions);
				}
				break;

			case Multiselect:
				json = ReportUtil.renderEnumFieldAsJson(fieldType, permissions);
				break;

			default:
				throw new Exception(fieldType + " not supported for autocomplete.");
			}
		} catch (Exception e) {
			logger.error("Unexpected exception in Autocompleter.", e);
			writeJsonErrorMessage(e);
		}

		return JSON;
	}

	public String reportSharingAutocomplete() {
		try {
			if (reportId <= 0) {
				throw new Exception("Invalid reportId parameter.");
			}

			jsonArray = autocompleteSharing.buildSharingAutocomplete(reportId, searchQuery, permissions);
		} catch (Exception e) {
			logger.error("Unexpected exception in reportSharingAutocomplete.", e);
		}

		return JSON_ARRAY;
	}

	private FieldType findFieldType(String fieldId) {
		try {
			Report report = reportDAO.findById(reportId);
			if (report == null) {
				throw new RecordNotFoundException("Report " + reportId + " was not found in the database");
			}

			Map<String, Field> availableFields = ReportModelFactory.build(report.getModelType(), permissions)
					.getAvailableFields();
			Field field = availableFields.get(fieldId.toUpperCase());

			if (field != null) {
				return field.getType();
			}
		} catch (Exception e) {
			logger.error("An error occurred while finding field for fieldID = {}", fieldId, e);
		}

		return null;
	}

	private void writeJsonErrorMessage(Exception e) {
		json.put("success", false);
		json.put("error", e.getCause() + " " + e.getMessage());
	}

	public void setFieldType(FieldType fieldType) {
		this.fieldType = fieldType;
	}

	public void setSearchQuery(String searchQuery) {
		this.searchQuery = searchQuery;
	}

	public void setSearchKey(String searchKey) {
		this.searchKey = searchKey;
	}

	public void setFieldId(String fieldId) {
		this.fieldId = fieldId;
	}

	public void setReportId(int reportId) {
		this.reportId = reportId;
	}
}