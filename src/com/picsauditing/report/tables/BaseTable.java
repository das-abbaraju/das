package com.picsauditing.report.tables;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.picsauditing.report.fields.FilterType;
import com.picsauditing.report.fields.QueryField;

public abstract class BaseTable {
	protected boolean innerJoin = true;
	protected String table;
	protected String prefix;
	protected String alias;
	protected String where;
	protected Map<String, QueryField> fields = new HashMap<String, QueryField>();
	protected List<BaseTable> joins = new ArrayList<BaseTable>();

	public BaseTable(String table, String prefix, String alias, String where) {
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

	public Map<String, QueryField> getFields() {
		return fields;
	}

	public void removeField(String name) {
		fields.remove(name.toUpperCase());
	}

	protected QueryField addField(String sql, FilterType filter) {
		String name = alias + sql.substring(0, 1).toUpperCase() + sql.substring(1);
		if (sql.equals("id"))
			name = alias + "ID";
		String fullSql = alias + "." + sql;
		return addField(name, fullSql, filter);
	}

	protected QueryField addField(String name, String sql, FilterType filter) {
		QueryField field = new QueryField(name, sql, filter);
		// We don't want to be case sensitive when matching names
		fields.put(name.toUpperCase(), field);
		return field;
	}

	public List<BaseTable> getJoins() {
		return joins;
	}

	public BaseTable addJoin(BaseTable join) {
		joins.add(join);
		return join;
	}

	public BaseTable addLeftJoin(BaseTable join) {
		joins.add(join);
		join.setLeftJoin();
		return join;
	}

	public BaseTable addAllFieldsAndJoins(BaseTable join) {
		joins.add(join);
		join.addFields();
		join.addJoins();
		return join;
	}

	public void addFields(@SuppressWarnings("rawtypes") Class clazz) {
		for (QueryField field : JpaFieldExtractor.addFields(clazz, prefix, alias)) {
			fields.put(field.getName().toUpperCase(), field);
		}
	}

}
