package com.picsauditing.report.models;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.picsauditing.access.Permissions;
import com.picsauditing.report.Column;
import com.picsauditing.report.Definition;
import com.picsauditing.report.Filter;
import com.picsauditing.report.Sort;
import com.picsauditing.report.fields.Field;
import com.picsauditing.report.tables.ReportJoin;
import com.picsauditing.report.tables.ReportTable;

public abstract class AbstractModel {

	protected ReportTable fromTable;
	private Map<String, ReportJoin> joins = new HashMap<String, ReportJoin>();
	protected Map<String, Field> availableFields;

	protected void addJoin(ReportJoin join, Permissions permissions) {
		if (joins.containsKey(join.getTable().getName())) {
			System.out.println("Adding join more than once " + join.getTable().getName());
		}
		joins.put(join.getTable().getName(), join);
		availableFields.putAll(join.getTable().getAvailableFields(permissions));
	}

	public Map<String, Field> getAvailableFields() {
		return availableFields;
	}

	public String getWhereClause(Permissions permissions, List<Filter> filters) {
		return "";
	}

	public String getFromClause() {
		return fromTable.toString();
	}

	public Collection<ReportJoin> getJoins() {
		return joins.values();
	}
	
	public boolean isJoinNeeded(ReportJoin join, Definition definition) {
		if (join.isRequired())
			return true;

//		for (ReportJoin otherJoin : joins.values()) {
//			if (join != otherJoin && otherJoin.requires(join))
//				return true;
//		}

		if (definition == null)
			return false;

		for (Field field : availableFields.values()) {
			String fieldName = field.getName();
			for (Column column : definition.getColumns()) {
				String columnName = column.getFieldName();
				if (columnName.equals(fieldName))
					return true;
			}

			for (Filter filter : definition.getFilters()) {
				String filterName = filter.getFieldName();
				if (filterName.equals(fieldName))
					return true;
			}

			for (Sort sort : definition.getSorts()) {
				String sortName = sort.getFieldName();
				if (sortName.equals(fieldName))
					return true;
			}
		}

		return false;
	}
}
