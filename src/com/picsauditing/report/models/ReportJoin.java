package com.picsauditing.report.models;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.picsauditing.report.fields.Field;
import com.picsauditing.report.tables.FieldImportance;
import com.picsauditing.report.tables.AbstractTable;
import com.picsauditing.util.Strings;

public class ReportJoin {
	private String alias = "";
	private AbstractTable toTable;
	private List<ReportJoin> joins = new ArrayList<ReportJoin>();
	private String onClause;
	private boolean required = true;
	private FieldImportance minimumImportance = FieldImportance.Low;

	private static final Logger logger = LoggerFactory.getLogger(ReportJoin.class);

	private boolean importantEnough(Field field) {
		boolean importantEnough = minimumImportance.ordinal() <= field.getImportance().ordinal();
		if (importantEnough) {
			logger.debug("Including " + alias + "." + field.getName());
		} else {
			logger.debug("   Excluding " + alias + "." + field.getName());
		}
		return importantEnough;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public AbstractTable getToTable() {
		return toTable;
	}

	public void setToTable(AbstractTable toTable) {
		this.toTable = toTable;
	}

	public List<ReportJoin> getJoins() {
		return joins;
	}

	public void setJoins(List<ReportJoin> joins) {
		this.joins = joins;
	}

	public void setOnClause(String onClause) {
		this.onClause = onClause;
	}

	public boolean isRequired() {
		return required;
	}

	public void setRequired(boolean required) {
		this.required = required;
	}

	public Collection<Field> getFields() {
		Collection<Field> fields = new ArrayList<Field>();
		for (Field field : toTable.getFields()) {
			if (importantEnough(field)) {
				Field fieldCopy = field.clone();
				fields.add(fieldCopy);
			}
		}

		for (ReportJoin join : joins) {
			fields.addAll(join.getFields());
		}

		return fields;
	}

	public String getTableClause() {
		if (!Strings.isEmpty(alias) && !alias.equals(toTable.toString()))
			return toTable + " AS " + alias;

		return toTable.toString();
	}

	public String toString() {
		String value = (required ? "" : "LEFT ") + "JOIN " + getTableClause();

		value += " ON " + onClause;
		for (ReportJoin join : joins) {
			value += "\n" + join.toString();
		}
		return value;
	}
}
