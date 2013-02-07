package com.picsauditing.actions.autocomplete;

import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.model.general.Autocomplete;
import com.picsauditing.report.ReportUtil;
import com.picsauditing.report.fields.FieldType;
import com.picsauditing.util.Strings;

@SuppressWarnings({ "unchecked", "serial" })
public class Autocompleter extends PicsActionSupport {

	@Autowired
	private Autocomplete autocomplete;

	private FieldType fieldType;

	// TODO: we should not have to assign these to empty strings
	private String searchQuery = Strings.EMPTY_STRING;
	private String searchKey;

	private static final Logger logger = LoggerFactory.getLogger(Autocompleter.class);

	/**
	 * For the general Autocomplete cases that are simple key-value pairs in the
	 * JSON.
	 */
	@Override
	public String execute() {
		try {
			json.put("success", true);
			switch (fieldType.getFilterType()) {
				case Autocomplete:
					if (Strings.isNotEmpty(searchKey)) {
						json = fieldType.getAutocompleteService().searchByKey(searchKey, permissions);
					} else {
						json = fieldType.getAutocompleteService().getJson(searchQuery, permissions);
					}
					break;

				case ShortList:
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
			int reportId = NumberUtils.toInt(getRequest().getParameter("reportId"));
			if (reportId <= 0) {
				throw new Exception("Invalid reportId parameter.");
			}

			json = autocomplete.sharingAutocomplete(reportId, searchQuery, permissions);
		} catch (Exception e) {
			logger.error("Unexpected exception in reportSharingAutocomplete.", e);
			writeJsonErrorMessage(e);
		}

		return JSON;
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
}
