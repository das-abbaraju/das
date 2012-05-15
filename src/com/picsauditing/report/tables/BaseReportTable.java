package com.picsauditing.report.tables;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.picsauditing.report.fields.FilterType;
import com.picsauditing.report.fields.Field;

public abstract class BaseReportTable {

	protected boolean innerJoin = true;
	protected String table;
	protected String prefix;
	protected String alias;
	protected String where;
	protected Map<String, Field> availableFields = new HashMap<String, Field>();
	protected List<BaseReportTable> joinedTables = new ArrayList<BaseReportTable>();

	public BaseReportTable(String table, String prefix, String alias, String where) {
		this.table = table;
		this.prefix = prefix;
		this.alias = alias;
		this.where = where;

		addFields();
	}

	public abstract void addFields();

	public abstract void addJoins();

	public boolean isInnerJoin() {
		return innerJoin;
	}

	public void setLeftJoin() {
		this.innerJoin = false;
	}

	public String getTable() {
		return table;
	}

	public void setTable(String table) {
		this.table = table;
	}

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public String getWhere() {
		return where;
	}

	public void setWhere(String where) {
		this.where = where;
	}

	public Map<String, Field> getAvailableFieldsMap() {
		return availableFields;
	}

	public void removeField(String name) {
		availableFields.remove(name.toUpperCase());
	}

	protected Field addField(String sql, FilterType filter) {
		String name = alias + sql.substring(0, 1).toUpperCase() + sql.substring(1);
		if (sql.equals("id"))
			name = alias + "ID";

		String fullSql = alias + "." + sql;
		return addField(name, fullSql, filter);
	}

	protected Field addField(String name, String sql, FilterType filter) {
		Field field = new Field(name, sql, filter);
		// We don't want to be case sensitive when matching names
		availableFields.put(name.toUpperCase(), field);
		return field;
	}

	public List<BaseReportTable> getJoins() {
		return joinedTables;
	}

	public BaseReportTable addJoin(BaseReportTable table) {
		joinedTables.add(table);
		return table;
	}

	public BaseReportTable addLeftJoin(BaseReportTable table) {
		joinedTables.add(table);
		table.setLeftJoin();
		return table;
	}

	public BaseReportTable addAllFieldsAndJoins(BaseReportTable table) {
		joinedTables.add(table);
		table.addFields();
		table.addJoins();

		return table;
	}

	public void addFields(@SuppressWarnings("rawtypes") Class clazz) {
		for (Field field : JpaFieldExtractor.addFields(clazz, prefix, alias)) {
			availableFields.put(field.getName().toUpperCase(), field);
		}
	}

}
