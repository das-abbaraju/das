package com.picsauditing.report.models;

import java.util.HashMap;
import java.util.Map;

import com.picsauditing.access.Permissions;
import com.picsauditing.report.fields.FilterType;
import com.picsauditing.report.fields.QueryField;
import com.picsauditing.report.fields.QueryFunction;
import com.picsauditing.report.fields.SimpleReportField;
import com.picsauditing.report.tables.BaseTable;

public class ModelBase {
	protected BaseTable from;
	protected String defaultSort = null;
	protected Map<String, QueryField> availableFields = new HashMap<String, QueryField>();
	protected Map<String, String> joins = new HashMap<String, String>();

	public BaseTable getFrom() {
		return from;
	}

	public String getDefaultSort() {
		return defaultSort;
	}

	public String getWhereClause(Permissions permissions) {
		return "";
	}

	protected QueryField addQueryField(String dataIndex, String sql, FilterType filterType) {
		return addQueryField(dataIndex, sql, filterType, null, false);
	}

	protected QueryField addQueryField(String dataIndex, String sql, FilterType filterType, String requireJoin) {
		return addQueryField(dataIndex, sql, filterType, requireJoin, false);
	}

	protected QueryField addQueryField(String dataIndex, String sql, FilterType filterType, boolean makeDefault) {
		return addQueryField(dataIndex, sql, filterType, null, makeDefault);
	}

	protected QueryField addQueryField(String dataIndex, String sql, FilterType filterType, String requireJoin,
			boolean makeDefault) {
		QueryField field = new QueryField(dataIndex, sql, filterType, requireJoin, makeDefault);
		availableFields.put(dataIndex, field);
		return field;
	}

	protected QueryField replaceQueryField(String source, String target) {
		QueryField field = availableFields.remove(source);
		field.setDataIndex(target);
		availableFields.put(target, field);
		return field;
	}

	private void addTotalField() {
		addQueryField("total", null, FilterType.Number);
		SimpleReportField total = new SimpleReportField();
		total.setField("total");
		total.setFunction(QueryFunction.Count);
		// columns.add(total);
	}

}
