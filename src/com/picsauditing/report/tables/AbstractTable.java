package com.picsauditing.report.tables;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.picsauditing.report.Column;
import com.picsauditing.report.Definition;
import com.picsauditing.report.Filter;
import com.picsauditing.report.Sort;
import com.picsauditing.report.SqlBuilder;
import com.picsauditing.report.fields.Field;
import com.picsauditing.report.fields.FilterType;
import com.picsauditing.util.Strings;

public abstract class AbstractTable {

	protected boolean innerJoin = true;
	protected String tableName;
	protected String prefix;
	protected String alias;
	protected String parentPrefix;
	protected String parentAlias;
	protected String onClause;
	protected FieldCategory overrideCategory;
	protected FieldImportance includedColumnImportance = FieldImportance.Required;
	protected Map<String, Field> availableFields = new HashMap<String, Field>();
	protected List<AbstractTable> joinedTables = new ArrayList<AbstractTable>();

	private static final Logger logger = LoggerFactory.getLogger(AbstractTable.class);
	
	public AbstractTable(String tableName, String prefix, String alias, String onClause) {
		this.tableName = tableName;
		this.prefix = prefix;
		this.alias = alias;
		this.onClause = onClause;

		// We need to wait to add the fields until they tell us how important this table is
		// addFields();
	}

	public abstract void addFields();

	public abstract void addJoins();

	public boolean isInnerJoin() {
		return innerJoin;
	}

	public void setLeftJoin() {
		this.innerJoin = false;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
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

	public String getParentPrefix() {
		return parentPrefix;
	}

	public void setParentPrefix(String parentPrefix) {
		this.parentPrefix = parentPrefix;
	}

	public String getParentAlias() {
		return parentAlias;
	}

	public void setParentAlias(String parentAlias) {
		this.parentAlias = parentAlias;
	}

	public FieldCategory getOverrideCategory() {
		return overrideCategory;
	}

	public void setOverrideCategory(FieldCategory overrideCategory) {
		this.overrideCategory = overrideCategory;
	}

	public FieldImportance getIncludedColumnImportance() {
		return includedColumnImportance;
	}

	public void includeOnlyRequiredColumns() {
		this.includedColumnImportance = FieldImportance.Required;
	}

	public void includeRequiredAndAverageColumns() {
		this.includedColumnImportance = FieldImportance.Average;
	}

	public void includeAllColumns() {
		this.includedColumnImportance = FieldImportance.Low;
	}

	public String getOnClause() {
		return onClause;
	}

	public void setOnClause(String onClause) {
		this.onClause = onClause;
	}

	public Map<String, Field> getAvailableFields() {
		return availableFields;
	}

	public void removeField(String name) {
		availableFields.remove(name.toUpperCase());
	}

	protected void addField(String fieldName, Field field) {
		availableFields.put(fieldName.toUpperCase(), field);
	}

	// Method chaining should only be for a single class
	protected Field addField(String sql, FilterType filter) {
		String name = alias + sql.substring(0, 1).toUpperCase() + sql.substring(1);
		if (sql.equals("id"))
			name = alias + "ID";

		String fullSql = alias + "." + sql;
		return addField(name, fullSql, filter, FieldCategory.General);
	}

	// Method chaining should only be for a single class
	protected Field addField(String fieldName, String sql, FilterType filter) {
		return addField(fieldName, sql, filter, FieldCategory.General);
	}

	// Method chaining should only be for a single class
	protected Field addField(String fieldName, String sql, FilterType filter, FieldCategory category) {
		Field field = new Field(fieldName, sql, filter);
		field.setCategory(category);
		overrideCategory(field);

		// We don't want to be case sensitive when matching names
		availableFields.put(fieldName.toUpperCase(), field);
		return field;
	}

	public List<AbstractTable> getJoins() {
		return joinedTables;
	}

	public AbstractTable addJoin(AbstractTable table) {
		joinedTables.add(table);
		return table;
	}

	public AbstractTable addLeftJoin(AbstractTable table) {
		joinedTables.add(table);
		table.setLeftJoin();
		return table;
	}

	public void addAllFieldsAndJoins(AbstractTable table) {
		joinedTables.add(table);
		// TODO rename this method to addAllJoinsPlusOneMoreLevel()
		// table.addFields();
		table.addJoins();
	}

	public void addFields(@SuppressWarnings("rawtypes") Class clazz) {
		for (Field field : JpaFieldExtractor.addFields(clazz, prefix, alias)) {
			if (importantEnough(field)) {
				overrideCategory(field);
				availableFields.put(field.getName().toUpperCase(), field);
			}
		}
	}

	private void overrideCategory(Field field) {
		if (overrideCategory != null) {
			field.setCategory(overrideCategory);
		}
	}

	private boolean importantEnough(Field field) {
		boolean importantEnough = includedColumnImportance.ordinal() <= field.getImportance().ordinal();
		if (importantEnough) {
			logger.debug("Including " + tableName + "." + field.getName());
		} else {
			logger.debug("  Excluding " + tableName + "." + field.getName());
		}
		return importantEnough;
	}

	public boolean isJoinNeeded(Definition definition) {
		if (isInnerJoin())
			return true;

		for (AbstractTable joinTable : getJoins()) {
			if (joinTable.isJoinNeeded(definition))
				return true;
		}

		if (definition == null)
			return false;

		for (Field field : getAvailableFields().values()) {
			for (Column column : definition.getColumns()) {
				if (column.getFieldNameWithoutMethod().equals(field.getName()))
					return true;
			}

			for (Filter filter : definition.getFilters()) {
				if (filter.getFieldName().equals(field.getName()))
					return true;
			}

			for (Sort sort : definition.getSorts()) {
				if (sort.getFieldName().equals(field.getName()))
					return true;
			}
		}

		return false;
	}

	public String getJoinSql() {
		String joinExpression = "";

		if (!isInnerJoin())
			joinExpression += "LEFT ";

		joinExpression += "JOIN " + getTableName();
		if (!Strings.isEmpty(getAlias()))
			joinExpression += " AS " + getAlias();

		joinExpression += " ON " + getOnClause();
		return joinExpression;
	}

	public void removeJoin(String prefix) {
		Iterator<AbstractTable> iterator = getJoins().iterator();
		while(iterator.hasNext()) {
			AbstractTable table = iterator.next();
			if (table.getPrefix().equals(prefix)) {
				iterator.remove();
				return;
			}
			table.removeJoin(prefix);
		}
	}

	public AbstractTable getTable(String prefix) {
		if (this.getPrefix().equals(prefix)) {
			return this;
		}
		for (AbstractTable table : getJoins()) {
			AbstractTable foundTable = table.getTable(prefix);
			if (foundTable != null) {
				return foundTable;
			}
		}
		return null;
	}
}