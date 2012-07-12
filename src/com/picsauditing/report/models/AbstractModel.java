package com.picsauditing.report.models;

import java.util.HashMap;
import java.util.Map;

import com.picsauditing.access.Permissions;
import com.picsauditing.report.fields.Field;
import com.picsauditing.report.tables.AbstractTable;

public abstract class AbstractModel {

	protected AbstractTable primaryTable;
	// TODO: Find a better way of passing down the parent table
	protected AbstractTable parentTable;
	protected String defaultSort = null;
	protected Map<String, Field> availableFields = new HashMap<String, Field>();

	public AbstractTable getPrimaryTable() {
		return primaryTable;
	}

	public String getDefaultSort() {
		return defaultSort;
	}

	public String getWhereClause(Permissions permissions) {
		return "";
	}
}
