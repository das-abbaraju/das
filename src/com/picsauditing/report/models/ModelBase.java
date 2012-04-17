package com.picsauditing.report.models;

import java.util.HashMap;
import java.util.Map;

import com.picsauditing.access.Permissions;
import com.picsauditing.report.fields.QueryField;
import com.picsauditing.report.tables.BaseReportTable;

abstract public class ModelBase {
	protected BaseReportTable from;
	protected String defaultSort = null;
	protected Map<String, QueryField> availableFields = new HashMap<String, QueryField>();

	public BaseReportTable getFrom() {
		return from;
	}

	public String getDefaultSort() {
		return defaultSort;
	}

	public String getWhereClause(Permissions permissions) {
		return "";
	}
}
