package com.picsauditing.report.models;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.picsauditing.access.Permissions;
import com.picsauditing.report.Filter;
import com.picsauditing.report.fields.Field;
import com.picsauditing.report.tables.AbstractTable;

public abstract class AbstractModel {

	/**
	 * The base table for the Highest Parent class. For example: AccountTable
	 */
	protected AbstractTable rootTable;

	/**
	 * The value for this should always be reset to the current model's table so
	 * that subclasses can use its as its parent. For example:
	 * PaymentCommissionTable
	 */
	protected AbstractTable parentTable;

	/**
	 * All selectable fields that a user can query/filter/sort from on this
	 * Model
	 */
	protected Map<String, Field> availableFields = new HashMap<String, Field>();

	public AbstractTable getRootTable() {
		return rootTable;
	}

	public String getWhereClause(Permissions permissions, List<Filter> filters) {
		return "";
	}
}
