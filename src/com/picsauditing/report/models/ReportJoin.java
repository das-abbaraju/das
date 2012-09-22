package com.picsauditing.report.models;

import java.util.ArrayList;
import java.util.List;

import com.picsauditing.report.tables.ReportTable;
import com.picsauditing.util.Strings;

public class ReportJoin {
	private String alias;
	private ReportTable toTable;
	private List<ReportJoin> joins = new ArrayList<ReportJoin>();
	private String onClause;
	private boolean required = true;

	public ReportJoin() {
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public ReportTable getToTable() {
		return toTable;
	}

	public void setToTable(ReportTable toTable) {
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

	public String toString() {
		String value = "";
		if (!required)
			value += "LEFT ";
		value += "JOIN " + toTable;
		
		if (!Strings.isEmpty(alias) && !alias.equals(toTable.toString()))
			value += " AS " + alias;
		
		value += " ON " + onClause;
		for (ReportJoin join : joins) {
			value += "\n" + join.toString();
		}
		return value;
	}
}
