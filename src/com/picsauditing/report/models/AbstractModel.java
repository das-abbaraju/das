package com.picsauditing.report.models;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.picsauditing.access.Permissions;
import com.picsauditing.report.Filter;
import com.picsauditing.report.fields.Field;
import com.picsauditing.report.tables.AbstractTable;

public abstract class AbstractModel {

	protected AbstractTable rootTable;
	
	// The value for this should always be reset to the current model's table so that subclasses can use its as its parent. 
	protected AbstractTable parentTable;
	protected String defaultSort = null;
	protected Map<String, Field> availableFields = new HashMap<String, Field>();

	public AbstractTable getRootTable() {
		return rootTable;
	}

	public String getDefaultSort() {
		return defaultSort;
	}

	public String getWhereClause(Permissions permissions, List<Filter> filters) {
		return "";
	}
}
