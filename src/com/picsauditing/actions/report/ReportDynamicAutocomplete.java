package com.picsauditing.actions.report;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.report.access.ReportUtil;
import com.picsauditing.report.fields.FieldType;
import com.picsauditing.report.fields.FilterType;

@SuppressWarnings({ "unchecked", "serial" })
public class ReportDynamicAutocomplete extends PicsActionSupport {

	private FieldType fieldType;
	private String searchQuery = "";

	private static final Logger logger = LoggerFactory.getLogger(ReportDynamicAutocomplete.class);

	public String execute() {
		try {
			json.put("success", true);
			if (fieldType.getFilterType() == FilterType.Autocomplete) {
				json = fieldType.getAutocompleteService().tokenJson(searchQuery, permissions);
				return JSON;
			}
			if (fieldType.getFilterType() == FilterType.ShortList) {
				json = ReportUtil.renderEnumFieldAsJson(fieldType, permissions);
				return JSON;
			}
			throw new Exception(fieldType + " not supported by list function.");
		} catch (Exception e) {
			logger.error("Unexpected exception in ReportDynamic.report()", e);
			writeJsonErrorMessage(e);
			return JSON;
		}
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
}
