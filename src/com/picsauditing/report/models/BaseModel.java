package com.picsauditing.report.models;

import java.util.HashMap;
import java.util.Map;

import com.picsauditing.access.Permissions;
import com.picsauditing.report.fields.Field;
import com.picsauditing.report.tables.BaseTable;

public abstract class BaseModel {

	protected BaseTable primaryTable;
	protected String defaultSort = null;
	protected Map<String, Field> availableFields = new HashMap<String, Field>();

	public BaseTable getPrimaryTable() {
		return primaryTable;
	}

	public String getDefaultSort() {
		return defaultSort;
	}

	public String getWhereClause(Permissions permissions) {
		return "";
	}
}
