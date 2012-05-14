package com.picsauditing.report.models;

import java.util.HashMap;
import java.util.Map;

import com.picsauditing.access.Permissions;
import com.picsauditing.report.fields.Field;
import com.picsauditing.report.tables.BaseReportTable;

abstract public class ModelBase {
	protected BaseReportTable from;
	protected String defaultSort = null;
	protected Map<String, Field> availableFields = new HashMap<String, Field>();

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
