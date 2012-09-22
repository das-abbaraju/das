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

abstract class ReportTable {
	private String sqlTableName;
	private Map<String, ReportForeignKey> keys = new HashMap<String, ReportForeignKey>();
	protected Map<String, Field> availableFields = new HashMap<String, Field>();
	protected Class entity; // Use this for filling fields and getting the sqlTableName from the Entity annotation

	private static final Logger logger = LoggerFactory.getLogger(ReportTable.class);

	public ReportTable(String sql) {
		this.sqlTableName = sql;
	}

	void addKey(ReportForeignKey join) {
		keys.put(join.getName(), join)
	}

	void addOptionalKey(ReportForeignKey join) {
		join.setRequired()
		addKey(join)
	}
	
	abstract protected void defineFields();

	protected void addFields(@SuppressWarnings("rawtypes") Class clazz, FieldImportance minimumImportance) {
		for (Field field : JpaFieldExtractor.addFields(clazz, symanticName, symanticName)) {
			if (importantEnough(field, minimumImportance)) {
				addField(field);
			}
		}
	}

	private boolean importantEnough(Field field, FieldImportance minimumImportance) {
		boolean importantEnough = minimumImportance.ordinal() <= field.getImportance().ordinal();
		if (importantEnough) {
			logger.debug("Including " + sqlTableName + "." + field.getName());
		} else {
			logger.debug("   Excluding " + sqlTableName + "." + field.getName());
		}
		return importantEnough;
	}

	//	public String getName() {
	//		return symanticName;
	//	}

	protected Field addPrimaryKey(FilterType filterType) {
		Field field = new Field(symanticName + "ID", symanticName + ".id", filterType);
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

	public ReportForeignKey getKey(String foreignKeyName) {
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