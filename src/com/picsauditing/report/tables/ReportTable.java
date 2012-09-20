package com.picsauditing.report.tables;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.picsauditing.access.Permissions;
import com.picsauditing.report.fields.Field;
import com.picsauditing.report.fields.FilterType;
import com.picsauditing.util.Strings;

public abstract class ReportTable {
	protected String name;
	private String sql;
	private Map<String, ReportJoin> joins = new HashMap<String, ReportJoin>();
	protected Map<String, Field> availableFields = new HashMap<String, Field>();
	protected FieldCategory overrideCategory;

	private static final Logger logger = LoggerFactory.getLogger(ReportTable.class);

	public ReportTable(String sql, String name) {
		this.sql = sql;
		this.name = name;
	}

	public void addJoin(ReportJoin join) {
		joins.put(join.getTable().getName(), join);
	}

	public void addLeftJoin(ReportJoin join) {
		join.setLeftJoin();
		joins.put(join.getTable().getName(), join);
	}
	abstract public void fill(Permissions permissions);
	
	protected void addFields(@SuppressWarnings("rawtypes") Class clazz, FieldImportance minimumImportance) {
		for (Field field : JpaFieldExtractor.addFields(clazz, name, name)) {
			if (importantEnough(field, minimumImportance)) {
				addField(field);
			}
		}
	}

	private boolean importantEnough(Field field, FieldImportance minimumImportance) {
		boolean importantEnough = minimumImportance.ordinal() <= field.getImportance().ordinal();
		if (importantEnough) {
			logger.debug("Including " + sql + "." + field.getName());
		} else {
			logger.debug("   Excluding " + sql + "." + field.getName());
		}
		return importantEnough;
	}

	public String getName() {
		return name;
	}
	
	protected Field addPrimaryKey(FilterType filterType) {
		Field field = new Field(name + "ID", name + ".id", filterType);
		return addField(field);
	}
	
	protected Field addField(Field field) {
		// We don't want to be case sensitive when matching names
		availableFields.put(field.getName().toUpperCase(), field);
		if (overrideCategory != null) {
			field.setCategory(overrideCategory);
		}
		return field;
	}
	
	public Map<String, Field> getAvailableFields(Permissions permissions) {
		fill(permissions);
		return availableFields;
	}

	public void setOverrideCategory(FieldCategory overrideCategory) {
		this.overrideCategory = overrideCategory;
	}
	
	public String toString() {
		if (Strings.isEmpty(sql) || name.equals(sql))
			return name;

		return sql + " AS " + name;
	}

	public ReportJoin getJoin(String toTableName) {
		logger.debug("Getting join for " + toTableName + " in " + joins.keySet());
		return joins.get(toTableName);
	}
}