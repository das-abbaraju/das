package com.picsauditing.report.tables;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.picsauditing.report.fields.Field;
import com.picsauditing.report.fields.FieldType;

public abstract class AbstractTable {
	private String sqlTableName;
	private Map<String, ReportForeignKey> keys = null;
	Collection<Field> fields = new ArrayList<Field>();

	private static final Logger logger = LoggerFactory.getLogger(AbstractTable.class);

	public AbstractTable(String sql) {
		this.sqlTableName = sql;
		logger.debug("Creating " + this);
	}

	protected abstract void addJoins();

	private ReportForeignKey addKey(ReportForeignKey join) {
		keys.put(join.getName(), join);
		return join;
	}

	public ReportForeignKey addRequiredKey(ReportForeignKey join) {
		join.setJoinType(JoinType.RequiredJoin);
		return addKey(join);
	}

	public ReportForeignKey addJoinKey(ReportForeignKey join) {
		join.setJoinType(JoinType.RequiredJoin);
		return addKey(join);
	}

	public ReportForeignKey addOptionalKey(ReportForeignKey join) {
		join.setJoinType(JoinType.LeftJoin);
		return addKey(join);
	}

	@SuppressWarnings("rawtypes")
	protected void addFields(Class entity) {
		for (Field field : JpaFieldExtractor.addFields(entity)) {
			addField(field);
		}
	}

	protected Field addField(Field field) {
		fields.add(field);
		return field;
	}

	protected Field addPrimaryKey() {
		return addPrimaryKey(FieldType.Integer);
	}

	protected Field addPrimaryKey(FieldType type) {
		Field field = new Field("ID", "id", type);
		field.setImportance(FieldImportance.Required);
		return addField(field);
	}

	public Field getField(String fieldName) {
		for (Field field : fields) {
			if (field.getName().equalsIgnoreCase(fieldName)) {
				return field;
			}
		}
		return null;
	}

	public ReportForeignKey getKey(String foreignKeyName) {
		if (keys == null) {
			keys = new HashMap<String, ReportForeignKey>();
			addJoins();
		}

		ReportForeignKey foreignKey = keys.get(foreignKeyName);
		if (foreignKey == null) {
			logger.error("Foreign key to " + foreignKeyName + " wasn't available in " + sqlTableName + " - "
					+ keys.keySet());
			return null;
		}
		return foreignKey;
	}

	public Collection<Field> getFields() {
		return fields;
	}

	public String toString() {
		return sqlTableName;
	}
}