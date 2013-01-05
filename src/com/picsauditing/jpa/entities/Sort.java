package com.picsauditing.jpa.entities;

import java.util.Map;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.picsauditing.report.fields.Field;

@SuppressWarnings("serial")
@Entity
@Table(name = "report_sort")
public class Sort extends ReportElement {
	
	private boolean ascending = true;

	public Sort() {
	}

	public Sort(String fieldName) {
		super(fieldName);
	}

	// We might want to consider moving this to QueryField
	public String toSQL(Map<String, Field> availableFields) {
		String fieldSQL = availableFields.get(name).getDatabaseColumnName();
		return fieldSQL;
	}

	public boolean isAscending() {
		return ascending;
	}

	public void setAscending(boolean ascending) {
		this.ascending = ascending;
	}

	public String toString() {
		return super.toString() + (ascending ? " ASC" : " DESC");
	}
}
