package com.picsauditing.report.tables;

import java.util.HashMap;
import java.util.Map;

import org.hibernate.annotations.ForeignKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.picsauditing.access.Permissions;
import com.picsauditing.report.fields.Field;
import com.picsauditing.report.fields.FilterType;
import com.picsauditing.report.models.ReportJoin;
import com.picsauditing.util.Strings;

abstract class AbstractTable {
	private String sqlTableName;
	private Map<String, ReportForeignKey> keys = null;
	Collection<Field> fields = new ArrayList<Field>();

	private static final Logger logger = LoggerFactory.getLogger(AbstractTable.class);

	public AbstractTable(String sql) {
		this.sqlTableName = sql;
		logger.debug("Creating " + this);
	}

	protected abstract void addJoins();
	
	ReportForeignKey addKey(ReportForeignKey join) {
		keys.put(join.getName(), join)
		return join;
	}

	ReportForeignKey addOptionalKey(ReportForeignKey join) {
		join.setRequired()
		return addKey(join)
	}

	protected void addFields(Class entity) {
		for (Field field : JpaFieldExtractor.addFields(entity)) {
			addField(field);
		}
	}

	protected Field addField(Field field) {
		fields.add field;
		return field;
	}

	protected Field addPrimaryKey(FilterType filterType) {
		Field field = new Field("ID", "id", filterType);
		field.setImportance(FieldImportance.Required)
		return addField(field);
	}
	
	public Field getField(String fieldName) {
		for (Field field : fields) {
			if (field.name.equalsIgnoreCase(fieldName)) {
				return field;
			}
		}
		return null;
	}
	
	public ReportForeignKey getKey(String foreignKeyName) {
		if (keys == null) {
			keys = new HashMap<String, ReportForeignKey>()
			addJoins()
		}
		System.out.println("Searching for Key = " + foreignKeyName);
		ReportForeignKey foreignKey = keys.get(foreignKeyName)
		if (foreignKey == null) {
			logger.error("Foreign key to " + foreignKeyName + " wasn't available in " + sqlTableName + " - " + keys.keySet());
			return null;
		}
		return foreignKey
	}

	public String toString() {
		sqlTableName;
	}
}